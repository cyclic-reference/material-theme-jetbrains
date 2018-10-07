/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Chris Magnussen and Elior Boukhobza
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */

package com.chrisrm.idea;

import com.chrisrm.idea.legacy.LegacySupportUtility;
import com.chrisrm.idea.themes.MTCustomTheme;
import com.chrisrm.idea.themes.MTLightCustomTheme;
import com.chrisrm.idea.themes.MTThemeable;
import com.chrisrm.idea.utils.MTUiUtils;
import com.chrisrm.idea.utils.PropertiesParser;
import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.laf.IntelliJLookAndFeelInfo;
import com.intellij.ide.ui.laf.LafManagerImpl;
import com.intellij.ide.ui.laf.darcula.DarculaLookAndFeelInfo;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.IconUtil;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.io.Serializable;

public abstract class MTAbstractTheme implements Serializable, MTThemeable {
  public static final ColorUIResource DEFAULT_BORDER_COLOR = new ColorUIResource(0x80cbc4);
  public static final ColorUIResource DEFAULT_CONTRAST = new ColorUIResource(0x1E272C);
  public static final ColorUIResource DEFAULT_FOREGROUND = new ColorUIResource(0xB0BEC5);
  public static final ColorUIResource DEFAULT_BACKGROUND = new ColorUIResource(0x263238);
  public static final ColorUIResource DEFAULT_PRIMARY = new ColorUIResource(0x263238);
  public static final int HC_FG_TONES = 4;
  public static final int HC_BG_TONES = 2;

  private final String id;
  private final String editorColorsScheme;
  private final boolean dark;
  private String name;
  private String icon;

  public MTAbstractTheme(final boolean dark) {
    this(getDefaultID(dark), getDefaultColorScheme(dark), dark);
  }

  protected MTAbstractTheme(@NotNull final String id,
                            final String editorColorsScheme,
                            final boolean dark) {
    this.id = id;
    this.editorColorsScheme = editorColorsScheme;
    this.dark = dark;
    name = id;
  }

  protected MTAbstractTheme(@NotNull final String id,
                            final String editorColorsScheme,
                            final boolean dark,
                            final String name,
                            final String icon) {
    this(id, editorColorsScheme, dark, name);
    this.icon = icon;
  }

  protected MTAbstractTheme(@NotNull final String id, final String editorColorsScheme, final boolean dark, final String name) {
    this(id, editorColorsScheme, dark);
    this.name = name;
  }

  @NotNull
  private static String getDefaultID(final boolean dark) {
    return dark ? "mt.custom" : "mt.light_custom";
  }

  @NotNull
  private static String getDefaultColorScheme(final boolean dark) {
    return dark ? "Darcula" : "Default";
  }

  /**
   * Get the theme id
   */
  @Override
  public String toString() {
    return getId();
  }

  /**
   * Activate the theme by overriding UIManager with the theme resources and by setting the relevant Look and feel
   */
  @Override
  public final void activate() {
    try {
      if (isDark()) {
        LegacySupportUtility.INSTANCE.invokeVoidMethodSafely(
            LafManagerImpl.class,
            "getTestInstance",
            ()-> LafManagerImpl.getTestInstance().setCurrentLookAndFeel(new DarculaLookAndFeelInfo()),
            ()-> {
              LafManager.getInstance().setCurrentLookAndFeel(new DarculaLookAndFeelInfo());
              UIManager.setLookAndFeel(new MTDarkLaf(this));
            }
        );

      } else {
        LegacySupportUtility.INSTANCE.invokeVoidMethodSafely(
            LafManagerImpl.class,
            "getTestInstance",
            ()-> LafManagerImpl.getTestInstance().setCurrentLookAndFeel(new IntelliJLookAndFeelInfo()),
            ()-> {
              LafManager.getInstance().setCurrentLookAndFeel(new IntelliJLookAndFeelInfo());
              UIManager.setLookAndFeel(new MTLightLaf(this));
            }
        );

      }
      JBColor.setDark(isDark());
      IconLoader.setUseDarkIcons(isDark());
      buildResources(getBackgroundResources(), contrastifyBackground(getBackgroundColorString()));
      buildResources(getForegroundResources(), getForegroundColorString());
      buildResources(getTextResources(), contrastifyForeground(getTextColorString()));
      buildResources(getSelectionBackgroundResources(), getSelectionBackgroundColorString());
      buildResources(getSelectionForegroundResources(), getSelectionForegroundColorString());
      buildResources(getButtonColorResource(), getButtonColorString());
      buildResources(getSecondaryBackgroundResources(), getSecondaryBackgroundColorString());
      buildResources(getDisabledResources(), getDisabledColorString());
      buildResources(getContrastResources(), contrastifyBackground(getContrastColorString()));
      buildResources(getTableSelectedResources(), getTableSelectedColorString());
      buildResources(getSecondBorderResources(), getSecondBorderColorString());
      buildResources(getHighlightResources(), getHighlightColorString());

      buildResources(getTreeSelectionResources(), getTreeSelectionColorString());
      buildResources(getNotificationsResources(), getNotificationsColorString());
      buildNotificationsColors();

      // Apply theme accent color if said so
      if (MTConfig.getInstance().isOverrideAccentColor()) {
        MTConfig.getInstance().setAccentColor(getAccentColor());
        MTThemeManager.getInstance().applyAccents();
      }

      if (isDark()) {
        UIManager.setLookAndFeel(new MTDarkLaf(this));
      } else {
        UIManager.setLookAndFeel(new MTLightLaf(this));
      }
    } catch (final UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
  }

  //region Getters/Setters

  /**
   * The theme name
   */
  @NotNull
  @Override
  public String getName() {
    return name;
  }

  /**
   * Set the theme name
   *
   * @param name
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Get the editor color scheme
   */
  @Override
  public String getEditorColorsScheme() {
    return editorColorsScheme;
  }

  /**
   * The theme id
   */
  @Override
  @NotNull
  public String getId() {
    return id;
  }

  /**
   * Whether the theme is a dark one
   */
  @Override
  public boolean isDark() {
    return dark;
  }

  /**
   * Get Theme ID
   */
  @NotNull
  @Override
  public String getThemeId() {
    return getId();
  }

  @NotNull
  @Override
  public final Icon getIcon() {
    return icon != null ? IconLoader.getIcon(icon) : IconUtil.getEmptyIcon(true);
  }

  public final void setIcon(final String icon) {
    this.icon = icon;
  }

  /**
   * Whether the theme is a custom or external one
   */
  @Override
  public boolean isCustom() {
    return false;
  }
  //endregion

  //region Theme methods

  /**
   * Get the default selection background
   */
  @NotNull
  @Override
  public String getSelectionBackground() {
    return dark ? MTCustomTheme.SELECTION_BACKGROUND : MTLightCustomTheme.SELECTION_BACKGROUND;
  }

  /**
   * Get disabled color
   */
  @NotNull
  @Override
  public String getDisabled() {
    return dark ? MTCustomTheme.DISABLED : MTLightCustomTheme.DISABLED;
  }

  /**
   * Get background color custom property
   */
  @Override
  @NotNull
  public Color getBackgroundColor() {
    final Color defaultValue = MTUiUtils.getColor(
        UIManager.getColor("material.background"),
        ObjectUtils.notNull(UIManager.getColor("darcula.background"), new ColorUIResource(0x3c3f41)),
        ObjectUtils.notNull(UIManager.getColor("intellijlaf.background"), new ColorUIResource(0xe8e8e8)));
    return ObjectUtils.notNull(defaultValue, DEFAULT_BACKGROUND);
  }

  /**
   * Get contrast color custom property
   */
  @Override
  @NotNull
  public Color getContrastColor() {
    final Color defaultValue = MTUiUtils.getColor(
        UIManager.getColor("material.contrast"),
        ObjectUtils.notNull(UIManager.getColor("darcula.contrastColor"), new ColorUIResource(0x262626)),
        ObjectUtils.notNull(UIManager.getColor("intellijlaf.contrastColor"), new ColorUIResource(0xeeeeee)));
    return ObjectUtils.notNull(defaultValue, DEFAULT_CONTRAST);
  }

  /**
   * Get foreground color custom property
   */
  @Override
  @NotNull
  public Color getForegroundColor() {
    final Color defaultValue = MTUiUtils.getColor(
        UIManager.getColor("material.foreground"),
        ObjectUtils.notNull(UIManager.getColor("darcula.foreground"), new ColorUIResource(0x3c3f41)),
        ObjectUtils.notNull(UIManager.getColor("intellijlaf.foreground"), new ColorUIResource(0xe8e8e8)));
    return ObjectUtils.notNull(defaultValue, DEFAULT_FOREGROUND);
  }

  /**
   * Get background color custom property
   */
  @Override
  @NotNull
  public Color getPrimaryColor() {
    //    final Color defaultValue = MTUiUtils.getColor(
    //        UIManager.getColor("material.primaryColor"),
    //        ObjectUtils.notNull(UIManager.getColor("darcula.primary"), new ColorUIResource(0x3c3f41)),
    //        ObjectUtils.notNull(UIManager.getColor("intellijlaf.primary"), new ColorUIResource(0xe8e8e8)));
    //    return ObjectUtils.notNull(defaultValue, DEFAULT_PRIMARY);
    return ColorUtil.fromHex(getTextColorString());
  }

  private String contrastifyForeground(final String colorString) {
    final boolean isHighContrast = MTConfig.getInstance().getIsHighContrast();
    if (!isHighContrast) {
      return colorString;
    }

    if (isDark()) {
      return ColorUtil.toHex(ColorUtil.brighter(ColorUtil.fromHex(colorString), HC_FG_TONES));
    } else {
      return ColorUtil.toHex(ColorUtil.darker(ColorUtil.fromHex(colorString), HC_FG_TONES));
    }
  }

  private Color contrastifyForeground(final Color color) {
    final boolean isHighContrast = MTConfig.getInstance().getIsHighContrast();
    if (!isHighContrast) {
      return color;
    }

    if (isDark()) {
      return ColorUtil.brighter(color, HC_FG_TONES);
    } else {
      return ColorUtil.darker(color, HC_FG_TONES);
    }
  }

  private String contrastifyBackground(final String colorString) {
    final boolean isHighContrast = MTConfig.getInstance().getIsHighContrast();
    if (!isHighContrast) {
      return colorString;
    }

    if (isDark()) {
      return ColorUtil.toHex(ColorUtil.darker(ColorUtil.fromHex(colorString), HC_BG_TONES));
    } else {
      return ColorUtil.toHex(ColorUtil.brighter(ColorUtil.fromHex(colorString), HC_BG_TONES));
    }
  }

  private Color contrastifyBackground(final Color color) {
    final boolean isHighContrast = MTConfig.getInstance().getIsHighContrast();
    if (!isHighContrast) {
      return color;
    }

    if (isDark()) {
      return ColorUtil.darker(color, HC_BG_TONES);
    } else {
      return ColorUtil.brighter(color, HC_BG_TONES);
    }
  }

  //endregion

  //region MTThemeable methods

  /**
   * Get resources using the background color
   */
  protected String[] getBackgroundResources() {
    return new String[]{
        //        "Menu.background",
        "window",
        "activeCaption",
        "control",
        "PopupMenu.translucentBackground",
        "EditorPane.inactiveBackground",
        "Table.background",
        "Table.gridColor",
        "Desktop.background",
        "PopupMenu.background",
        "Separator.background",
        "MenuBar.background",
        "MenuBar.disabledBackground",
        "MenuBar.shadow",
        "TabbedPane.background",
        "TabbedPane.borderColor",
        "TextField.background",
        "PasswordField.background",
        "FormattedTextField.background",
        "TextArea.background",
        "CheckBox.background",
        "OptionPane.background",
        "ColorChooser.background",
        "Slider.background",
        "TabbedPane.mt.tab.background",
        "TextPane.background",
        "RadioButton.background",
        "CheckBox.darcula.backgroundColor1",
        "CheckBox.darcula.backgroundColor2",
        "CheckBox.darcula.checkSignColor",
        "CheckBox.darcula.shadowColor",
        "CheckBox.darcula.shadowColorDisabled",
        "CheckBox.darcula.focusedArmed.backgroundColor1",
        "CheckBox.darcula.focusedArmed.backgroundColor2",
        "CheckBox.darcula.focused.backgroundColor1",
        "CheckBox.darcula.focused.backgroundColor2",
        "ComboBox.background",
        "ComboBox.disabledBackground",
        "ComboBox.arrowFillColor",
        "ComboBox.darcula.arrowButtonBackground",
        "RadioButton.darcula.selectionDisabledColor",
        "StatusBar.topColor",
        "StatusBar.top2Color",
        "StatusBar.bottomColor",
        "Button.background",
        "Button.darcula.color1",
        "Button.darcula.color2",
        "Button.darcula.disabledText.shadow",
        "ToolTip.background",
        "Spinner.background",
        "SplitPane.highlight",
        "Tree.background",
        "Popup.Header.activeBackground",
        "Popup.Border.inactiveColor",
        "Popup.inactiveBorderColor",
        "Popup.preferences.background",
        "Popup.preferences.borderColor",
        "HelpTooltip.backgroundColor",
        //        "Panel.background",
        "SidePanel.background",
        "DialogWrapper.southPanelDivider",
        "Dialog.titleColor",
        "SearchEverywhere.background",
        "CheckBoxMenuItem.background",
        "ToolWindow.header.background",
        "ToolWindow.header.closeButton.background",
        "material.tab.backgroundColor",
        "TextField.borderColor",
        "TextField.hoverBorderColor",
        "SearchEverywhere.Dialog.background",
        "SearchEverywhere.SearchField.Border.color",
        "TextField.focusedBorderColor",
        "ComboBox.darcula.nonEditableBackground",
        "darcula.background",
        "intellijlaf.background",
        "material.background"
    };
  }

  /**
   * Get resources using the foreground color
   */
  protected String[] getForegroundResources() {
    return new String[]{
        "OptionPane.messageForeground",
        "Menu.foreground",
        "MenuItem.foreground",
        "Label.foreground",
        "Label.selectedDisabledForeground",
        "CheckBox.foreground",
        "ComboBox.foreground",
        "RadioButton.foreground",
        "ColorChooser.foreground",
        "MenuBar.foreground",
        "RadioButtonMenuItem.foreground",
        "CheckBoxMenuItem.foreground",
        "MenuItem.foreground",
        //        "OptionPane.foreground",
        "PopupMenu.foreground",
        "Spinner.foreground",
        "TabbedPane.foreground",
        "TextField.foreground",
        "FormattedTextField.foreground",
        "PasswordField.foreground",
        "TextArea.foreground",
        "TextPane.foreground",
        "EditorPane.foreground",
        "ToolBar.foreground",
        "ToolTip.foreground",
        "List.foreground",
        "SearchEverywhere.foreground",
        "Table.foreground",
        "TableHeader.foreground",
        "ToggleButton.foreground",
        "Table.sortIconColor",
        "material.branchColor",
        "material.foreground",
        "CheckBox.darcula.borderColor1",
        "RadioButton.darcula.borderColor1",
        "HelpTooltip.textColor",
        "darcula.foreground",
        "intellijlaf.foreground",
        "TitledBorder.titleColor"
    };
  }

  /**
   * Get resources using the label color
   */
  protected String[] getTextResources() {
    return new String[]{
        "Menu.acceleratorForeground",
        "text",
        "textText",
        "textInactiveText",
        "infoText",
        "controlText",
        "MenuItem.acceleratorForeground",
        "TextField.separatorColorDisabled",
        "material.tagColor",
        "material.primaryColor",
        "SearchEverywhere.shortcutForeground",
        "Button.foreground",
        "Button.mt.foreground",
        "HelpTooltip.shortcutTextColor",
        "Tree.foreground"
    };
  }

  /**
   * Get resources using the selection background color
   */
  protected String[] getSelectionBackgroundResources() {
    return new String[]{
        "Menu.selectionBackground",
        "MenuItem.selectionBackground",
        "RadioButtonMenuItem.selectionBackground",
        "CheckBoxMenuItem.selectionBackground",
        "EditorPane.selectionBackground",
        "Autocomplete.selectionBackground",
        "List.selectionBackground",
        "TabbedPane.selected",
    };
  }

  /**
   * Get resources using the selection foreground color
   */
  protected String[] getSelectionForegroundResources() {
    return new String[]{
        "Menu.selectionForeground",
        "Menu.acceleratorSelectionForeground",
        "MenuItem.selectionForeground",
        "MenuItem.acceleratorSelectionForeground",
        "Table.selectionForeground",
        "TextField.selectionForeground",
        "PasswordField.selectionForeground",
        "Button.mt.selectedForeground",
        "TextArea.selectionForeground",
        "List.selectionForeground",
        "ComboBox.selectionForeground",
        "FormattedTextField.selectionForeground",
        "CheckBoxMenuItem.selectionForeground",
        "TextPane.selectionForeground",
        "EditorPane.selectionForeground",
        "Tree.selectionForeground",
        "TableHeader.focusCellForeground",
        "TabbedPane.selectedForeground",
        //        "Label.selectedForeground",
        "Button.darcula.selectedButtonForeground"
    };
  }

  /**
   * Get resources using the button color
   */
  protected String[] getButtonColorResource() {
    return new String[]{
        "Button.mt.color1",
        "Button.mt.color2",
        "Button.mt.background",
        "Button.darcula.startColor",
        "Button.darcula.endColor",
        "Button.darcula.defaultStartColor",
        "Button.darcula.defaultEndColor",
        "Button.darcula.disabledBorderColor",
        "Button.darcula.borderColor",
        "Button.darcula.defaultBorderColor",
        "Button.darcula.outlineColor",
        "Outline.color",
        "Button.darcula.defaultOutlineColor",
        "material.mergeCommits"
    };
  }

  /**
   * Get resources using the secondary background color
   */
  protected String[] getSecondaryBackgroundResources() {
    return new String[]{
        "inactiveCaption",
        "ToolWindow.header.active.background",
        "ToolWindow.header.border.background",
        "MemoryIndicator.unusedColor",
        "List.background"
    };
  }

  /**
   * Get resources using the disabled color
   */
  protected String[] getDisabledResources() {
    return new String[]{
        "MenuItem.disabledForeground",
        "ComboBox.disabledForeground",
        "CheckBox.darcula.disabledBorderColor1",
        "CheckBox.darcula.disabledBorderColor2",
        "TextField.inactiveForeground",
        "FormattedTextField.inactiveForeground",
        "PasswordField.inactiveForeground",
        "TextArea.inactiveForeground",
        "TextPane.inactiveForeground",
        "EditorPane.inactiveForeground",
        "Button.disabledText",
        "TabbedPane.selectedDisabledColor",
        "Menu.disabledForeground",
        "Label.disabledForeground",
        "RadioButtonMenuItem.disabledForeground",
        "Outline.disabledColor",
        "CheckBoxMenuItem.disabledForeground",
        "CheckBox.darcula.checkSignColorDisabled"
    };
  }

  /**
   * Get resources using the contrast color
   */
  protected String[] getContrastResources() {
    return new String[]{
        "Table.stripedBackground",
        "ToolWindow.header.tab.selected.background",
        "ToolWindow.header.tab.selected.active.background",
        "Table.focusCellBackground",
        "ScrollBar.thumb",
        "EditorPane.background",
        "ToolBar.background",
        "Popup.Header.inactiveBackground",
        "Popup.Toolbar.background",
        "Popup.Border.color",
        "Popup.Toolbar.Border.color",
        "SearchEverywhere.SearchField.background",
        "material.contrast"
    };
  }

  /**
   * Get resources using the table/button selection color
   */
  protected String[] getTableSelectedResources() {
    return new String[]{
        "Table.selectionBackground",
        "TextField.selectionBackground",
        "PasswordField.selectionBackground",
        "FormattedTextField.selectionBackground",
        "ComboBox.selectionBackground",
        "TextArea.selectionBackground",
        "TextPane.selectionBackground",
        "Button.darcula.selection.color1",
        "Button.darcula.selection.color2",
        "Button.darcula.focusedBorderColor",
        "Button.darcula.defaultFocusedBorderColor",
        "Button.mt.selection.color2",
        "Button.mt.selection.color1"
    };
  }

  /**
   * Get resources using the second border color
   */
  protected String[] getSecondBorderResources() {
    return new String[]{
        "Button.darcula.shadowColor",
        "Separator.foreground",
        "TabbedPane.highlight",
        "TabbedPane.darkShadow",
        "OnePixelDivider.background",
        "Button.darcula.disabledOutlineColor",
        "HelpTooltip.borderColor",
        "SearchEverywhere.List.Separator.Color",
        "TabbedPane.shadow"
    };
  }

  /**
   * Get resources using the highlight color
   */
  protected String[] getHighlightResources() {
    return new String[]{
        "Focus.color",
        "TextField.separatorColor",
        "ProgressBar.halfColor",
        "Autocomplete.selectionUnfocus",
        "CheckBox.darcula.inactiveFillColor",
        "TabbedPane.selectHighlight",
        "TabbedPane.selectedColor",
        "TabbedPane.hoverColor",
        "TabbedPane.contentAreaColor",
        "SearchEverywhere.Tab.selected.background",
        "TableHeader.borderColor",
        "Outline.focusedColor",
        "MemoryIndicator.usedColor"
    };
  }

  /**
   * Get resources using the tree selected row color
   */
  protected String[] getTreeSelectionResources() {
    return new String[]{
        "Tree.selectionBackground"
    };
  }

  /**
   * Get notifications colors resources
   */
  protected String[] getNotificationsResources() {
    return new String[]{
        "Notifications.background",
        "Notifications.borderColor"
    };
  }
  //endregion

  /**
   * Iterate over theme resources and fill up the UIManager
   *
   * @param resources
   * @param color
   */
  private void buildResources(final String[] resources, final String color) {
    for (final String resource : resources) {
      UIManager.getDefaults().put(resource, PropertiesParser.parseColor(color));
    }
  }

  private void buildNotificationsColors() {
    UIManager.put("Notifications.errorBackground", new JBColor(new ColorUIResource(0xef5350), new ColorUIResource(0xb71c1c)));
    UIManager.put("Notifications.warnBackground", new JBColor(new ColorUIResource(0xFFD54F), new ColorUIResource(0xFFF59D)));
    UIManager.put("Notifications.infoBackground", new JBColor(new ColorUIResource(0x66BB6A), new ColorUIResource(0x1B5E20)));
  }
}
