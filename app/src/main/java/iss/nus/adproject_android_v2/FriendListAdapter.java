package iss.nus.adproject_android_v2;

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

import java.util.ArrayList;

import iss.nus.adproject_android_v2.ui.ImageViewPlus;

public class FriendListAdapter extends ArrayAdapter<Object> {

    private final Context context;
    private final ArrayList<UserHelper> friends;

    public FriendListAdapter(Context context, ArrayList<UserHelper> friends) {
        super(context, R.layout.friendrow);
        this.context = context;
        this.friends = friends;
        addAll(new Object[friends.size()]);
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.friendrow, parent, false);
        }
        TextView friendName = view.findViewById(R.id.friendName);
        friendName.setText("Name: " + friends.get(pos).getName());

        TextView friendUsername = view.findViewById(R.id.friendUsername);
        friendUsername.setText("Username: " + friends.get(pos).getUsername());

        ImageView friendDetailBtn = view.findViewById(R.id.friendDetailBtn);
        friendDetailBtn.setBackgroundResource(R.drawable.view_detail);

        ImageViewPlus friendImage = view.findViewById(R.id.friendImage);
        String url = "http://192.168.1.8:8080/api/friends/profilePic";
        String queryString = "?fileName=";
        String fileName = friends.get(pos).getProfilePic();
        String userId = friends.get(pos).getUserId();

        Glide.with(view)
                .load(url + queryString + fileName + "&userId=" + userId)
                .placeholder(R.drawable.default_profile_picture)
                .into(friendImage);

        return view;
    }
}
