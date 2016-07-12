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

public class LoginActivity$$ViewBinder<T extends LoginActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131361832, "field 'etUsername'");
    target.etUsername = finder.castView(view, 2131361832, "field 'etUsername'");
    view = finder.findRequiredView(source, 2131361834, "field 'etPwd'");
    target.etPwd = finder.castView(view, 2131361834, "field 'etPwd'");
    view = finder.findRequiredView(source, 2131361836, "field 'tvRegister' and method 'onClick'");
    target.tvRegister = finder.castView(view, 2131361836, "field 'tvRegister'");
    unbinder.view2131361836 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361835, "method 'onClick'");
    unbinder.view2131361835 = view;
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

  protected static class InnerUnbinder<T extends LoginActivity> implements Unbinder {
    private T target;

    View view2131361836;

    View view2131361835;

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
      target.etUsername = null;
      target.etPwd = null;
      view2131361836.setOnClickListener(null);
      target.tvRegister = null;
      view2131361835.setOnClickListener(null);
    }
  }
}
