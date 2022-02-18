package iss.nus.adproject_android_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateAccount2Activity extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private Button dateBtn;
    TextView mParticularsValidate;
    EditText mCreateAccName;
    TextView mDobValidate;
    EditText mCreateAccHeight;
    EditText mCreateAccWeight;
    TextView mHeightValidate;
    TextView mWeightValidate;
    Button mCreateAccBtn;
    RadioGroup mGenderRadio;
    Button mProceedLoginBtn;

    UserHelper user = new UserHelper();
    String gender = "";
    boolean flag = false;
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account2);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        initDatePicker();
        dateBtn = findViewById(R.id.dateBtn);
        dateBtn.setText(getTodaysDate());

        mCreateAccBtn = findViewById(R.id.createAccBtn);
        mCreateAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                mCreateAccName = findViewById(R.id.createAccName);
                String name = mCreateAccName.getText().toString();

                mCreateAccHeight = findViewById(R.id.createAccHeight);
                String height = mCreateAccHeight.getText().toString();

                mCreateAccWeight = findViewById(R.id.createAccWeight);
                String weight = mCreateAccWeight.getText().toString();

                mGenderRadio = findViewById(R.id.genderRadio);
                if (mGenderRadio.getCheckedRadioButtonId() == -1) {
                    flag = false;
                } else {
                    flag = true;
                }

                String dob = dateBtn.getText().toString();

                if (validateEntries(name, height, weight, flag)) {
                    validateWithServer(name, height, weight, gender, dob);
                }

            }
        });
    }

    public Boolean validateEntries(String name, String height, String weight, boolean flag) {
        if (name == null || name.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Name is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (height == null || height.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Height is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (weight == null || weight.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Weight is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (flag == false) {
            Toast.makeText(getApplicationContext(), "Gender is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void validateWithServer(String name, String height, String weight, String gender, String dob) {
        String url = getResources().getString(R.string.IP) + "/api/login/create02";
        RequestPost(url, name, height, weight, gender, dob);
    }

    protected void RequestPost(String url, String name, String height, String weight, String gender, String dob) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("username", username);
        formBuilder.add("name", name);
        formBuilder.add("height", height);
        formBuilder.add("weight", weight);
        formBuilder.add("gender", gender);
        formBuilder.add("dob", dob);

        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
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

                try {
                    JSONObject jObj = new JSONObject(res);
                    status = jObj.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (status.equals("OK")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mParticularsValidate = findViewById(R.id.particularsValidate);
                            mParticularsValidate.setText("Account Creation Successful");
                            mParticularsValidate.setTextColor(Color.GREEN);
                            mParticularsValidate.setVisibility(View.VISIBLE);

                            mProceedLoginBtn = findViewById(R.id.proceedLoginBtn);
                            mProceedLoginBtn.setVisibility(View.VISIBLE);
                            mProceedLoginBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(CreateAccount2Activity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                } else if (status.equals("Invalid W & H")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mParticularsValidate = findViewById(R.id.particularsValidate);
                            mParticularsValidate.setVisibility(View.VISIBLE);
                            mHeightValidate = findViewById(R.id.heightValidate);
                            mHeightValidate.setVisibility(View.VISIBLE);
                            mWeightValidate = findViewById(R.id.weightValidate);
                            mWeightValidate.setVisibility(View.VISIBLE);
                        }
                    });
                } else if (status.equals("Invalid W")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mParticularsValidate = findViewById(R.id.particularsValidate);
                            mParticularsValidate.setVisibility(View.VISIBLE);
                            mWeightValidate = findViewById(R.id.weightValidate);
                            mWeightValidate.setVisibility(View.VISIBLE);
                        }
                    });
                } else if (status.equals("Invalid H")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mParticularsValidate = findViewById(R.id.particularsValidate);
                            mParticularsValidate.setVisibility(View.VISIBLE);
                            mHeightValidate = findViewById(R.id.heightValidate);
                            mHeightValidate.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mParticularsValidate = findViewById(R.id.particularsValidate);
                            mParticularsValidate.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.male:
                if (checked) {
                    gender = "M";
                }
                break;
            case R.id.female:
                if (checked) {
                    gender = "F";
                }
                break;
            case R.id.others:
                if (checked) {
                    gender = "O";
                }
                break;
        }
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateBtn.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year) {
        return day + "-" + getMonthFormat(month) + "-" + year;
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private String getMonthFormat(int month) {
        if(month == 1)
            return "Jan";
        if(month == 2)
            return "Feb";
        if(month == 3)
            return "Mar";
        if(month == 4)
            return "Apr";
        if(month == 5)
            return "May";
        if(month == 6)
            return "Jun";
        if(month == 7)
            return "Jul";
        if(month == 8)
            return "Aug";
        if(month == 9)
            return "Sep";
        if(month == 10)
            return "Oct";
        if(month == 11)
            return "Nov";
        if(month == 12)
            return "Dec";

        //default should never happen
        return "Jan";
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}