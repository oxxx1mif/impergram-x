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
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.thunderdog.challegram.R;
import org.thunderdog.challegram.component.base.SettingView;
import org.thunderdog.challegram.telegram.Tdlib;
import org.thunderdog.challegram.theme.ColorId;
import org.thunderdog.challegram.theme.Theme;
import org.thunderdog.challegram.tool.Paints;
import org.thunderdog.challegram.tool.Screen;
import org.thunderdog.challegram.tool.UI;
import org.thunderdog.challegram.ui.ListItem;
import org.thunderdog.challegram.ui.RecyclerViewController;
import org.thunderdog.challegram.ui.SettingHolder;
import org.thunderdog.challegram.ui.SettingsAdapter;
import org.thunderdog.challegram.unsorted.Settings;
import org.thunderdog.challegram.v.CustomRecyclerView;
import org.thunderdog.challegram.widget.SliderWrapView;

import java.util.ArrayList;
import java.util.List;

public class ImperAvatarController extends RecyclerViewController<Void>
    implements View.OnClickListener {

  private static final int ROUNDNESS_STEPS = 11; // 0% … 100%
  private static final int SIZE_STEPS      = 9;  // 80% … 120%

  private long lastToastTime = 0L;

  public ImperAvatarController (Context context, Tdlib tdlib) {
    super(context, tdlib);
  }

  @Override public CharSequence getName () { return "Avatar shape"; }
  @Override public int getId () { return R.id.controller_imper_avatar; }

  private SettingsAdapter adapter;
  private AvatarPreviewView previewView;

  @Override
  protected void onCreateView (Context context, CustomRecyclerView recyclerView) {
    adapter = new SettingsAdapter(this) {
      @Override
      protected SettingHolder initCustom (ViewGroup parent) {
        AvatarPreviewView preview = new AvatarPreviewView(parent.getContext());
        preview.setLayoutParams(new RecyclerView.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, Screen.dp(180f)));
        previewView = preview;
        return new SettingHolder(preview);
      }

      @Override
      protected void onSliderValueChanged (ListItem item, SliderWrapView view, int newValue, int oldValue) {
        final int id = item.getId();
        if (id == R.id.btn_imper_avatar_radius) {
          Settings.instance().setImperAvatarRadius(newValue / (float) (ROUNDNESS_STEPS - 1));
        } else if (id == R.id.btn_imper_avatar_size) {
          Settings.instance().setImperAvatarSize(0.8f + newValue * 0.05f);
        }
        if (previewView != null) {
          previewView.invalidate();
        }
        showRestartToast();
      }

      @Override
      public void setValuedSetting (ListItem item, SettingView v, boolean isUpdate) {
        if (item.getId() == R.id.btn_imper_avatar_noframes) {
          v.getToggler().setRadioEnabled(Settings.instance().isImperAvatarNoFrames(), isUpdate);
        }
      }
    };

    List<ListItem> items = new ArrayList<>();

    items.add(new ListItem(ListItem.TYPE_CUSTOM_SINGLE, R.id.btn_imper_avatar_preview, 0, 0));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    // Roundness slider
    final int radiusIdx = Math.round(Settings.instance().getImperAvatarRadius() * (ROUNDNESS_STEPS - 1));
    final String[] radiusValues = new String[ROUNDNESS_STEPS];
    for (int i = 0; i < ROUNDNESS_STEPS; i++) {
      radiusValues[i] = Math.round(100f * i / (float) (ROUNDNESS_STEPS - 1)) + "%";
    }
    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.ImperAvatarRadius));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(new ListItem(ListItem.TYPE_SLIDER, R.id.btn_imper_avatar_radius, 0, R.string.ImperAvatarRadius)
      .setSliderInfo(radiusValues, radiusIdx));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    // Size slider
    final int sizeIdx = Math.round((Settings.instance().getImperAvatarSize() - 0.8f) / 0.05f);
    final String[] sizeValues = new String[SIZE_STEPS];
    for (int i = 0; i < SIZE_STEPS; i++) {
      sizeValues[i] = Math.round(100f * (0.8f + i * 0.05f)) + "%";
    }
    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.ImperAvatarSize));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(new ListItem(ListItem.TYPE_SLIDER, R.id.btn_imper_avatar_size, 0, R.string.ImperAvatarSize)
      .setSliderInfo(sizeValues, sizeIdx));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.ImperAvatarFrames));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(new ListItem(ListItem.TYPE_RADIO_SETTING, R.id.btn_imper_avatar_noframes, 0, R.string.ImperAvatarNoFrames));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));
    items.add(new ListItem(ListItem.TYPE_DESCRIPTION, 0, 0, R.string.ImperAvatarNoFramesDesc));

    adapter.setItems(items, false);
    recyclerView.setAdapter(adapter);
  }

  @Override
  public void onClick (View v) {
    int id = v.getId();
    if (id == R.id.btn_imper_avatar_noframes) {
      boolean enabled = adapter.toggleView(v);
      Settings.instance().setImperAvatarNoFrames(enabled);
      if (previewView != null) previewView.invalidate();
      showRestartToast();
    }
  }

  private void showRestartToast () {
    if (System.currentTimeMillis() - lastToastTime > 3000L) {
      lastToastTime = System.currentTimeMillis();
      UI.showToast("Restart the app to apply to all avatars", Toast.LENGTH_SHORT);
    }
  }

  private static class AvatarPreviewView extends View {
    private final Paint fillPaint = Paints.fillingPaint(0);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();

    AvatarPreviewView (Context context) {
      super(context);
      textPaint.setColor(0xFFFFFFFF);
      textPaint.setTextSize(Screen.sp(28f));
      textPaint.setTypeface(org.thunderdog.challegram.tool.Fonts.getRobotoBold());
      textPaint.setTextAlign(Paint.Align.CENTER);
      setWillNotDraw(false);
    }

    @Override
    protected void onDraw (@NonNull Canvas c) {
      final int w = getWidth();
      final int h = getHeight();
      if (w == 0 || h == 0) return;

      final int cx = w / 2;
      final int cy = h / 2;

      final float baseSize = Screen.dp(100f);
      final float size = baseSize * Settings.instance().getImperAvatarSize();
      final float radius = (size / 2f) * Settings.instance().getImperAvatarRadius();

      rect.set(cx - size / 2f, cy - size / 2f, cx + size / 2f, cy + size / 2f);

      fillPaint.setColor(Theme.getColor(ColorId.fillingPositive));
      c.drawRoundRect(rect, radius, radius, fillPaint);

      if (Settings.instance().isImperAvatarNoFrames()) {
        Paint stroke = Paints.getProgressPaint(Theme.getColor(ColorId.icon), Screen.dp(1.5f));
        c.drawRoundRect(rect, radius, radius, stroke);
      }

      final Paint.FontMetrics fm = textPaint.getFontMetrics();
      final float baseline = cy - (fm.ascent + fm.descent) / 2f;
      c.drawText("A", cx, baseline, textPaint);
    }
  }
}