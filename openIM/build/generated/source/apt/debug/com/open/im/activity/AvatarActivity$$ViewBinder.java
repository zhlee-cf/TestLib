// Generated code from Butter Knife. Do not modify!
package com.open.im.activity;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class AvatarActivity$$ViewBinder<T extends AvatarActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131361803, "field 'ibBack'");
    target.ibBack = finder.castView(view, 2131361803, "field 'ibBack'");
    view = finder.findRequiredView(source, 2131361804, "field 'tvBack'");
    target.tvBack = finder.castView(view, 2131361804, "field 'tvBack'");
    view = finder.findRequiredView(source, 2131361805, "field 'ivMore'");
    target.ivMore = finder.castView(view, 2131361805, "field 'ivMore'");
    view = finder.findRequiredView(source, 2131361806, "field 'ivSave'");
    target.ivSave = finder.castView(view, 2131361806, "field 'ivSave'");
    view = finder.findRequiredView(source, 2131361807, "field 'ivAvatar'");
    target.ivAvatar = finder.castView(view, 2131361807, "field 'ivAvatar'");
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends AvatarActivity> implements Unbinder {
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
      target.ibBack = null;
      target.tvBack = null;
      target.ivMore = null;
      target.ivSave = null;
      target.ivAvatar = null;
    }
  }
}
