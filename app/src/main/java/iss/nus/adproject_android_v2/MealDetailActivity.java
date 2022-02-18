package iss.nus.adproject_android_v2;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import iss.nus.adproject_android_v2.datepicker.CustomDatePicker;
import iss.nus.adproject_android_v2.datepicker.DateFormatUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MealDetailActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView mealDetailImg;

    TextView mealTitle;

    TextView mealtime;
    ImageButton deleteBtn;
    ImageButton timeBtn;
    ImageButton editBtn;

    EditText mealDesc;

//    ImageButton facebookBtn;
//    ImageButton instagramBtn;

    Switch publicSwitch;

    Button saveChage;

    MealHelper meal;

    private CustomDatePicker mTimerPicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_detail);

        Intent intent = getIntent();
        meal = (MealHelper)intent.getSerializableExtra("meal");
//        MealHelper meal = new MealHelper();
        initTheUi();
        showData(meal);
        initTimerPicker();
    }

    public void initTheUi(){
        mealDetailImg = findViewById(R.id.mealdetailimage);
        mealTitle = findViewById(R.id.detailMealtitle);
        mealDesc = findViewById(R.id.mealDetailDes);
        mealtime = findViewById(R.id.detailMealtime);
        saveChage = findViewById(R.id.submitChange);

        deleteBtn = findViewById(R.id.deleteMealBtn);
        timeBtn = findViewById(R.id.editTimeBtn);
        editBtn = findViewById(R.id.editNoteBtn);
//        facebookBtn = findViewById(R.id.facebookBtn);
//        instagramBtn = findViewById(R.id.instagramBtn);

        publicSwitch = findViewById(R.id.publicStates);

        deleteBtn.setOnClickListener(this);
        timeBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);
//        facebookBtn.setOnClickListener(this);
//        instagramBtn.setOnClickListener(this);
        saveChage.setOnClickListener(this);

        publicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    publicSwitch.setText("Public");
                }else {
                    publicSwitch.setText("Private");
                }
            }
        });

    }


    public void showData(MealHelper meal){



        showphoto(meal.getImageURL());

        mealTitle.setText(meal.getTitle());
        mealDesc.setText(meal.getDescription());
        mealtime.setText(meal.getTimeStamp());

        String timeStr = meal.getTimeStamp();
        String newStr = timeStr.replaceAll("T"," ");
        mealtime.setText(newStr);

//        mealTitle.setText("chicken");
//        mealDesc.setText("very nice dinner, health, beauty, wonderful");
//        mealtime.setText("2022-2-9 14:18");

        if (meal.isVisibility()){
            //public
            publicSwitch.setChecked(true);
            publicSwitch.setText("Public");
        }else {
            publicSwitch.setChecked(false);
            publicSwitch.setText("Private");
        }


        deleteBtn.setBackgroundResource(R.drawable.delete);
        timeBtn.setBackgroundResource(R.drawable.time);
        editBtn.setBackgroundResource(R.drawable.edit);
//        facebookBtn.setBackgroundResource(R.drawable.facebook);
//        instagramBtn.setBackgroundResource(R.drawable.instagram);

    }




    @Override
    public void onClick(View v){

        int id = v.getId();
        if(id == R.id.deleteMealBtn){
            showMessage("delete meal");
            String url = "http://192.168.86.248:9999/api/deleteMeal";
            String UserName = "Ken";
            deleteMealRequest(url,UserName,meal.getId());
        }else if (id == R.id.editTimeBtn){

            showMessage("edit time");
            // date formate--> yyyy-MM-dd HH:mm
            mTimerPicker.show(mealtime.getText().toString());

        }else if (id == R.id.editNoteBtn){

            if (mealDesc.isEnabled()){
                mealDesc.setEnabled(false);
            }else {
                mealDesc.setEnabled(true);
                showMessage("Now you can edit the Meal description");
            }

            saveChage.setVisibility(View.VISIBLE);

        }else if(id == R.id.submitChange){

            showMessage("submit changes");

            String url = "http://192.168.86.248:9999/api/modifyMealInfo";
            String UserName = "Ken";

            String mealTime = mealtime.getText().toString();
            String mealDes = mealDesc.getText().toString();
            String publicStates;
            if (publicSwitch.getText().toString().equals("Public")){
                publicStates = "1";
            }else {
                publicStates = "0";
            }


            updateMealRequest(url,UserName,mealTime,mealDes,publicStates,meal.getId());

        }

    }


    public void showMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }


    private void initTimerPicker() {
        String beginTime = "2018-10-17 18:00";
        String endTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);

        //yyyy-MM-dd HH:mm
        mTimerPicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {

                mealtime.setText(DateFormatUtils.long2Str(timestamp, true));
                System.out.println("selected new time: " + DateFormatUtils.long2Str(timestamp, true));
                saveChage.setVisibility(View.VISIBLE);
                // post request save time to database
            }
        }, beginTime, endTime);
        mTimerPicker.setCancelable(true);
        mTimerPicker.setCanShowPreciseTime(true);
        mTimerPicker.setScrollLoop(true);
        mTimerPicker.setCanShowAnim(true);
    }


    private void deleteMealRequest(String url,String UserName,String mealId){
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("UserName", UserName);
        formBuilder.add("mealId", mealId);

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
                                Toast.makeText(MealDetailActivity.this, "server error", Toast.LENGTH_SHORT).show();
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
//                    String dataStr = jsonObj.getString("code");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(MealDetailActivity.this, "success", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }



    private void updateMealRequest(String url,String UserName,String mealTime,String mealDes,String publicStates,String mealId){
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("UserName", UserName);
        formBuilder.add("mealTime", mealTime);
        formBuilder.add("mealDes", mealDes);
        formBuilder.add("publicStates", publicStates);
        formBuilder.add("mealId", mealId);

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
                                Toast.makeText(MealDetailActivity.this, "server error", Toast.LENGTH_SHORT).show();
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MealDetailActivity.this, "success", Toast.LENGTH_SHORT).show();
                            saveChage.setVisibility(View.INVISIBLE);
                        }
                    });



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }


    public void showphoto(String imageName){
//        mealImage.setImageResource(R.drawable.food2);
        String imageApiUrl = "http://192.168.86.248:9999/api/foodImage/get";
        String queryString = "?imagePath=";
        String imageDir = "/static/blog/images/";

        Glide.with(this)
                .load(imageApiUrl + queryString + imageDir + imageName)
                .placeholder(R.drawable.food1)
                .into(mealDetailImg);

    }


    @Override

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Intent intent = new Intent();

            setResult(RESULT_OK, intent);

        }

        return super.onKeyDown(keyCode, event);

    }




}