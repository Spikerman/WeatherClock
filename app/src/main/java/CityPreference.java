import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by chenhao on 15/5/10.
 */
public class CityPreference
{
    //SharedPreferences是Android平台上一个轻量级的存储类，用来保存应用的一些常用配置。
    SharedPreferences prefs;

    public CityPreference(Activity activity)
    {
        prefs=activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getCity()
    {
        return prefs.getString("city","Sydney,AU");
    }

    void setCity(String city)
    {
        prefs.edit().putString("city",city).commit();
    }


}
