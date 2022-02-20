package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResponseActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView responseTextView;
    private TextView title1;
    private Button okResponseBtn;
    private String responseResult;
    private String recommendation;
    private boolean setNewGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        responseTextView = findViewById(R.id.response);
        title1 = findViewById(R.id.title1);
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
            setNewGoal = false;
            return "Off-Track";
        }
        else if (responseResult.equals("false")){
            setNewGoal = false;
            return "On-Track";
        }
        else {
            setNewGoal = true;
            title1.setText("Congratulations on completing a goal!");
            return "Set a new goal";
        }
    }

    @Override
    public void onClick(View view) {
        if (view == okResponseBtn) {
            if (setNewGoal == false) {
                Intent intent = new Intent(this, PastMealsActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(this, SetGoalActivity.class);
                startActivity(intent);
            }
        }
    }
}