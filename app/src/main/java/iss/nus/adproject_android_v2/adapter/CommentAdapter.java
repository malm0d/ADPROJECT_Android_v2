package iss.nus.adproject_android_v2.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import iss.nus.adproject_android_v2.R;
import iss.nus.adproject_android_v2.helper.Comment;

public class CommentAdapter extends ArrayAdapter<Object> {
    private final Context context;
    private List<Comment> comments;


    public CommentAdapter(Context context, List<Comment> comments){
        super(context, R.layout.comment_row);
        this.context = context;
        this.comments = comments;
        addAll(new Object[comments.size()]);

    }

    @NonNull
    @Override
    public View getView(int pos, View view, @NonNull ViewGroup parent){
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.comment_row,parent,false);
        }

        TextView authorUsername = view.findViewById(R.id.author_username);
        authorUsername.setText(comments.get(pos).getAuthorUsername()+":");

        TextView caption = view.findViewById(R.id.caption_text);
        caption.setText(comments.get(pos).getCaption());

        return view;

    }
}
