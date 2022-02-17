package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationBarView;

public class SettingPage extends AppCompatActivity implements View.OnClickListener {

    private Button profileBtn;
    private Button notificationBtn;
    private Button logoutBtn;
    NavigationBarView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        profileBtn = findViewById(R.id.editProfile);
       // profileBtn.setOnClickListener(this);

        notificationBtn = findViewById(R.id.notificationPage);
        notificationBtn.setOnClickListener(this);

        logoutBtn = findViewById(R.id.logout);
        // profileBtn.setOnClickListener(this);

        //bottom navigation bar
        bottomNavigation = findViewById(R.id.bottom_navigation);
        //set Add selected
        bottomNavigation.setSelectedItemId(R.id.settingsMenu);

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.mealMenu: break;
                    case R.id.pathMenu: break;
                    case R.id.addMenu:
                        Intent add = new Intent(getApplicationContext(), CaptureActivity.class);
                        startActivity(add);
                        break;
                    case R.id.friendsMenu: break;
                    case R.id.settingsMenu:
                        Intent settings = new Intent(getApplicationContext(), SettingPage.class);
                        startActivity(settings);
                        break;
                }
                return false;
            }
        });
    }
    @Override
    public void onClick(View v){


        if (v == notificationBtn) {
            Intent intent = new Intent(this, Notification.class);
            startActivity(intent);
        }


    }
}