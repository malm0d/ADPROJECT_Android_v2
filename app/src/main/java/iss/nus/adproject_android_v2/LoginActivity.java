package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    EditText mUsernameTxt;
    EditText mPasswordTxt;
    Button mLoginBtn;
    Button mCreateAccBtn;
    TextView mForgotPwdTxt;
    TextView mInvalidLoginTxt;

    UserHelper user = new UserHelper();

    private String login_outcome = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameTxt = findViewById(R.id.username);
        mPasswordTxt = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateAccBtn = findViewById(R.id.createAccBtn);
        mInvalidLoginTxt = findViewById(R.id.invalid_login);
        mForgotPwdTxt = findViewById(R.id.forgotpwd);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsernameTxt.getText().toString();
                String password = mPasswordTxt.getText().toString();

                if(validateLogin(username, password)) {
                    authenticate(username, password);
                }
            }
        });

        mForgotPwdTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startResetPwdActivity();
            }
        });
    }

    private Boolean validateLogin(String username, String password) {
        if (username == null || username.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void authenticate(String username, String password) {
        String url = "http://192.168.1.107:8080/api/login/auth";
        RequestPost(url, username, password);
        if (login_outcome.isEmpty()) {
            mInvalidLoginTxt.setVisibility(View.VISIBLE);
            mUsernameTxt.getText().clear();
            mPasswordTxt.getText().clear();
            mInvalidLoginTxt.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mInvalidLoginTxt.setVisibility(View.INVISIBLE);
                }
            }, 5000);
        } else {
            try {
                JSONObject jObj = new JSONObject(login_outcome);
                user.setUserId(jObj.getInt("userId"));
                user.setUsername(jObj.getString("username"));
                user.setName(jObj.getString("name"));
                user.setProfilePic(jObj.getString("profilePic"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startDashboardActivity();
        }
    }

    private void startResetPwdActivity() {
        Intent intent = new Intent(this, ResetPwdActivity.class);
        startActivity(intent);
    }

    private void startDashboardActivity() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void RequestPost(String url, String username, String password) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("username", username);
        formBuilder.add("password", password);

        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "server error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
                System.out.println("Information returned from server:");
                System.out.println(res);
                login_outcome = res;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }
}