package iss.nus.adproject_android_v2.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.time.LocalDateTime;
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
        timestampText.setText(timestamp.toString());

        TextView likes = view.findViewById(R.id.likesText);
        String likeString = "Liked by " + blogEntries.get(pos).getNumberOfLikes() + " users";
        likes.setText((likeString));

        ImageView entryImageView = view.findViewById(R.id.entryImage);
        String imageApiUrl = "http://192.168.0.108:8080/api/image/get";

        String queryString = "?imagePath=";
        String imageDir = "/static";
        Glide   .with(view)
                .load(imageApiUrl + queryString + imageDir + blogEntries.get(pos).getImageURL())
                .placeholder(R.drawable.no_img)
                .into(entryImageView);








        return view;
    }
}
