package iss.nus.adproject_android_v2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoalDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    int goalId;
    TextView goalName, StatusText,targetTotalCount, pathProgressText2,startDate,MealsOnPath,MealsOffPath;
    Button EndCurrentGoal;
    NavigationBarView bottomNavigation;
    private Context context;
    private String shareusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_details);

        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        shareusername = pref.getString("username", "");

        context = this;
        EndCurrentGoal = findViewById(R.id.EndCurrentGoal);
        EndCurrentGoal.setOnClickListener(this);

        Intent intent = getIntent();
        Goal currentGoal = (Goal) intent.getSerializableExtra("currentGoal");
        String mealstrList = intent.getStringExtra("mealstrList");
        String intentprogresstext = intent.getStringExtra("intentprogresstext");
        String mealsOnPath = intent.getStringExtra("mealsOnPath");
        String mealsOffPath = intent.getStringExtra("mealsOffPath");
        initDetailsView(currentGoal,intentprogresstext,mealsOnPath,mealsOffPath);
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

    protected void initDetailsView(Goal goal, String intentprogresstext, String mealsOnPath, String mealsOffPath  ){

        goalName = findViewById(R.id.goalName);
        StatusText = findViewById(R.id.StatusText);
        targetTotalCount = findViewById(R.id.targetTotalCount);
        pathProgressText2= findViewById(R.id.pathProgressText2);
        startDate = findViewById(R.id.startDate);
        MealsOnPath = findViewById(R.id.MealsOnPath);
        MealsOffPath = findViewById(R.id.MealsOffPath);
        goalId = goal.getId();
        goalName.setText(goal.getGoalDescription());
        StatusText.setText("Status:" + goal.getStatus());
        targetTotalCount.setText(goal.getTargetCount() +" & "+ goal.getTotalMealCount());
        pathProgressText2.setText(intentprogresstext);
        startDate.setText("Starts from: " + goal.getStartDate());
        MealsOnPath.setText("meals on-path: " + mealsOnPath);
        MealsOffPath.setText("meals off-path: " + mealsOffPath);
    }

    @Override
    public void onClick(View v){

        int id = v.getId();
        if(id == R.id.EndCurrentGoal){
            String title = getString(R.string.endGoal);
            String msg = getString(R.string.confirm_endGoal);
            AlertDialog.Builder dlg = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dlg, int which) {
                                    // your action here
                                    String url = getResources().getString(R.string.IP) + "/api/endGoal";
                                    String UserName = shareusername;
                                    endtheGoal(url,UserName,goalId);
                                }
                            })
                    .setNegativeButton(android.R.string.no,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                    .setIcon(android.R.drawable.ic_dialog_alert);
            dlg.show();

        }
    }

    private void endtheGoal(String url,String UserName,int goalId){
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("UserName", UserName);
        formBuilder.add("goalId", String.valueOf(goalId));

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
                                Toast.makeText(GoalDetailsActivity.this, "server error", Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(GoalDetailsActivity.this, "success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setClass(context, SetGoalActivity.class);
                            startActivity(intent);
                        }
                    });



            }
        });

    }
}