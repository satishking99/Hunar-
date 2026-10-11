package com.example.ui.theme

enum class AppThemeMode(val title: String, val description: String, val icon: String) {
    LIGHT(
        title = "White Theme",
        description = "Clean #FFFFFF background, crisp black text & icons, Hunar Orange accents.",
        icon = "☀️"
    ),
    DARK(
        title = "Black Theme",
        description = "Pure #000000 black background, white text & icons, Hunar Orange accents.",
        icon = "🌙"
    ),
    SYSTEM(
        title = "Automatic (System)",
        description = "Automatically follows your device's system dark/light theme setting.",
        icon = "⚙️"
    )
}
