package iss.nus.adproject_android_v2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.IOException;

import iss.nus.adproject_android_v2.ui.ImageViewPlus;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {



    //    TextView profileValidation;

    ImageButton UploadProfilePic;
    EditText NameEdit, DateOfBirthEdit, heightEdit, weightedit;
    Button submitProfileChangeBtn;
    private Context context;
    Uri selectedImage;
    ImageViewPlus profilePhoto;
    private User user;
    NavigationBarView bottomNavigation;
    private String path = "";
    private String fileName = "";
    private static final int PICK_IMAGE_REQUEST = 9544;
    private String shareusername;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        context = this;
//        profileValidation =findViewById(R.id.profileValidationText);
//        profileValidation.setVisibility(View.INVISIBLE);

        SharedPreferences pref = getSharedPreferences("user_login_info", MODE_PRIVATE);
        shareusername = pref.getString("username", "");

        UploadProfilePic = findViewById(R.id.uploadProfilePic);
        UploadProfilePic.setOnClickListener(this);
        submitProfileChangeBtn = findViewById(R.id.submitProfileChange);
        submitProfileChangeBtn.setOnClickListener(this);

        //get intent
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("User");
        initView(user);
        initBoomNacigation();
}

    private void initBoomNacigation(){
        //bottom navigation bar
        bottomNavigation = findViewById(R.id.bottom_navigation);
        //set Setting selected
        bottomNavigation.setSelectedItemId(R.id.settingsMenu);

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.mealMenu:
                        Intent pastMeal = new Intent(getApplicationContext(), PastMealsActivity.class);
                        startActivity(pastMeal);
                        break;
                    case R.id.pathMenu:
                        Intent currentPath = new Intent(getApplicationContext(), ViewGoalActivity.class);
                        startActivity(currentPath);
                        break;
                    case R.id.addMenu:
                        Intent add = new Intent(getApplicationContext(), CaptureActivity.class);
                        startActivity(add);
                        break;
                    case R.id.friendsMenu:
                        Intent friends = new Intent(getApplicationContext(), ManageSocialsActivity.class);
                        startActivity(friends);
                        break;
                    case R.id.settingsMenu:
                        Intent settings = new Intent(getApplicationContext(), SettingPage.class);
                        startActivity(settings);
                        break;
                }
                return false;
            }
        });
    }

    protected void initView(User user) {

        NameEdit = findViewById(R.id.NameEdit);
        DateOfBirthEdit = findViewById(R.id.DateOfBirthEdit);
        heightEdit = findViewById(R.id.heightEdit);
        weightedit = findViewById(R.id.weightedit);
        NameEdit.setText(user.getName());
        DateOfBirthEdit.setText(user.getDateOfBirth());
        heightEdit.setText(user.getHeight());
        weightedit.setText(user.getWeight());
        fileName = user.getProfilePic();
        showphoto();
    }

    public void showphoto(){
        String imageApiUrl = "http://192.168.31.50:8888/api/image/get";

        profilePhoto = findViewById(R.id.profilePhoto);
        String queryString = "?imagePath=";
        String imageDir = "/static/blog/images/";
        String profilePic = user.getProfilePic();
        Glide.with(this)
                .load(imageApiUrl + queryString + imageDir + profilePic)
                .placeholder(R.drawable.no_img)
                .into(profilePhoto);

    }
    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.submitProfileChange) {

            String url = getResources().getString(R.string.IP) + "/api/saveProfile";
            String UserName = shareusername;
            String Name = NameEdit.getText().toString();
            String dateOfBirth = DateOfBirthEdit.getText().toString();
            String Height = heightEdit.getText().toString();
            String Weight = weightedit.getText().toString();
            if (Name.isEmpty() || dateOfBirth.isEmpty() || Height.isEmpty() || Weight.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Please fill up every row", Toast.LENGTH_SHORT).show();
//                profileValidation =findViewById(R.id.profileValidationText);
//                profileValidation.setVisibility(View.VISIBLE);
            } else {

                RequestPost(url, UserName, Name, dateOfBirth, Height, Weight);

            }


        }

        if (id == R.id.uploadProfilePic) {

                selectImage();


        }
    }

    //select image from document
    public void selectImage() {

        verifyStoragePermissions(EditProfileActivity.this);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                selectedImage = data.getData();
                System.out.println("Uri: " + selectedImage.toString());
                profilePhoto = findViewById(R.id.profilePhoto);
                profilePhoto.setImageURI(selectedImage);
                fileName = DocumentFile.fromSingleUri(getApplicationContext(), selectedImage).getName();
                String path0 = uriToFilePath(selectedImage);
                path = path0.substring(0,path0.length()-9) + fileName;
            }
        }
    }

    private String uriToFilePath(Uri uri) {
        String path = null;
        if ((Build.VERSION.SDK_INT < 19) && (Build.VERSION.SDK_INT > 11)) {
            path = getRealPath_API11to18(this, uri);
        } else {
            path = getFilePath(this,uri);
        }
        return path;
    }
    public static String getRealPath_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;
        CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj,null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getFilePath(Context context, Uri uri) {
        String filePath = "";
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            String[] splits = wholeID.split(":");
            if (splits.length == 2) {
                String id = splits[1];
                String[] column = {MediaStore.Images.Media.DATA};
                String sel = MediaStore.Images.Media._ID + "= ?";
                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, null, null, null);
                if (null != cursor) {
                if (cursor.moveToFirst()) {

                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    filePath = cursor.getString(index);
                }
                cursor.close();
            }
        }
        } else {
            filePath = uri.getPath();
        }
        return filePath;
    }

    //verify permissions
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void RequestPost(String url, String UserName, String Name, String dateOfBirth, String Height, String Weight) {

        OkHttpClient client = new OkHttpClient();
        final File imageFile = new File(path);

        if (!imageFile.exists()) {
            Toast.makeText(getApplicationContext(), "Please set profile picture", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody fileBody = RequestBody.create(imageFile, MediaType.parse("multipart/form-data"));
        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("multipartFile", fileName, fileBody)
                .addFormDataPart("fileName", fileName)
                .addFormDataPart("imageURL", path)
                .addFormDataPart("Name", Name)
                .addFormDataPart("UserName", UserName)
                .addFormDataPart("dateOfBirth", dateOfBirth)
                .addFormDataPart("Height", Height)
                .addFormDataPart("Weight", Weight)
                .build();

        Request request = new Request.Builder().url(url).post(multipartBody).build();
//        FormBody.Builder formBuilder = new FormBody.Builder();
//
//        formBuilder.add("UserName", UserName);
//        formBuilder.add("Name", Name);
//        formBuilder.add("dateOfBirth", dateOfBirth);
//        formBuilder.add("Height", Height);
//        formBuilder.add("Weight", Weight);
//
//        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
//        System.out.println(fileName);
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



//    public Boolean validateEntries(String username, String email, String password) {
//        if (username == null || username.trim().length() == 0) {
//            Toast.makeText(getApplicationContext(), "Username is required", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (email == null || email.trim().length() == 0) {
//            Toast.makeText(getApplicationContext(), "Email is required", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (password == null || password.trim().length() == 0) {
//            Toast.makeText(getApplicationContext(), "Password is required", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }