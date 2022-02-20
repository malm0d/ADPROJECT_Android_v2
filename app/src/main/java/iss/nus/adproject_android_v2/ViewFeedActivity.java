package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import iss.nus.adproject_android_v2.adapter.ViewBlogAdapter;
import iss.nus.adproject_android_v2.helper.BlogEntry;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ViewFeedActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener{
    private String url;
    private List<BlogEntry> blogEntries = new ArrayList<BlogEntry>();;
    private Thread getBlogEntriesThread;
    private Integer activeUserId;
    private String activeUsername;
    private Integer friendUserId;
    private String friendUsername;
    private Integer pageNo;
    private Integer pageLength;
    private ImageView likeBtn;
    private ImageView flagBtn;
    private Button viewMoreBtn;
    private ListView listView;
    private ViewBlogAdapter adapter;

    NavigationBarView bottomNavigation;
    Parcelable state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feed);
        //Intent intent = getIntent();
        //activeUserId = intent.getIntExtra("activeUserId", 0);
        //activeUsername = intent.getStringExtra("activeUsername");
        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        activeUsername = pref.getString("username", "");
        activeUserId = Integer.valueOf( pref.getString("userId",""));

        pageNo = 0;
        pageLength = 10;
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
    @Override
    public void onResume() {

        super.onResume();

        if( (pageNo + 1) * pageLength == blogEntries.size()) {
            return;
        }

        getBlogEntriesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getBlogEntries();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        getBlogEntriesThread.start();

        // Make sure all blog entry Json data is loaded before proceeding
        try {
            getBlogEntriesThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updateUi();
    }
    private void getBlogEntries() throws IOException {
        url = "http://192.168.0.108:8080/api/blogentry/page";
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder .addQueryParameter("activeUserId",activeUserId.toString())
                    .addQueryParameter("pageNo", pageNo.toString())
                    .addQueryParameter("pageLength",pageLength.toString());


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

            List<BlogEntry> downloadedBlogEntries = mapper.readValue(dataStr, new TypeReference<ArrayList<BlogEntry>>(){});
            blogEntries.addAll(downloadedBlogEntries);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    private void updateUi() {
        likeBtn = findViewById(R.id.rowLikeBtn);
        if(likeBtn != null) {
            likeBtn.setOnClickListener(this);
        }
        flagBtn = findViewById(R.id.flagBtn);
        if(flagBtn != null) {
            flagBtn.setOnClickListener(this);
        }
        TextView authorText = findViewById(R.id.authorText);
        authorText.setText(activeUsername+"'s feed");

        adapter = new ViewBlogAdapter(this, blogEntries);
        listView = findViewById(R.id.blogEntryList);
        if(listView != null) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }

        if(viewMoreBtn == null) {
            viewMoreBtn = new Button(this);
            viewMoreBtn.setText("View More");
            listView.addFooterView(viewMoreBtn);

            viewMoreBtn.setOnClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int pos, long id) {
        Intent intent = new Intent(this, ViewBlogEntryActivity.class);
        intent.putExtra("activeUserId",activeUserId);
        intent.putExtra("activeUsername",activeUsername);
        intent.putExtra("blogEntry", blogEntries.get(pos));
        startActivity(intent);



    }

    @Override
    public void onClick(View view) {
       if(view == viewMoreBtn){
            pageNo++;
            int currentPosition = listView.getFirstVisiblePosition();
            onResume();
            // For maintaining scroll position
            listView.setSelectionFromTop(currentPosition + 1, 0);



        }
    }

}