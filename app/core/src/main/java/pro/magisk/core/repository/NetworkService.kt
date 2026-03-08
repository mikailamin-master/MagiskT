package pro.magisk.core.repository

import pro.magisk.core.BuildConfig
import pro.magisk.core.Config
import pro.magisk.core.Config.Value.BETA_CHANNEL
import pro.magisk.core.Config.Value.CUSTOM_CHANNEL
import pro.magisk.core.Config.Value.DEBUG_CHANNEL
import pro.magisk.core.Config.Value.DEFAULT_CHANNEL
import pro.magisk.core.Config.Value.STABLE_CHANNEL
import pro.magisk.core.Info
import pro.magisk.core.data.GithubApiServices
import pro.magisk.core.data.RawUrl
import pro.magisk.core.ktx.dateFormat
import pro.magisk.core.model.Release
import pro.magisk.core.model.ReleaseAssets
import pro.magisk.core.model.UpdateInfo
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class NetworkService(
    private val raw: RawUrl,
    private val api: GithubApiServices,
) {
    suspend fun fetchUpdate() = safe {
        var info = when (Config.updateChannel) {
            DEFAULT_CHANNEL -> if (BuildConfig.DEBUG) fetchDebugUpdate() else fetchStableUpdate()
            STABLE_CHANNEL -> fetchStableUpdate()
            BETA_CHANNEL -> fetchBetaUpdate()
            DEBUG_CHANNEL -> fetchDebugUpdate()
            CUSTOM_CHANNEL -> fetchCustomUpdate(Config.customChannelUrl)
            else -> throw IllegalArgumentException()
        }
        if (info.versionCode < Info.env.versionCode &&
            Config.updateChannel == DEFAULT_CHANNEL &&
            !BuildConfig.DEBUG
        ) {
            Config.updateChannel = BETA_CHANNEL
            info = fetchBetaUpdate()
        }
        info
    }

    suspend fun fetchUpdate(version: Int) = safe {
        findRelease { it.versionCode == version }.asInfo()
    }

    private suspend inline fun findRelease(predicate: (Release) -> Boolean): Release? {
        var page = 1
        while (true) {
            val response = api.fetchReleases(page = page)
            val releases = response.body() ?: throw HttpException(response)
            // Remove all non-Magisk releases
            releases.removeAll { it.tag != "build" && it.tag != "canary_build" }
            // Sort descending by createdTime
            releases.sortByDescending { it.createdTime }
            releases.find(predicate)?.let { return it }
            if (response.headers()["link"]?.contains("rel=\"next\"", ignoreCase = true) == true) {
                page += 1
            } else {
                return null
            }
        }
    }

    private inline fun Release?.asInfo(
        selector: (ReleaseAssets) -> Boolean = { it.name == "app-release.apk" || it.name == "app-debug.apk" }
    ): UpdateInfo {
        if (this == null) return UpdateInfo()
        return when (tag) {
            "build" -> asPublicInfo(selector)
            "canary_build" -> asCanaryInfo(selector)
            else -> UpdateInfo()
        }
    }

    private inline fun Release.asPublicInfo(selector: (ReleaseAssets) -> Boolean): UpdateInfo {
        val releaseApk = assets.find { it.name == "app-release.apk" }?.url
        val debugApk = assets.find { it.name == "app-debug.apk" }?.url
        return UpdateInfo(
            version = tag,
            versionCode = versionCode,
            link = releaseApk,
            debugLink = debugApk,
            note = "## $name\n\n$body"
        )
    }

    private inline fun Release.asCanaryInfo(selector: (ReleaseAssets) -> Boolean): UpdateInfo {
        val releaseApk = assets.find { it.name == "app-release.apk" }?.url
        val debugApk = assets.find { it.name == "app-debug.apk" }?.url
        return UpdateInfo(
            version = tag,
            versionCode = versionCode,
            link = releaseApk,
            debugLink = debugApk,
            note = "## $name\n\n$body"
        )
    }

    private suspend fun fetchStableUpdate() = findRelease { it.tag == "build" }.asInfo()
    private suspend fun fetchBetaUpdate() = findRelease { it.tag == "canary_build" }.asInfo()
    private suspend fun fetchDebugUpdate() = findRelease { it.tag == "build" }.asInfo { it.name == "app-debug.apk" }
    private suspend fun fetchCustomUpdate(url: String): UpdateInfo {
        val info = raw.fetchUpdateJson(url).magisk
        return info.let { it.copy(note = raw.fetchString(it.note)) }
    }

    private inline fun <T> safe(factory: () -> T): T? {
        return try {
            if (Info.isConnected.value == true)
                factory()
            else
                null
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    private inline fun <T> wrap(factory: () -> T): T {
        return try {
            factory()
        } catch (e: HttpException) {
            throw IOException(e)
        }
    }

    suspend fun fetchFile(url: String) = wrap { raw.fetchFile(url) }
    suspend fun fetchString(url: String) = wrap { raw.fetchString(url) }
    suspend fun fetchModuleJson(url: String) = wrap { raw.fetchModuleJson(url) }
}