package com.example.miany_u.coldbamboo.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by miany_u on 2019/11/12.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;


    }
}
