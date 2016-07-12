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

public class ClientActivity$$ViewBinder<T extends ClientActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131361829, "field 'tvVersion'");
    target.tvVersion = finder.castView(view, 2131361829, "field 'tvVersion'");
    view = finder.findRequiredView(source, 2131361830, "field 'ivQrcode' and method 'onClick'");
    target.ivQrcode = finder.castView(view, 2131361830, "field 'ivQrcode'");
    unbinder.view2131361830 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361803, "method 'onClick'");
    unbinder.view2131361803 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361804, "method 'onClick'");
    unbinder.view2131361804 = view;
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

  protected static class InnerUnbinder<T extends ClientActivity> implements Unbinder {
    private T target;

    View view2131361830;

    View view2131361803;

    View view2131361804;

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
      target.tvVersion = null;
      view2131361830.setOnClickListener(null);
      target.ivQrcode = null;
      view2131361803.setOnClickListener(null);
      view2131361804.setOnClickListener(null);
    }
  }
}
