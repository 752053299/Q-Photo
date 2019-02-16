
package com.example.qiaoguan.photo.Executor;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    private static final int THREAD_COUNT = 3;

    private Executor singleThread;

    private Executor ThreeThread;

    private Executor mainThread;

    @VisibleForTesting
    AppExecutors(Executor singleThread, Executor ThreeThread, Executor mainThread) {
        this.singleThread = singleThread;
        this.ThreeThread = ThreeThread;
        this.mainThread = mainThread;
    }

    private AppExecutors() {
        this(new DiskIOThreadExecutor(), Executors.newFixedThreadPool(THREAD_COUNT),
                new MainThreadExecutor());
    }

    private static class AppExecutorHolder{
        private static final AppExecutors INSTANCE = new AppExecutors();
    }

    public static AppExecutors getInstance(){
        return AppExecutorHolder.INSTANCE;
    }

    public Executor singleThread() {
        return singleThread;
    }

    public Executor ThreeThread() {
        return ThreeThread;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    public void changeNetIOThreadCount(int count){
        ThreeThread = Executors.newFixedThreadPool(count);
    }


    public interface NormalCallback<T>{
        void onFinish(T backData);
    }
}
