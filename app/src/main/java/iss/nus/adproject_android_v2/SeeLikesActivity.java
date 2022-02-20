package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import iss.nus.adproject_android_v2.adapter.LikesAdapter;
import iss.nus.adproject_android_v2.helper.BlogEntry;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SeeLikesActivity extends AppCompatActivity {
    private BlogEntry blogEntry;
    private TextView likedByText;
    private ListView likeList;
    private List<String> usernames; // get from API
    private Thread getUsernamesThread;
    private LikesAdapter adapter;



    NavigationBarView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_likes);
        Intent intent = getIntent();
        blogEntry = (BlogEntry) intent.getSerializableExtra("blogEntry");

        //bottom navigation bar
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.friendsMenu);
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

    public void updateUi() {
        likedByText = findViewById(R.id.likedByText);
        likeList = findViewById(R.id.likeList);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        adapter = new LikesAdapter(this,usernames);
        likeList.setAdapter(adapter);



    }

    private void getUsernames() throws IOException {
        String url = "http://192.168.0.108:8080/api/likes/get";
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder.addQueryParameter("mealEntryId",blogEntry.getId().toString());
        HttpUrl httpUrl = httpBuilder.build();


        Request request = new Request   .Builder()
                .url(httpUrl)
                .get()
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        final String res = response.body().string();

        try {
            JSONArray jsonArray = new JSONArray(res);
            String dataStr = jsonArray.toString();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JSR310Module());
            usernames = mapper.readValue(dataStr, new TypeReference<List<String>>(){});


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        getUsernamesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    getUsernames();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        getUsernamesThread.start();

        try {
            getUsernamesThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updateUi();

    }
}