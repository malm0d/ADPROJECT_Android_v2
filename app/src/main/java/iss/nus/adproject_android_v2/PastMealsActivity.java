package iss.nus.adproject_android_v2;

import android.content.Intent;

import java.io.Serializable;
import java.lang.reflect.Type;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
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
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PastMealsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,View.OnClickListener  {

    private ArrayList<MealHelper> mealsDataArray;
    private String GoalStr;

    private Button setGoalBtn;
    private String shareusername;

    NavigationBarView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_meals);

        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        shareusername = pref.getString("username", "");

        setGoalBtn = findViewById(R.id.setCurrentGoalBtn);
        setGoalBtn.setOnClickListener(this);

        mealsDataArray = new ArrayList<>();
        getDataFromServer();

        initBoomNacigation();
    }

    private void initBoomNacigation(){
        //bottom navigation bar
        bottomNavigation = findViewById(R.id.bottom_navigation);
        //set Setting selected
        bottomNavigation.setSelectedItemId(R.id.addMenu);

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

        int id = v.getId();
        if(id == R.id.setCurrentGoalBtn){
            Intent intent = new Intent();
            intent.setClass(this,SetGoalActivity.class);
            startActivity(intent);

        }


    }
    

    protected void initListView
            (ArrayList<MealHelper> mealList){

        mealsDataArray = mealList;
        ListView listView = findViewById(R.id.mealslist);
        if (listView != null){
            listView.setAdapter(new mealListAdapter(this,mealList));
            listView.setOnItemClickListener(this);
        }

            if (GoalStr.equals(" ")){
                setGoalBtn.setVisibility(View.VISIBLE);

            }else {
                setGoalBtn.setVisibility(View.INVISIBLE);
            }
            TextView goalText = findViewById(R.id.currentgoal);
            String goalStr = "Current Goal: " + GoalStr;
            goalText.setText(goalStr);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){

        Intent intent = new Intent();
        intent.setClass(this,MealDetailActivity.class);
        MealHelper detailMeal = mealsDataArray.get(position);
        intent.putExtra("meal",detailMeal);
//        startActivity(intent);
        startActivityForResult(intent, 1);


        System.out.println("clicked position: " + position);

    }


    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            System.out.println("刷新了"); //刷新操作
            getDataFromServer();
        }

    }


    private void getDataFromServer(){
        String url = getResources().getString(R.string.IP) + "/api/pastMeals";
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
                                Toast.makeText(PastMealsActivity.this, "server error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
                System.out.println("This information return from server side");
                System.out.println(res);
                try {
                    JSONObject jsonObj = new JSONObject(res);

                    String dataStr = jsonObj.getString("data");

                    String goalStr = jsonObj.getString("goalStr");

                    GoalStr = goalStr;

                    ObjectMapper mapper = new ObjectMapper();

                    final ArrayList<MealHelper> mealList = mapper.readValue(dataStr, new TypeReference<ArrayList<MealHelper>>(){});

                    System.out.println("check data ");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mealList.size() > 0){
                                initListView(mealList);
                                Toast.makeText(PastMealsActivity.this, "success", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(PastMealsActivity.this, "current no meals", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }



}