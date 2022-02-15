package iss.nus.adproject_android_v2;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_goals);

        pastGoals = new ArrayList<>();
        getDataFromServer();

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
        String url = "http://192.168.31.50:8888/api/pastGoal";
        String UserName = "jake";
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