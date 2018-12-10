package com.example.qiaoguan.photo.BaseView;

import android.os.Bundle;
import android.support.annotation.Nullable;


import com.example.qiaoguan.photo.R;

import me.yokeyword.fragmentation.SupportFragment;

public abstract class SingleFragmentActivity<T extends SupportFragment> extends BaseActivity {

    protected abstract SupportFragment createFragment();

    protected abstract Class<T> finFragmentClass();

    protected int getLayoutResId() {
        return R.layout.activity_fragment_layout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        SupportFragment fragment = findFragment(finFragmentClass());
        if (fragment == null){
            loadRootFragment(R.id.fl_container,createFragment());
        }
    }
}
