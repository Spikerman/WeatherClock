package com.example.chenhao.myweather;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
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

   //一个呈现在卡片背面的fragment
    public class CardBackFragment extends android.app.Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
        {
            View rootView=inflater.inflate(R.layout.fragment_clock, container, false);
            lv=(ListView)rootView.findViewById(R.id.clockList);
            lv.setAdapter(new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_checked,strs));
            return rootView;
        }
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
                tv.setText("you set time: "+hourOfDay+"hour"+minute+"minute");
                timePickerDialog.dismiss();
            }
        };

        Calendar calendar=Calendar.getInstance(TimeZone.getDefault());

        int hourOfDay=calendar.get(Calendar.HOUR_OF_DAY);

        int minute=calendar.get(Calendar.MINUTE);

        timePickerDialog=new TimePickerDialog(this,otsl,hourOfDay,minute,true);
    }


}
