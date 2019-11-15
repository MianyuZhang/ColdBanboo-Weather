package com.example.miany_u.coldbamboo.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by miany_u on 2019/11/12.
 */

public class Forecast {
    public String date;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt_d")
        public String info;
    }



    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature{
        public int max;
        public int min;
    }
}
