package iss.nus.adproject_android_v2;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.DocumentsContract;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class testUploadImageActivity extends AppCompatActivity implements View.OnClickListener {

    private String path ;
    EditText filenameEditText;
    ImageView view;
    Button choose;
    Button upload;
    Button download;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_upload_image);


        filenameEditText = findViewById(R.id.name);
        view = findViewById(R.id.view);
        choose = findViewById(R.id.choosee);
        upload = findViewById(R.id.upload);
        download = findViewById(R.id.download);

        choose.setOnClickListener(this);
        upload.setOnClickListener(this);
        download.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){

        int id = v.getId();
        if(id == R.id.choosee){
            chooseFile();
        }else if (id == R.id.upload){

            uploadFile(path,filenameEditText.getText().toString());

        }else if (id == R.id.download){

            downloadFile(filenameEditText.getText().toString());
//            File file = ;
//            if(file == null)
//
//            else
//            {
//                view.setImageURI(null);
//                view.setImageURI(Uri.fromFile(file));
//                showMessage("download successful");
//            }
        }
    }



    public void chooseFile()
    {
        String [] permissions = new String[]{
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };//permission
        if(
                ActivityCompat.checkSelfPermission(this,permissions[0]) != PackageManager.PERMISSION_GRANTED
                        ||
                        ActivityCompat.checkSelfPermission(this,permissions[1]) != PackageManager.PERMISSION_GRANTED
        )
        //if no permission
        {
            ActivityCompat.requestPermissions(this,permissions,1);//apply permission
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//system pick selector
        intent.setType("*/*");//all type of file
        intent.addCategory(Intent.CATEGORY_OPENABLE);//期望获取的数据可以作为一个File打开
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            Uri uri = data.getData();
            File dir = getExternalFilesDir(null);
            if(dir != null)
            {
                path = dir.toString().substring(0,dir.toString().indexOf("0")+2) +
                        DocumentsContract.getDocumentId(uri).split(":")[1];
            }
        }
    }

    public void uploadFile(String path,String filename)
    {
        String url = getResources().getString(R.string.IP) + "/api/upload";
//        String url = "http://192.168.86.248:8888/api/upload";
        OkHttpClient okhttp = new OkHttpClient();
        File file = new File(path);
        if(path.isEmpty() || !file.exists()){
            Toast.makeText(testUploadImageActivity.this, "no this file", Toast.LENGTH_SHORT).show();
        }

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",filename,RequestBody.create(MediaType.parse("multipart/form-data"),new File(path)))
                .addFormDataPart("filename",filename)
                .build();

        Request request = new Request.Builder().url(url).post(body).build();
        Call call = okhttp.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(testUploadImageActivity.this, "server error", Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(testUploadImageActivity.this, "success", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }

    public void downloadFile(final String filename)
    {
        String url = getResources().getString(R.string.IP) + "/api/download";
//        String url = "http://192.168.86.248:8888/api/download";

        OkHttpClient okhttp = new OkHttpClient();
        if(filename == null || filename.isEmpty()){
            // file
        }

        RequestBody body = new MultipartBody.Builder().addFormDataPart("filename",filename).build();

        Request request = new Request.Builder().url(url).post(body).build();
        Call call = okhttp.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(testUploadImageActivity.this, "server error", Toast.LENGTH_SHORT).show();
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

                        if(getExternalFilesDir(null) != null)
                        {
                            File file = new File(getExternalFilesDir(null).toString() + "/" + filename);
                            try (
                                    InputStream inputStream = response.body().byteStream();
                                    FileOutputStream outputStream = new FileOutputStream(file)
                            )
                            {
                                byte[] b = new byte[1024];
                                int n;
                                if((n = inputStream.read(b)) != -1)
                                {
                                    outputStream.write(b,0,n);
                                    while ((n = inputStream.read(b)) != -1)
                                        outputStream.write(b, 0, n);

                                    view.setImageURI(null);
                                    view.setImageURI(Uri.fromFile(file));
                                    showMessage("download successful");
                                }
                                else
                                {
                                    file.delete();
                                    showMessage("Image does not exist");
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(testUploadImageActivity.this, "success", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });



    }

    public void showMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void e(String message)
    {
        Log.e("LOG_E",message);
    }

}