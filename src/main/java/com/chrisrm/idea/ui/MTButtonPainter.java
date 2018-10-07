/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2018 Chris Magnussen and Elior Boukhobza
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */
package com.chrisrm.idea.ui;

import com.chrisrm.idea.legacy.LegacySupportUtility;
import com.intellij.ide.ui.laf.darcula.DarculaUIUtil;
import com.intellij.ide.ui.laf.darcula.ui.DarculaButtonPainter;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.util.ui.JBUI;

import java.awt.*;

/**
 * @author Konstantin Bulenkov
 */
public class MTButtonPainter extends DarculaButtonPainter {
  @Override
  public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {

  }

  /**
   * Set bigger border insets for "Material style buttons"
   *
   * @param c
   * @return
   */
  @Override
  public Insets getBorderInsets(final Component c) {
    return LegacySupportUtility.INSTANCE.useFieldSafely(
        DarculaUIUtil.class,
        "LW",
        ()->  JBUI.insets(3).asUIResource(),
        ()->
            c.getParent() instanceof ActionToolbar ?
                JBUI.insets(4, 16, 4, 16).asUIResource() :
                JBUI.insets(6, 16, 6, 16).asUIResource()
    );
  }

  @Override
  protected int getOffset() {
    return 16;
  }

  @Override
  public final boolean isBorderOpaque() {
    return false;
  }
}
