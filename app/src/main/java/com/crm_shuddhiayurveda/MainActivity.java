package com.crm_shuddhiayurveda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crm_shuddhiayurveda.utils.Config;
import com.crm_shuddhiayurveda.utils.GpsTracker;
import com.crm_shuddhiayurveda.utils.RecordEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    LinearLayout today_click,clinic_visited_click,total_request_click,start_div,end_div;
    TextView today_count, clinic_visited_count,total_request_count,login_name,date,start_time,end_time;
    Button end, start, add_new_request;
    SharedPreferences sf;
    SharedPreferences.Editor editor;
    ImageView logout,searchBtn;
    String tvLatitude,tvLongitude;
    ProgressDialog progressDialog;
    EditText searchInput;
    String searchMobile;
    boolean NoDataFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sf = getSharedPreferences(Config.api_key, Context.MODE_PRIVATE);
        editor = sf.edit();
        editor.remove("rid");
        editor.commit();
        if (progressDialog != null) {
            progressDialog.hide();
        }
        today_click = (LinearLayout)findViewById(R.id.today_click);
        clinic_visited_click = (LinearLayout)findViewById(R.id.clinic_visited_click);
        total_request_click = (LinearLayout)findViewById(R.id.total_request_click);
        start_div = (LinearLayout)findViewById(R.id.start_div);
        end_div = (LinearLayout)findViewById(R.id.end_div);
        searchBtn = (ImageView)findViewById(R.id.searchBtn);
        searchInput = (EditText)findViewById(R.id.searchInput);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mob = searchInput.getText().toString();
                if(mob.trim().length() == 10){
                    searchMobile = mob;
                    new SearchByMobile().execute();
                }
            }
        });
        editor.remove("dataType");
        editor.commit();
        today_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("dataType","3");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), DataView.class);
                startActivity(intent);
            }
        });
        clinic_visited_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("dataType","2");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), DataView.class);
                startActivity(intent);
            }
        });
        total_request_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("dataType","1");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), DataView.class);
                startActivity(intent);
            }
        });

        today_count = (TextView)findViewById(R.id.today_count);
        clinic_visited_count = (TextView)findViewById(R.id.clinic_visited_count);
        total_request_count = (TextView)findViewById(R.id.total_request_count);
        start_time = (TextView)findViewById(R.id.start_time);
        end_time = (TextView)findViewById(R.id.end_time);

        total_request_count.setText(sf.getString("data_set_1","0"));
        clinic_visited_count.setText(sf.getString("data_set_2","0"));
        today_count.setText(sf.getString("data_set_3","0"));

        login_name = (TextView)findViewById(R.id.login_name);
        date = (TextView)findViewById(R.id.date);

        login_name.setText(sf.getString("loginfullname","Hello,"));
        date.setText(sf.getString("logindate"," "));

        end = (Button)findViewById(R.id.shift_end);
        start = (Button)findViewById(R.id.shift_start);
        add_new_request = (Button)findViewById(R.id.add_new_request);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AttendanceAction().execute("1");
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AttendanceAction().execute("2");
            }
        });

        logout = (ImageView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.remove("loginfullname");
                editor.remove("logindate");
                editor.remove("data_set_1");
                editor.remove("data_set_2");
                editor.remove("data_set_3");
                editor.remove("data_set_4");
                editor.remove("employee_id");
                editor.remove("app_user_id");
                editor.remove("isLogin");
                editor.remove("login");
                editor.remove("logout");
                editor.remove("loginpassword");
                editor.remove("rid");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        add_new_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserRegister.class);
                startActivity(intent);
            }
        });

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        getLocation();
        attendanceCheck();
    }

    public void attendanceCheck(){
        String login_time = sf.getString("login",null);
        String logout_time = sf.getString("logout",null);
        int isShiftStarted = 0;
        if(null != login_time && !login_time.equalsIgnoreCase("0")){
            start_div.setVisibility(View.VISIBLE);
            start.setVisibility(View.GONE);
            start_time.setText(login_time);
            isShiftStarted = 1;
        }else{
            start_div.setVisibility(View.GONE);
            start.setVisibility(View.VISIBLE);
        }
        if(isShiftStarted == 1) {
            if (null != logout_time && !logout_time.equalsIgnoreCase("00:00:00")
                    && !logout_time.equalsIgnoreCase("0")) {
                end_div.setVisibility(View.VISIBLE);
                end.setVisibility(View.GONE);
                end_time.setText(logout_time);
            } else {
                end_div.setVisibility(View.GONE);
                end.setVisibility(View.VISIBLE);
            }
        }else{
            end_div.setVisibility(View.GONE);
            end.setVisibility(View.GONE);
        }
    }

    public void getLocation(){
        GpsTracker gpsTracker = new GpsTracker(MainActivity.this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            tvLatitude = String.valueOf(latitude);
            tvLongitude = String.valueOf(longitude);
        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    private class AttendanceAction extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            getLocation();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.processing));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(Config.attendance);
                JSONObject cred = new JSONObject();
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());
                    String time = formatter.format(curDate);
                    int type = Integer.parseInt(params[0]);
                    if(type == 1){
                        cred.put("login_time", time);
                        cred.put("logout_time", "00:00:00");
                        editor.putString("login",time);
                    }else{
                        cred.put("login_time", "00:00:00");
                        cred.put("logout_time", time);
                        editor.putString("logout",time);
                    }
                    editor.commit();
                    cred.put("employee", sf.getString("employee_id","0"));
                    cred.put("type", type);
                    cred.put("date", sf.getString("logindate","2022-01-01"));
                    cred.put("latitude", tvLatitude);
                    cred.put("longitude", tvLongitude);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();
                OutputStream os = connection.getOutputStream();
                os.write(cred.toString().getBytes("UTF-8"));
                os.close();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            progressDialog.hide();
            attendanceCheck();
        }
    }

    private class SearchByMobile extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            getLocation();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.processing));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(Config.searchbymobile);
                JSONObject cred = new JSONObject();
                try {
                    cred.put("mobile", searchMobile);
                    cred.put("employee_id", sf.getString("employee_id","0"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();
                OutputStream os = connection.getOutputStream();
                os.write(cred.toString().getBytes("UTF-8"));
                os.close();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }
                if(buffer.toString().length() > 0){
                    try{
                        JSONObject obj = new JSONObject(buffer.toString());
                        String rid = obj.get("id").toString();
                        if(!rid.equalsIgnoreCase("0")){
                            NoDataFound = false;
                            editor.putString("rid",rid);
                            editor.commit();
                            Intent in = new Intent(getApplicationContext(), UserRegister.class);
                            startActivity(in);
                        }else{
                            NoDataFound = true;
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            progressDialog.hide();
            if(NoDataFound) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("No Record Found").setMessage("Please add new request.")
                        .setNegativeButton("OK", null).show();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finishAffinity();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}