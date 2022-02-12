package iss.nus.adproject_android_v2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import iss.nus.adproject_android_v2.ui.ImageViewPlus;

public class EditProfileActivity extends AppCompatActivity {

    EditText NameEdit, DateOfBirthEdit,heightEdit,weightedit;
    Button submitProfileChangeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);



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
}