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

public class UpdatePasswordActivity$$ViewBinder<T extends UpdatePasswordActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131361865, "field 'etPwdOld'");
    target.etPwdOld = finder.castView(view, 2131361865, "field 'etPwdOld'");
    view = finder.findRequiredView(source, 2131361866, "field 'ivLock1' and method 'onClick'");
    target.ivLock1 = finder.castView(view, 2131361866, "field 'ivLock1'");
    unbinder.view2131361866 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361867, "field 'etPwd1'");
    target.etPwd1 = finder.castView(view, 2131361867, "field 'etPwd1'");
    view = finder.findRequiredView(source, 2131361868, "field 'ivLock2' and method 'onClick'");
    target.ivLock2 = finder.castView(view, 2131361868, "field 'ivLock2'");
    unbinder.view2131361868 = view;
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
    view = finder.findRequiredView(source, 2131361869, "method 'onClick'");
    unbinder.view2131361869 = view;
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

  protected static class InnerUnbinder<T extends UpdatePasswordActivity> implements Unbinder {
    private T target;

    View view2131361866;

    View view2131361868;

    View view2131361803;

    View view2131361804;

    View view2131361869;

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
      target.etPwdOld = null;
      view2131361866.setOnClickListener(null);
      target.ivLock1 = null;
      target.etPwd1 = null;
      view2131361868.setOnClickListener(null);
      target.ivLock2 = null;
      view2131361803.setOnClickListener(null);
      view2131361804.setOnClickListener(null);
      view2131361869.setOnClickListener(null);
    }
  }
}
