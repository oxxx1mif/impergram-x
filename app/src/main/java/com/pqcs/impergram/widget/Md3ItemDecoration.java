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

package com.pqcs.impergram.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.thunderdog.challegram.tool.Screen;
import org.thunderdog.challegram.ui.ListItem;
import org.thunderdog.challegram.ui.SettingsAdapter;
import org.thunderdog.challegram.unsorted.Settings;

import java.util.List;

public class Md3ItemDecoration extends RecyclerView.ItemDecoration {
  private final int sideMargin;

  public Md3ItemDecoration () {
    this.sideMargin = Screen.dp(10f);
  }

  @Override
  public void getItemOffsets (@NonNull Rect outRect, @NonNull View view,
                              @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
    if (!Settings.instance().isImperMd3Enabled()) return;

    int pos = parent.getChildAdapterPosition(view);
    if (pos == RecyclerView.NO_POSITION) return;

    RecyclerView.Adapter<?> adapter = parent.getAdapter();
    if (!(adapter instanceof SettingsAdapter)) return;

    List<ListItem> items = ((SettingsAdapter) adapter).getItems();
    if (items == null || pos < 0 || pos >= items.size()) return;

    ListItem item = items.get(pos);
    if (item == null) return;

    if (!isServiceType(item.getViewType())) {
      outRect.left = sideMargin;
      outRect.right = sideMargin;
    }
  }

  public static boolean isServiceType (int type) {
    switch (type) {
      case ListItem.TYPE_SEPARATOR:
      case ListItem.TYPE_SEPARATOR_FULL:
      case ListItem.TYPE_SHADOW_TOP:
      case ListItem.TYPE_SHADOW_BOTTOM:
      case ListItem.TYPE_HEADER:
      case ListItem.TYPE_HEADER_PADDED:
      case ListItem.TYPE_HEADER_MULTILINE:
      case ListItem.TYPE_HEADER_WITH_ACTION:
      case ListItem.TYPE_DESCRIPTION:
      case ListItem.TYPE_DESCRIPTION_SMALL:
      case ListItem.TYPE_DESCRIPTION_CENTERED:
      case ListItem.TYPE_BUILD_NO:
      case ListItem.TYPE_EMPTY_OFFSET:
      case ListItem.TYPE_EMPTY_OFFSET_SMALL:
      case ListItem.TYPE_EMPTY_OFFSET_NO_HEAD:
      case ListItem.TYPE_EMPTY:
      case ListItem.TYPE_PADDING:
      case ListItem.TYPE_ZERO_VIEW:
      case ListItem.TYPE_FAKE_PAGER_TOPVIEW:
        return true;
      default:
        return false;
    }
  }
}