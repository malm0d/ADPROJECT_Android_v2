package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import iss.nus.adproject_android_v2.helper.BlogEntry;

public class FlagBlogEntryActivity extends AppCompatActivity implements View.OnClickListener{
    private BlogEntry blogEntry;
    private ImageView entryImage;
    private TextView blogTitle;
    private TextView timeStampText;
    private TextView likesText;
    private TextView otherReasonText;
    private String reason;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag_blog_entry);
        Intent intent = getIntent();
        blogEntry = (BlogEntry) intent.getSerializableExtra("blogEntry");

        initUi();
        renderBlogEntry();


    }
    public void initUi() {
        entryImage = findViewById(R.id.entryImage);
        blogTitle = findViewById(R.id.blogTitle);
        timeStampText = findViewById(R.id.timestampText);
        likesText = findViewById(R.id.likesText);
        otherReasonText = findViewById(R.id.other_reason_text);
        submitBtn = findViewById(R.id.submit_report_btn);
        if(submitBtn!= null) {
            submitBtn.setOnClickListener(this);
        }
    }

    public void renderBlogEntry() {

        String imageApiUrl = "http://192.168.0.108:8080/api/image/get";

        String queryString = "?imagePath=";
        String imageDir = "/static";
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

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radio_offensive_language:
                if(checked) {
                    reason = "offensive language";
                }
                break;
            case R.id.radio_offensive_image:
                if(checked) {
                    reason = "offensive image";
                }
                break;
            case R.id.radio_irrelevant:
                if(checked) {
                    reason = "irrelevant";
                }
                break;
            case R.id.radio_advertisement:
                if(checked) {
                    reason = "advertisement";
                }
                break;
            case R.id.radio_other:
                if(checked) {
                    reason = otherReasonText.getText().toString();
                }
                break;
        }
//        System.out.println(reason);
    }

    public void submitForm() {

    }

    @Override
    public void onClick(View view) {

        if(view == submitBtn) {
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
        }

    }
}