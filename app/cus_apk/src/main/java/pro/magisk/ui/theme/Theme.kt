package pro.magisk.ui.theme

import pro.magisk.R
import pro.magisk.core.Config

enum class Theme(
    val themeName: String,
    val themeRes: Int
) {

    Rayquaza(
        themeName = "Rayquaza:M",
        themeRes = R.style.ThemeFoundationMD2_Rayquaza
    ),
    Piplup(
        themeName = "Piplup:D",
        themeRes = R.style.ThemeFoundationMD2_Piplup
    ),
    PiplupAmoled(
        themeName = "AMOLED",
        themeRes = R.style.ThemeFoundationMD2_Amoled
    ),
    Zapdos(
        themeName = "Zapdos",
        themeRes = R.style.ThemeFoundationMD2_Zapdos
    ),
    Charmeleon(
        themeName = "Charmeleon",
        themeRes = R.style.ThemeFoundationMD2_Charmeleon
    ),
    Mew(
        themeName = "Mew",
        themeRes = R.style.ThemeFoundationMD2_Mew
    ),
    Salamence(
        themeName = "Salamence",
        themeRes = R.style.ThemeFoundationMD2_Salamence
    ),
    Fraxure(
        themeName = "Fraxure:L",
        themeRes = R.style.ThemeFoundationMD2_Fraxure
    ),
    Monochrome(
        themeName = "Monochrome",
        themeRes = R.style.ThemeFoundationMD2_Monochrome
    ),
    Dynamic(
        themeName = "Dynamic",
        themeRes = R.style.ThemeFoundationMD2_Dynamic
    );

    val isSelected get() = Config.themeOrdinal == ordinal

    companion object {
        val selected get() = values().getOrNull(Config.themeOrdinal) ?: Piplup
    }

}
