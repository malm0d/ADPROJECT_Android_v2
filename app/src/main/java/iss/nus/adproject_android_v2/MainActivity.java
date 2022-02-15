package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button getBtn;
    private Button pastmealBtn;
    private Button captureBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getBtn = findViewById(R.id.get);
        getBtn.setOnClickListener(this);

        pastmealBtn = findViewById(R.id.pastMeal);
        pastmealBtn.setOnClickListener(this);

        captureBtn = findViewById(R.id.capture);
        captureBtn.setOnClickListener(this);

    }



    @Override
    public void onClick(View v){

        int id = v.getId();
        if(id == R.id.get){

            Intent intent = new Intent();
            intent.setClass(this,SetGoalActivity.class);
            startActivity(intent);

        }else if (id == R.id.pastMeal){

            Intent intent = new Intent();
            intent.setClass(this,PastMealsActivity.class);
            startActivity(intent);

        }

        if (v == captureBtn) {
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivity(intent);
        }
    }
}