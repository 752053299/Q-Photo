package com.example.qiaoguan.photo.Utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private static Toast toast;

    public static void showToast(Context context,
                                 String content) {
        if (toast == null) {

            toast = Toast.makeText(context.getApplicationContext(),
                    content,
                    Toast.LENGTH_SHORT);
            toast.setText(content);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    public static void showToastLong(Context context,
                                     String content){
        if (toast == null) {

            toast = Toast.makeText(context.getApplicationContext(),
                    content,
                    Toast.LENGTH_LONG);
            toast.setText(content);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
