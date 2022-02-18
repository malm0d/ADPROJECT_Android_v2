package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FriendRequestsActivity extends AppCompatActivity {

    Button mSentReqBtn;
    Button mReceivedReqBtn;
    TextView mFriendReqListHeader;
    ListView mReqList;
    NavigationBarView bottomNavigation;
    Dialog confirmDel_dialog;
    Dialog addFriend_dialog;

    private String username;
    private String friend_username;
    private ArrayList<UserHelper> requests;
    private String sent;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        username = pref.getString("username", "");

        initUI();

        String url_getReq = getResources().getString(R.string.IP) + "/api/friends/requests";

        mSentReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSentReqBtn.setBackgroundColor(Color.rgb(0x00, 0x66, 0xff));
                mReceivedReqBtn.setBackgroundColor(Color.rgb(0x99, 0x99, 0x99));
                sent = "true";
                getRequests(url_getReq, username, sent);
            }
        });

        mReceivedReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReceivedReqBtn.setBackgroundColor(Color.rgb(0x00, 0x66, 0xff));
                mSentReqBtn.setBackgroundColor(Color.rgb(0x99, 0x99, 0x99));
                sent = "false";
                getRequests(url_getReq, username, sent);
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
                        //link to path
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

    private void initUI() {
        mSentReqBtn = findViewById(R.id.sentRequestsBtn);
        mReceivedReqBtn = findViewById(R.id.receivedRequestsBtn);
        mFriendReqListHeader = findViewById(R.id.friendRequestListHeader);
        mReqList = findViewById(R.id.requestsList);
        confirmDel_dialog = new Dialog(this);
        addFriend_dialog = new Dialog(this);
    }

    private void initSentReqListView(ArrayList<UserHelper> requests) {
        mFriendReqListHeader.setText("   Sent Requests");
        if (mReqList != null) {
            mReqList.setAdapter(new SentReqAdapter(this, requests));
            mReqList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                    UserHelper user = requests.get(pos);
                    openConfirmDialog(user);
                }
            });
        }
    }

    private void initReceivedReqListView(ArrayList<UserHelper> requests) {
        mFriendReqListHeader.setText("   Received Requests");
        if (mReqList != null) {
            mReqList.setAdapter(new ReceivedReqAdapter(this, requests));
            mReqList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                    UserHelper user = requests.get(pos);
                    openAddFriendDialog(user);
                }
            });
        }
    }

    private void getRequests(String url, String username, String sent) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder.addQueryParameter("username", username);
        httpBuilder.addQueryParameter("sent", sent);

        HttpUrl httpUrl = httpBuilder.build();

        Request request = new Request.Builder().url(httpUrl).get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String res = response.body().string();
                System.out.println("Information from server:");
                System.out.println(res);

                try {
                    JSONArray jArray = new JSONArray(res);
                    String data = jArray.toString();
//
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JSR310Module());
                    requests = mapper.readValue(data, new TypeReference<ArrayList<UserHelper>>() {});

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (sent.equals("true")) {
                                initSentReqListView(requests);
                            } else {
                                initReceivedReqListView(requests);
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void processFriendRequest(String friend_username) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();

        String url_processReq = getResources().getString(R.string.IP) + "/api/friends/request/process";

        formBuilder.add("username", username);
        formBuilder.add("sender", friend_username);
        formBuilder.add("action", action);

        Request request = new Request.Builder().url(url_processReq).post(formBuilder.build()).build();
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
                    System.out.println("Information from server: ");
                    System.out.println(res);

                    if (!res.isEmpty()) {
                        try {
                            JSONObject jObj = new JSONObject(res);
                            String message = jObj.getString("message");

                            if (message.equals("You are now friends with " + friend_username)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                        mReceivedReqBtn.performClick();
                                    }
                                });
                            } else if (message.equals("Rejected friend request from " + friend_username)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                        mReceivedReqBtn.performClick();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                        mSentReqBtn.performClick();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void openConfirmDialog(UserHelper user) {
        confirmDel_dialog.setContentView(R.layout.confirm_delete_dialog);
        confirmDel_dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));

        Button mConfirmDelReqBtn = confirmDel_dialog.findViewById(R.id.confirmDelBtn);
        mConfirmDelReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action = "delete";
                friend_username = user.getUsername();
                processFriendRequest(friend_username);
                confirmDel_dialog.dismiss();
            }
        });

        Button mRevertReqBtn = confirmDel_dialog.findViewById(R.id.revertBtn);
        mRevertReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDel_dialog.dismiss();
            }
        });

        confirmDel_dialog.show();
    }

    private void openAddFriendDialog(UserHelper user) {
        addFriend_dialog.setContentView(R.layout.add_friend_dialog);
        addFriend_dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));

        TextView mProcessReqTxt = addFriend_dialog.findViewById(R.id.processReqTxt02);
        mProcessReqTxt.setText("From: " + user.getName());

        Button mAcceptReqBtn = addFriend_dialog.findViewById(R.id.acceptReqBtn);
        mAcceptReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action = "accept";
                friend_username = user.getUsername();
                processFriendRequest(friend_username);
                addFriend_dialog.dismiss();
            }
        });

        Button mRejectReqBtn = addFriend_dialog.findViewById(R.id.rejectReqBtn);
        mRejectReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action = "reject";
                friend_username = user.getUsername();
                processFriendRequest(friend_username);
                addFriend_dialog.dismiss();
            }
        });

        Button mCancelReqBtn = addFriend_dialog.findViewById(R.id.cancelReqBtn);
        mCancelReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             addFriend_dialog.dismiss();
            }
        });

        addFriend_dialog.show();

    }
}