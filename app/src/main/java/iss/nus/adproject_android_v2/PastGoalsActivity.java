package iss.nus.adproject_android_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PastGoalsActivity extends AppCompatActivity {

    NavigationBarView bottomNavigation;
    private ArrayList<Goal> pastGoals;
    private String shareusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_goals);

        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        shareusername = pref.getString("username", "");

        pastGoals = new ArrayList<>();
        getDataFromServer();
        initBoomNacigation();
}

    private void initBoomNacigation(){
        //bottom navigation bar
        bottomNavigation = findViewById(R.id.bottom_navigation);
        //set Setting selected
        bottomNavigation.setSelectedItemId(R.id.pathMenu);

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

    protected void initListView
            (ArrayList<Goal> pastGoalsList){

        pastGoals = pastGoalsList;
        ListView listView = findViewById(R.id.pastGoalList);
        if (listView != null){
            listView.setAdapter(new pastGoalListAdapter(this,pastGoalsList));
        }


    }

    private void getDataFromServer(){
        String url = getResources().getString(R.string.IP) + "/api/pastGoal";
        String UserName = shareusername;
        RequestPost(url,UserName);
    }

    private void RequestPost(String url,String UserName){

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("UserName", UserName);


        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PastGoalsActivity.this, "server error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String res = response.body().string();
                System.out.println("This information return from server side");
                System.out.println(res);
                try {
                    JSONObject jsonObj = new JSONObject(res);

                    String dataStr = jsonObj.getString("data");

                    ObjectMapper mapper = new ObjectMapper();

                    final ArrayList<Goal> completedGoal1 = mapper.readValue(dataStr, new TypeReference<ArrayList<Goal> >(){});

                    System.out.println("check data ");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initListView(completedGoal1);
                            Toast.makeText(PastGoalsActivity.this, "success", Toast.LENGTH_SHORT).show();
                        }
                    });



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }
}