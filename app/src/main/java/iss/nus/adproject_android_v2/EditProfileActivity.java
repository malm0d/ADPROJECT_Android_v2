package iss.nus.adproject_android_v2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import iss.nus.adproject_android_v2.ui.ImageViewPlus;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    EditText NameEdit, DateOfBirthEdit,heightEdit,weightedit;
    Button submitProfileChangeBtn;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        context = this;

        submitProfileChangeBtn = findViewById(R.id.submitProfileChange);
        submitProfileChangeBtn.setOnClickListener(this);
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("User");
        initView(user);
    }

    protected void initView(User user){

        ImageViewPlus profilephoto = findViewById(R.id.profilePhoto);
        profilephoto.setImageResource(R.drawable.cat01);
        NameEdit = findViewById(R.id.NameEdit);
        DateOfBirthEdit = findViewById(R.id.DateOfBirthEdit);
        heightEdit = findViewById(R.id.heightEdit);
        weightedit = findViewById(R.id.weightedit);
        NameEdit.setText(user.getName());
        DateOfBirthEdit.setText(user.getDateOfBirth());
        heightEdit.setText(user.getHeight());
        weightedit.setText(user.getWeight());
    }

    @Override
    public void onClick(View v){

        int id = v.getId();
        if(id == R.id.submitProfileChange){

            String url = "http://192.168.31.50:8888/api/saveProfile";
            String UserName = "jake";
            String Name = NameEdit.getText().toString();
            String dateOfBirth = DateOfBirthEdit.getText().toString();
            String Height = heightEdit.getText().toString();
            String Weight =   weightedit.getText().toString();
            if (Name.isEmpty() || dateOfBirth.isEmpty() ||Height.isEmpty()||Weight.isEmpty()){
                Toast.makeText(EditProfileActivity.this, "Please fill up every row", Toast.LENGTH_SHORT).show();
            }else {

                RequestPost(url,UserName,Name,dateOfBirth,Height,Weight);

            }


        }
    }



    private void RequestPost(String url,String UserName,String Name,String dateOfBirth,String Height, String Weight){

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("UserName", UserName);
        formBuilder.add("Name", Name);
        formBuilder.add("dateOfBirth", dateOfBirth);
        formBuilder.add("Height", Height);
        formBuilder.add("Weight", Weight);

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
                                Toast.makeText(EditProfileActivity.this, "server error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
                System.out.println("This information return from server side");
                System.out.println(res);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(EditProfileActivity.this, "success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(context, UserProfile.class);
                        startActivity(intent);
                    }
                });
            }
        });


    }
}