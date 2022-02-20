package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationBarView;

public class ManageSocialsActivity extends AppCompatActivity {

    Button mManageFriendsBtn;
    Button mAddFriendBtn;
    Button mFriendRequestsBtn;
    Button mFeedBtn;
    NavigationBarView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_socials);

        mManageFriendsBtn = findViewById(R.id.manageFriendsBtn);
        mManageFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ManageSocialsActivity.this, ManageFriendsActivity.class);
                startActivity(intent1);
            }
        });

        mAddFriendBtn = findViewById(R.id.addFriendBtn);
        mAddFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(ManageSocialsActivity.this, AddFriendActivity.class);
                startActivity(intent2);
            }
        });

        mFriendRequestsBtn = findViewById(R.id.friendRequestsBtn);
        mFriendRequestsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(ManageSocialsActivity.this, FriendRequestsActivity.class);
                startActivity(intent3);
            }
        });

  //       To link up with FeedActivity
        mFeedBtn = findViewById(R.id.feedBtn);
        mFeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent4 = new Intent(ManageSocialsActivity.this,ViewFeedActivity.class);
                startActivity(intent4);

            }
        });

        //bottom navigation bar
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.friendsMenu);
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
}