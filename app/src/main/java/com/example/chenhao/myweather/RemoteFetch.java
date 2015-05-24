package com.example.chenhao.myweather; /**
 * Created by chenhao on 15/5/9.
 */

import android.content.Context;

import com.example.chenhao.myweather.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class RemoteFetch {

    private static final String OPEN_WEATHER_MAP_API=
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

    //新建与openweatherAPI的远程连接，获取相应的JSON数据
    public static JSONObject getJSON(Context context,String city)
    {
        try
        {

            URL url=new URL(String.format(OPEN_WEATHER_MAP_API,city));//补全完整的API，调用format函数，将请求URL中的city参数补全

            HttpURLConnection connection=(HttpURLConnection)url.openConnection();//Returns a new connection to the resource referred to by this URL.
            //Adds the given property to the request header.

            //Adds the given property to the request header.
            //参数为相应键值对
            connection.addRequestProperty("x-api-key",context.getString(R.string.open_weather_maps_app_id));

            //建立数据输入缓存流
            BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));

            //新建可变长StringBuffer对象
            StringBuffer json=new StringBuffer(1024);

            String tmp="";

            //循环读入数据
            while((tmp= reader.readLine())!=null)
            {
                json.append(tmp).append("\n");
            }

            reader.close();

            //将String转换为JSON对象
            JSONObject data=new JSONObject(json.toString());

            //如果调用API读取数据失败
            if(data.getInt("cod")!=200)
            {
                return null;
            }

            return data;

        } catch (Exception e)
        {
           return null;
        }

    }


}
