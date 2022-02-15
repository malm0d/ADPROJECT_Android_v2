package iss.nus.adproject_android_v2;

import android.content.Intent;

import java.io.Serializable;
import java.lang.reflect.Type;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

public class PastMealsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener  {

    private ArrayList<MealHelper> mealsDataArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_meals);

        mealsDataArray = new ArrayList<>();
        getDataFromServer();
    }
    

    protected void initListView
            (ArrayList<MealHelper> mealList){

        mealsDataArray = mealList;
        ListView listView = findViewById(R.id.mealslist);
        if (listView != null){
            listView.setAdapter(new mealListAdapter(this,mealList));
            listView.setOnItemClickListener(this);
        }

        if (mealList.size() >= 0){
            Goal latestGoal = mealList.get(0).getGoal();
            TextView goalText = findViewById(R.id.currentgoal);
            String goalStr = "Current Goal: " + latestGoal.getGoalDescription();
            goalText.setText(goalStr);
        }


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){

        Intent intent = new Intent();
        intent.setClass(this,MealDetailActivity.class);
        MealHelper detailMeal = mealsDataArray.get(position);
        intent.putExtra("meal",detailMeal);
        startActivity(intent);

        System.out.println("clicked position: " + position);

    }

    private void getDataFromServer(){
        String url = "http://192.168.86.248:8888/api/pastMeals";
        String UserName = "Ken";
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

                    ObjectMapper mapper = new ObjectMapper();


                    final ArrayList<MealHelper> mealList = mapper.readValue(dataStr, new TypeReference<ArrayList<MealHelper>>(){});

                    System.out.println("check data ");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initListView(mealList);
                            Toast.makeText(PastMealsActivity.this, "success", Toast.LENGTH_SHORT).show();
                        }
                    });



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }



}