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

import com.chrisrm.idea.themes.MTCustomTheme;
import com.chrisrm.idea.themes.MTLightCustomTheme;
import com.intellij.ide.ui.laf.LafManagerImpl;
import com.intellij.ui.JBColor;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;


public class MTAbstractThemeTest extends MTFixtureTestCase {

  private final MTAbstractTheme darkAbstractTheme = new MTAbstractTheme(true) {
    @Override
    public String getBackgroundColorString() {
      return "000000";
    }

    @Override
    public String getForegroundColorString() {
      return "000000";
    }

    @Override
    public String getTextColorString() {
      return "000000";
    }

    @Override
    public String getSelectionBackgroundColorString() {
      return "000000";
    }

    @Override
    public String getSelectionForegroundColorString() {
      return "000000";
    }

    @Override
    public String getButtonColorString() {
      return "000000";
    }

    @Override
    public String getSecondaryBackgroundColorString() {
      return "000000";
    }

    @Override
    public String getDisabledColorString() {
      return "000000";
    }

    @Override
    public String getContrastColorString() {
      return "000000";
    }

    @Override
    public String getTableSelectedColorString() {
      return "000000";
    }

    @Override
    public String getSecondBorderColorString() {
      return "000000";
    }

    @Override
    public String getHighlightColorString() {
      return "000000";
    }

    @Override
    public String getTreeSelectionColorString() {
      return "000000";
    }

    @Override
    public String getNotificationsColorString() {
      return "000000";
    }

    @Override
    public String getAccentColor() {
      return "000000";
    }

    @Override
    public String getExcludedColor() {
      return "000000";
    }

    @Override
    public int getOrder() {
      return 0;
    }
  };
  private final MTAbstractTheme lightAbstractTheme = new MTAbstractTheme(false) {
    @Override
    public String getBackgroundColorString() {
      return "FFFFFF";
    }

    @Override
    public String getForegroundColorString() {
      return "FFFFFF";
    }

    @Override
    public String getTextColorString() {
      return "FFFFFF";
    }

    @Override
    public String getSelectionBackgroundColorString() {
      return "FFFFFF";
    }

    @Override
    public String getSelectionForegroundColorString() {
      return "FFFFFF";
    }

    @Override
    public String getButtonColorString() {
      return "FFFFFF";
    }

    @Override
    public String getSecondaryBackgroundColorString() {
      return "FFFFFF";
    }

    @Override
    public String getDisabledColorString() {
      return "FFFFFF";
    }

    @Override
    public String getContrastColorString() {
      return "FFFFFF";
    }

    @Override
    public String getTableSelectedColorString() {
      return "FFFFFF";
    }

    @Override
    public String getSecondBorderColorString() {
      return "FFFFFF";
    }

    @Override
    public String getHighlightColorString() {
      return "FFFFFF";
    }

    @Override
    public String getTreeSelectionColorString() {
      return "FFFFFF";
    }

    @Override
    public String getNotificationsColorString() {
      return "FFFFFF";
    }

    @Override
    public boolean isCustom() {
      return true;
    }

    @Override
    public String getAccentColor() {
      return "FFFFFF";
    }

    @Override
    public String getExcludedColor() {
      return "FFFFFF";
    }

    @Override
    public int getOrder() {
      return 0;
    }
  };

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @Override
  public void tearDown() throws Exception {
    MTConfig.getInstance().resetSettings();
    MTThemeManager.getInstance().activate();
    super.tearDown();
  }

  @Test
  public void testGetSelectionBackground() {
    // Verify the results
    assertEquals("should have custom theme selection background", MTCustomTheme.SELECTION_BACKGROUND,
        darkAbstractTheme.getSelectionBackground());
    assertEquals("should have light custom theme selection background", MTLightCustomTheme.SELECTION_BACKGROUND,
        lightAbstractTheme.getSelectionBackground());
  }

  @Test
  public void testGetDisabled() {
    // Verify the results
    assertEquals("should have custom theme disabled color", MTCustomTheme.DISABLED, darkAbstractTheme.getDisabled());
    assertEquals("should have light custom theme disabled color", MTLightCustomTheme.DISABLED, lightAbstractTheme.getDisabled());
  }

  @Test
  public void testActivateDarkLaf() {
    darkAbstractTheme.activate();
    assertTrue(Objects.requireNonNull(LafManagerImpl.getTestInstance().getCurrentLookAndFeel()).getClassName().contains("DarculaLaf"));
    assertFalse(JBColor.isBright());
    assertTrue("Should be wearing the Dark Laf", UIManager.getLookAndFeel().getDescription().contains("Dark Material"));
  }

  @Test
  public void testActivateLightLaf() {
    lightAbstractTheme.activate();
    assertTrue(Objects.requireNonNull(LafManagerImpl.getTestInstance().getCurrentLookAndFeel()).getClassName().contains("IntelliJLaf"));
    assertTrue(JBColor.isBright());
    assertTrue("Should be wearing the Light Laf", UIManager.getLookAndFeel().getDescription().contains("Light Material"));
  }

  @Test
  public void testActivate() {
    final Object oldColor = UIManager.get("material.background");
    darkAbstractTheme.activate();
    final Color newColor = UIManager.getColor("material.background");
    assertNotSame("It should have activated the theme resources", newColor, oldColor);
  }

  @Test
  public void testIsCustom() {
    assertFalse("dark theme should not be custom", darkAbstractTheme.isCustom());
    assertTrue("light theme should be custom", lightAbstractTheme.isCustom());
  }

  @Test
  public void testGetPrimaryColor() {
    assertEquals("should return dark material primary color", UIManager.getColor("material.primaryColor"),
        darkAbstractTheme.getPrimaryColor());
    assertEquals("should return light material primary color", UIManager.getColor("material.primaryColor"),
        lightAbstractTheme.getPrimaryColor());
  }

  @Test
  public void testDarculaGetPrimaryColor() {
    // Setup
    MTConfig.getInstance().setIsMaterialTheme(false);
    darkAbstractTheme.activate();

    // Run the test
    final Color result = darkAbstractTheme.getPrimaryColor();

    // Verify the results
    assertEquals("should return darcula primary color", UIManager.getColor("darcula.primary"), result);
  }

  @Test
  public void testLightGetPrimaryColor() {
    // Setup
    MTConfig.getInstance().setIsMaterialTheme(false);
    lightAbstractTheme.activate();

    // Run the test
    final Color result = lightAbstractTheme.getPrimaryColor();

    // Verify the results
    assertEquals("should return light primary color", UIManager.getColor("intellijlaf.primary"), result);
  }

  @Test
  public void testGetBackgroundColor() {
    // Verify the results
    assertEquals("should return dark material background color", UIManager.getColor("material.background"),
        darkAbstractTheme.getBackgroundColor());
    assertEquals("should return light material background color", UIManager.getColor("material.background"),
        lightAbstractTheme.getBackgroundColor());
  }

  @Test
  public void testDarculaGetBackgroundColor() {
    // setup
    MTConfig.getInstance().setIsMaterialTheme(false);
    darkAbstractTheme.activate();

    // Run the test
    final Color result = darkAbstractTheme.getBackgroundColor();

    // Verify the results
    assertEquals("should return darcula background color", UIManager.getColor("darcula.background"), result);
  }

  @Test
  public void testLightGetBackgroundColor() {
    // setup
    MTConfig.getInstance().setIsMaterialTheme(false);
    lightAbstractTheme.activate();

    // Run the test
    final Color result = lightAbstractTheme.getBackgroundColor();

    // Verify the results
    assertEquals("should return darcula background color", UIManager.getColor("intellijlaf.background"), result);
  }

  @Test
  public void testGetForegroundColor() {
    assertEquals("should return dark material foreground", UIManager.getColor("material.foreground"),
        darkAbstractTheme.getForegroundColor());
    assertEquals("should return light material foreground", UIManager.getColor("material.foreground"),
        lightAbstractTheme.getForegroundColor());
  }

  @Test
  public void testDarculaGetForegroundColor() {
    // Run the test
    MTConfig.getInstance().setIsMaterialTheme(false);
    darkAbstractTheme.activate();

    final Color result = darkAbstractTheme.getForegroundColor();

    // Verify the results
    assertEquals("should return darcula foreground color", UIManager.getColor("darcula.foreground"), result);
  }

  @Test
  public void testLightGetForegroundColor() {
    // Run the test
    MTConfig.getInstance().setIsMaterialTheme(false);
    lightAbstractTheme.activate();

    final Color result = lightAbstractTheme.getForegroundColor();

    // Verify the results
    assertEquals("should return darcula foreground color", UIManager.getColor("intellijlaf.foreground"), result);
  }

  @Test
  public void testGetContrastColor() {
    assertEquals("should return dark material contrast", UIManager.getColor("material.contrast"), darkAbstractTheme.getContrastColor());
    assertEquals("should return light material contrast", UIManager.getColor("material.contrast"), lightAbstractTheme.getContrastColor());
  }

  @Test
  public void testDarculaGetContrastColor() {
    // setup
    MTConfig.getInstance().setIsMaterialTheme(false);
    darkAbstractTheme.activate();

    // Run the test
    final Color result = darkAbstractTheme.getContrastColor();

    // Verify the results
    assertEquals("should return default contrast", UIManager.getColor("darcula.contrastColor"), result);
  }

  @Test
  public void testLightGetContrastColor() {
    // setup
    MTConfig.getInstance().setIsMaterialTheme(false);
    lightAbstractTheme.activate();

    // Run the test
    final Color result = lightAbstractTheme.getContrastColor();

    // Verify the results
    assertEquals("should return default contrast", UIManager.getColor("intellijlaf.contrastColor"), result);
  }
}
