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

public class ReceivedReqAdapter extends ArrayAdapter<Object> {

    private final Context context;
    private final ArrayList<UserHelper> requests;

    public ReceivedReqAdapter(Context context, ArrayList<UserHelper> requests) {
        super(context, R.layout.userrow);
        this.context = context;
        this.requests = requests;
        addAll(new Object[requests.size()]);
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.userrow, parent, false);
        }

        TextView userName = view.findViewById(R.id.userName);
        userName.setText("Name: " + requests.get(pos).getName());

        TextView userUsername = view.findViewById(R.id.userUsername);
        userUsername.setText("Username: " + requests.get(pos).getUsername());

        ImageView reqActionBtn = view.findViewById(R.id.addUserBtn);
        reqActionBtn.setBackgroundResource(R.drawable.accept_reject);

        ImageViewPlus userImage = view.findViewById(R.id.userImage);
        String url = context.getResources().getString(R.string.IP) + "/api/friends/profilePic";
        String queryString = "?fileName=";
        String fileName = requests.get(pos).getProfilePic();
        String userId = requests.get(pos).getUserId();

        Glide.with(view)
                .load(url + queryString + fileName + "&userId=" + userId)
                .placeholder(R.drawable.default_profile_picture)
                .into(userImage);

        return view;
    }
}
