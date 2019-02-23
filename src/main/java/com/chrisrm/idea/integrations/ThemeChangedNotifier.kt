package com.chrisrm.idea.integrations

import com.chrisrm.idea.themes.MTThemeFacade
import com.chrisrm.idea.themes.models.MTThemeable
import com.chrisrm.idea.utils.MTAccents
import com.chrisrm.idea.utils.MTUiUtils
import com.intellij.compiler.server.CustomBuilderMessageHandler
import com.intellij.ui.ColorUtil
import com.intellij.util.messages.MessageBus
import org.json.JSONObject
import java.awt.Color

object MaterialThemeChangedNotifier {
  private const val THEME_CHANGED = "Theme Changed"
  private const val ACCENT_CHANGED = "Accent Changed"

  fun themeChanged(messageBus: MessageBus, newTheme: MTThemeFacade) {
    messageBus.syncPublisher(CustomBuilderMessageHandler.TOPIC)
        .messageReceived(MTUiUtils.PLUGIN_ID, THEME_CHANGED,
            toJson(createThemeDelta(newTheme.theme)))
  }

  fun accentChanged(messageBus: MessageBus, accentColor: Color) {
    messageBus.syncPublisher(CustomBuilderMessageHandler.TOPIC)
        .messageReceived(MTUiUtils.PLUGIN_ID, ACCENT_CHANGED,
         toJson(createAccentDelta(accentColor)))
  }

  private fun toJson(accentChangedInfo: AccentChangedInformation): String {
    val accentDeltaJson = JSONObject()
    accentDeltaJson.put("accentColor", accentChangedInfo.accentColor)
    return accentDeltaJson.toString()
  }

  private fun toJson(themeChangedDelta: ThemeChangedInformation): String {
    val themeChangedDeltaJson = JSONObject()
    themeChangedDeltaJson.put("accentColor", themeChangedDelta.accentColor)
    themeChangedDeltaJson.put("isDark", themeChangedDelta.isDark)
    themeChangedDeltaJson.put("contrastColor", themeChangedDelta.contrastColor)
    themeChangedDeltaJson.put("foregroundColor", themeChangedDelta.foregroundColor)
    return themeChangedDeltaJson.toString()
  }

  private fun createAccentDelta(accentColor: Color): AccentChangedInformation =
      AccentChangedInformation(ColorUtil.toHex(accentColor))

  private fun createThemeDelta(themeable: MTThemeable): ThemeChangedInformation =
      ThemeChangedInformation(
          themeable.isDark,
          ColorUtil.toHex(themeable.accentColor ?: MTAccents.OCEANIC.color),
          ColorUtil.toHex(themeable.contrastColor),
          ColorUtil.toHex(themeable.textColor))
}