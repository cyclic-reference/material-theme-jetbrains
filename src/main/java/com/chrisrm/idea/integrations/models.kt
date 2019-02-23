package com.chrisrm.idea.integrations

data class ThemeChangedInformation(val isDark: Boolean,
                                   val accentColor: String,
                                   val contrastColor: String,
                                   val foregroundColor: String)

data class AccentChangedInformation(val accentColor: String)