import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chenhao.myweather.R;

import org.json.JSONObject;

import android.os.Handler;//Java util 内建Handler属于抽象类，不能用于Android Handler的创建

/**
 * Created by chenhao on 15/5/10.
 */
public class WeatherFragment extends Fragment
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


    public View OnCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_weather, container,false);

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
        weatherFont =Typeface.createFromAsset(getActivity().getAssets(),"fonts/weather.ttf");
        //updateWeatherData-->unfinished yet
    }

    private void updateWeatherData(final String city)
    {
        new Thread()
        {
            public void run()
            {
                final JSONObject json= RemoteFetch.getJSON(getActivity(), city);
                if(json==null)
                {
                    //Causes the Runnable r to be added to the message queue.
                    //The runnable will be run on the thread to which this handler is attached.
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),getActivity().getString(R.string.place_not_found),Toast.LENGTH_LONG).show();
                        }

                    });
                }else{
                    handler.post(new Runnable(){
                        public void run(){
                            //renderWeather(json);unfinished yet
                        }
                    });
                }

            }
        }.start();
    }


}
