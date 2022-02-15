package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import iss.nus.adproject_android_v2.helper.BlogEntry;
import iss.nus.adproject_android_v2.helper.Comment;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommentBlogEntryActivity extends AppCompatActivity {
    private BlogEntry blogEntry;
    private Integer activeUserId;
    private List<Comment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_blog_entry);
        Intent intent = getIntent();
        blogEntry = (BlogEntry) intent.getSerializableExtra("blogEntry");
        activeUserId = intent.getIntExtra("activeUserId", 0);




    }
    @Override
    public void onResume() {

        super.onResume();

        Thread downloadCommentsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    downloadComments();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        downloadCommentsThread.start();

        try {
            downloadCommentsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(Comment comment : comments) {
            System.out.println(comment);
        }

    }

    public void downloadComments() throws  IOException{

        String url = "http://192.168.0.108:8080/api/comment/get";
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder .addQueryParameter("mealEntryId",blogEntry.getId().toString());
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
            comments = mapper.readValue(dataStr, new TypeReference<List<Comment>>() {
            });

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }


}