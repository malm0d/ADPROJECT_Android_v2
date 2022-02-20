package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Userdashboard extends AppCompatActivity {


    float[] mealTrack = new float[2];
    float[] feeling = new float[4];
    TextView viewCurrentGoal, myProgress, intro, myFeeling;
    PieChart pieChart,pieChart2;
    PieData pieData, pieData2;
    List<PieEntry> pieEntryList = new ArrayList<>();
    List<PieEntry> pieEntryList2 = new ArrayList<>();
    NavigationBarView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdashboard);

        viewCurrentGoal = findViewById(R.id.viewCurrentGoal);
        myProgress = findViewById(R.id.myProgress);
        intro = findViewById(R.id.intro);
        myFeeling=findViewById(R.id.myFeeling);

        //get userId
        //get from Shared Preference
        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        String userId = pref.getString("userId","0");

        //get and set current goal to view
        //change IP address if necessary
        String url1 = getResources().getString(R.string.IP) +"/api/dashboard/getCurrentGoal/" +userId;
        getCurrentGoal(url1);

        //get meal track score
        //change IP address if necessary
        String url2 = getResources().getString(R.string.IP) +"/api/dashboard/getTrack/"+userId;
        getMealTrack(url2);

        //get feeling record
        //change IP address if necessary
        String url3 = getResources().getString(R.string.IP) +"/api/dashboard/getFeeling/"+userId;
        getFeeling(url3);

        pieChart = findViewById(R.id.pieChart);
        pieChart2 = findViewById(R.id.pieChart2);

        //bottom navigation bar
        bottomNavigation = findViewById(R.id.bottom_navigation);
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



    private String getCurrentGoal(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
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
                                Toast.makeText(Userdashboard.this, "server error", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Userdashboard.this, "success", Toast.LENGTH_SHORT).show();
                        if (res.contains("Internal Server Error"))
                        {
                            viewCurrentGoal.setText("No goal set yet");
                        }
                        else{
                            viewCurrentGoal.setText(res);
                        }


                    }
                });
            }
        });
        return "";
    }

    private String getMealTrack(String url) {
        OkHttpClient client = new OkHttpClient();
        client.newBuilder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
        Request request = new Request.Builder().url(url).get().build();
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
                                Toast.makeText(Userdashboard.this, "server error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res2 = response.body().string();
                JSONObject json = null;
                try {
                    json = new JSONObject(res2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray JArray = json.getJSONArray("mealTrack");
                    System.out.println("JSONArray:");
                    System.out.println(JArray);
                    for (int i = 0; i < JArray.length(); i++) {
                        mealTrack[i] = Float.parseFloat(JArray.getString(i));
                        System.out.println(mealTrack[i]);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Userdashboard.this, "success", Toast.LENGTH_SHORT).show();

                        int onTrackPercent = (int) (mealTrack[0]/ (mealTrack[0]+mealTrack[1])*100);
                        int totalMeal= (int) (mealTrack[0]+mealTrack[1]);

                        if(totalMeal==0){
                            myProgress.setText("No meals recorded yet");
                            myProgress.setTypeface(null, Typeface.ITALIC);

                            intro.setVisibility(View.VISIBLE);

                            pieChart.getLegend().setEnabled(false);
                        }
                        else{
                            //set center text
                            pieChart.setCenterText(onTrackPercent+ "% On Track");
                            pieChart.setCenterTextSize(12f);
                            pieChart.getLegend().setEnabled(true);

                            //set legend
                            Legend legend = pieChart.getLegend();
                            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
                            legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                            legend.setTextSize(15f);
                            legend.setXOffset(10f);
                            legend.setYOffset(10f);
                        }

                        pieEntryList.add(new PieEntry(mealTrack[0],"On Track"));
                        pieEntryList.add(new PieEntry(mealTrack[1],"Off Track"));
                        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"");
                        pieDataSet.setColors(getResources().getColor(R.color.blueOnT),
                                getResources().getColor(R.color.greyOffT));
                        pieData = new PieData(pieDataSet);

                        pieChart.setDrawEntryLabels(false);
                        pieData.setValueTextSize(12f);
                        pieData.setValueTextColor(Color.WHITE);
                        //pieChart.setEntryLabelColor(Color.WHITE);
                        pieData.setValueFormatter(new PercentFormatter(pieChart));
                        pieChart.setUsePercentValues(true);
                        //pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                        //pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);


                        pieChart.getDescription().setEnabled(false);
                        pieChart.setExtraLeftOffset(10f);

                        pieChart.setData(pieData);
                        pieChart.invalidate();


                    }
                });
            }
        });
        return "";
    }

    private String getFeeling(String url) {
        OkHttpClient client = new OkHttpClient();
        client.newBuilder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
        Request request = new Request.Builder().url(url).get().build();
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
                                Toast.makeText(Userdashboard.this, "server error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res2 = response.body().string();
                JSONObject json = null;
                try {
                    json = new JSONObject(res2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray JArray = json.getJSONArray("feeling");
                    System.out.println("JSONArray:");
                    System.out.println(JArray);
                    for (int i = 0; i < JArray.length(); i++) {
                        feeling[i] = Float.parseFloat(JArray.getString(i));
                        System.out.println(feeling[i]);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Userdashboard.this, "success", Toast.LENGTH_SHORT).show();


                        pieEntryList2.add(new PieEntry(feeling[0],getResources().getString(R.string.joy)));
                        pieEntryList2.add(new PieEntry(feeling[1],getResources().getString(R.string.happy)));
                        pieEntryList2.add(new PieEntry(feeling[2],getResources().getString(R.string.pensive)));
                        pieEntryList2.add(new PieEntry(feeling[3],getResources().getString(R.string.cry)));
                        PieDataSet pieDataSet2 = new PieDataSet(pieEntryList2,"");
                        pieDataSet2.setColors(getResources().getColor(R.color.joy),
                                getResources().getColor(R.color.happy),
                                getResources().getColor(R.color.pensive),
                                getResources().getColor(R.color.cry));
                        pieData2 = new PieData(pieDataSet2);

                        //format piechart
                        pieChart2.setDrawEntryLabels(false);
                        pieData2.setValueTextSize(12f);
                        pieData2.setValueTextColor(Color.BLACK);
                        //pieChart2.setEntryLabelColor(Color.BLACK);
                        pieData2.setValueFormatter(new PercentFormatter(pieChart));
                        pieChart2.setUsePercentValues(true);
                        //pieDataSet2.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                        //pieDataSet2.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                        //set centre text
                        int joyPercent = (int) (feeling[0]/(feeling[0]+feeling[1]+feeling[2]+feeling[3]) *100);
                        int totalMeal= (int) (feeling[0]+feeling[1]+feeling[2]+feeling[3]);

                        /*if (totalMeal>0){
                            pieChart2.setCenterText(joyPercent+"% "+ getResources().getString(R.string.joy));
                            pieChart2.setCenterTextSize(15f);
                        }*/

                        //make title disappear if no meal entries recorded
                        if(totalMeal==0){
                            myFeeling.setVisibility(View.INVISIBLE);
                            pieChart2.getLegend().setEnabled(false);
                        }
                        else{
                            pieChart2.setCenterText(joyPercent+"% "+ getResources().getString(R.string.joy));
                            pieChart2.setCenterTextSize(15f);

                            //format legend
                            pieChart2.getLegend().setEnabled(true);
                            Legend legend2 = pieChart2.getLegend();
                            legend2.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                            legend2.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
                            legend2.setOrientation(Legend.LegendOrientation.VERTICAL);
                            legend2.setTextSize(15f);
                            legend2.setXOffset(10f);
                            legend2.setYOffset(10f);
                        }

                        pieChart2.getDescription().setEnabled(false);
                        pieChart2.setExtraLeftOffset(10f);

                        pieChart2.setData(pieData2);
                        pieChart2.invalidate();


                    }
                });
            }
        });
        return "";
    }

}