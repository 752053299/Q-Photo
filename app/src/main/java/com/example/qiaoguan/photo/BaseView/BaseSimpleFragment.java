package com.example.qiaoguan.photo.BaseView;

import com.example.qiaoguan.photo.MyApplication;
import com.squareup.leakcanary.RefWatcher;

import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

public class BaseSimpleFragment extends SupportFragment {

    protected Unbinder unbinder;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null){
            unbinder.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getContext() != null){
            RefWatcher watcher = MyApplication.getRefWatcher(getContext());
            watcher.watch(this);
        }
    }
}
