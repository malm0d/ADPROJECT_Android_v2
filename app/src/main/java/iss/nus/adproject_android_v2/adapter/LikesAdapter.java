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

public class LikesAdapter extends ArrayAdapter<Object> {
    private final Context context;
    private List<String> usernames;


    public LikesAdapter(Context context, List<String> usernames) {
        super(context, R.layout.like_row);

        this.context = context;
        this.usernames = usernames;
        addAll(new Object[usernames.size()]);
    }
    @NonNull
    @Override
    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.like_row,parent,false);
        }
        TextView likeUsernameText = view.findViewById(R.id.likeUsernameText);
        likeUsernameText.setText(usernames.get(pos));




        return view;

    }
}
