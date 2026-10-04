/*
 * This file is a part of Impergram
 * Copyright 2026 Gleb Obitotsky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.pqcs.impergram.general;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.thunderdog.challegram.R;
import org.thunderdog.challegram.telegram.Tdlib;
import org.thunderdog.challegram.theme.Theme;
import org.thunderdog.challegram.tool.Fonts;
import org.thunderdog.challegram.tool.Screen;
import org.thunderdog.challegram.tool.UI;
import org.thunderdog.challegram.ui.ListItem;
import org.thunderdog.challegram.ui.RecyclerViewController;
import org.thunderdog.challegram.ui.SettingHolder;
import org.thunderdog.challegram.ui.SettingsAdapter;
import org.thunderdog.challegram.unsorted.Settings;
import org.thunderdog.challegram.v.CustomRecyclerView;
import org.thunderdog.challegram.widget.SliderWrapView;

import com.pqcs.impergram.widget.ImperBarRenderer;

import java.util.ArrayList;
import java.util.List;

import me.vkryl.core.MathUtils;

public class ImperBarsController extends RecyclerViewController<Void>
  implements View.OnClickListener {

  private static final int RADIUS_STEPS = 13; // 0..24 dp
  private static final int MARGIN_STEPS = 13; // 0..24 dp
  private static final int WIDTH_STEPS = 11; // Auto + 100..200 dp

  private long lastToastTime = 0L;

  public ImperBarsController (Context context, Tdlib tdlib) {
    super(context, tdlib);
  }

  @Override public CharSequence getName () { return "Bars"; }
  @Override public int getId () { return R.id.controller_imper_bars; }

  private SettingsAdapter adapter;
  private PreviewView previewView;

  private ListItem floatingItem;
  private ListItem lineItem;
  private ListItem alignLeftItem;
  private ListItem alignCenterItem;
  private ListItem alignRightItem;

  @Override
  protected void onCreateView (Context context, CustomRecyclerView recyclerView) {
    adapter = new SettingsAdapter(this) {
      @Override
      protected SettingHolder initCustom (ViewGroup parent) {
        PreviewView v = new PreviewView(parent.getContext());
        v.setLayoutParams(new RecyclerView.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, Screen.dp(180f)));
        previewView = v;
        return new SettingHolder(v);
      }

      @Override
      protected void onSliderValueChanged (ListItem item, SliderWrapView view,
                                           int newValue, int oldValue) {
        final int id = item.getId();
        if (id == R.id.btn_imper_bar_radius) {
          Settings.instance().setImperBarCornerRadius(newValue);
        } else if (id == R.id.btn_imper_bar_margin) {
          Settings.instance().setImperBarFloatingMargin(newValue);
        } else if (id == R.id.btn_imper_bar_width) {
          Settings.instance().setImperBarFloatingWidthDp(
            newValue == 0 ? 0f : (100f + (newValue - 1) * 10f));
        }
        refreshPreview();
        showRestartToast();
      }
    };

    List<ListItem> items = new ArrayList<>();

    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));

    // Preview
    items.add(new ListItem(ListItem.TYPE_CUSTOM_SINGLE, R.id.btn_imper_bar_preview, 0, 0));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    // Corner radius
    final int rIdx = MathUtils.clamp(
      Math.round(Settings.instance().getImperBarCornerRadius()),
      0, RADIUS_STEPS - 1);
    final String[] rValues = new String[RADIUS_STEPS];
    for (int i = 0; i < RADIUS_STEPS; i++) rValues[i] = i + "dp";
    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.ImperBarRadius));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(new ListItem(ListItem.TYPE_SLIDER, R.id.btn_imper_bar_radius, 0, R.string.ImperBarRadius)
      .setSliderInfo(rValues, rIdx));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    // Floating toggle
    floatingItem = new ListItem(ListItem.TYPE_RADIO_SETTING,
      R.id.btn_imper_bar_floating, 0, R.string.ImperBarFloating)
      .setSelected(Settings.instance().isImperBarFloating());
    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.ImperBarFloating));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(floatingItem);
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));
    items.add(new ListItem(ListItem.TYPE_DESCRIPTION, 0, 0, R.string.ImperBarFloatingDesc));

    // Edge margin
    final int mIdx = MathUtils.clamp(
      Math.round(Settings.instance().getImperBarFloatingMargin()),
      0, MARGIN_STEPS - 1);
    final String[] mValues = new String[MARGIN_STEPS];
    for (int i = 0; i < MARGIN_STEPS; i++) mValues[i] = i + "dp";
    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.ImperBarMargin));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(new ListItem(ListItem.TYPE_SLIDER, R.id.btn_imper_bar_margin, 0, R.string.ImperBarMargin)
      .setSliderInfo(mValues, mIdx));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    // Bar width
    final float wDp = Settings.instance().getImperBarFloatingWidthDp();
    int wIdx = 0;
    if (wDp > 0f) {
      wIdx = MathUtils.clamp(1 + Math.round((wDp - 100f) / 10f), 1, WIDTH_STEPS - 1);
    }
    final String[] wValues = new String[WIDTH_STEPS];
    wValues[0] = "Auto";
    for (int i = 1; i < WIDTH_STEPS; i++) {
      wValues[i] = (100 + (i - 1) * 10) + "dp";
    }
    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.ImperBarWidth));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(new ListItem(ListItem.TYPE_SLIDER, R.id.btn_imper_bar_width, 0, R.string.ImperBarWidth)
      .setSliderInfo(wValues, wIdx));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));
    items.add(new ListItem(ListItem.TYPE_DESCRIPTION, 0, 0, R.string.ImperBarWidthDesc));

    // Alignment
    final int curAlign = Settings.instance().getImperBarAlignment();

    alignLeftItem = new ListItem(ListItem.TYPE_RADIO_OPTION,
      R.id.btn_imper_bar_align_left, 0, R.string.ImperBarAlignLeft,
      R.id.btn_imper_bar_align, curAlign == Settings.IMPER_BAR_ALIGN_LEFT);
    alignCenterItem = new ListItem(ListItem.TYPE_RADIO_OPTION,
      R.id.btn_imper_bar_align_center, 0, R.string.ImperBarAlignCenter,
      R.id.btn_imper_bar_align, curAlign == Settings.IMPER_BAR_ALIGN_CENTER);
    alignRightItem = new ListItem(ListItem.TYPE_RADIO_OPTION,
      R.id.btn_imper_bar_align_right, 0, R.string.ImperBarAlignRight,
      R.id.btn_imper_bar_align, curAlign == Settings.IMPER_BAR_ALIGN_RIGHT);

    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.ImperBarAlign));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(alignLeftItem);
    items.add(new ListItem(ListItem.TYPE_SEPARATOR_FULL));
    items.add(alignCenterItem);
    items.add(new ListItem(ListItem.TYPE_SEPARATOR_FULL));
    items.add(alignRightItem);
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    // Divider line
    lineItem = new ListItem(ListItem.TYPE_RADIO_SETTING,
      R.id.btn_imper_bar_line, 0, R.string.ImperBarLine)
      .setSelected(Settings.instance().isImperBarLineEnabled());
    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.ImperBarLine));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(lineItem);
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));
    items.add(new ListItem(ListItem.TYPE_DESCRIPTION, 0, 0, R.string.ImperBarLineDesc));

    adapter.setItems(items, false);
    recyclerView.setAdapter(adapter);
    recyclerView.setItemAnimator(null);
  }

  @Override
  public void onClick (View v) {
    final int id = v.getId();

    if (id == R.id.btn_imper_bar_line) {
      final boolean enabled = adapter.toggleView(v);
      Settings.instance().setImperBarLineEnabled(enabled);
      if (lineItem != null) lineItem.setSelected(enabled);
      refreshPreview();
      showRestartToast();

    } else if (id == R.id.btn_imper_bar_floating) {
      final boolean enabled = adapter.toggleView(v);
      Settings.instance().setImperBarFloating(enabled);
      if (floatingItem != null) floatingItem.setSelected(enabled);
      refreshPreview();
      showRestartToast();

    } else if (id == R.id.btn_imper_bar_align_left
      || id == R.id.btn_imper_bar_align_center
      || id == R.id.btn_imper_bar_align_right) {

      final int newAlign =
        id == R.id.btn_imper_bar_align_left  ? Settings.IMPER_BAR_ALIGN_LEFT :
          id == R.id.btn_imper_bar_align_right ? Settings.IMPER_BAR_ALIGN_RIGHT :
          Settings.IMPER_BAR_ALIGN_CENTER;

      if (Settings.instance().getImperBarAlignment() == newAlign) {
        return;
      }
      Settings.instance().setImperBarAlignment(newAlign);

      if (alignLeftItem != null) {
        alignLeftItem.setSelected(newAlign == Settings.IMPER_BAR_ALIGN_LEFT);
      }
      if (alignCenterItem != null) {
        alignCenterItem.setSelected(newAlign == Settings.IMPER_BAR_ALIGN_CENTER);
      }
      if (alignRightItem != null) {
        alignRightItem.setSelected(newAlign == Settings.IMPER_BAR_ALIGN_RIGHT);
      }

      notifyItemChanged(alignLeftItem);
      notifyItemChanged(alignCenterItem);
      notifyItemChanged(alignRightItem);

      refreshPreview();
      showRestartToast();
    }
  }

  private void notifyItemChanged (ListItem item) {
    if (item == null) return;
    final int index = adapter.indexOfView(item);
    if (index >= 0) {
      adapter.notifyItemChanged(index);
    }
  }

  private void refreshPreview () {
    if (previewView != null) {
      previewView.invalidate();
    }
  }

  private void showRestartToast () {
    if (System.currentTimeMillis() - lastToastTime > 3000L) {
      lastToastTime = System.currentTimeMillis();
      UI.showToast("Restart the app to apply everywhere", Toast.LENGTH_SHORT);
    }
  }

  private static class PreviewView extends View {
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    PreviewView (Context context) {
      super(context);
      textPaint.setTextAlign(Paint.Align.CENTER);
      setWillNotDraw(false);
    }

    @Override
    protected void onDraw (@NonNull Canvas c) {
      final int w = getWidth();
      final int h = getHeight();
      if (w == 0 || h == 0) return;

      final float barH = Screen.dp(56f);
      final float margin = Screen.dp(8f);

      // Top bar
      if (ImperBarRenderer.isFloating()) {
        float barW = ImperBarRenderer.computeFloatingWidth(w);
        float left = ImperBarRenderer.computeFloatingLeft(w, barW);
        float right = left + barW;
        float top = margin;
        float bottom = top + barH;
        ImperBarRenderer.drawBackground(c, ImperBarRenderer.EDGE_ALL,
          left, top, right, bottom, Theme.headerColor());
        if (Settings.instance().isImperBarLineEnabled()) {
          ImperBarRenderer.drawEdgeLine(c, left, bottom, right, 1f, false);
        }
      } else {
        ImperBarRenderer.drawBackground(c, ImperBarRenderer.EDGE_TOP,
          0f, 0f, w, barH, Theme.headerColor());
      }

      // Bottom bar
      float bottomY = h - barH;
      if (ImperBarRenderer.isFloating()) {
        float barW = ImperBarRenderer.computeFloatingWidth(w);
        float left = ImperBarRenderer.computeFloatingLeft(w, barW);
        float right = left + barW;
        float top = bottomY + margin;
        float bottom = h - margin;
        ImperBarRenderer.drawBackground(c, ImperBarRenderer.EDGE_ALL,
          left, top, right, bottom, Theme.fillingColor());
        if (Settings.instance().isImperBarLineEnabled()) {
          ImperBarRenderer.drawEdgeLine(c, left, top, right, 1f, true);
        }
      } else {
        ImperBarRenderer.drawBackground(c, ImperBarRenderer.EDGE_BOTTOM,
          0f, bottomY, w, h, Theme.fillingColor());
      }

      // Labels
      textPaint.setTextSize(Screen.sp(14f));
      textPaint.setTypeface(Fonts.getRobotoMedium());

      textPaint.setColor(0xFFFFFFFF);
      drawCentered(c, "Header", w / 2f, margin + barH / 2f);

      textPaint.setColor(Theme.textAccentColor());
      drawCentered(c, "Input", w / 2f, h - margin - barH / 2f);
    }

    private void drawCentered (Canvas c, String text, float cx, float cy) {
      float baseline = cy - (textPaint.descent() + textPaint.ascent()) / 2f;
      c.drawText(text, cx, baseline, textPaint);
    }
  }
}