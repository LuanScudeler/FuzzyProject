package com.example.luan.fuzzy;

import android.app.Application;
import android.content.Context;

/**
 * Created by Luan on 04/04/2015.
 */
public class MyApplication extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
