package com.example.chenhao.myweather;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chenhao.myweather.CityPreference;
import com.example.chenhao.myweather.R;
import com.example.chenhao.myweather.RemoteFetch;

import org.json.JSONArray;
import org.json.JSONObject;


import android.os.Handler;//Java util 内建Handler属于抽象类，不能用于Android Handler的创建

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by chenhao on 15/5/10.
 */
public class WeatherFragment extends android.support.v4.app.Fragment
{
    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;

    Handler handler;

    public WeatherFragment()
    {
       handler=new Handler();
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_weather, container,false);

        //Finds a view that was identified by the id attribute from the XML that was processed in onCreate(Bundle).
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);

        weatherIcon.setTypeface(weatherFont);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //getActivity():Return the Activity this fragment is currently associated with.
        weatherFont =Typeface.createFromAsset(getActivity().getAssets(),"weather.ttf");

        updateWeatherData(new CityPreference(getActivity()).getCity());
    }

    private void updateWeatherData(final String city) {
        new Thread()
        {
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(getActivity(), city);
                if (json == null) {
                    //Causes the Runnable r to be added to the message queue.
                    //The runnable will be run on the thread to which this handler is attached.
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.place_not_found), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json)
    {
        try
        {

            String x=json.getString("name").toUpperCase(Locale.US);
            String y=json.getJSONObject("sys").getString("country");

            cityField.setText(x.toUpperCase(Locale.US)+ ", "+y);


            JSONObject details=json.getJSONArray("weather").getJSONObject(0);//通过index获取json数组中的指定json对象
            JSONObject main=json.getJSONObject("main");

            String a=details.getString("description").toUpperCase(Locale.US)+
                    "\n"+"Humidity: "+main.getString("humidity")+"%"+
                    "\n"+"Pressure: "+main.getString("pressure")+" hPa";


            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US)+
                            "\n"+"Humidity: "+main.getString("humidity")+"%"+
                            "\n"+"Pressure: "+main.getString("pressure")+" hPa");

           String b=String.format("%.2f",main.getDouble("temp"))+" c";


            currentTemperatureField.setText(
                    String.format("%.2f",main.getDouble("temp"))+" c"
            );

            DateFormat df= DateFormat.getDateTimeInstance();
            String updateOn=df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: "+updateOn);

            setWeatherIcon(details.getInt("id"),json.getJSONObject("sys").getLong("sunrise")*1000,
                    json.getJSONObject("sys").getLong("sunset")*1000);

        }catch(Exception e){
            Log.e("SimpleWeather","One or more field not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId,long sunrise,long sunset)
    {
        int id = actualId/100;
        String icon="";
        if(actualId==800)
        {
            long currentTime=new Date().getTime();
            if(currentTime>=sunrise&&currentTime<=sunset)
            {
                icon=getActivity().getString(R.string.weather_sunny);
            }else{
                icon=getActivity().getString(R.string.weather_clear_night);
            }

        }
        else{
            switch(id){
                case 2:icon=getActivity().getString(R.string.weather_thunder);
                    break;
                case 3:icon=getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7:icon=getActivity().getString(R.string.weather_foggy);
                    break;
                case 8:icon=getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6:icon=getActivity().getString(R.string.weather_snowy);
                    break;
                case 5:icon=getActivity().getString(R.string.weather_rainy);
            }
        }
        weatherIcon.setText(icon);
    }

    public void changeCity(String city){
        updateWeatherData(city);
    }



}
