package com.example.miany_u.coldbamboo.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by miany_u on 2019/11/12.
 */

public class Now {

    @SerializedName("tmp")
    public int temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
