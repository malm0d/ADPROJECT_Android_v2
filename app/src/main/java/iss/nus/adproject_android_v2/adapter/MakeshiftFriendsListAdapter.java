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

import iss.nus.adproject_android_v2.R;

public class MakeshiftFriendsListAdapter  extends ArrayAdapter<Object> {

    private final Context context;

    protected String[] friendsUsernames;

    public MakeshiftFriendsListAdapter(Context context, String[] friendsUsernames) {
        super(context, R.layout.friends_list_row);
        this.context = context;
        this.friendsUsernames = friendsUsernames;


        addAll(new Object[friendsUsernames.length]);
    }
    @NonNull
    @Override
    public View getView(int pos, View view, @NonNull ViewGroup parent){

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.friends_list_row,parent,false);
        }




        // set the text for TextView
        TextView textView = view.findViewById(R.id.friendsUsernameTextView);
        textView.setText(friendsUsernames[pos]);

        return view;
    }
}
