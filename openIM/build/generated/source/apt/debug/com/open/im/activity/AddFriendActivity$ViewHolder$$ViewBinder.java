// Generated code from Butter Knife. Do not modify!
package com.open.im.activity;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class AddFriendActivity$ViewHolder$$ViewBinder<T extends AddFriendActivity.ViewHolder> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131361932, "field 'ivIcon'");
    target.ivIcon = finder.castView(view, 2131361932, "field 'ivIcon'");
    view = finder.findRequiredView(source, 2131361802, "field 'tvTitle'");
    target.tvTitle = finder.castView(view, 2131361802, "field 'tvTitle'");
    view = finder.findRequiredView(source, 2131361933, "field 'tvMsg'");
    target.tvMsg = finder.castView(view, 2131361933, "field 'tvMsg'");
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends AddFriendActivity.ViewHolder> implements Unbinder {
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
      target.ivIcon = null;
      target.tvTitle = null;
      target.tvMsg = null;
    }
  }
}
