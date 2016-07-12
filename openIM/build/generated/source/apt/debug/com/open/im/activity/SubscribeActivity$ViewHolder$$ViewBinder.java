// Generated code from Butter Knife. Do not modify!
package com.open.im.activity;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class SubscribeActivity$ViewHolder$$ViewBinder<T extends SubscribeActivity.ViewHolder> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131361807, "field 'ivAvatar'");
    target.ivAvatar = finder.castView(view, 2131361807, "field 'ivAvatar'");
    view = finder.findRequiredView(source, 2131361942, "field 'tvName'");
    target.tvName = finder.castView(view, 2131361942, "field 'tvName'");
    view = finder.findRequiredView(source, 2131361839, "field 'tvState'");
    target.tvState = finder.castView(view, 2131361839, "field 'tvState'");
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends SubscribeActivity.ViewHolder> implements Unbinder {
    private T target;

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
      target.tvName = null;
      target.tvState = null;
    }
  }
}
