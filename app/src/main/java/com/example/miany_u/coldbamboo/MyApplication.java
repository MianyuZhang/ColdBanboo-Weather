package com.example.miany_u.coldbamboo;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * Created by miany_u on 2019/11/9.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        context=getApplicationContext();
        LitePal.initialize(context);
    }

    public static Context getContext(){
        return context;
    }
}
