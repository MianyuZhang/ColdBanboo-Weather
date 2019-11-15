package com.example.miany_u.coldbamboo.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 封装一个发送网络请求的方法
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }


}
