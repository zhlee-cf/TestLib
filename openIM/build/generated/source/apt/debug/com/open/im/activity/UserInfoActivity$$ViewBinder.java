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

public class UserInfoActivity$$ViewBinder<T extends UserInfoActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131361802, "field 'tvTitle'");
    target.tvTitle = finder.castView(view, 2131361802, "field 'tvTitle'");
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
    view = finder.findRequiredView(source, 2131361871, "field 'ivFlush' and method 'onClick'");
    target.ivFlush = finder.castView(view, 2131361871, "field 'ivFlush'");
    unbinder.view2131361871 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361872, "field 'mListView'");
    target.mListView = finder.castView(view, 2131361872, "field 'mListView'");
    view = finder.findRequiredView(source, 2131361873, "field 'btn1' and method 'onClick'");
    target.btn1 = finder.castView(view, 2131361873, "field 'btn1'");
    unbinder.view2131361873 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361874, "field 'btn2' and method 'onClick'");
    target.btn2 = finder.castView(view, 2131361874, "field 'btn2'");
    unbinder.view2131361874 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361870, "field 'llRoot'");
    target.llRoot = finder.castView(view, 2131361870, "field 'llRoot'");
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends UserInfoActivity> implements Unbinder {
    private T target;

    View view2131361803;

    View view2131361804;

    View view2131361871;

    View view2131361873;

    View view2131361874;

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
      view2131361803.setOnClickListener(null);
      target.ibBack = null;
      view2131361804.setOnClickListener(null);
      target.tvBack = null;
      view2131361871.setOnClickListener(null);
      target.ivFlush = null;
      target.mListView = null;
      view2131361873.setOnClickListener(null);
      target.btn1 = null;
      view2131361874.setOnClickListener(null);
      target.btn2 = null;
      target.llRoot = null;
    }
  }
}
