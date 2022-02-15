package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Settings extends AppCompatActivity {

    private static final String CHANNEL_ID = "888888";
    private static final String CHANNEL_NAME = "Message Notification Channel";
    private static final String CHANNEL_DESCRIPTION = "This channel is for displaying message";
    Button setTimeBtn;
    Switch timeSwitch;
    TimePicker timePicker;
    PendingIntent pendingIntent;

    int hour, min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        timeSwitch = findViewById(R.id.timeSwitch);
        SharedPreferences sp = getSharedPreferences("switchKey",MODE_PRIVATE);
        boolean isOn= sp.getBoolean("switchKey",false);
        timeSwitch.setChecked(isOn);

        setTimeBtn = findViewById(R.id.setTime);
        setTimeBtn.setEnabled(isOn);

        timePicker = findViewById(R.id.timePicker);
        timePicker.setEnabled(isOn);

        Intent alarmIntent= new Intent(Settings.this, AlertReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(Settings.this,0, alarmIntent, 0);

        initSwitchListener();

    }

    private void initSwitchListener(){
        timeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                if(isOn){
                    //Toast.makeText(getApplicationContext(),"notification turned on", Toast.LENGTH_SHORT).show();
                    timePicker.setEnabled(true);
                    setTimeBtn.setEnabled(true);

                    startAlarm();

                }
                else{
                    //Toast.makeText(getApplicationContext(),"notification turned off", Toast.LENGTH_SHORT).show();
                    timePicker.setEnabled(false);
                    setTimeBtn.setEnabled(false);

                    cancelAlarm();


                }
                SharedPreferences sp = getSharedPreferences("switchKey", MODE_PRIVATE);
                SharedPreferences.Editor switchEditor = sp.edit();
                switchEditor.putBoolean("switchKey",isOn);
                switchEditor.commit();
            }

        });


        setTimeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                hour = timePicker.getHour();
                min= timePicker.getMinute();

                String msg = "notification set at " + hour +":" +String.format("%02d",min) +" daily";
                Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();

                startAlarm();
            }
        });
    }


    private void startAlarm(){

        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
        alarmTime.set(Calendar.MINUTE,timePicker.getMinute());
        alarmTime.set(Calendar.SECOND,0);

        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmTime.before(Calendar.getInstance())) {
            alarmTime.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), alarmManager.INTERVAL_DAY , pendingIntent);
        System.out.println("Alarm set for notification");

        /*AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 8000;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this,"Alarm Set", Toast.LENGTH_SHORT).show();*/

    }

    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(getApplicationContext(),"notification turned off", Toast.LENGTH_SHORT).show();

        /*AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();*/


    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager= getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification(){
        Intent intent = new Intent(this, Settings.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.meal)
                .setContentTitle("Food Diary")
                .setContentText("Have you logged in your entry today?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        int notificationId=11111;
        NotificationManagerCompat mgr = NotificationManagerCompat.from(this);
        Notification notification = builder.build();
        mgr.notify(notificationId, notification);
    }

}