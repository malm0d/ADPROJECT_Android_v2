package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
        profileBtn.setOnClickListener(this);

        notificationBtn = findViewById(R.id.notificationPage);
        notificationBtn.setOnClickListener(this);

        logoutBtn = findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
                SharedPreferences prefSwitch = getSharedPreferences("switchKey", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                SharedPreferences.Editor editorSwitch = prefSwitch.edit();
                editor.clear();
                editor.commit();
                editorSwitch.clear();
                editorSwitch.commit();

                startLoginActivity();
            }
        });

        //bottom navigation bar
        bottomNavigation = findViewById(R.id.bottom_navigation);
        //set Setting selected
        bottomNavigation.setSelectedItemId(R.id.settingsMenu);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.mealMenu:
                        Intent pastMeal = new Intent(getApplicationContext(), PastMealsActivity.class);
                        startActivity(pastMeal);
                        break;
                    case R.id.pathMenu:
                        Intent currentPath = new Intent(getApplicationContext(), ViewGoalActivity.class);
                        startActivity(currentPath);
                        break;
                    case R.id.addMenu:
                        Intent add = new Intent(getApplicationContext(), CaptureActivity.class);
                        startActivity(add);
                        break;
                    case R.id.friendsMenu:
                        Intent friends = new Intent(getApplicationContext(), ManageSocialsActivity.class);
                        startActivity(friends);
                        break;
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
        if(v ==profileBtn){
            Intent intent = new Intent(this, UserProfile.class);
            startActivity(intent);
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(SettingPage.this, LoginActivity.class);
        startActivity(intent);
    }
}