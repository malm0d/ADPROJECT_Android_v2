package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

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
    ConstraintLayout mDelReqPopup;
    ConstraintLayout mProcessReqPopup;

    private String username;
    private String friend_username;
    private ArrayList<UserHelper> requests;
    private String url_getReq = "http://192.168.1.8:8080/api/friends/requests";
    private String url_processReq = "http://192.168.1.8:8080/api/friends/request/process";
    private String sent;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        username = pref.getString("username", "");

        initUI();

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

    }

    private void initUI() {
        mSentReqBtn = findViewById(R.id.sentRequestsBtn);
        mReceivedReqBtn = findViewById(R.id.receivedRequestsBtn);
        mFriendReqListHeader = findViewById(R.id.friendRequestListHeader);
        mReqList = findViewById(R.id.requestsList);
    }

    private void initSentReqListView(ArrayList<UserHelper> requests) {
        mFriendReqListHeader.setText("   Sent Requests");
        if (mReqList != null) {
            mReqList.setAdapter(new SentReqAdapter(this, requests));
            mReqList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                    mDelReqPopup = findViewById(R.id.delReqPopup);
                    mDelReqPopup.setVisibility(View.VISIBLE);
                    mReqList.setEnabled(false);

                    Button mConfirmDelReqBtn = findViewById(R.id.confirmDelReqBtn);
                    mConfirmDelReqBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            action = "delete";
                            friend_username = requests.get(pos).getUsername();
                            processFriendRequest(friend_username);
                        }
                    });

                    Button mRevertReqBtn = findViewById(R.id.revertReqBtn);
                    mRevertReqBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDelReqPopup.setVisibility(View.INVISIBLE);
                            mReqList.setEnabled(true);
                        }
                    });
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
                    mProcessReqPopup = findViewById(R.id.processReqPopup);
                    mProcessReqPopup.setVisibility(View.VISIBLE);
                    mReqList.setEnabled(false);
                    TextView mProcessReqTxt = findViewById(R.id.processReqTxt02);
                    mProcessReqTxt.setText("From: " + requests.get(pos).getName());

                    Button mAcceptReqBtn = findViewById(R.id.acceptReqBtn);
                    mAcceptReqBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            action = "accept";
                            friend_username = requests.get(pos).getUsername();
                            processFriendRequest(friend_username);
                        }
                    });

                    Button mRejectReqBtn = findViewById(R.id.rejectReqBtn);
                    mRejectReqBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            action = "reject";
                            friend_username = requests.get(pos).getUsername();
                            processFriendRequest(friend_username);
                        }
                    });

                    Button mCancelReqBtn = findViewById(R.id.cancelReqBtn);
                    mCancelReqBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mProcessReqPopup.setVisibility(View.INVISIBLE);
                            mReqList.setEnabled(true);
                        }
                    });
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
                                        mProcessReqPopup.setVisibility(View.INVISIBLE);
                                        mReqList.setEnabled(true);
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                        mReceivedReqBtn.performClick();
                                    }
                                });
                            } else if (message.equals("Rejected friend request from" + friend_username)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProcessReqPopup.setVisibility(View.INVISIBLE);
                                        mReqList.setEnabled(true);
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                        mReceivedReqBtn.performClick();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDelReqPopup.setVisibility(View.INVISIBLE);
                                        mReqList.setEnabled(true);
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
}