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

public class ChatActivity$$ViewBinder<T extends ChatActivity> implements ViewBinder<T> {
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
    view = finder.findRequiredView(source, 2131361814, "field 'ivMinus' and method 'onClick'");
    target.ivMinus = finder.castView(view, 2131361814, "field 'ivMinus'");
    unbinder.view2131361814 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361816, "field 'ivSay' and method 'onClick'");
    target.ivSay = finder.castView(view, 2131361816, "field 'ivSay'");
    unbinder.view2131361816 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361817, "field 'ivKeyboard' and method 'onClick'");
    target.ivKeyboard = finder.castView(view, 2131361817, "field 'ivKeyboard'");
    unbinder.view2131361817 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361818, "field 'etMsg' and method 'onClick'");
    target.etMsg = finder.castView(view, 2131361818, "field 'etMsg'");
    unbinder.view2131361818 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361819, "field 'tvSay'");
    target.tvSay = finder.castView(view, 2131361819, "field 'tvSay'");
    view = finder.findRequiredView(source, 2131361820, "field 'imageFace' and method 'onClick'");
    target.imageFace = finder.castView(view, 2131361820, "field 'imageFace'");
    unbinder.view2131361820 = view;
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
    view = finder.findRequiredView(source, 2131361822, "field 'tvSend' and method 'onClick'");
    target.tvSend = finder.castView(view, 2131361822, "field 'tvSend'");
    unbinder.view2131361822 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = finder.findRequiredView(source, 2131361823, "field 'gvMore'");
    target.gvMore = finder.castView(view, 2131361823, "field 'gvMore'");
    view = finder.findRequiredView(source, 2131361825, "field 'mListView'");
    target.mListView = finder.castView(view, 2131361825, "field 'mListView'");
    view = finder.findRequiredView(source, 2131361967, "field 'volume'");
    target.volume = finder.castView(view, 2131361967, "field 'volume'");
    view = finder.findRequiredView(source, 2131361968, "field 'img1'");
    target.img1 = finder.castView(view, 2131361968, "field 'img1'");
    view = finder.findRequiredView(source, 2131361969, "field 'delRe'");
    target.delRe = finder.castView(view, 2131361969, "field 'delRe'");
    view = finder.findRequiredView(source, 2131361965, "field 'voiceRcdHintRcding'");
    target.voiceRcdHintRcding = finder.castView(view, 2131361965, "field 'voiceRcdHintRcding'");
    view = finder.findRequiredView(source, 2131361971, "field 'voiceRcdHintLoading'");
    target.voiceRcdHintLoading = finder.castView(view, 2131361971, "field 'voiceRcdHintLoading'");
    view = finder.findRequiredView(source, 2131361973, "field 'voiceRcdHintTooshort'");
    target.voiceRcdHintTooshort = finder.castView(view, 2131361973, "field 'voiceRcdHintTooshort'");
    view = finder.findRequiredView(source, 2131361826, "field 'llRecordWindow'");
    target.llRecordWindow = finder.castView(view, 2131361826, "field 'llRecordWindow'");
    return unbinder;
  }

  protected InnerUnbinder<T> createUnbinder(T target) {
    return new InnerUnbinder(target);
  }

  protected static class InnerUnbinder<T extends ChatActivity> implements Unbinder {
    private T target;

    View view2131361803;

    View view2131361804;

    View view2131361814;

    View view2131361816;

    View view2131361817;

    View view2131361818;

    View view2131361820;

    View view2131361821;

    View view2131361822;

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
      view2131361814.setOnClickListener(null);
      target.ivMinus = null;
      view2131361816.setOnClickListener(null);
      target.ivSay = null;
      view2131361817.setOnClickListener(null);
      target.ivKeyboard = null;
      view2131361818.setOnClickListener(null);
      target.etMsg = null;
      target.tvSay = null;
      view2131361820.setOnClickListener(null);
      target.imageFace = null;
      view2131361821.setOnClickListener(null);
      target.ivAdd = null;
      view2131361822.setOnClickListener(null);
      target.tvSend = null;
      target.gvMore = null;
      target.mListView = null;
      target.volume = null;
      target.img1 = null;
      target.delRe = null;
      target.voiceRcdHintRcding = null;
      target.voiceRcdHintLoading = null;
      target.voiceRcdHintTooshort = null;
      target.llRecordWindow = null;
    }
  }
}
