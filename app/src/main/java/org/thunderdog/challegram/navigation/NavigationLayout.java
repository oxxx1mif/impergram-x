/*
 * This file is a part of Telegram X
 * Copyright © 2014 (tgx-android@pm.me)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.thunderdog.challegram.navigation;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.RecyclerView;

import com.pqcs.impergram.widget.ImperBarRenderer;

import org.thunderdog.challegram.tool.Screen;
import org.thunderdog.challegram.tool.Views;
import org.thunderdog.challegram.widget.RootFrameLayout;

import me.vkryl.android.widget.FrameLayoutFix;
import me.vkryl.core.lambda.Destroyable;

public class NavigationLayout extends FrameLayoutFix implements Destroyable, RootFrameLayout.InsetsChangeListener {

  private static final int IMPER_EXTRA_DP = 7;

  public NavigationLayout (Context context) {
    super(context);
  }

  private RootFrameLayout rootView;
  private NavigationController navigationController;
  private boolean pendingRefresh;

  private final ViewTreeObserver.OnPreDrawListener stateWatcher = () -> {
    scheduleRefresh();
    return true;
  };

  private void scheduleRefresh () {
    if (!pendingRefresh) {
      pendingRefresh = true;
      post(this::refreshState);
    }
  }

  @Override
  protected void onAttachedToWindow () {
    super.onAttachedToWindow();
    rootView = Views.findAncestor(this, RootFrameLayout.class, true);
    if (rootView != null) {
      rootView.addInsetsChangeListener(this);
    }
    getViewTreeObserver().addOnPreDrawListener(stateWatcher);
    for (int i = 0; i < getChildCount(); i++) {
      applyPaddingTo(getChildAt(i));
    }
  }

  @Override
  protected void onDetachedFromWindow () {
    super.onDetachedFromWindow();
    if (rootView != null) {
      rootView.removeInsetsChangeListener(this);
      rootView = null;
    }
    getViewTreeObserver().removeOnPreDrawListener(stateWatcher);
    pendingRefresh = false;
  }

  @Override
  public void onViewAdded (View child) {
    super.onViewAdded(child);
    if (child != null) {
      applyPaddingTo(child);
    }
  }

  @Override
  public void onInsetsChanged (RootFrameLayout viewGroup,
                               Rect effectiveInsets,
                               Rect effectiveInsetsWithoutIme,
                               Rect systemInsets,
                               Rect systemInsetsWithoutIme,
                               boolean isUpdate) {
    scheduleRefresh();
  }

  private void refreshState () {
    pendingRefresh = false;
    if (navigationController != null && navigationController.isAnimating()) {
      return;
    }
    for (int i = 0; i < getChildCount(); i++) {
      applyPaddingTo(getChildAt(i));
    }
  }

  private void applyPaddingTo (View view) {
    final int target = HeaderView.getSize(true)
      + (ImperBarRenderer.isFloating() ? Screen.dp(IMPER_EXTRA_DP) : 0);
    applyRecursive(view, target);
  }

  private void applyRecursive (View view, int target) {
    if (view == null) return;

    if (view instanceof RecyclerView) {
      RecyclerView rv = (RecyclerView) view;
      if (rv.getAdapter() == null) return;

      if (rv.isComputingLayout()) return;

      final boolean needsClip = rv.getClipToPadding();
      final boolean needsPadding = rv.getPaddingTop() != target;
      if (!needsClip && !needsPadding) return;

      if (needsClip) {
        rv.setClipToPadding(false);
      }
      if (needsPadding) {
        rv.setPadding(rv.getPaddingLeft(), target, rv.getPaddingRight(), rv.getPaddingBottom());
      }
      return;
    }

    if (view instanceof ViewGroup) {
      ViewGroup g = (ViewGroup) view;
      for (int i = 0; i < g.getChildCount(); i++) {
        View child = g.getChildAt(i);
        if (child != null && child != this) {
          applyRecursive(child, target);
        }
      }
    }
  }

  @Override
  public void performDestroy () {
    if (rootView != null) {
      rootView.removeInsetsChangeListener(this);
      rootView = null;
    }
  }

  private boolean preventLayout;
  private boolean layoutRequested;

  public void preventLayout () { preventLayout = true; }

  public void layoutIfRequested () {
    preventLayout = false;
    if (layoutRequested) {
      layoutRequested = false;
      requestLayout();
    }
  }

  public void cancelLayout () {
    preventLayout = false;
    layoutRequested = false;
  }

  public boolean isLayoutRequested () {
    return layoutRequested;
  }

  @Override
  public void requestLayout () {
    if (!preventLayout) {
      if (layoutLimit == -1) {
        super.requestLayout();
      } else if (layoutComplete < layoutLimit) {
        layoutComplete++;
        super.requestLayout();
      }
    } else {
      layoutRequested = true;
    }
  }

  private int layoutLimit = -1;
  private int layoutComplete;

  public void preventNextLayouts (int limit) {
    layoutLimit = limit;
    layoutComplete = 0;
  }

  public void completeNextLayout () {
    layoutLimit = -1;
    layoutComplete = 0;
  }

  public void setController (NavigationController controller) {
    this.navigationController = controller;
  }

  public void setTargetRecyclerView (RecyclerView rv) {
    scheduleRefresh();
  }
}