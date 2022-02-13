package iss.nus.adproject_android_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import iss.nus.adproject_android_v2.ui.ImageViewPlus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

public class UserProfile extends AppCompatActivity implements View.OnClickListener {

    private User currentUser;
    private ArrayList<Goal> completedGoal;
    ImageViewPlus profilephoto;
    TextView userName;
    TextView onPathReCord;
    TextView age;
    TextView height;
    TextView weight;
    TextView bmi;
    Button EditProfileBtn, achievement1, achievement2;
    TextView UserNameText;


//    private final String[] profileName = {
//            "Age:", "dateOfBirth:", "Height:", "Weight","BMI:"
//    };
//    private String[] profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        EditProfileBtn = findViewById(R.id.EditProfileBtn);
        EditProfileBtn.setOnClickListener(this);

        getDataFromServer();
        getDataFromServer1();
    }


    protected void initView(User user){

        ImageViewPlus profilephoto = findViewById(R.id.mine_iv_headportrait);
        profilephoto.setImageResource(R.drawable.cat01);
        currentUser = user;
        age = findViewById(R.id.age);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        bmi = findViewById(R.id.bmi);
        UserNameText = findViewById(R.id.UserNameText);

        UserNameText.setText(user.getName());
        String Age ="Date of Birth : " + user.getDateOfBirth();
        age.setText(Age);
        String Height = "Height :" +user.getHeight();
        height.setText(Height);
        String Weight ="Weight :" + user.getWeight();
        weight.setText(Weight);
        double heightNum =Integer.parseInt(user.getHeight());
        double weightNum = Integer.parseInt(user.getWeight());
        double bmiFloat = weightNum/(heightNum*heightNum/10000);
        String bmiStr = String.format("%.1f",bmiFloat);
        String BMI = "BMI :" + bmiStr;
        bmi.setText(BMI);
    }

    protected void initView1(ArrayList<Goal> completedGoal){

        completedGoal = completedGoal;

        achievement1 = findViewById(R.id.achievement1);
        achievement2 = findViewById(R.id.achievement2);

        achievement1.setText(completedGoal.get(completedGoal.size() - 1).getGoalDescription());
        achievement2.setText(completedGoal.get(completedGoal.size() - 2).getGoalDescription());
    }

    private void getDataFromServer(){
        String url1 = "http://192.168.31.50:8888/api/userProfile";
        String UserName = "jake";
        RequestPost(url1,UserName);
    }

    private void RequestPost(String url,String UserName) {

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
                                Toast.makeText(UserProfile.this, "server error", Toast.LENGTH_SHORT).show();
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

                    final User currentUser = mapper.readValue(dataStr, new TypeReference<User>() {
                    });

                    System.out.println("check data ");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView(currentUser);
                            Toast.makeText(UserProfile.this, "success", Toast.LENGTH_SHORT).show();
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

        private void getDataFromServer1(){
            String url1 = "http://192.168.31.50:8888/api/completedGoal";
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
                                    Toast.makeText(UserProfile.this, "server error", Toast.LENGTH_SHORT).show();
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
                                initView1(completedGoal1);
                                Toast.makeText(UserProfile.this, "success", Toast.LENGTH_SHORT).show();
                            }
                        });



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });


        }


    @Override
    public void onClick(View v){

        int id = v.getId();
        if(id == R.id.EditProfileBtn){

            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra("User", currentUser);
            startActivity(intent);



        }
    }
}