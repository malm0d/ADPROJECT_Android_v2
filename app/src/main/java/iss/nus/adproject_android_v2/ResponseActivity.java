package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResponseActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView responseTextView;
    private Button okResponseBtn;
    private String responseResult;
    private String recommendation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        responseTextView = findViewById(R.id.response);
        okResponseBtn = findViewById(R.id.okResponseBtn);
        initElements();

        Intent intent = getIntent();
        responseResult = intent.getStringExtra("responseResult");
        recommendation = recommend(responseResult);
        responseTextView.setText(recommendation);
    }

    private void initElements() {
        okResponseBtn.setOnClickListener(this);
    }

    private String recommend(String responseResult) {
        if (responseResult.equals("true")) {
            return "On-Track";
        }
        else {
            return "Off-Track";
        }
    }

    @Override
    public void onClick(View view) {
        if (view == okResponseBtn) {
            Intent intent = new Intent(this, PastMealsActivity.class);
            startActivity(intent);
        }
    }
}