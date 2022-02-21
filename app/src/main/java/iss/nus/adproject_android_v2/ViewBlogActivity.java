package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import iss.nus.adproject_android_v2.adapter.ViewBlogAdapter;
import iss.nus.adproject_android_v2.helper.BlogEntry;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewBlogActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener, View.OnClickListener{
    private String url;
//private final String url = getResources().getString(R.string.IP) + "/api/blogentry/blog";
    private List<BlogEntry> blogEntries;
    private Thread getBlogEntriesThread;
    private Integer activeUserId;
    private Integer friendUserId;
    private String friendUsername;
    private String activeUsername;
    private ImageView likeBtn;
    private ImageView flagBtn;
    NavigationBarView bottomNavigation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            friendUserId = Integer.valueOf( intent.getStringExtra("friendUserId"));
            friendUsername = intent.getStringExtra("friendUsername");
        }

        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        activeUsername = pref.getString("username", "");
        activeUserId = Integer.valueOf( pref.getString("userId",""));

        url = getResources().getString(R.string.IP) + "/api/blogentry/blog";

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
                        //link to path
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
        getBlogEntriesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getBlogEntries(url,activeUserId,friendUserId);
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
        authorText.setText(friendUsername+"'s blog");

        ViewBlogAdapter adapter = new ViewBlogAdapter(this, blogEntries);
        ListView listView = findViewById(R.id.blogEntryList);
        if(listView != null) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
    }




    private void getBlogEntries(String url, Integer activeUserId, Integer friendUserId) throws IOException {

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder .addQueryParameter("activeUserId",activeUserId.toString())
                    .addQueryParameter("friendUserId", friendUserId.toString());

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

            blogEntries = mapper.readValue(dataStr, new TypeReference<ArrayList<BlogEntry>>(){});

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int pos, long id) {
        Integer blogEntryId = blogEntries.get(pos).getId();
        Intent intent = new Intent(this, ViewBlogEntryActivity.class);
        intent.putExtra("activeUserId",activeUserId);
        intent.putExtra("activeUsername",activeUsername);
        intent.putExtra("friendUsername",friendUsername);
        intent.putExtra("friendUserId",friendUserId);
        intent.putExtra("blogEntry", blogEntries.get(pos));
        startActivity(intent);



    }

    @Override
    public void onClick(View view) {

    }
}