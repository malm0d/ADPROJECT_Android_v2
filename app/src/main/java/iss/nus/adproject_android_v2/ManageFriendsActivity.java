package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ManageFriendsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ArrayList<UserHelper> friends;
    private String username;
    Button mSearchFriendBtn;
    Button mClearSearchBtn;
    EditText mFriendQuery;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);

        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        username = pref.getString("username", "");
        getFriends();


        mSearchFriendBtn = findViewById(R.id.searchFriendBtn);
        mSearchFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFriendQuery = findViewById(R.id.friendQuery);
                query = mFriendQuery.getText().toString();
                getQueryResult();
            }
        });

        mClearSearchBtn = findViewById(R.id.clearSearchBtn);
        mClearSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFriends();
            }
        });

    }

    protected void initListView(ArrayList<UserHelper> data) {
        ListView listView  = findViewById(R.id.friendsList);
        if (listView != null) {
            listView.setAdapter(new FriendListAdapter(this, data));
            listView.setOnItemClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mClearSearchBtn.performClick();
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
        Intent intent = new Intent(ManageFriendsActivity.this, FriendDetailActivity.class);
        UserHelper user = friends.get(pos);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void getFriends() {
        String url = "http://192.168.1.8:8080/api/friends/all";
        requestPost(url, username);
    }

    private void getQueryResult() {
        String url = "http://192.168.1.8:8080/api/friends/find";
        queryRequest(url, username);
    }

    private void requestPost(String url, String username) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder.addQueryParameter("username", username);

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

                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JSR310Module());
                    friends = mapper.readValue(data, new TypeReference<ArrayList<UserHelper>>(){});

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initListView(friends);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void queryRequest(String url, String username) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder.addQueryParameter("query", query);
        httpBuilder.addQueryParameter("username", username);

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

                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JSR310Module());
                    friends = mapper.readValue(data, new TypeReference<ArrayList<UserHelper>>(){});

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initListView(friends);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}