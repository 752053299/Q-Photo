package com.example.qiaoguan.photo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.qiaoguan.photo.BaseView.SingleFragmentActivity;
import com.squareup.haha.perflib.Main;

import me.yokeyword.fragmentation.SupportFragment;

public class MainActivity extends SingleFragmentActivity<MainFragment> {

    @Override
    protected SupportFragment createFragment() {
        return MainFragment.newInstance();
    }

    @Override
    protected Class<MainFragment> finFragmentClass() {
        return MainFragment.class;
    }


}
