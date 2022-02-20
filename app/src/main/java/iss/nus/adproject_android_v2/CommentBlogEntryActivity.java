package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import iss.nus.adproject_android_v2.adapter.CommentAdapter;
import iss.nus.adproject_android_v2.helper.BlogEntry;
import iss.nus.adproject_android_v2.helper.Comment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentBlogEntryActivity extends AppCompatActivity implements View.OnClickListener{
    private BlogEntry blogEntry;
    private ImageView entryImage;
    private TextView blogTitle;
    private TextView timeStampText;
    private TextView likesText;
    private ImageView likeBtn;
    private ImageView flagBtn;
    private TextView rowAuthor;
    private Integer activeUserId;
    private List<Comment> comments;
    private ListView commentSection;
    private Button submitBtn;
    private EditText commentInput;
    private TextView activeUsernameTextView;
    private String activeUsername;
    NavigationBarView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_blog_entry);
        Intent intent = getIntent();
        blogEntry = (BlogEntry) intent.getSerializableExtra("blogEntry");
        activeUserId = intent.getIntExtra("activeUserId", 0);
        activeUsername = intent.getStringExtra("activeUsername");

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


        initUi();
        updateUI();


    }

    public void initUi() {
        entryImage = findViewById(R.id.entryImage);
        blogTitle = findViewById(R.id.blogTitle);
        timeStampText = findViewById(R.id.timestampText);
        likesText = findViewById(R.id.likesText);
        rowAuthor = findViewById(R.id.rowAuthor);
        likeBtn = findViewById(R.id.rowLikeBtn);
        flagBtn = findViewById(R.id.rowFlagBtn);
        submitBtn = findViewById(R.id.submit_comment_button);
        submitBtn.setOnClickListener(this);
        commentInput = findViewById(R.id.comment_input);
        activeUsernameTextView = findViewById(R.id.active_username);


    }
    public void renderBlogEntry() {

        String imageApiUrl = "http://192.168.0.108:8080/api/image/get";
        String imageDir = "upload/";
        String queryString = "?imagePath=";
        Glide.with(this)
                .load(imageApiUrl + queryString + imageDir + blogEntry.getImageURL())
                .placeholder(R.drawable.no_img)
                .into(entryImage);

        blogTitle.setText(blogEntry.getTitle());
        LocalDateTime timestamp = blogEntry.getTimeStamp();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a");
        String formattedTimestamp = timestamp.format(formatter);
        timeStampText.setText(formattedTimestamp);
        String likeString = "Liked by " + blogEntry.getNumberOfLikes() + " users";
        likesText.setText(likeString);
        String authorText = "by " + blogEntry.getAuthorUsername();
        rowAuthor.setText(authorText);

        if(blogEntry.isLikedByActiveUser()){
            likeBtn.setBackgroundResource(R.drawable.thumb_logo_blue_fill);
        }
        else {
            likeBtn.setBackgroundResource(R.drawable.thumb_logo_no_fill);
        }


        if(blogEntry.isFlaggedByActiveUser()) {
            flagBtn.setBackgroundResource(R.drawable.flag_logo_red_fill);
        }
        else {
            flagBtn.setBackgroundResource(R.drawable.flag_logo_no_fill);
        }
    }

    private void updateUI(){
        renderBlogEntry();
        commentInput.setText("");
        activeUsernameTextView.setText(activeUsername);
        CommentAdapter adapter = new CommentAdapter(this,comments);


        commentSection = findViewById(R.id.comment_section);
        if(commentSection != null) {
            commentSection.setAdapter(adapter);
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
    public void submitComment(String caption) {
        String url = "http://192.168.0.108:8080/api/comment/submit";
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder .addQueryParameter("activeUserId",activeUserId.toString())
                    .addQueryParameter("mealEntryId",blogEntry.getId().toString())
                    .addQueryParameter("caption",caption);
        HttpUrl httpUrl = httpBuilder.build();
        //Define empty request body
        RequestBody reqbody = RequestBody.create(null, new byte[0]);
        Request request = new Request   .Builder()
                .url(httpUrl)
                .post(reqbody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.code() != 200) {
                    System.out.println("Failed to submit comment");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast msg = Toast.makeText(CommentBlogEntryActivity.this,"Failed to submit comment",Toast.LENGTH_SHORT);
                            msg.show();

                        }
                    });
                    return;
                }
                downloadComments();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });


            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view == submitBtn){
            // Submit comment
            String caption = commentInput.getText().toString();


            submitComment(caption);







        }
    }
}