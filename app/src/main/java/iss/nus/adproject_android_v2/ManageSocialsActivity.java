package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ManageSocialsActivity extends AppCompatActivity {

    Button mManageFriendsBtn;
    Button mAddFriendBtn;
    Button mFriendRequestsBtn;
    Button mFeedBtn;

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

        // To link up with FeedActivity
//        mFeedBtn = findViewById(R.id.feedBtn);
//        mFeedBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }
}