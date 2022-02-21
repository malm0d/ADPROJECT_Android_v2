package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import iss.nus.adproject_android_v2.helper.BlogEntry;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FlagBlogEntryActivity extends AppCompatActivity implements View.OnClickListener{
    private BlogEntry blogEntry;
    private ImageView entryImage;
    private TextView blogTitle;
    private TextView timeStampText;
    private TextView likesText;
    private TextView otherReasonText;
    private TextView rowAuthor;
    private String reason;
    private RadioButton offensiveLanguageRadioBtn;
    private RadioButton offensiveImageRadioBtn;
    private RadioButton irrelevantRadioBtn;
    private RadioButton advertisementRadioBtn;
    private RadioButton otherReasonRadioBtn;
    private Button submitBtn;

    private Integer activeUserId;

    NavigationBarView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag_blog_entry);
        Intent intent = getIntent();
        blogEntry = (BlogEntry) intent.getSerializableExtra("blogEntry");
        activeUserId = intent.getIntExtra("activeUserId",0);

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


        initUi();
        renderBlogEntry();


    }
    public void initUi() {
        entryImage = findViewById(R.id.entryImage);
        blogTitle = findViewById(R.id.blogTitle);
        timeStampText = findViewById(R.id.timestampText);
        likesText = findViewById(R.id.likesText);
        rowAuthor = findViewById(R.id.rowAuthor);
        otherReasonText = findViewById(R.id.other_reason_text);
        offensiveLanguageRadioBtn = findViewById(R.id.radio_offensive_language);
        offensiveImageRadioBtn = findViewById(R.id.radio_offensive_image);
        irrelevantRadioBtn = findViewById(R.id.radio_irrelevant);
        advertisementRadioBtn = findViewById(R.id.radio_advertisement);
        otherReasonRadioBtn = findViewById(R.id.radio_other);
        submitBtn = findViewById(R.id.submit_report_btn);
        if(submitBtn!= null) {
            submitBtn.setOnClickListener(this);
        }
    }

    public void renderBlogEntry() {
        String imageApiUrl = getResources().getString(R.string.IP) + "/api/image/get";
//        String imageApiUrl = "http://192.168.0.108:8080/api/image/get";
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
    }



    public void submitForm() {
        String url = getResources().getString(R.string.IP) + "/api/report/submit";
//        String url = "http://192.168.0.108:8080/api/report/submit";
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder .addQueryParameter("userId",activeUserId.toString())
                    .addQueryParameter("mealEntryId",blogEntry.getId().toString())
                    .addQueryParameter("reason",reason);
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
                    System.out.println("Failed to submit report");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast msg = Toast.makeText(FlagBlogEntryActivity.this,"Failed to submit report",Toast.LENGTH_SHORT);
                            msg.show();

                        }
                    });
                    return;
                }

                blogEntry.setFlaggedByActiveUser(true);
                Intent responseIntent = new Intent();
                responseIntent.putExtra("blogEntry",blogEntry);
                setResult(RESULT_OK,responseIntent);
                finish();
            }
        });

    }

    @Override
    public void onClick(View view) {

        if(view == submitBtn) {
            if(offensiveLanguageRadioBtn.isChecked()) {
                reason = "offensive language";
            }
            else if(offensiveImageRadioBtn.isChecked()) {
                reason = "offensive image";
            }
            else if(irrelevantRadioBtn.isChecked()) {
                reason = "irrelevant";
            }
            else if(advertisementRadioBtn.isChecked()) {
                reason = "advertisement";
            }
            else if(otherReasonRadioBtn.isChecked()) {
                reason = otherReasonText.getText().toString();
            }

            // Validate
            Toast errorMsg = Toast.makeText(this, "Please input a reason",Toast.LENGTH_SHORT);
            if(reason == null) {
                errorMsg.show();
                return;
            }
            reason = reason.trim();
            if(reason.isEmpty()) {
                errorMsg.show();
                return;
            }
            submitForm();


        }

    }
}