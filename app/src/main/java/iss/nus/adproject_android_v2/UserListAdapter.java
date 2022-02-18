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

public class UserListAdapter extends ArrayAdapter<Object> {
    private final Context context;
    private final ArrayList<UserHelper> users;

    public UserListAdapter(Context context, ArrayList<UserHelper> users) {
        super(context, R.layout.userrow);
        this.context = context;
        this.users = users;
        addAll(new Object[users.size()]);
    }

    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.userrow, parent, false);
        }
        TextView userName = view.findViewById(R.id.userName);
        userName.setText("Name: " + users.get(pos).getName());

        TextView userUsername = view.findViewById(R.id.userUsername);
        userUsername.setText("Username: " + users.get(pos).getUsername());

        ImageView addUserBtn = view.findViewById(R.id.addUserBtn);
        addUserBtn.setBackgroundResource(R.drawable.add_user);

        ImageView userImage = view.findViewById(R.id.userImage);
        String url = getContext().getResources().getString(R.string.IP) + "/api/friends/profilePic";
        String queryString = "?fileName=";
        String fileName = users.get(pos).getProfilePic();
        String userId = users.get(pos).getUserId();

        Glide.with(view)
                .load(url + queryString + fileName + "&userId=" + userId)
                .placeholder(R.drawable.default_profile_picture)
                .into(userImage);

        return view;
    }
}
