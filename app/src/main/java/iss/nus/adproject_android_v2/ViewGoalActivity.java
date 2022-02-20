package iss.nus.adproject_android_v2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewGoalActivity extends AppCompatActivity implements View.OnClickListener   {

    private List<String> mealstrList;
    private Goal currentGoal;
    String intentprogresstext, mealsOnPath, mealsOffPath;
    TextView goalName;
    TextView totalCount;
    TextView targetCount;
    TextView pathProgressText;
    TextView TotalMealRecord;
    Button GoDetailsBtn,PastGoalBtn,analyticsBtn;
    private ProgressBar progressBar;
    NavigationBarView bottomNavigation;
    private String shareusername;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_goal);

        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        shareusername = pref.getString("username", "");

        context = this;
        GoDetailsBtn = findViewById(R.id.SeeDetailsBtn);
        GoDetailsBtn.setOnClickListener(this);
        PastGoalBtn =findViewById(R.id.PastGoalBtn);
        PastGoalBtn.setOnClickListener(this);
        analyticsBtn = findViewById(R.id.analyticsBtn);
        analyticsBtn.setOnClickListener(this);
        getCurrentGoalFromServer();
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
                        //Intent currentPath = new Intent(getApplicationContext(), ViewGoalActivity.class);
                        //startActivity(currentPath);
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
    protected void initView(Goal goal){

        currentGoal = goal;
        goalName = findViewById(R.id.goalName);
        totalCount = findViewById(R.id.totalCount);
        targetCount = findViewById(R.id.targetCount);
        String GoalName = goal.getGoalDescription();
        goalName.setText(GoalName);
        String goalTargetCount = "Target Count : " + goal.getTargetCount();
        targetCount.setText(goalTargetCount);
        String goalTotalCount = "Total Count ：" +goal.getTotalMealCount();
        totalCount.setText(goalTotalCount);
        getMealsDataFromServer();
    }
    protected void initView1(List<String> strList){

        mealstrList = strList;
        TotalMealRecord = findViewById(R.id.TotalMealRecord);
        pathProgressText = findViewById(R.id.pathProgressText2);
        String progresstext1 ="You are " + strList.get(2) + " % on path!";
        pathProgressText.setText(progresstext1);
        progressBar = findViewById(R.id.progressBar);
        String progress = strList.get(2);
        Double progressdouble = Double.parseDouble(progress);
        int progressint = progressdouble.intValue();
        progressBar.setProgress(progressint);
        //create string data for intent
        intentprogresstext = progresstext1;
        mealsOnPath = strList.get(0);
        mealsOffPath = strList.get(1);
        int totalmealsInt = Integer.parseInt(strList.get(0)) + Integer.parseInt(strList.get(1));
        String totalmeals = "Meals in record :" + String.valueOf(totalmealsInt);
        TotalMealRecord.setText(totalmeals);
    }

    private void getCurrentGoalFromServer(){
        String url1 = getResources().getString(R.string.IP) + "/api/currentgoal";
        String UserName = shareusername;
        RequestPost(url1,UserName);
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
                                Toast.makeText(ViewGoalActivity.this, "server error", Toast.LENGTH_SHORT).show();
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
                if(res.isEmpty()) {

                            Intent intent = new Intent();
                            intent.setClass(context, SetGoalActivity.class);
                            startActivity(intent);


                }
                else {
                    try {
                        JSONObject jsonObj = new JSONObject(res);
                        String dataStr = jsonObj.toString();

                        ObjectMapper mapper = new ObjectMapper();

                        final Goal currentGoal = mapper.readValue(dataStr, new TypeReference<Goal>() {
                        });

                        System.out.println("check data ");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initView(currentGoal);
                                Toast.makeText(ViewGoalActivity.this, "success", Toast.LENGTH_SHORT).show();
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


    }

    private void getMealsDataFromServer(){
        String url1 = getResources().getString(R.string.IP) + "/api/goalsMeal";
        String UserName = shareusername;
        RequestPost1(url1,UserName);
    }

    private void RequestPost1(String url,String UserName){

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
                                Toast.makeText(ViewGoalActivity.this, "server error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String res1 = response.body().string();
                res1= res1.replace("\"", "");
                System.out.println("This information return from server side");
                System.out.println(res1);
                System.out.println("check data ");
                String resSub = res1.substring(1,res1.length()-1);
                String[] resSplit= resSub.split(",");
                String mealontrack = resSplit[0];
                String mealofftrack = resSplit[1];
                String percentProgress = resSplit[2];

                List<String> strList = new ArrayList<>() ;
                strList.add(mealontrack);
                strList.add(mealofftrack);
                strList.add(percentProgress);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                           initView1(strList);

                        Toast.makeText(ViewGoalActivity.this, "success", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


    }


    @Override
    public void onClick(View v){

        int id = v.getId();
        if(id == R.id.SeeDetailsBtn){

            Intent intent = new Intent(this, GoalDetailsActivity.class);
            intent.putExtra("mealstrList", String.valueOf(mealstrList));
            intent.putExtra("currentGoal", currentGoal);
            intent.putExtra("intentprogresstext",intentprogresstext);
            intent.putExtra("mealsOnPath",mealsOnPath);
            intent.putExtra("mealsOffPath",mealsOffPath);
            startActivity(intent);

        }

        if(id == R.id.PastGoalBtn){
            Intent intent = new Intent(this, PastGoalsActivity.class);
            startActivity(intent);
        }

        if(id==R.id.analyticsBtn){
            Intent intent = new Intent(this, Userdashboard.class);
            startActivity(intent);
        }
    }


}