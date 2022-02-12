package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import iss.nus.adproject_android_v2.helper.BlogEntry;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewBlogActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog);
        getDataFromServer();

    }

    private void getDataFromServer(){
        String url = "http://192.168.0.108:8080/api/blogentry/page";
        RequestPost(url);
    }



    private void RequestPost(String url){

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        httpBuilder .addQueryParameter("activeUserId","3")
                    .addQueryParameter("pageNo", "0")
                    .addQueryParameter("pageLength", "10");

        HttpUrl httpUrl = httpBuilder.build();


        Request request = new Request   .Builder()
                                        .url(httpUrl)
                                        .get()
                                        .build();
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
                                System.out.println("API call failed");

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
                try {

                    JSONArray jsonArray = new JSONArray(res);

                    JSONObject jsonObject = jsonArray.getJSONObject(0);


                    String dataStr = jsonArray.toString();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JSR310Module());

                    final ArrayList<BlogEntry> blogEntries = mapper.readValue(dataStr, new TypeReference<ArrayList<BlogEntry>>(){});
/*                    for(BlogEntry blogEntry : blogEntries) {
                        System.out.println(blogEntry);
                    }*/





                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }
}