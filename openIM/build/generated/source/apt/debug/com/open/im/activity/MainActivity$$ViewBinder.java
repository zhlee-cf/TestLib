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

public class MainActivity$$ViewBinder<T extends MainActivity> implements ViewBinder<T> {
  @Override
  public Unbinder bind(final Finder finder, final T target, Object source) {
    InnerUnbinder unbinder = createUnbinder(target);
    View view;
    view = finder.findRequiredView(source, 2131361802, "field 'tvTitle'");
    target.tvTitle = finder.castView(view, 2131361802, "field 'tvTitle'");
    view = finder.findRequiredView(source, 2131361838, "field 'ivLoading'");
    target.ivLoading = finder.castView(view, 2131361838, "field 'ivLoading'");
    view = finder.findRequiredView(source, 2131361837, "field 'rlState'");
    target.rlState = finder.castView(view, 2131361837, "field 'rlState'");
    view = finder.findRequiredView(source, 2131361814, "field 'ivMinus' and method 'onClick'");
    target.ivMinus = finder.castView(view, 2131361814, "field 'ivMinus'");
    unbinder.view2131361814 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361821, "field 'ivAdd' and method 'onClick'");
    target.ivAdd = finder.castView(view, 2131361821, "field 'ivAdd'");
    unbinder.view2131361821 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361840, "field 'llNet'");
    target.llNet = finder.castView(view, 2131361840, "field 'llNet'");
    view = finder.findRequiredView(source, 2131361842, "field 'viewPager'");
    target.viewPager = finder.castView(view, 2131361842, "field 'viewPager'");
    view = finder.findRequiredView(source, 2131361843, "field 'ibNews' and method 'onClick'");
    target.ibNews = finder.castView(view, 2131361843, "field 'ibNews'");
    unbinder.view2131361843 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361844, "field 'ibContact' and method 'onClick'");
    target.ibContact = finder.castView(view, 2131361844, "field 'ibContact'");
    unbinder.view2131361844 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361845, "field 'ibSetting' and method 'onClick'");
    target.ibSetting = finder.castView(view, 2131361845, "field 'ibSetting'");
    unbinder.view2131361845 = view;
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

  protected static class InnerUnbinder<T extends MainActivity> implements Unbinder {
    private T target;

    View view2131361814;

    View view2131361821;

    View view2131361843;

    View view2131361844;

    View view2131361845;

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
      target.ivLoading = null;
      target.rlState = null;
      view2131361814.setOnClickListener(null);
      target.ivMinus = null;
      view2131361821.setOnClickListener(null);
      target.ivAdd = null;
      target.llNet = null;
      target.viewPager = null;
      view2131361843.setOnClickListener(null);
      target.ibNews = null;
      view2131361844.setOnClickListener(null);
      target.ibContact = null;
      view2131361845.setOnClickListener(null);
      target.ibSetting = null;
    }
  }
}
