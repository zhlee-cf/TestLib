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

public class UserInfoUpdateActivity$$ViewBinder<T extends UserInfoUpdateActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131361802, "field 'tvTitle'");
    target.tvTitle = finder.castView(view, 2131361802, "field 'tvTitle'");
    view = finder.findRequiredView(source, 2131361877, "field 'etInfo'");
    target.etInfo = finder.castView(view, 2131361877, "field 'etInfo'");
    view = finder.findRequiredView(source, 2131361875, "method 'onClick'");
    unbinder.view2131361875 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361876, "method 'onClick'");
    unbinder.view2131361876 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends UserInfoUpdateActivity> implements Unbinder {
    private T target;

    View view2131361875;

    View view2131361876;

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
      target.tvTitle = null;
      target.etInfo = null;
      view2131361875.setOnClickListener(null);
      view2131361876.setOnClickListener(null);
    }
  }
}
