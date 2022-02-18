package iss.nus.adproject_android_v2;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SetGoalActivity extends AppCompatActivity implements View.OnClickListener {

    EditText userGoal;
    Spinner totalSpinner;
    Spinner targetSpinner;
    Button saveBtn;

    private Context context;

    private String shareusername;

    NavigationBarView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        context = this;

        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        shareusername = pref.getString("username", "");

        ImageView logoImage = findViewById(R.id.logoimageView);
        logoImage.setImageResource(R.drawable.app_logo);

        saveBtn = findViewById(R.id.submitGoal);
        saveBtn.setOnClickListener(this);

        userGoal = findViewById(R.id.goalDescrible);
        totalSpinner = findViewById(R.id.totalCount);
        targetSpinner = findViewById(R.id.targetCount);

        String[] data = new String[100];
        for (int i = 0; i < 100;i++){
            data[i] = String.valueOf(i + 1);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,data);
        totalSpinner.setAdapter(adapter);
        targetSpinner.setAdapter(adapter);

        initBoomNacigation();

    }

    @Override
    public void onClick(View v){

        int id = v.getId();
        if(id == R.id.submitGoal){

            String url = "http://192.168.86.248:9999/api/setGoal";
            String UserName = shareusername;
            String goalDescription = userGoal.getText().toString();
            String totalMealCount = totalSpinner.getSelectedItem().toString();
            String targetCount =   targetSpinner.getSelectedItem().toString();
            if (Integer.valueOf(totalMealCount) < Integer.valueOf(targetCount)){
                Toast.makeText(SetGoalActivity.this, "targetCount can not bigger than totalMealCount", Toast.LENGTH_SHORT).show();
            }else {

                RequestPost(url,UserName,goalDescription,totalMealCount,targetCount);

            }


        }
    }



    private void RequestPost(String url,String UserName,String goalDescription,String totalMealCount,String targetCount){

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("UserName", UserName);
        formBuilder.add("goalDescription", goalDescription);
        formBuilder.add("totalMealCount", totalMealCount);
        formBuilder.add("targetCount", targetCount);

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
                                Toast.makeText(SetGoalActivity.this, "server error", Toast.LENGTH_SHORT).show();
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(SetGoalActivity.this, "success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(context, PastMealsActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });


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



}