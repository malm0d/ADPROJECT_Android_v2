package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingPage extends AppCompatActivity implements View.OnClickListener {

    private Button profileBtn;
    private Button notificationBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        profileBtn = findViewById(R.id.editProfile);
       // profileBtn.setOnClickListener(this);

        notificationBtn = findViewById(R.id.notificationPage);
        notificationBtn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){


        if (v == notificationBtn) {
            Intent intent = new Intent(this, Notification.class);
            startActivity(intent);
        }


    }
}