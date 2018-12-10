package com.example.qiaoguan.photo;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;

import static android.os.Process.killProcess;

public class MyApplication extends Application {
    private Fragmentation fragmentation;
    private RefWatcher refWatcher;
    @Override
    public void onCreate() {
        super.onCreate();

        //fragmentation
        fragmentation = Fragmentation.builder()
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(true)
                .install();
        registerCrashHandler();

        //leakCanary
        refWatcher = setupLeakCanary();

    }

    //fragmentation
    private void registerCrashHandler() {
        fragmentation.setHandler(new ExceptionHandler() {
            @Override
            public void onException(Exception e) {
                //BaseActivity.destroyAllOnExit();
                killProcess(android.os.Process.myPid());
            }
        });
    }

    public static RefWatcher getRefWatcher(Context context){
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher setupLeakCanary(){
        if (LeakCanary.isInAnalyzerProcess(this)){
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

}
