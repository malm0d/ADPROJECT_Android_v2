package iss.nus.adproject_android_v2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import iss.nus.adproject_android_v2.datepicker.CustomDatePicker;
import iss.nus.adproject_android_v2.helper.BlogEntry;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewBlogEntryActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView mealDetailImg;

    TextView mealTitle;

    TextView mealtime;
    ImageButton likeBtn;
    ImageButton commentBtn;
    ImageButton flagBtn;

    EditText mealDesc;
    TextView entryAuthor;

    Button saveChage;

    private BlogEntry blogEntry;
    private Integer activeUserId;
    private ActivityResultLauncher<Intent> rlFlagBlogEntryActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog_entry);
        System.out.println("Executing onCreate");

        Intent intent = getIntent();
        blogEntry = (BlogEntry) intent.getSerializableExtra("blogEntry");
        activeUserId = intent.getIntExtra("activeUserId",0);



        rlFlagBlogEntryActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == AppCompatActivity.RESULT_OK){
                        Intent data = result.getData();
                        blogEntry = (BlogEntry) data.getSerializableExtra("blogEntry");
                    }
                }
        );
    }
    @Override
    protected void onResume() {

        super.onResume();
        initTheUi();
        showData();
    }
    public void initTheUi(){
        mealDetailImg = findViewById(R.id.mealdetailimage);
        mealTitle = findViewById(R.id.detailMealtitle);
        mealDesc = findViewById(R.id.mealDetailDes);
        mealtime = findViewById(R.id.detailMealtime);
        saveChage = findViewById(R.id.submitChange);

        likeBtn = findViewById(R.id.likeBtn);
        commentBtn = findViewById(R.id.commentBtn);
        flagBtn = findViewById(R.id.flagBtn);
        entryAuthor = findViewById(R.id.entryAuthor);

        likeBtn.setOnClickListener(this);
        if(blogEntry.isFlaggedByActiveUser()) {
            flagBtn.setOnClickListener(null);
        }
        else {
            flagBtn.setOnClickListener(this);
        }
        commentBtn.setOnClickListener(this);


    }
    public void showData(){
        String imageApiUrl = "http://192.168.0.108:8080/api/image/get";

        String queryString = "?imagePath=";
        String imageDir = "/static";
        Glide   .with(this)
                .load(imageApiUrl + queryString + imageDir + blogEntry.getImageURL())
                .placeholder(R.drawable.no_img)
                .into(mealDetailImg);


        mealTitle.setText(blogEntry.getTitle());
        entryAuthor.setText("by " + blogEntry.getAuthorUsername());
        mealDesc.setText(blogEntry.getDescription());

        LocalDateTime timestamp = blogEntry.getTimeStamp();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a");
        String formattedTimestamp = timestamp.format(formatter);
        mealtime.setText(formattedTimestamp);
        if(blogEntry.isLikedByActiveUser()) {
            likeBtn.setBackgroundResource(R.drawable.thumb_logo_blue_fill);
        }
        else {
            likeBtn.setBackgroundResource(R.drawable.thumb_logo_no_fill);
        }
        if(blogEntry.isFlaggedByActiveUser()) {
            flagBtn.setBackgroundResource(R.drawable.flag_logo_red_fill);
        }
        else{
            flagBtn.setBackgroundResource(R.drawable.flag_logo_no_fill);
        }
        commentBtn.setBackgroundResource(R.drawable.speech_bubble);


    }



    @Override
    public void onClick(View v){
        if (v == likeBtn) {
            toggleLike();
        }
        else if (v == flagBtn) {
            Intent intent = new Intent(this,FlagBlogEntryActivity.class);
            intent.putExtra("blogEntry",blogEntry);
            intent.putExtra("activeUserId",activeUserId);

            rlFlagBlogEntryActivity.launch(intent);
        }
        else if (v == commentBtn) {
            Intent intent = new Intent(this,CommentBlogEntryActivity.class);
            intent.putExtra("blogEntry",blogEntry);
            intent.putExtra("activeUserId",activeUserId);
            startActivity(intent);
        }


    }

    public void toggleLike() {
        String operation = null;
        Integer drawableId = null;
        Boolean likeEndState = null;
        if(blogEntry.isLikedByActiveUser()) {
            //If currently liked, unlike
            operation = "unlike";
            likeEndState = false;
            drawableId =  R.drawable.thumb_logo_no_fill;
        }
        else {
            //If currently unliked, like
            operation = "like";
            likeEndState = true;
            drawableId =  R.drawable.thumb_logo_blue_fill;
        }



        String url = "http://192.168.0.108:8080/api/likes/" + operation;
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        httpBuilder .addQueryParameter("userId",activeUserId.toString())
                    .addQueryParameter("mealEntryId",blogEntry.getId().toString());

        HttpUrl httpUrl = httpBuilder.build();
        //Define empty request body
        RequestBody reqbody = RequestBody.create(null, new byte[0]);

        Request request = new Request   .Builder()
                                        .url(httpUrl)
                                        .post(reqbody)
                                        .build();
        Call call = client.newCall(request);
        Integer finalDrawableId = drawableId;
        Boolean finalLikeEndState = likeEndState;
        String finalOperation = operation;
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(finalOperation + " operation failed");
                //On failure doesn't seem to work


            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if(response.code() != 200) {
                    System.out.println(finalOperation + " operation failed");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast msg = Toast.makeText(ViewBlogEntryActivity.this,finalOperation + " operation failed",Toast.LENGTH_SHORT);
                            msg.show();

                        }
                    });
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        blogEntry.setLikedByActiveUser(finalLikeEndState);
                        likeBtn.setBackgroundResource(finalDrawableId);

                    }
                });

            }
        });



    }
}