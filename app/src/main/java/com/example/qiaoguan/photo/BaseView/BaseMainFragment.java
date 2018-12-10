package com.example.qiaoguan.photo.BaseView;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.example.qiaoguan.photo.MyApplication;
import com.example.qiaoguan.photo.R;
import com.example.qiaoguan.photo.Utils.ToastUtil;
import com.squareup.leakcanary.RefWatcher;


import java.lang.reflect.Field;

import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportFragment;

public class BaseMainFragment extends SupportFragment {
    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;

    protected Unbinder unbinder;

    @Override
    public boolean onBackPressedSupport() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME){
            _mActivity.finish();
        }else {
            TOUCH_TIME = System.currentTimeMillis();
            ToastUtil.showToast(_mActivity,"再按一次退出");
        }
        return true;
    }

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


    /**
     * 修正 Toolbar 的位置
     * 在 Android 4.4 版本下无法显示内容在 StatusBar 下，所以无需修正 Toolbar 的位置
     *
     * @param toolbar
     */
    protected void fixToolbar(Toolbar toolbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusHeight = getStatusBarHeight(getActivity());
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            layoutParams.setMargins(0, statusHeight, 0, 0);
        }
    }

    /**
     * 获取系统状态栏高度
     *
     * @param context
     * @return
     */
    public int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    protected void postDelayedRun(Runnable runnable,long delayMillis) {
        Handler handler = new Handler();
        handler.postDelayed(runnable,delayMillis);
    }
}
