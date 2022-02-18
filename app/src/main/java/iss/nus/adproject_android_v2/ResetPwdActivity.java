package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResetPwdActivity extends AppCompatActivity {

    TextView mSendEmailOutcome;
    Button mSendEmailBtn;
    Button mRedirectLoginBtn;
    EditText mEmailLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);

        mEmailLink = findViewById(R.id.emailLink);
        mSendEmailOutcome = findViewById(R.id.sendEmailOutcome);
        mSendEmailBtn = findViewById(R.id.sendEmailBtn);
        mSendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailLink.getText().toString();
                if(validateEmail(email)) {
                    sendEmail(email);
                }
            }
        });
    }

    private boolean validateEmail(String email) {
        if (email == null || email.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void sendEmail(String email) {
        String url = getResources().getString(R.string.IP) + "/api/login/reset_email";
        RequestPost(url, email);
    }

    private void RequestPost(String url, String email) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("email", email);

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
                if (response.isSuccessful()) {
                    final String res = response.body().string();
                    System.out.println("Information returned from server:");
                    System.out.println(res);

                    try {
                        JSONObject jObj = new JSONObject(res);
                        if (jObj.getInt("status") == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSendEmailOutcome.setText("Reset password link has been sent to email. Please check.");
                                    mSendEmailOutcome.setTextColor(Color.GREEN);
                                    mSendEmailOutcome.setVisibility(View.VISIBLE);

                                    mEmailLink.getText().clear();

                                    mRedirectLoginBtn = findViewById(R.id.backToLoginBtn);
                                    mRedirectLoginBtn.setVisibility(View.VISIBLE);
                                    mRedirectLoginBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(ResetPwdActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    });

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSendEmailOutcome.setText("Invalid email");
                                    mSendEmailOutcome.setTextColor(Color.RED);
                                    mSendEmailOutcome.setVisibility(View.VISIBLE);

                                    mEmailLink.getText().clear();

                                    mRedirectLoginBtn = findViewById(R.id.backToLoginBtn);
                                    mRedirectLoginBtn.setVisibility(View.VISIBLE);
                                    mRedirectLoginBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(ResetPwdActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    });

                                    mSendEmailOutcome.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mSendEmailOutcome.setVisibility(View.INVISIBLE);
                                        }
                                    }, 5000);
                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}