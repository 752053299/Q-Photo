package com.example.qiaoguan.photo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qiaoguan.photo.BaseView.BaseActivity;
import com.example.qiaoguan.photo.Executor.AppExecutors;

public class TestActivity extends BaseActivity {
    private static final String TAG = "TestActivity";
    private ImageView imageView;
    private Button button;
    private final String test = "https://img.xcitefun.net/users/2012/11/309561,xcitefun-ileana-wallpaper-6.jpg";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        imageView = findViewById(R.id.image_test);
        button = findViewById(R.id.image_test_bt);
        loadImage();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void loadImage(){
        AppExecutors.getInstance().singleThread().execute(()->{
            PhotoDataManger.getINSTANCE().getImageBitmap(test, new PhotoDataManger.GetImageCallback() {
                @Override
                public void onImageLoad(Drawable drawable) {
                    runOnUiThread(()->{
                        imageView.setImageDrawable(drawable);
                    });
                }

                @Override
                public void onError(String msg) {
                    Log.e(TAG, "onError: " + msg );
                }
            });
        });
    }
}
