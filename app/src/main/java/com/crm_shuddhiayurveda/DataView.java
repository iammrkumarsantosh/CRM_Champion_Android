package com.crm_shuddhiayurveda;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.crm_shuddhiayurveda.adapter.RecordListAdapter;
import com.crm_shuddhiayurveda.utils.Config;
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
import java.util.ArrayList;

public class DataView extends AppCompatActivity {

    SharedPreferences sf;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    String dataType = "";
    ArrayList<RecordEntity> recordList = new ArrayList<RecordEntity>();
    RecordListAdapter recordListAdapter;
    ListView r_list;
    ImageView back;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);
        sf = getSharedPreferences(Config.api_key, Context.MODE_PRIVATE);
        editor = sf.edit();
        dataType = sf.getString("dataType","0");
        if (progressDialog != null) {
            progressDialog.hide();
        }
        r_list = (ListView)findViewById(R.id.r_list);
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        title = (TextView)findViewById(R.id.title);
        if(dataType.equalsIgnoreCase("1")){
            title.setText("Total Request");
        }else if(dataType.equalsIgnoreCase("2")){
            title.setText("Clinic Visited");
        }else if(dataType.equalsIgnoreCase("3")){
            title.setText("Today Request");
        }
        new LoadData().execute();
    }

    private class LoadData extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(DataView.this);
            progressDialog.setMessage(getResources().getString(R.string.processing));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(Config.viewuserrequest);
                JSONObject cred = new JSONObject();
                try {
                    cred.put("type", dataType);
                    cred.put("employee", sf.getString("employee_id","0"));
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
                        JSONArray arr = new JSONArray(buffer.toString());
                        for(int i =0; i < arr.length(); i++){
                            JSONObject obj = arr.getJSONObject(i);
                            RecordEntity r = new RecordEntity();
                            r.setId(obj.get("id").toString());
                            r.setFirstname(obj.get("first_name").toString());
                            r.setLastname(obj.get("last_name").toString());
                            r.setCareof(obj.get("care_of").toString());
                            r.setMobile(obj.get("mobile").toString());
                            r.setRequestdate(obj.get("datetime").toString());
                            r.setAppointment(obj.get("appointment_datetime").toString());
                            r.setState(obj.get("state").toString());
                            r.setClinic(obj.get("clinic").toString());
                            r.setVisited(obj.get("is_clinic_visit").toString());
                            recordList.add(r);
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
            recordListAdapter = new RecordListAdapter(DataView.this,R.layout.record_list,recordList);
            r_list.setAdapter(recordListAdapter);
            if(recordList.size()==0){
                new AlertDialog.Builder(DataView.this)
                        .setTitle("No Data Found").setMessage("There is no matching data found.")
                        .setNegativeButton("OK", null).show();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}