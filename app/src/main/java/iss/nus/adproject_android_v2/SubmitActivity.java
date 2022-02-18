package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SubmitActivity extends AppCompatActivity implements View.OnClickListener{

    private Bitmap imageBitmap;
    private String currentPhotoPath;
    private Uri photoURI;
    private String mealTitle;
    private String description;
    private String feeling = null;
    private int trackScore = -1;
    private String timeStamp;
    private String userId;
    private boolean flagged = false;
    private boolean visibility = true;

    private boolean selectedTrack = false;
    private int trackClicks = 0;
    private int feelingClicks = 0;

    private ImageView capturedImageView;
    private EditText mealTitleEntry;
    private EditText descriptionEntry;
    private ImageButton offTrackBtn;
    private TextView offTrackTitle;
    private ImageButton onTrackBtn;
    private TextView onTrackTitle;
    private ImageView cryingBtn;
    private ImageView pensiveBtn;
    private ImageView happyBtn;
    private ImageView joyfulBtn;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        capturedImageView = findViewById(R.id.capturedImageView);
        Intent intent = getIntent();
        timeStamp = intent.getStringExtra("timeStamp");
        currentPhotoPath = intent.getStringExtra("currentPhotoPath");
        photoURI = intent.getParcelableExtra("photoURI");
        getBitmapFromUri(photoURI);
        capturedImageView.setImageURI(photoURI); //alt: capturedImageView.setImageBitmap(imageBitmap);
        userId = intent.getStringExtra("userId");

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mealTitleEntry = findViewById(R.id.mealTitleEditText);
                        descriptionEntry = findViewById(R.id.descriptionEditText);
                        offTrackBtn = findViewById(R.id.offTrackBtn);
                        offTrackTitle = findViewById(R.id.offTrackTitle);
                        onTrackBtn = findViewById(R.id.onTrackBtn);
                        onTrackTitle = findViewById(R.id.onTrackTitle);
                        cryingBtn = findViewById(R.id.cryingBtn);
                        pensiveBtn = findViewById(R.id.pensiveBtn);
                        happyBtn = findViewById(R.id.happyBtn);
                        joyfulBtn = findViewById(R.id.joyfulBtn);
                        submitBtn = findViewById(R.id.submitBtn);

                        initElements();
                    }
                });
            }
        }).start();
    }

    private void initElements() {
        offTrackBtn.setOnClickListener(this);
        onTrackBtn.setOnClickListener(this);
        cryingBtn.setOnClickListener(this);
        pensiveBtn.setOnClickListener(this);
        happyBtn.setOnClickListener(this);
        joyfulBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    private void getBitmapFromUri(Uri uri) {
        try {
            ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), uri);
            imageBitmap = ImageDecoder.decodeBitmap(source);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to get Bitmap from URI");
        }
        /*
        //The following segment of code, if used, requires to be run on a background thread
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public void onClick(View view) {
        if (view == offTrackBtn) {
            trackClicks++;
            if (trackClicks == 1) {
                trackScore = 0;
                selectedTrack = true;
                onTrackBtn.setVisibility(View.GONE);
                onTrackTitle.setVisibility(View.GONE);
            }
            else if (trackClicks == 2) {
                trackScore = -1;
                selectedTrack = false;
                onTrackBtn.setVisibility(View.VISIBLE);
                onTrackTitle.setVisibility(View.VISIBLE);
                trackClicks = 0;
            }
        }

        if (view == onTrackBtn) {
            trackClicks++;
            if (trackClicks == 1) {
                trackScore = 1;
                selectedTrack = true;
                offTrackBtn.setVisibility(View.GONE);
                offTrackTitle.setVisibility(View.GONE);
            }
            else if (trackClicks == 2) {
                trackScore = -1;
                selectedTrack = false;
                offTrackBtn.setVisibility(View.VISIBLE);
                offTrackTitle.setVisibility(View.VISIBLE);
                trackClicks = 0;
            }
        }

        if (view == cryingBtn) {
            feelingClicks++;
            if (feelingClicks == 1) {
                feeling = "cry";
                pensiveBtn.setVisibility(View.GONE);
                happyBtn.setVisibility(View.GONE);
                joyfulBtn.setVisibility(View.GONE);
            }
            else if (feelingClicks == 2) {
                feeling = null;
                pensiveBtn.setVisibility(View.VISIBLE);
                happyBtn.setVisibility(View.VISIBLE);
                joyfulBtn.setVisibility(View.VISIBLE);
                feelingClicks = 0;
            }
        }

        if (view == pensiveBtn) {
            feelingClicks++;
            if (feelingClicks == 1) {
                feeling = "pensive";
                cryingBtn.setVisibility(View.GONE);
                happyBtn.setVisibility(View.GONE);
                joyfulBtn.setVisibility(View.GONE);
            }
            else if (feelingClicks == 2) {
                feeling = null;
                cryingBtn.setVisibility(View.VISIBLE);
                happyBtn.setVisibility(View.VISIBLE);
                joyfulBtn.setVisibility(View.VISIBLE);
                feelingClicks = 0;
            }
        }

        if (view == happyBtn) {
            feelingClicks++;
            if (feelingClicks == 1) {
                feeling = "happy";
                cryingBtn.setVisibility(View.GONE);
                pensiveBtn.setVisibility(View.GONE);
                joyfulBtn.setVisibility(View.GONE);
            }
            else if (feelingClicks == 2) {
                feeling = null;
                cryingBtn.setVisibility(View.VISIBLE);
                pensiveBtn.setVisibility(View.VISIBLE);
                joyfulBtn.setVisibility(View.VISIBLE);
                feelingClicks = 0;
            }
        }

        if (view == joyfulBtn) {
            feelingClicks++;
            if (feelingClicks == 1) {
                feeling = "joy";
                cryingBtn.setVisibility(View.GONE);
                pensiveBtn.setVisibility(View.GONE);
                happyBtn.setVisibility(View.GONE);
            }
            else if (feelingClicks == 2) {
                feeling = null;
                cryingBtn.setVisibility(View.VISIBLE);
                pensiveBtn.setVisibility(View.VISIBLE);
                happyBtn.setVisibility(View.VISIBLE);
                feelingClicks = 0;
            }
        }

        if (view == submitBtn) {
            mealTitle = mealTitleEntry.getText().toString();
            description = descriptionEntry.getText().toString();
            if (mealTitle != null && !mealTitle.trim().isEmpty() && description != null && !description.trim().isEmpty() && selectedTrack == true && feeling != null) {
                mealTitle = mealTitle.trim();
                description = description.trim();
                System.out.println(mealTitle);
                System.out.println(description);
                System.out.println(trackScore);
                System.out.println(feeling);
                uploadData();

            } else {
                String validationMsg = "Meal title and description must have a valid entry and a track and feeling must both be selected";
                Toast toast = Toast.makeText(this, validationMsg, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    protected void uploadData() {
        setButtonsUnclickable();
        //"(IP:8080)/api/uploadMealEntry"
        String serverUrl = getString(R.string.IP) + "/api/uploadMealEntry";
        String fileName = currentPhotoPath.substring(currentPhotoPath.indexOf("JPEG"));
        File imageFile = new File(currentPhotoPath);
        if (imageFile.exists() == false) {
            setButtonsClickable();
            Toast.makeText(this, "File is missing", Toast.LENGTH_SHORT).show();
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("multipartFile", fileName, RequestBody.create(imageFile, MediaType.parse("multipart/form-data")))
                .addFormDataPart("imageFileName", fileName)
                .addFormDataPart("imageURL", currentPhotoPath)
                .addFormDataPart("mealTitle", mealTitle)
                .addFormDataPart("description", description)
                .addFormDataPart("feeling", feeling)
                .addFormDataPart("trackScore", String.valueOf(trackScore))
                .addFormDataPart("timeStamp", timeStamp)
                .addFormDataPart("userId", userId)
                .addFormDataPart("flagged", String.valueOf(flagged))
                .addFormDataPart("visibility", String.valueOf(visibility))
                .build();

        Request request = new Request.Builder().url(serverUrl).post(requestBody).build();
        Call requestCall = okHttpClient.newCall(request);
        requestCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setButtonsClickable();
                        Toast.makeText(SubmitActivity.this, "Error sending request to server", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                setButtonsClickable();
                final String res = response.body().string();
                System.out.println(res);
                if (res == null) {
                    Toast.makeText(SubmitActivity.this, "An error occurred getting a proper response from the recommender", Toast.LENGTH_LONG).show();
                }
                if (res.equals("IOException") || res.equals("MultipartFileFailure")) {
                    Toast.makeText(SubmitActivity.this, "An error occurred on the server", Toast.LENGTH_LONG).show();
                }
                startResponseActivity(res);
            }
        });
    }

    private void startResponseActivity(String res) {
        Intent intent = new Intent(this, ResponseActivity.class);
        intent.putExtra("responseResult", res);
        startActivity(intent);
    }

    private void setButtonsUnclickable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mealTitleEntry.setEnabled(false);
                descriptionEntry.setEnabled(false);
                submitBtn.setClickable(false);
                onTrackBtn.setClickable(false);
                offTrackBtn.setClickable(false);
                cryingBtn.setClickable(false);
                pensiveBtn.setClickable(false);
                happyBtn.setClickable(false);
                joyfulBtn.setClickable(false);
            }
        });
    }

    private void setButtonsClickable(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mealTitleEntry.setEnabled(true);
                descriptionEntry.setEnabled(true);
                submitBtn.setClickable(true);
                onTrackBtn.setClickable(true);
                offTrackBtn.setClickable(true);
                cryingBtn.setClickable(true);
                pensiveBtn.setClickable(true);
                happyBtn.setClickable(true);
                joyfulBtn.setClickable(true);
            }
        });
    }

}