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

public class ReLoginActivity$$ViewBinder<T extends ReLoginActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131361807, "field 'ivAvatar'");
    target.ivAvatar = finder.castView(view, 2131361807, "field 'ivAvatar'");
    view = finder.findRequiredView(source, 2131361850, "field 'tvNick'");
    target.tvNick = finder.castView(view, 2131361850, "field 'tvNick'");
    view = finder.findRequiredView(source, 2131361834, "field 'etPwd'");
    target.etPwd = finder.castView(view, 2131361834, "field 'etPwd'");
    view = finder.findRequiredView(source, 2131361835, "field 'btnLogin' and method 'onClick'");
    target.btnLogin = finder.castView(view, 2131361835, "field 'btnLogin'");
    unbinder.view2131361835 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361851, "field 'tvChange' and method 'onClick'");
    target.tvChange = finder.castView(view, 2131361851, "field 'tvChange'");
    unbinder.view2131361851 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361829, "field 'tvVersion'");
    target.tvVersion = finder.castView(view, 2131361829, "field 'tvVersion'");
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends ReLoginActivity> implements Unbinder {
    private T target;

    View view2131361835;

    View view2131361851;

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
      target.ivAvatar = null;
      target.tvNick = null;
      target.etPwd = null;
      view2131361835.setOnClickListener(null);
      target.btnLogin = null;
      view2131361851.setOnClickListener(null);
      target.tvChange = null;
      target.tvVersion = null;
    }
  }
}
