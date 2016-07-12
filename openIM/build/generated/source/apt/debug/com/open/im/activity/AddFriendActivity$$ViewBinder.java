// Generated code from Butter Knife. Do not modify!
package com.open.im.activity;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class AddFriendActivity$$ViewBinder<T extends AddFriendActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131361803, "field 'ibBack' and method 'onClick'");
    target.ibBack = finder.castView(view, 2131361803, "field 'ibBack'");
    unbinder.view2131361803 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361804, "field 'tvBack' and method 'onClick'");
    target.tvBack = finder.castView(view, 2131361804, "field 'tvBack'");
    unbinder.view2131361804 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361814, "field 'ivMinus' and method 'onClick'");
    target.ivMinus = finder.castView(view, 2131361814, "field 'ivMinus'");
    unbinder.view2131361814 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361855, "field 'etSearchKey'");
    target.etSearchKey = finder.castView(view, 2131361855, "field 'etSearchKey'");
    view = finder.findRequiredView(source, 2131361856, "field 'btnSearch' and method 'onClick'");
    target.btnSearch = finder.castView(view, 2131361856, "field 'btnSearch'");
    unbinder.view2131361856 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361857, "field 'llSearchList'");
    target.llSearchList = finder.castView(view, 2131361857, "field 'llSearchList'");
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends AddFriendActivity> implements Unbinder {
    private T target;

    View view2131361803;

    View view2131361804;

    View view2131361814;

    View view2131361856;

    protected InnerUnbinder(T target) {
      this.target = target;
    }

    @Override
    public final void unbind() {
      if (target == null) throw new IllegalStateException("Bindings already cleared.");
      unbind(target);
      target = null;
    }

    protected void unbind(T target) {
      view2131361803.setOnClickListener(null);
      target.ibBack = null;
      view2131361804.setOnClickListener(null);
      target.tvBack = null;
      view2131361814.setOnClickListener(null);
      target.ivMinus = null;
      target.etSearchKey = null;
      view2131361856.setOnClickListener(null);
      target.btnSearch = null;
      target.llSearchList = null;
    }
  }
}
