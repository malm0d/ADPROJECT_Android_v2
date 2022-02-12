package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import iss.nus.adproject_android_v2.adapter.MakeshiftFriendsListAdapter;

public class MakeshiftFriendsListActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener{
    private final String[] friendsUsernames = {
            "james", "bond_james", "jake", "jay", "jane", "jane123", "john", "admin","Feed"
    };
    private final Integer[] friendsUserIds = {1,2,4,5,6,7,8,9,0};

    private final Integer activeUserId = 3;
    private final String activeUserName = "jill";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeshift_friends_list);

        MakeshiftFriendsListAdapter adapter = new MakeshiftFriendsListAdapter(this,friendsUsernames);
        ListView listView = findViewById(R.id.friendsListView);
        if(listView != null) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long id)
    {
        // view v is the selected row
        TextView textView = v.findViewById(R.id.friendsUsernameTextView);
        String str = textView.getText().toString();
        Toast msg = Toast.makeText(this,str,Toast.LENGTH_SHORT);
        msg.show();

        Integer friendUserId = friendsUserIds[pos];
        String friendUsername = friendsUsernames[pos];
        String activeUsername = "jill";
        Intent intent = new Intent(this, ViewBlogActivity.class);
        intent.putExtra("activeUserId", activeUserId);
        intent.putExtra("friendUserId", friendUserId);
        intent.putExtra("friendUsername", friendUsername);
        intent.putExtra("activeUsername",activeUsername);
        startActivity(intent);



    }
}