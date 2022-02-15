package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RecSearchResultActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener{

    Button closeBadResultMsg;
    ConstraintLayout popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_search_result);

        Intent intent = getIntent();
        String[] titles = intent.getStringArrayExtra("titles");
        String goodResult = intent.getStringExtra("goodResult");
        System.out.println(goodResult);

        System.out.println("Inside RecSearchResultActivity");
        for (String s : titles){
            System.out.println("out " + s);
        }

        ListView listView = findViewById(R.id.listView);
        if (listView != null) {
            listView.setAdapter(new RecSearchResultAdapter(this, titles));
            listView.setOnItemClickListener(this);
        }

        closeBadResultMsg = findViewById(R.id.badResultOKBtn);
        closeBadResultMsg.setOnClickListener(this);

        popup = findViewById(R.id.badResultPopup);
        if (goodResult.equalsIgnoreCase("false")){
            popup.setVisibility(View.VISIBLE);
            System.out.println("popup");
        }
    }

    @Override
    public void onClick(View view) {
        if (view == closeBadResultMsg){
            popup.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> av,
                            View v, int pos, long id) {

        TextView textView = v.findViewById(R.id.foodTitle);
        String title = textView.getText().toString();

        Toast toast = Toast.makeText(this, title, Toast.LENGTH_SHORT);
        toast.show();
    }
}