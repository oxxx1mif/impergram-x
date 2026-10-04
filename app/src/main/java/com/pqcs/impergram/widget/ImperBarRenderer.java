/*
 * This file is a part of Impergram
 * Copyright 2026 Gleb Obitotsky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.pqcs.impergram.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import org.thunderdog.challegram.theme.Theme;
import org.thunderdog.challegram.tool.Paints;
import org.thunderdog.challegram.tool.Screen;
import org.thunderdog.challegram.ui.ListItem;
import org.thunderdog.challegram.unsorted.Settings;

import me.vkryl.core.ColorUtils;

public final class ImperBarRenderer {
  private ImperBarRenderer () { }

  public static final int EDGE_TOP = 0;
  public static final int EDGE_BOTTOM = 1;
  public static final int EDGE_ALL = 2;

  private static final Path PATH = new Path();

  public static boolean isEnabled () {
    return Settings.instance().isImperBarCustomizationEnabled();
  }

  public static boolean isFloating () {
    return Settings.instance().isImperBarFloating();
  }

  public static float getCornerRadiusPx () {
    return Screen.dp(Settings.instance().getImperBarCornerRadius());
  }

  public static float getFloatingMarginPx () {
    return Screen.dp(Settings.instance().getImperBarFloatingMargin());
  }

  public static float computeFloatingWidth (float containerWidth) {
    float margin = getFloatingMarginPx();
    float avail = Math.max(0f, containerWidth - margin * 2f);
    float widthDp = Settings.instance().getImperBarFloatingWidthDp();
    if (widthDp <= 0f) return avail;
    return Math.min(avail, Screen.dp(widthDp));
  }

  public static float computeFloatingLeft (float containerWidth, float barWidth) {
    float margin = getFloatingMarginPx();
    int align = Settings.instance().getImperBarAlignment();
    switch (align) {
      case Settings.IMPER_BAR_ALIGN_LEFT:
        return margin;
      case Settings.IMPER_BAR_ALIGN_RIGHT:
        return containerWidth - margin - barWidth;
      case Settings.IMPER_BAR_ALIGN_CENTER:
      default:
        return (containerWidth - barWidth) / 2f;
    }
  }

  public static void drawBackground (Canvas c, int edge,
                                     float left, float top, float right, float bottom,
                                     int color) {
    float w = right - left;
    float h = bottom - top;
    if (w <= 0f || h <= 0f) return;

    if (!isEnabled()) {
      c.drawRect(left, top, right, bottom, Paints.fillingPaint(color));
      return;
    }
    float r = getCornerRadiusPx();
    if (r <= 0f) {
      c.drawRect(left, top, right, bottom, Paints.fillingPaint(color));
      return;
    }
    r = Math.min(r, Math.min(w / 2f, h / 2f));
    Paint paint = Paints.fillingPaint(color);

    if (edge == EDGE_ALL) {
      c.drawRoundRect(left, top, right, bottom, r, r, paint);
      return;
    }

    PATH.reset();
    if (edge == EDGE_TOP) {
      PATH.moveTo(left, top);
      PATH.lineTo(right, top);
      PATH.lineTo(right, bottom - r);
      PATH.quadTo(right, bottom, right - r, bottom);
      PATH.lineTo(left + r, bottom);
      PATH.quadTo(left, bottom, left, bottom - r);
      PATH.close();
    } else { // EDGE_BOTTOM
      PATH.moveTo(left + r, top);
      PATH.lineTo(right - r, top);
      PATH.quadTo(right, top, right, top + r);
      PATH.lineTo(right, bottom);
      PATH.lineTo(left, bottom);
      PATH.lineTo(left, top + r);
      PATH.quadTo(left, top, left + r, top);
      PATH.close();
    }
    c.drawPath(PATH, paint);
  }

  public static void drawEdgeLine (Canvas c, float left, float y, float right,
                                   float alpha, boolean fromBelow) {
    if (!isEnabled() || alpha <= 0f) return;
    float thickness = Math.max(1f, Screen.dpf(0.5f));
    int baseColor = Theme.separatorColor();
    int lineColor = ColorUtils.alphaColor(0.9f * alpha, baseColor);
    float yTop = fromBelow ? y : y - thickness;
    c.drawRect(left, yTop, right, yTop + thickness, Paints.fillingPaint(lineColor));
  }
}