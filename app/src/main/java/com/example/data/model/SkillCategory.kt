package com.example.data.model

enum class SkillCategory(val displayName: String, val icon: String, val hindiName: String) {
    ALL("All", "🔥", "सभी"),
    EDUCATION("Education", "📚", "शिक्षा"),
    FITNESS("Fitness", "💪", "फिटनेस"),
    SPORTS("Sports", "⚽", "खेल"),
    GAMING("Gaming", "🎮", "गेमिंग"),
    ART("Art", "🎨", "कला"),
    MUSIC("Music", "🎵", "संगीत"),
    DANCE("Dance", "💃", "नृत्य"),
    COOKING("Cooking", "🍳", "पाक कला"),
    SKILLS_WORK("Skills & Work", "🛠️", "हुनर व काम"),
    TECH("Technology", "💻", "तकनीक"),
    MOTIVATION("Motivation", "🌟", "प्रेरणा"),
    LIFESTYLE("Lifestyle", "🌿", "जीवनशैली"),
    BEAUTY_TALENT("Beauty with Talent", "✨", "हुनर संग निखार"),
    COMEDY("Comedy", "🎭", "कॉमेडी")
}
