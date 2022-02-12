package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;

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

public class ViewBlogActivity extends AppCompatActivity {
    private final String url = "http://192.168.0.108:8080/api/blogentry/blog";
    private final String imageApiUrl = "http://192.168.0.108:8080/api/image/get";
    private List<BlogEntry> blogEntries;
    private Thread getBlogEntriesThread;
    private Thread downloadImagesThread;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog);
        Intent intent = getIntent();
        Integer activeUserId = intent.getIntExtra("activeUserId", 0);
        Integer friendUserId = intent.getIntExtra("friendUserId",0);

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

        downloadImagesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getBlogEntriesThread.join();
                    System.out.println("getBlogEntries finished");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for(BlogEntry blogEntry : blogEntries) {
                    System.out.println(blogEntry);
                }
                System.out.println("Start downloading images");
                downloadImages();


            }
        });
        downloadImagesThread.start();
        try {
            getBlogEntriesThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            downloadImagesThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Both background threads complete");

        ViewBlogAdapter adapter = new ViewBlogAdapter(this, blogEntries);
        ListView listView = findViewById(R.id.blogEntryList);
        if(listView != null) {
            listView.setAdapter(adapter);
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
            System.out.println("blogEntries saved");


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void downloadImages() {
        if (blogEntries == null) {
            return;
        }


        for (BlogEntry blogEntry : blogEntries) {
            // Download Images here
        }

    }
}