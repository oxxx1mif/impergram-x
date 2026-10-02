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
import android.view.View;
import android.widget.Toast;

import org.thunderdog.challegram.R;
import org.thunderdog.challegram.component.base.SettingView;
import org.thunderdog.challegram.telegram.Tdlib;
import org.thunderdog.challegram.tool.UI;
import org.thunderdog.challegram.ui.ListItem;
import org.thunderdog.challegram.ui.RecyclerViewController;
import org.thunderdog.challegram.ui.SettingsAdapter;
import org.thunderdog.challegram.unsorted.Settings;
import org.thunderdog.challegram.v.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ImperAppearanceController extends RecyclerViewController<Void> implements View.OnClickListener {

  public ImperAppearanceController (Context context, Tdlib tdlib) {
    super(context, tdlib);
  }

  @Override
  public CharSequence getName () {
    return "Imper appearances";
  }

  @Override
  public int getId () {
    return R.id.controller_imper_settings_appearance;
  }

  private SettingsAdapter adapter;

  @Override
  protected void onCreateView (Context context, CustomRecyclerView recyclerView) {
    adapter = new SettingsAdapter(this) {
      @Override
      public void setValuedSetting (ListItem item, SettingView v, boolean isUpdate) {
        if (item.getId() == R.id.btn_imper_md3) {
          v.getToggler().setRadioEnabled(Settings.instance().isImperMd3Enabled(), isUpdate);
        }
      }
    };

    List<ListItem> items = new ArrayList<>();
    items.add(new ListItem(ListItem.TYPE_EMPTY_OFFSET_SMALL));

    items.add(new ListItem(ListItem.TYPE_RADIO_SETTING, R.id.btn_imper_md3, 0, R.string.ImperMD3));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    adapter.setItems(items, false);
    recyclerView.setAdapter(adapter);
  }

  @Override
  public void onClick (View v) {
    int id = v.getId();
    if (id == R.id.btn_imper_md3) {
      boolean enabled = adapter.toggleView(v);
      Settings.instance().setImperMd3Enabled(enabled);
      UI.showToast(R.string.ImperRestart, Toast.LENGTH_LONG);
    }
  }
}