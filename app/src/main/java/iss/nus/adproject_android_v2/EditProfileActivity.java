package iss.nus.adproject_android_v2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.IOException;

import iss.nus.adproject_android_v2.ui.ImageViewPlus;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int MY_ADD_CASE_CALL_PHONE2 = 7;
    //    TextView profileValidation;
    ImageButton UploadProfilePic;
    EditText NameEdit, DateOfBirthEdit, heightEdit, weightedit;
    Button submitProfileChangeBtn;
    private Context context;
    Uri selectedImage;
    ImageViewPlus profilePhoto;

    private static final int PICK_IMAGE_REQUEST = 9544;
    private String path = "";
    private String fileName = "";


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

        UploadProfilePic = findViewById(R.id.uploadProfilePic);
        UploadProfilePic.setOnClickListener(this);
        submitProfileChangeBtn = findViewById(R.id.submitProfileChange);
        submitProfileChangeBtn.setOnClickListener(this);

        //get intent
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("User");
        initView(user);
    }

    protected void initView(User user) {

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
        fileName = user.getProfilePic();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.submitProfileChange) {

            String url = "http://192.168.31.50:8888/api/saveProfile";
            String UserName = "jake";
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
            if (ContextCompat.checkSelfPermission(EditProfileActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditProfileActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_ADD_CASE_CALL_PHONE2);

            } else {
                selectImage();
            }

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
                path = uriToFilePath(selectedImage);
                fileName = DocumentFile.fromSingleUri(getApplicationContext(), selectedImage).getName();
            }
        }
    }

    private String uriToFilePath(Uri uri) {
        String path = null;

        path = getFilePath(this, uri);

        return path;
    }

    public static String getFilePath(Context context, Uri uri) {
        String filePath = "";
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            String[] splits = wholeID.split(":");
            if (splits.length == 2) {
                String id = splits[1];
                String[] column = {MediaStore.Images.Media.DATA};
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
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


//        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart("multipartFile", fileName, RequestBody.create(imageFile, MediaType.parse("multipart/form-data")))
//                .addFormDataPart("fileName", fileName)
//                .addFormDataPart("imageURL", path)
//                .addFormDataPart("Name", Name)
//                .addFormDataPart("UserName", UserName)
//                .addFormDataPart("dateOfBirth", dateOfBirth)
//                .addFormDataPart("Height", Height)
//                .addFormDataPart("Weight", Weight)
//                .build();
//
//        Request request = new Request.Builder().url(url).post(body).build();
        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("UserName", UserName);
        formBuilder.add("Name", Name);
        formBuilder.add("dateOfBirth", dateOfBirth);
        formBuilder.add("Height", Height);
        formBuilder.add("Weight", Weight);

        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        System.out.println(fileName);
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

//                String status = "";
//                String username = "";
//
//                try {
//                    JSONObject jObj = new JSONObject(res);
//                    status = jObj.getString("status");
//                    username = jObj.getString("username");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                if (status.equals("OK")) {
//                    Intent intent = new Intent();
//                    intent.setClass(context, UserProfile.class);
//                    startActivity(intent);

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//                        Toast.makeText(EditProfileActivity.this, "success", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent();
//                        intent.setClass(context, UserProfile.class);
//                        startActivity(intent);
//                    }
//                });
//                }
//            }
//
//
//        });
//    }
//}

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