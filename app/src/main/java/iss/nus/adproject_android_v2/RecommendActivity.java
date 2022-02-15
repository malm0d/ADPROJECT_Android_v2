package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecommendActivity extends AppCompatActivity implements View.OnClickListener{

    EditText submitTextBox;
    Button submitBtn;
    ImageView cryingImg, pensiveImg, happyImg, joyfulImg;
    ImageView cryingCircle, pensiveCircle, happyCircle, joyfulCircle;
    Map<ImageView, String> feelings = new HashMap<>();
    String input = " ";
    String feeling = " ";
    String track = "ontrack";
    SwitchCompat trackSwitch;
    String[] titles;
    String goodResult;
    String userId = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        submitTextBox = findViewById(R.id.submitTextBox);


        cryingImg = findViewById(R.id.cryingImg);
        cryingImg.setOnClickListener(this);
        pensiveImg = findViewById(R.id.pensiveImg);
        pensiveImg.setOnClickListener(this);
        happyImg = findViewById(R.id.happyImg);
        happyImg.setOnClickListener(this);
        joyfulImg = findViewById(R.id.joyfulImg);
        joyfulImg.setOnClickListener(this);

        cryingCircle = findViewById(R.id.cryingCircle);
        pensiveCircle = findViewById(R.id.pensiveCircle);
        happyCircle = findViewById(R.id.happyCircle);
        joyfulCircle = findViewById(R.id.joyfulCircle);

        trackSwitch = findViewById(R.id.trackSwitch);
        trackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                TextView textOn = findViewById(R.id.onText);
                TextView textOff = findViewById(R.id.offText);
                if (b == true){
                    track = "ontrack";
                    textOn.setVisibility(View.VISIBLE);
                    textOff.setVisibility(View.INVISIBLE);
                }
                else{
                    track = "offtrack";
                    textOff.setVisibility(View.VISIBLE);
                    textOn.setVisibility(View.INVISIBLE);
                }
            }
        });

        feelings.put(cryingImg, "cry");
        feelings.put(pensiveImg, "pensive");
        feelings.put(happyImg, "happy");
        feelings.put(joyfulImg, "joy");

        submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(this);

//        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("userId", "1");
    }

    @Override
    public void onClick(View view) {
//        if (view == getBtn){
//            String url = "http://192.168.50.208:8080/api/getStringData";
//            RequestGet(url);
//        }

        if (view == cryingImg){
            if (cryingCircle.getVisibility() == View.VISIBLE){
                clearFeelings();
                feeling = " ";
            }
            else{
                feeling = feelings.get(view);
                clearFeelings();
                cryingCircle.setVisibility(View.VISIBLE);
            }
        }

        if (view == pensiveImg){
            if (pensiveCircle.getVisibility() == View.VISIBLE){
                clearFeelings();
                feeling = " ";
            }
            else{
                feeling = feelings.get(view);
                clearFeelings();
                pensiveCircle.setVisibility(View.VISIBLE);
            }
        }

        if (view == happyImg){
            if (happyCircle.getVisibility() == View.VISIBLE){
                clearFeelings();
                feeling = " ";
            }
            else {
                feeling = feelings.get(view);
                clearFeelings();
                happyCircle.setVisibility(View.VISIBLE);
            }
        }

        if (view == joyfulImg){
            if (joyfulCircle.getVisibility() == View.VISIBLE){
                clearFeelings();
                feeling = " ";
            }
            else{
                feeling = feelings.get(view);
                clearFeelings();
                joyfulCircle.setVisibility(View.VISIBLE);
            }
        }

        if (view == submitBtn){
//            String postUrl = "http://192.168.50.208:8080/api/recommend/postStringData";
//            input = String.valueOf(submitTextBox.getText());
//            System.out.println(input);
            //send input to spring
            SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
            input = String.valueOf(submitTextBox.getText());
            //userId = pref.getString("userId", " ");
            String postUrl = "http://192.168.50.208:8080/api/recommend/postStringData/"
                    + userId + "/" + input + "/" + feeling + "/" + track;
            //RequestPost(postUrl, input, feeling, track);
//            RequestPost(postUrl);
//            String getUrl = "http://192.168.50.208:8080/api/recommend/getResultJson/" + userId;
            RequestGet(postUrl);
        }
    }

    public void startResult(){
        Intent intent = new Intent(this, RecSearchResultActivity.class);
        intent.putExtra("titles", titles);
        intent.putExtra("goodResult", goodResult);
        System.out.println("submit button");
        for (String s : titles){
            System.out.println(s);
        }
        startActivity(intent);
    }

    public void clearFeelings(){
        cryingCircle.setVisibility(View.INVISIBLE);
        pensiveCircle.setVisibility(View.INVISIBLE);
        happyCircle.setVisibility(View.INVISIBLE);
        joyfulCircle.setVisibility(View.INVISIBLE);
    }

    private void RequestGet(String url){
        OkHttpClient client = new OkHttpClient();
        client.newBuilder()
                .connectTimeout(5, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                .readTimeout(5, TimeUnit.MINUTES) // read timeout
                .retryOnConnectionFailure(false)
                .build();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RecommendActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String res = response.body().string();
                System.out.println(res);
                JSONObject json = null;
                try {
                    json = new JSONObject(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray Jarray = json.getJSONArray("titles");
                    System.out.println("JSONArray:");
                    System.out.println(Jarray);
                    titles = new String[5];
                    for (int i = 0; i < Jarray.length(); i++){
                        titles[i] = Jarray.getString(i);
                    }
                    goodResult = json.getString("goodResult");
                    System.out.println(goodResult);
                    startResult(); //startResult activity
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("successful get from server");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(RecommendActivity.this, "Successful", Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });
    }

//    private void RequestPost(String url){
//        OkHttpClient client = new OkHttpClient();
////        FormBody.Builder formBuilder = new FormBody.Builder();
////        formBuilder.add("input", input);
////        formBuilder.add("feeling", feeling);
////        formBuilder.add("track", track);
//        //Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
//        Request request = new Request.Builder().url(url).build();
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(RecommendActivity.this, "Server error", Toast.LENGTH_SHORT).show();
//                                e.printStackTrace();
//                            }
//                        });
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String res = response.body().string();
//                System.out.println(res);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(RecommendActivity.this, "Successful", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }
}