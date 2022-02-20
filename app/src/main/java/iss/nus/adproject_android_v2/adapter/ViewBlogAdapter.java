package iss.nus.adproject_android_v2.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import iss.nus.adproject_android_v2.R;
import iss.nus.adproject_android_v2.helper.BlogEntry;


public class ViewBlogAdapter extends ArrayAdapter<Object> {
    private final Context context;
    private List<BlogEntry> blogEntries;
    public ViewBlogAdapter(Context context, List<BlogEntry> blogEntries) {

        super(context, R.layout.blog_row);
        this.context = context;
        this.blogEntries = blogEntries;
        addAll(new Object[blogEntries.size()]);
    }
    public void setBlogEntries(List<BlogEntry> blogEntries){
        this.blogEntries = blogEntries;
    }

    @NonNull
    @Override
    public View getView(int pos, View view, @NonNull ViewGroup parent){

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.blog_row,parent,false);
        }
        TextView blogTitle = view.findViewById(R.id.blogTitle);
        blogTitle.setText(blogEntries.get(pos).getTitle());

        TextView timestampText = view.findViewById(R.id.timestampText);
        LocalDateTime timestamp = blogEntries.get(pos).getTimeStamp();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a");
        String formattedTimestamp = timestamp.format(formatter);
        timestampText.setText(formattedTimestamp);

        TextView likes = view.findViewById(R.id.likesText);
        String likeString = "Liked by " + blogEntries.get(pos).getNumberOfLikes() + " users";
        likes.setText((likeString));

        ImageView entryImageView = view.findViewById(R.id.entryImage);
        String imageApiUrl = "http://192.168.0.108:8080/api/image/get";
        String queryString = "?imagePath=";

        Glide   .with(view)
                .load(imageApiUrl + queryString + blogEntries.get(pos).getImageURL())
                .placeholder(R.drawable.no_img)
                .into(entryImageView);


        TextView authorText = view.findViewById(R.id.rowAuthor);
        String authorString = "by " + blogEntries.get(pos).getAuthorUsername();
        authorText.setText(authorString);

        ImageView likeBtn = view.findViewById(R.id.rowLikeBtn);
        if(blogEntries.get(pos).isLikedByActiveUser()){
            likeBtn.setBackgroundResource(R.drawable.thumb_logo_blue_fill);
        }
        else {
            likeBtn.setBackgroundResource(R.drawable.thumb_logo_no_fill);
        }

        ImageView flagBtn = view.findViewById(R.id.rowFlagBtn);
        if(blogEntries.get(pos).isFlaggedByActiveUser()) {
            flagBtn.setBackgroundResource(R.drawable.flag_logo_red_fill);
        }
        else {
            flagBtn.setBackgroundResource(R.drawable.flag_logo_no_fill);
        }

        return view;
    }
}