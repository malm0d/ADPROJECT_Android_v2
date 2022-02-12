package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.node.IntNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DashboardActivity extends AppCompatActivity {

    UserHelper user = new UserHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Intent intent = getIntent();
        user = (UserHelper) intent.getSerializableExtra("user");

        TextView show_userId = findViewById(R.id.show_userId);
        TextView show_username = findViewById(R.id.show_username);
        TextView show_name = findViewById(R.id.show_name);
        TextView show_profilePic = findViewById(R.id.show_profilePic);
        Button logout_btn = findViewById(R.id.dashboard_logout);

        show_userId.setText("User ID: " + user.getUserId());
        show_username.setText("Username: " + user.getUsername());
        show_name.setText("Name: " + user.getName());
        show_profilePic.setText("Profile Pic: " + user.getProfilePic());

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();

                startLoginActivity();
            }
        });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}