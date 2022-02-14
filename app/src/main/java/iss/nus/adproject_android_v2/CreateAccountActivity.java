package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.ipsec.ike.TunnelModeChildSessionParams;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateAccountActivity extends AppCompatActivity {

    TextView mCredentialsValidate;
    ImageButton mProfilePic;
    TextView mUploadProfilePic;
    EditText mUsername;
    TextView mUsernameValidate;
    EditText mEmail;
    TextView mEmailValidate;
    EditText mPwd;
    Button mNextBtn;
    Uri selectedImage;
    private String path = "";
    private String fileName = "";
    private static final int PICK_IMAGE_REQUEST = 9544;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mProfilePic = findViewById(R.id.createAccProfilePic);
        mUploadProfilePic = findViewById(R.id.uploadProfilePic);
        mUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        mNextBtn = findViewById(R.id.createAccNextBtn);
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsername = findViewById(R.id.createAccUsername);
                String username = mUsername.getText().toString();

                mEmail = findViewById(R.id.createAccEmail);
                String email = mEmail.getText().toString();

                mPwd = findViewById(R.id.createAccPwd);
                String pwd = mPwd.getText().toString();

                if (validateEntries(username, email, pwd)) {
                    validateWithServer(username, email, pwd);
                }
            }
        });
    }

    public void selectImage() {
        verifyStoragePermissions(CreateAccountActivity.this);
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
                mProfilePic.setImageURI(selectedImage);
                path = uriToFilePath(selectedImage);
                fileName = DocumentFile.fromSingleUri(getApplicationContext(), selectedImage).getName();
            }
        }
    }

    public Boolean validateEntries(String username, String email, String password) {
        if (username == null || username.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email == null || email.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password == null || password.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void validateWithServer(String username, String email, String password) {
        String url = "http://192.168.1.8:8080/api/login/create01";
        RequestPost(url, username, email, password);
    }

    protected void RequestPost(String url, String username, String email, String password) {
        OkHttpClient client = new OkHttpClient();

        final File imageFile = new File(uriToFilePath(selectedImage));

        if (path.isEmpty() || !imageFile.exists()) {
            Toast.makeText(getApplicationContext(), "File does not exists", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("multipartFile",fileName, RequestBody.create(imageFile, MediaType.parse("multipart/form-data")))
                .addFormDataPart("fileName", fileName)
                .addFormDataPart("imageURL", path)
                .addFormDataPart("username", username)
                .addFormDataPart("password", password)
                .addFormDataPart("email", email)
                .build();

        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
                System.out.println("Information returned from server:");
                System.out.println(res);

                String status = "";
                String username = "";

                try {
                    JSONObject jObj = new JSONObject(res);
                    status = jObj.getString("status");
                    username = jObj.getString("username");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equals("OK")) {
                    Intent intent = new Intent(CreateAccountActivity.this, CreateAccount2Activity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else if (status.equals("Username error")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCredentialsValidate = findViewById(R.id.credentialsValidate);
                            mCredentialsValidate.setVisibility(View.VISIBLE);
                            mUsernameValidate = findViewById(R.id.usernameValidate);
                            mUsernameValidate.setVisibility(View.VISIBLE);
                        }
                    });
                } else if (status.equals("Email error")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCredentialsValidate = findViewById(R.id.credentialsValidate);
                            mCredentialsValidate.setVisibility(View.VISIBLE);
                            mEmailValidate = findViewById(R.id.emailValidate);
                            mEmailValidate.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
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
}