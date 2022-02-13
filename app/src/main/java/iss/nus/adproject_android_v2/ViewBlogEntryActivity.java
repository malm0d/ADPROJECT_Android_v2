package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import iss.nus.adproject_android_v2.datepicker.CustomDatePicker;
import iss.nus.adproject_android_v2.helper.BlogEntry;

public class ViewBlogEntryActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView mealDetailImg;

    TextView mealTitle;

    TextView mealtime;
    ImageButton likeBtn;
    ImageButton commentBtn;
    ImageButton flagBtn;

    EditText mealDesc;
    TextView entryAuthor;


    Switch publicSwitch;

    Button saveChage;

    MealHelper meal;

    private CustomDatePicker mTimerPicker;

    private BlogEntry blogEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog_entry);

        Intent intent = getIntent();
        blogEntry = (BlogEntry) intent.getSerializableExtra("blogEntry");
        initTheUi();
        showData();


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






        likeBtn.setBackgroundResource(R.drawable.thumb_logo_no_fill);
        commentBtn.setBackgroundResource(R.drawable.speech_bubble);
        flagBtn.setBackgroundResource(R.drawable.flag_logo_no_fill);

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
        commentBtn.setOnClickListener(this);


    }
    @Override
    public void onClick(View v){

    }
}