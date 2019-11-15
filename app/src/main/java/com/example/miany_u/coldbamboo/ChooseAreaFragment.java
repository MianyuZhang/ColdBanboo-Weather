package com.example.miany_u.coldbamboo;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miany_u.coldbamboo.db.City;
import com.example.miany_u.coldbamboo.db.County;
import com.example.miany_u.coldbamboo.db.Province;
import com.example.miany_u.coldbamboo.gson.Basic;
import com.example.miany_u.coldbamboo.util.HttpUtil;
import com.example.miany_u.coldbamboo.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 遍历省市县的数据
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    boolean result=false;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String>dataList=new ArrayList<>();

    //省列表
    private  List<Province> provinceList;

    //市列表
    private List<City> cityList;

    //县列表
    private List<County> countyList;

    //被选中的省份
    private Province selectedProvince;

    //被选择的市
    private City selectedCity;

    //当前选中的级别
    private int currentLevel;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);
         titleText=(TextView)view.findViewById(R.id.title_text);
        backButton=(Button)view.findViewById(R.id.back_button);
        listView=(ListView)view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(MyApplication.getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(currentLevel==LEVEL_PROVINCE)
                {
                    selectedProvince=provinceList.get(position);
                    queryCity();
                }
                else if(currentLevel==LEVEL_CITY)
                {
                    selectedCity=cityList.get(position);
                    queryCounty();
                }else if(currentLevel==LEVEL_COUNTY)
                {
                    String weatherId=countyList.get(position).getWeatherId();
                    Intent intent=new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    getActivity().finish();

                }
            }
        }                               );

        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(currentLevel==LEVEL_COUNTY)
                {
                         queryCity();
                }else if(currentLevel==LEVEL_CITY)
                    {
                        queryProvince();
                    }
            }
        }                             );




                        queryProvince();//一开始从这里加载省级列表
    }


    /**
     * 先从数据库中读取省级数据，如果数据库中没有，就从服务器中获取
     */
    private void queryProvince()
    {
        titleText.setText("中国");//页面处于省级列表时，将标题设为为“中国”

        backButton.setVisibility(View.GONE);//页面处于省级列表时，无法返回，将返回按钮隐藏

        provinceList= DataSupport.findAll(Province.class);//从省级表中读取数据

        if(provinceList.size()>0)//若读到了数据

        {
            dataList.clear();//先将dataList清空

            for(Province province:provinceList)//遍历从表中读取到的数据
            {
                dataList.add(province.getProvinceName());//将从数据中读取到的省份名称添加进dataList
            }
            adapter.notifyDataSetChanged();//刷新适配器

            listView.setSelection(0);//设置偏移量

            currentLevel=LEVEL_PROVINCE;//将当前列表等级设为省级

        }else//若没有读取到数据，则从服务器端获取
            {
                String address="http://guolin.tech/api/china";
                queryFromServer(address,"province");
            }

    }

    /**
     * 先从数据库中读取市级数据，如果数据库中没有，就从服务器中获取
      */

    private void queryCity()
    {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList=DataSupport.where("provinceId=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size()>0)
        {
           dataList.clear();
            for(City city:cityList)
            {
                dataList.add(city.getCityName());
            }
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                currentLevel=LEVEL_CITY;
        }else
                {
                    int provinceCode=selectedProvince.getProvinceCode();
                    String address="http://guolin.tech/api/china/"+provinceCode;
                    queryFromServer(address,"city");
                }
    }
    /**
     * 先从数据库中读取县区级数据，如果数据库中没有，就从服务器中获取
      */

    private void queryCounty()
    {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityId=?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()>0)
        {
            dataList.clear();
            for(County county:countyList)
            {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else
                {
                    int provinceCode=selectedProvince.getProvinceCode();
                    int cityCode=selectedCity.getCityCode();
                    String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
                    queryFromServer(address,"county");
                }
    }


    /**
     *根据传入的地址和类型从服务器上查询省市县数据
     */
    private void queryFromServer(String address, final String type)
    {

        HttpUtil.sendOkHttpRequest(address, new Callback()//调用sendOkHttpRequest()方法发送网络请求
        {
            @Override
            public void onFailure(Call call, IOException e)//若网络请求失败则回调onFailure()方法
            {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(MyApplication.getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException//网络请求成功则回调onResponse()方法
                    {
                        String responseText=response.body().string();//将返回的数据传入responseText中

                        if("province".equals(type))//若传入进来的查询类型是province，则调用handleProvinceResponse()来解析数据
                        {
                            result= Utility.handleProvinceResponse(responseText);
                        }
                        else if("city".equals(type))
                            {
                                result=Utility.handleCityResponse(responseText,selectedProvince.getId());
                            }
                        else if("county".equals(type))
                                    {
                                       result=Utility.handleCountyResponse(responseText,selectedCity.getId());
                                    }
                            if(result)//若数据解析成功，此处应该为true

                            {       //此处有UI更改，通过runOnUiThread()方法回到主线程处理逻辑
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run()
                                        {

                                            if("province".equals(type))
                                            {
                                                queryProvince();
                                            }else if("city".equals(type))
                                                {
                                                    queryCity();
                                                }else if("county".equals(type))
                                                    {
                                                        queryCounty();
                                                    }
                                        }
                                });
                            }
                     }
        }                           );
    }

}
