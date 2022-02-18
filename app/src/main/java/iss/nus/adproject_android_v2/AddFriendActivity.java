package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

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

public class AddFriendActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    EditText mUserQuery;
    Button mSearchBtn;
    TextView mUsersFound;
    ListView mUsersList;
    ConstraintLayout mAddPopup;
    Button mConfirmAddBtn;
    Button mRevertAddBtn;
    TextView mConfirmAddTxt;
    NavigationBarView bottomNavigation;

    private String query;
    private String username;
    private ArrayList<UserHelper> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        username = pref.getString("username", "");

        initUI();

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                query = mUserQuery.getText().toString();
                String url_query = getResources().getString(R.string.IP) + "/api/friends/find_users";
                getUsersByQuery(url_query, query, username);
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
        mUsersFound = findViewById(R.id.usersFoundTxt);

        mUserQuery = findViewById(R.id.userQuery);

        mSearchBtn = findViewById(R.id.searchUserBtn);
    }

    protected void initListView(ArrayList<UserHelper> data) {
        mUsersList  = findViewById(R.id.usersList);
        mUsersFound.setVisibility(View.VISIBLE);
        if (mUsersList != null) {
            mUsersList.setAdapter(new UserListAdapter(this, data));
            mUsersList.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
        mUsersList.setEnabled(false);
        mAddPopup = findViewById(R.id.addPopup);
        mAddPopup.setVisibility(View.VISIBLE);
        mConfirmAddTxt = findViewById(R.id.confirmAddFriendTxt02);
        mConfirmAddTxt.setText("To: " + users.get(pos).getName());


        mRevertAddBtn = findViewById(R.id.revertAddBtn);
        mRevertAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddPopup.setVisibility(View.INVISIBLE);
                mUsersList.setEnabled(true);
            }
        });

        mConfirmAddBtn = findViewById(R.id.confirmAddBtn);
        mConfirmAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url_friendRequest = getResources().getString(R.string.IP) + "/api/friends/add";
                sendFriendRequest(url_friendRequest, users.get(pos), username);
            }
        });
    }

    private void getUsersByQuery(String url, String query, String username) {
        OkHttpClient client = new OkHttpClient();

        String url_query = getResources().getString(R.string.IP) + "/api/friends/find_users";

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url_query).newBuilder();
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
//
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JSR310Module());
                    users = mapper.readValue(data, new TypeReference<ArrayList<UserHelper>>(){});

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            initListView(users);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendFriendRequest(String url, UserHelper user, String username) {
        OkHttpClient client = new OkHttpClient();

        String friend_username = user.getUsername();

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
                    JSONObject jObj = new JSONObject(res);

                    if (jObj.getString("status").equals("OK")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String url_query = getResources().getString(R.string.IP) + "/api/friends/find_users";
                                getUsersByQuery(url_query, query, username);
                                mUsersList.setEnabled(true);
                                Toast.makeText(getApplicationContext(), "Friend Request Sent", Toast.LENGTH_LONG).show();
                                mAddPopup.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}