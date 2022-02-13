package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import iss.nus.adproject_android_v2.helper.BlogEntry;

public class CommentBlogEntryActivity extends AppCompatActivity {
    private BlogEntry blogEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_blog_entry);
        Intent intent = getIntent();
        blogEntry = (BlogEntry) intent.getSerializableExtra("blogEntry");

    }
}