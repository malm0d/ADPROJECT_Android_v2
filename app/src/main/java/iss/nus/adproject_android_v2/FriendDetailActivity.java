package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.jar.JarException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FriendDetailActivity extends AppCompatActivity {

    ImageView mFriendProfilePic;
    TextView mFriendName;
    TextView mFriendUsername;
    Button mViewFoodBlogBtn;
    Button mDelFriendBtn;
    Button mBackToMSBtn;
    TextView mDelConfirmation;
    ConstraintLayout mDelPopup;
    LinearLayout mManageFriendsLL02;
    Button mCfmDelBtn;
    Button mRevertBtn;

    String username;
    UserHelper friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);

        Intent intent = getIntent();
        friend = (UserHelper) intent.getSerializableExtra("user");

        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        username = pref.getString("username", "");

        initUI();
    }

    private void initUI() {
        mFriendName = findViewById(R.id.friendDetailName);
        mFriendName.setText("Name: " + friend.getName());

        mFriendUsername = findViewById(R.id.friendDetailUsername);
        mFriendUsername.setText("Username: " + friend.getUsername());

        mFriendProfilePic = findViewById(R.id.friendProfilePic);
        String url = "http://192.168.1.8:8080/api/friends/profilePic";
        String queryString = "?fileName=";
        String fileName = friend.getProfilePic();
        String userId = friend.getUserId();

        Glide.with(this)
                .load(url + queryString + fileName + "&userId=" + userId)
                .placeholder(R.drawable.default_profile_picture)
                .into(mFriendProfilePic);

        mViewFoodBlogBtn = findViewById(R.id.viewFoodBlogBtn);
        // To link with view food blog
//        mViewFoodBlogBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//            }
//        });

        mBackToMSBtn = findViewById(R.id.backToMSBtn);
        mBackToMSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendDetailActivity.this, ManageFriendsActivity.class);
                startActivity(intent);
            }
        });

        mDelFriendBtn = findViewById(R.id.delFriendBtn);
        mDelFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManageFriendsLL02 = findViewById(R.id.manageFriendsLL02);
                mManageFriendsLL02.setVisibility(View.INVISIBLE);

                mDelPopup = findViewById(R.id.delPopup);
                mDelPopup.setVisibility(View.VISIBLE);

                mCfmDelBtn = findViewById(R.id.confirmDelBtn);
                mCfmDelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteFriend();
                    }
                });

                mRevertBtn = findViewById(R.id.revertBtn);
                mRevertBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDelPopup.setVisibility(View.INVISIBLE);
                        mManageFriendsLL02.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void deleteFriend() {
        String url = "http://192.168.1.8:8080/api/friends/delete";
        String friend_username = friend.getUsername();
        requestPost(url, username, friend_username);
    }

    private void requestPost(String url, String username, String friend_username) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("username", username);
        formBuilder.add("friend_username", friend_username);

        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "server error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String res = response.body().string();
                    System.out.println("Information from server:");
                    System.out.println(res);

                    try {
                        JSONObject jObj = new JSONObject(res);
                        if (jObj.getString("status").equals("OK")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mDelPopup.setVisibility(View.INVISIBLE);
                                    mDelConfirmation = findViewById(R.id.delConfirmation);
                                    mDelConfirmation.setText("You are not longer friends with: " + friend.getName());
                                    mDelConfirmation.setTextColor(Color.GREEN);
                                    mDelConfirmation.setVisibility(View.VISIBLE);

                                    mManageFriendsLL02.setVisibility(View.VISIBLE);
                                    mViewFoodBlogBtn.setVisibility(View.INVISIBLE);
                                    mDelFriendBtn.setVisibility(View.INVISIBLE);
                                    mBackToMSBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}