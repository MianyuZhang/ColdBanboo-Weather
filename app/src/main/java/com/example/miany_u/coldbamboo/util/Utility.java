package com.example.miany_u.coldbamboo.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.miany_u.coldbamboo.db.City;
import com.example.miany_u.coldbamboo.db.County;
import com.example.miany_u.coldbamboo.db.Province;
import com.example.miany_u.coldbamboo.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miany_u on 2019/11/8.
 */

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) //判断传入的数据是否为空，如果为空就返回false
        {
            try {
                JSONArray allProvinces = new JSONArray(response);//将数据传入一个JSON数组中

                for (int i = 0; i <allProvinces.length(); i++)//遍历这个数组
                {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);//取出单条数据

                    Province province = new Province();//创建一个省级对象

                    province.setProvinceName(provinceObject.getString("name"));//将单条数据的name字段数据存入省级表ProvinceName中
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();

                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
        public static boolean handleCityResponse(String response,int provinceId){
            if(!TextUtils.isEmpty(response)){
                try {
                    JSONArray allCities=new JSONArray(response);
                    for(int i=0;i<allCities.length();i++){
                        JSONObject cityObject=allCities.getJSONObject(i);
                        City city=new City();
                        city.setCityName(cityObject.getString("name"));
                        city.setCityCode(cityObject.getInt("id"));
                        city.setProvinceId((provinceId));
                        city.save();
                    }return true;
                }catch (JSONException e)
                {
                    e.printStackTrace();}
            }return false;
        }

/**
 * 解析和处理服务器返回的县区级数据
 */
        public static boolean handleCountyResponse(String response,int cityId){
            if(!TextUtils.isEmpty(response)){
                try{
                JSONArray allCounty=new JSONArray(response);
                for(int i=0;i<allCounty.length();i++) {
                    JSONObject countyObject = allCounty.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();

                }return true;
                }catch (JSONException e){e.printStackTrace();}
            }
         return false;
        }

/**
 * 将返回的JSON数据解析成Weather实体类
 */
    public static Weather handleWeatherResponse(String response)
    {   try {


        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
        String weatherContent = jsonArray.getJSONObject(0).toString();
        Weather weather= new Gson().fromJson(weatherContent, Weather.class);
        return weather;

            }catch (JSONException e)
        {
            e.printStackTrace();
        }
        return  null;
    }


}