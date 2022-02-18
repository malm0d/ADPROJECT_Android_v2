package iss.nus.adproject_android_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import iss.nus.adproject_android_v2.ui.ImageViewPlus;
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
    TextView Age;
    TextView height;
    TextView weight;
    TextView bmi;
    Button EditProfileBtn, achievement1, achievement2;
    TextView UserNameText;
    NavigationBarView bottomNavigation;
    private String shareusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        shareusername = pref.getString("username", "");

        EditProfileBtn = findViewById(R.id.EditProfileBtn);
        EditProfileBtn.setOnClickListener(this);


        getDataFromServer();
        getDataFromServer1();
        initBoomNacigation();
    }

    private void initBoomNacigation(){
        //bottom navigation bar
        bottomNavigation = findViewById(R.id.bottom_navigation);
        //set Setting selected
        bottomNavigation.setSelectedItemId(R.id.settingsMenu);

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

    protected void initView(User user) {

        currentUser = user;
        Age = findViewById(R.id.age);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        bmi = findViewById(R.id.bmi);
        UserNameText = findViewById(R.id.UserNameText);


        UserNameText.setText(user.getName());

        //DOB to Age
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dateofBirth = sdf.parse(user.getDateOfBirth());
            int age = getAge(dateofBirth);
            String Age1 = String.valueOf(age);
            Age.setText("Age :" + Age1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String Height = "Height :" +user.getHeight() +"cm";
        height.setText(Height);
        String Weight ="Weight :" + user.getWeight() +"kg";
        weight.setText(Weight);
        double heightNum =Integer.parseInt(user.getHeight());
        double weightNum = Integer.parseInt(user.getWeight());
        double bmiFloat = weightNum/(heightNum*heightNum/10000);
        String bmiStr = String.format("%.1f",bmiFloat);
        String BMI = "BMI :" + bmiStr;
        bmi.setText(BMI);

        showphoto();
    }

    protected void initView1(ArrayList<Goal> completedGoal){

        completedGoal = completedGoal;

        achievement1 = findViewById(R.id.achievement1);
        achievement2 = findViewById(R.id.achievement2);

        achievement1.setText(completedGoal.get(completedGoal.size() - 1).getGoalDescription());
        achievement2.setText(completedGoal.get(completedGoal.size() - 2).getGoalDescription());
    }

    private void getDataFromServer(){
        String url1 = getResources().getString(R.string.IP) + "/api/userProfile";
        String UserName = shareusername;
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

                    final User currentUser1 = mapper.readValue(dataStr, new TypeReference<User>() {
                    });

                    System.out.println("check data ");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView(currentUser1);
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
            String url1 = getResources().getString(R.string.IP) + "/api/completedGoal";
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

    public static  int getAge(Date birthDay) {
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            }else{
                age--;

            } } return age; }


    public void showphoto(){
        String imageApiUrl = "http://192.168.31.50:8888/api/image/get";

        profilephoto = findViewById(R.id.mine_iv_headportrait);
        String queryString = "?imagePath=";
        String imageDir = "/static/blog/images/";
        Glide.with(this)
                .load(imageApiUrl + queryString + imageDir + currentUser.getProfilePic())
                .placeholder(R.drawable.no_img)
                .into(profilephoto);

    }
}