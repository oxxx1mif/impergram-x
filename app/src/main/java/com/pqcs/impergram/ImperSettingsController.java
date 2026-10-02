/*
 * This file is a part of Impergram
 * Copyright 2026 Gleb Obitotsky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.pqcs.impergram;

import android.content.Context;
import android.view.View;

import com.pqcs.impergram.general.ImperAppearanceController;

import org.thunderdog.challegram.BuildConfig;
import org.thunderdog.challegram.R;
import org.thunderdog.challegram.telegram.Tdlib;
import org.thunderdog.challegram.telegram.TdlibUi;
import org.thunderdog.challegram.ui.ListItem;
import org.thunderdog.challegram.ui.RecyclerViewController;
import org.thunderdog.challegram.ui.SettingsAdapter;
import org.thunderdog.challegram.v.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ImperSettingsController extends RecyclerViewController<Void> implements View.OnClickListener {

  private static final String CHANNEL_URL = "https://t.me/GuAMain";
  private static final String SOURCE_URL = "https://github.com/oxxx1mif/impergram-x";

  public ImperSettingsController (Context context, Tdlib tdlib) {
    super(context, tdlib);
  }

  @Override
  public CharSequence getName () {
    return "Imper preferences";
  }

  @Override
  public int getId () {
    return R.id.controller_imper_settings;
  }

  private SettingsAdapter adapter;

  @Override
  protected void onCreateView (Context context, CustomRecyclerView recyclerView) {
    adapter = new SettingsAdapter(this);

    List<ListItem> items = new ArrayList<>();
    items.add(new ListItem(ListItem.TYPE_EMPTY_OFFSET_SMALL));

    // General
    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.ImperSettingsGeneral));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(new ListItem(ListItem.TYPE_SETTING, R.id.btn_imper_ui_settings, R.drawable.baseline_palette_24, R.string.ImperUiSettings));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    // Links
    items.add(new ListItem(ListItem.TYPE_HEADER, 0, 0, R.string.ImperSettingsLinks));
    items.add(new ListItem(ListItem.TYPE_SHADOW_TOP));
    items.add(new ListItem(ListItem.TYPE_SETTING, R.id.btn_imper_channel, R.drawable.baseline_bullhorn_24, R.string.ImperChannel));
    items.add(new ListItem(ListItem.TYPE_SEPARATOR_FULL));
    items.add(new ListItem(ListItem.TYPE_SETTING, R.id.btn_imper_source, R.drawable.baseline_github_24, R.string.ImperSourceCode));
    items.add(new ListItem(ListItem.TYPE_SHADOW_BOTTOM));

    // Footer
    items.add(new ListItem(ListItem.TYPE_BUILD_NO, R.id.btn_build, 0, "Impergram " + BuildConfig.VERSION_NAME, false));

    adapter.setItems(items, false);
    recyclerView.setAdapter(adapter);
  }

  @Override
  public void onClick (View v) {
    int id = v.getId();
    if (id == R.id.btn_imper_ui_settings) {
      navigateTo(new ImperAppearanceController(context, tdlib));
    } else if (id == R.id.btn_imper_channel) {
      openUrl(CHANNEL_URL);
    } else if (id == R.id.btn_imper_source) {
      openUrl(SOURCE_URL);
    }
  }

  private void openUrl (String url) {
    tdlib.ui().openUrl(this, url, new TdlibUi.UrlOpenParameters().disableInstantView());
  }
}