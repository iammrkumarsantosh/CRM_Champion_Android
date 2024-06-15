package com.crm_shuddhiayurveda;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.crm_shuddhiayurveda.utils.Config;

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

public class Sucess extends AppCompatActivity {

    SharedPreferences sf;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucess);
        sf = getSharedPreferences(Config.api_key, Context.MODE_PRIVATE);
        editor = sf.edit();
        if (progressDialog != null) {
            progressDialog.hide();
        }
        TextView home = (TextView)findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginAction().execute();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            new LoginAction().execute();
        }
        return super.onKeyDown(keyCode, event);
    }

    private class LoginAction extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Sucess.this);
            progressDialog.setMessage(getResources().getString(R.string.processing));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(Config.login);
                JSONObject cred = new JSONObject();
                try {
                    cred.put("username", sf.getString("loginusername","null"));
                    cred.put("password", sf.getString("loginpassword","null"));
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
                        if(obj.has("username")){
                            editor.putString("loginusername",obj.get("username").toString());
                            editor.putString("loginfullname",obj.get("fullname").toString());
                            editor.putString("logindate",obj.get("date").toString());
                            editor.putString("data_set_1",obj.get("data_set_1").toString());
                            editor.putString("data_set_2",obj.get("data_set_2").toString());
                            editor.putString("data_set_3",obj.get("data_set_3").toString());
                            editor.putString("data_set_4",obj.get("data_set_4").toString());
                            editor.putString("employee_id",obj.get("employee_id").toString());
                            editor.putString("app_user_id",obj.get("app_user_id").toString());
                            editor.putString("login",obj.get("login").toString());
                            editor.putString("logout",obj.get("logout").toString());
                            editor.putString("isLogin","Y");
                            editor.commit();
                        }else{
                            editor.remove("isLogin");
                            editor.commit();
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
            if(sf.getString("isLogin",null) != null){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }else{
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
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        }
    }
}