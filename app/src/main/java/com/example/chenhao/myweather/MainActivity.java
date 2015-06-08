package com.example.chenhao.myweather;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.TimeZone;


public class MainActivity extends FragmentActivity implements FragmentManager.OnBackStackChangedListener {


    private TimePickerDialog timePickerDialog=null;
    private TextView tv=null;

    private Handler mHandler =new Handler();

    private boolean mShowingBack=false;

    private String[] strs=new String[]{"one","two","three","four","five"};
    private ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);



        tv=new TextView(this);

        RelativeLayout.LayoutParams lp_tv=new RelativeLayout.LayoutParams(-2,-2);

        lp_tv.addRule(RelativeLayout.CENTER_IN_PARENT);
        //tv.setLayoutParams(lp_tv);
        //this.addContentView(tv,lp_tv);



        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new WeatherFragment()).commit();
        }else{
            mShowingBack=(getFragmentManager().getBackStackEntryCount()>0);
        }


        getFragmentManager().addOnBackStackChangedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //getMenuInflater().inflate(R.menu.menu_weather, menu);

        MenuItem item=menu.add(Menu.NONE,R.id.action_flip,Menu.NONE,
                mShowingBack
                        ?R.string.action_clock
                        :R.string.action_weather);
       //set icon!!!!!
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        //return super.onOptionsItemSelected(item);
//        if(item.getItemId() == R.id.change_city){
//            showInputDialog();
//        }
//        return false;
        switch(item.getItemId()){
            case android.R.id.home:

                NavUtils.navigateUpTo(this,new Intent(this,MainActivity.class));
                return true;

            case R.id.action_flip:
                flipCard();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void showInputDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setTitle("Change city");
        final EditText input=new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeCity(input.getText().toString());
            }
        });
        builder.show();
    }

    public void changeCity(String city){
        WeatherFragment wf=(WeatherFragment)getSupportFragmentManager().findFragmentById(R.id.container);

        wf.changeCity(city);
        new CityPreference(this).setCity(city);
    }

    //一个呈现在卡片前方的fragment
    public class CardFrontFragment extends android.app.Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater,ViewGroup container,
                                 Bundle savedInstanceState){
            return inflater.inflate(R.layout.fragment_weather,container,false);
        }
    }

    private ArrayList<HashMap<String,Object>> listItem=new ArrayList<HashMap<String,Object>>();
    private SimpleAdapter simpleAdapter=null;
    //一个呈现在卡片背面的fragment
    public class CardBackFragment extends android.app.Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
        {
            View rootView=inflater.inflate(R.layout.fragment_clock, container, false);

            lv=(ListView)rootView.findViewById(R.id.clockList);

            //MyAdapter myAdapter=new MyAdapter(getBaseContext());
            //lv.setAdapter(myAdapter);


                HashMap<String,Object> map=new HashMap<String,Object>();
                map.put("ItemTitle","9:00");
                map.put("ItemText"," Clock");
                //map.put("ItemText","the text is "+i);
                listItem.add(map);

            simpleAdapter=new SimpleAdapter(getBaseContext(),listItem,R.layout.item,
                    new String[]{"ItemTitle","ItemText","ItemSwitch"},new int[]{R.id.ItemTitle,R.id.ItemText,R.id.ItemSwitch});
            lv.setAdapter(simpleAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    Log.v("MyListViewBase", "你点击了ListView条目" + arg2);//在LogCat中输出信息
                }
            });
            return rootView;
        }
    }



    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return getDate().size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            Log.v("MyListViewBase", "getView " + position + " " + convertView);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item, null);
                holder = new ViewHolder();

                holder.title = (TextView) convertView.findViewById(R.id.ItemTitle);
                holder.text = (TextView) convertView.findViewById(R.id.ItemText);
                holder.sw = (Switch) convertView.findViewById(R.id.ItemSwitch);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.title.setText(getDate().get(position).get("ItemTitle").toString());
                holder.text.setText(getDate().get(position).get("ItemText").toString());

                holder.sw.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Log.v("MyListViewBase", "you choose" + position);

                            }
                        }
                );

            }
            return convertView;
        }


        public final class ViewHolder {
            public TextView title;
            public TextView text;
            public Switch sw;
        }
    }
    private ArrayList<HashMap<String,Object>> getDate(){
        ArrayList<HashMap<String,Object>> listItem=new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<3;i++){
            HashMap<String,Object> map=new HashMap<String, Object>();
            map.put("ItemTitle","No."+i);
            map.put("ItemText","This is  No."+i);
            listItem.add(map);
        }
        return listItem;
    }

    private void flipCard(){
        if(mShowingBack){
            getFragmentManager().popBackStack();
            return;
        }

        mShowingBack=true;

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out)
                .replace(R.id.container,new CardBackFragment())
                .addToBackStack(null)
                .commit();

        mHandler.post(new Runnable(){
            @Override
        public void run(){
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onBackStackChanged(){
        mShowingBack=(getFragmentManager().getBackStackEntryCount()>0);

        invalidateOptionsMenu();
    }

    //按钮的相应事件
    public void Change(View view)
    {
        showInputDialog();
    }


    public void PickTime(View view)
    {
        if(timePickerDialog==null){
            timePickerDialogInit();
        }
        timePickerDialog.show();


    }

    void timePickerDialogInit()
    {
        TimePickerDialog.OnTimeSetListener otsl=new TimePickerDialog.OnTimeSetListener(){
            public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                //tv.setText("you set time: "+hourOfDay+"hour"+minute+"minute");
                String hour;
                String min;
                if(hourOfDay<10)
                    hour="0"+hourOfDay;
                else
                    hour=String.valueOf(hourOfDay);

                if(minute<10)
                    min="0"+minute;
                else
                    min=String.valueOf(minute);

                HashMap<String,Object>map=new HashMap<String,Object>();
                map.put("ItemTitle",hour+":"+min);
                map.put("ItemText"," Clock");
                listItem.add(map);
                simpleAdapter=new SimpleAdapter(getBaseContext(),listItem,R.layout.item,
                        new String[]{"ItemTitle","ItemText","ItemSwitch"},new int[]{R.id.ItemTitle,R.id.ItemText,R.id.ItemSwitch});
                lv.setAdapter(simpleAdapter);
                timePickerDialog.dismiss();
            }
        };

        Calendar calendar=Calendar.getInstance(TimeZone.getDefault());

        int hourOfDay=calendar.get(Calendar.HOUR_OF_DAY);

        int minute=calendar.get(Calendar.MINUTE);

        timePickerDialog=new TimePickerDialog(this,otsl,hourOfDay,minute,true);
    }


}
