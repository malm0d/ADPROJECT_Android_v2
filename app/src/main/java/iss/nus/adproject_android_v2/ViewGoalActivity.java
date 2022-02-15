package iss.nus.adproject_android_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    Button GoDetailsBtn,PastGoalBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_goal);

        GoDetailsBtn = findViewById(R.id.SeeDetailsBtn);
        GoDetailsBtn.setOnClickListener(this);
        PastGoalBtn =findViewById(R.id.PastGoalBtn);
        PastGoalBtn.setOnClickListener(this);
        getDataFromServer();
        getDataFromServer1();
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
    }
    protected void initView1(List<String> strList){

        mealstrList = strList;
        TotalMealRecord = findViewById(R.id.TotalMealRecord);
        pathProgressText = findViewById(R.id.pathProgressText2);
        String progresstext1 ="You are " + strList.get(2) + " % on path!";
        pathProgressText.setText(progresstext1);
        //create string data for intent
        intentprogresstext = progresstext1;
        mealsOnPath = strList.get(0);
        mealsOffPath = strList.get(1);
        int totalmealsInt = Integer.parseInt(strList.get(0)) + Integer.parseInt(strList.get(1));
        String totalmeals = "Meals in record :" + String.valueOf(totalmealsInt);
        TotalMealRecord.setText(totalmeals);
    }

    private void getDataFromServer(){
        String url1 = "http://192.168.31.50:8888/api/currentgoal";
        String UserName = "jake";
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
                try {
                    JSONObject jsonObj = new JSONObject(res);
                    String dataStr = jsonObj.toString();

                    ObjectMapper mapper = new ObjectMapper();

                     final Goal currentGoal = mapper.readValue(dataStr, new TypeReference<Goal>(){});

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
        });


    }

    private void getDataFromServer1(){
        String url1 = "http://192.168.31.50:8888/api/goalsMeal";
        String UserName = "jake";
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
                System.out.println("This information return from server side");
                System.out.println(res1);
                System.out.println("check data ");

                String res2 = res1.substring(2,3);
                String res3 = res1.substring(6,7);
                String res4 = res1.substring(10,14);

                List<String> strList = new ArrayList<>() ;
                strList.add(res2);
                strList.add(res3);
                strList.add(res4);
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
    }


}