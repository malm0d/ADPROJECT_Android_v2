package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CaptureActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mealWithPhotoBtn;
    private Button systemRecommendBtn;
    private final int REQ_CAMERA_PERMISSION = 8;
    private final int REQ_CAMERA = 21;
    private String currentPhotoPath;
    private Uri photoURI;
    private String timeStamp;
    private String userId;
    private int goalId;
    NavigationBarView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        userId = pref.getString("userId", "");
        //goalId = intent.getIntExtra("goalId", -1);
        //if (userId == null || userId.equals("") || goalId == -1) {
        //    Toast.makeText(this, "No valid user or goal", Toast.LENGTH_LONG).show();
        //    finish();
        //}

        mealWithPhotoBtn = findViewById(R.id.mealWithPhotoBtn);
        systemRecommendBtn = findViewById(R.id.systemRecommendBtn);
        initButtons();

        //bottom navigation bar
        bottomNavigation = findViewById(R.id.bottom_navigation);
        //set Setting selected
        bottomNavigation.setSelectedItemId(R.id.addMenu);

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.mealMenu: break;
                    case R.id.pathMenu: break;
                    case R.id.addMenu:
                        finish();
                        startActivity(getIntent());
                        break;
                    case R.id.friendsMenu: break;
                    case R.id.settingsMenu:
                        Intent settings = new Intent(getApplicationContext(), SettingPage.class);
                        startActivity(settings);
                        break;
                }
                return false;
            }
        });

    }

    private void initButtons() {
        mealWithPhotoBtn.setOnClickListener(this);
        systemRecommendBtn.setOnClickListener(this);
    }

    private File createImageFile() throws IOException {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter format_ = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        timeStamp = localDateTime.format(format_);
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @SuppressWarnings("deprecation")
    private void startCameraActivity() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error occurred while creating the File");
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "iss.nus.adproject_android_v2.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQ_CAMERA);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA && resultCode == RESULT_OK) {
            //galleryAddPic();
            startSubmitActivity();
        }
    }

    protected void startSubmitActivity() {
        Intent intent = new Intent(this, SubmitActivity.class);
        intent.putExtra("currentPhotoPath", currentPhotoPath);
        intent.putExtra("photoURI", photoURI);
        intent.putExtra("timeStamp", timeStamp);
        intent.putExtra("userId", userId);
        //intent.putExtra("goalId", goalId);
        startActivity(intent);
    }

    /*
    //Work on this after MVP
    private void galleryAddPic() {
        //Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        //File f = new File(currentPhotoPath);
        //Uri contentUri = Uri.fromFile(f);
        //mediaScanIntent.setData(contentUri);
        //this.sendBroadcast(mediaScanIntent);

        //String[] pathArray = {currentPhotoPath};
        //MediaScannerConnection.scanFile(this, pathArray, null, null);

        FileOutputStream fos;
        try {
            File f = new File(currentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            ContentResolver resolver = getContentResolver();
            fos = (FileOutputStream) resolver.openOutputStream(contentUri);
        }
        catch (Exception e) {
            e.printStackTrace();
            String msg = "Error saving image";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }
     */

    @Override
    public void onClick(View view) {
        if (view == mealWithPhotoBtn) {
            String[] permissions = {Manifest.permission.CAMERA};
            if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                startCameraActivity();
            }
            else {
                ActivityCompat.requestPermissions(this, permissions, REQ_CAMERA_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String msg = "";

        if (requestCode == REQ_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraActivity();
            }
            else {
                msg = "Permission to use camera denied";
            }
        }

        if (msg.isEmpty() == false) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

}