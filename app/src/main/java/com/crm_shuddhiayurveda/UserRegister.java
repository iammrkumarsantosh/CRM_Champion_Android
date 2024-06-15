package com.crm_shuddhiayurveda;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.crm_shuddhiayurveda.utils.Config;
import com.crm_shuddhiayurveda.utils.ValueId;

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
import java.util.List;

public class UserRegister extends AppCompatActivity {

    ImageView back;
    SharedPreferences sf;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    EditText mobile,firstname,lastname,hcnum,address,city,remark,careof,age,mobile2;
    TextView dob,apt,hcnumlable,pageTitle;
    Spinner country,hctype,disease,clinic,state,gender,coType,empType;
    Button submit;
    String selectedGender,selectedCountry,selectedHCT,selectedDisease,selectedState,selectedClinic,selectedCoType;
    String selectedEmpType = "1";
    String selectedDOB = "1999-01-01";
    String selectedAPT = "1999-01-01";
    List<ValueId> countryList = new ArrayList<ValueId>();
    List<ValueId> stateList = new ArrayList<ValueId>();
    List<ValueId> hctList = new ArrayList<ValueId>();
    List<ValueId> diseaseList = new ArrayList<ValueId>();
    List<ValueId> clinicList = new ArrayList<ValueId>();
    List<ValueId> coTypeList = new ArrayList<ValueId>();
    List<ValueId> empTypeList = new ArrayList<ValueId>();
    JSONObject cred = new JSONObject();
    boolean isSucess = false;
    LinearLayout typeBlock;
    String rid = "0";
    List<String> genderList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        sf = getSharedPreferences(Config.api_key, Context.MODE_PRIVATE);
        editor = sf.edit();
        if (progressDialog != null) {
            progressDialog.hide();
        }
        mobile = (EditText)findViewById(R.id.mobile);
        mobile2 = (EditText)findViewById(R.id.mobile2);
        firstname = (EditText)findViewById(R.id.firstname);
        lastname = (EditText)findViewById(R.id.lastname);
        hcnum = (EditText)findViewById(R.id.hcnum);
        address = (EditText)findViewById(R.id.address);
        city = (EditText)findViewById(R.id.city);
        remark = (EditText)findViewById(R.id.remark);
        careof = (EditText)findViewById(R.id.careof);
        age  = (EditText)findViewById(R.id.age);
        back = (ImageView)findViewById(R.id.back);
        country = (Spinner)findViewById(R.id.country);
        hctype = (Spinner)findViewById(R.id.hctype);
        disease = (Spinner)findViewById(R.id.disease);
        clinic = (Spinner)findViewById(R.id.clinic);
        state = (Spinner)findViewById(R.id.state);
        gender = (Spinner)findViewById(R.id.gender);
        coType = (Spinner)findViewById(R.id.coType);
        empType = (Spinner)findViewById(R.id.empType);
        typeBlock = (LinearLayout)findViewById(R.id.typeBlock);
        dob = (TextView)findViewById(R.id.dob);
        apt = (TextView)findViewById(R.id.apt);
        pageTitle = (TextView)findViewById(R.id.pageTitle);
        hcnumlable = (TextView)findViewById(R.id.hcnumlable);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertUserRequest();
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateSelector();
            }
        });

        apt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateTimeSelector();
            }
        });

        genderList.add("Select");
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("other");
        ArrayAdapter<String> gAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderList);
        gAdapter.setDropDownViewResource(R.layout.multiline_item);
        gender.setAdapter(gAdapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = gender.getSelectedItem().toString().trim();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        populateClink();
        //EmpTypeDropdown
        String healthcardtypecategoryJson = sf.getString("healthcardtypecategory",null);
        List<String> healthcardtypecatTList = new ArrayList<String>();
        ValueId htvi = new ValueId();
        if(null != healthcardtypecategoryJson){
            try{
                JSONArray ar = new JSONArray(healthcardtypecategoryJson);
                for(int i = 0; i < ar.length(); i++){
                    JSONObject o = ar.getJSONObject(i);
                    htvi = new ValueId();
                    htvi.setValue(o.getString("id"));
                    htvi.setLabel(o.getString("name"));
                    healthcardtypecatTList.add(htvi.getLabel());
                    empTypeList.add(htvi);
                }
                ArrayAdapter<String> coAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, healthcardtypecatTList);
                coAdapter.setDropDownViewResource(R.layout.multiline_item);
                empType.setAdapter(coAdapter);
                empType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedEmpType = empTypeList.get(position).getValue();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        //CoTypeDropdown
        String careofJson = sf.getString("careof",null);
        List<String> cOfTList = new ArrayList<String>();
        if(null != careofJson){
            try{
                JSONArray ar = new JSONArray(careofJson);
                for(int i = 0; i < ar.length(); i++){
                    JSONObject o = ar.getJSONObject(i);
                    ValueId vi = new ValueId();
                    vi.setValue(o.getString("id"));
                    vi.setLabel(o.getString("name"));
                    cOfTList.add(vi.getLabel());
                    coTypeList.add(vi);
                }
                ArrayAdapter<String> coAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cOfTList);
                coAdapter.setDropDownViewResource(R.layout.multiline_item);
                coType.setAdapter(coAdapter);
                coType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCoType = coTypeList.get(position).getValue();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        //countryDropdown
        String countryJson = sf.getString("country",null);
        List<String> cStrList = new ArrayList<String>();
        if(null != countryJson){
            try{
                JSONArray ar = new JSONArray(countryJson);
                for(int i = 0; i < ar.length(); i++){
                    JSONObject o = ar.getJSONObject(i);
                    ValueId vi = new ValueId();
                    vi.setValue(o.getString("id"));
                    vi.setLabel(o.getString("name"));
                    cStrList.add(vi.getLabel());
                    countryList.add(vi);
                }
                ArrayAdapter<String> cAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cStrList);
                cAdapter.setDropDownViewResource(R.layout.multiline_item);
                country.setAdapter(cAdapter);
                country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCountry = countryList.get(position).getValue();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        //HCTDropdown
        String hctJson = sf.getString("healthcardtype",null);
        List<String> hctStrList = new ArrayList<String>();
        ValueId vi = new ValueId();
        vi.setValue("0");
        vi.setLabel("Select");
        hctStrList.add(vi.getLabel());
        hctList.add(vi);
        if(null != hctJson){
            try{
                JSONArray ar = new JSONArray(hctJson);
                for(int i = 0; i < ar.length(); i++){
                    JSONObject o = ar.getJSONObject(i);
                    vi = new ValueId();
                    vi.setValue(o.getString("id"));
                    vi.setLabel(o.getString("name"));
                    hctStrList.add(vi.getLabel());
                    hctList.add(vi);
                }
                ArrayAdapter<String> hctAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hctStrList);
                hctAdapter.setDropDownViewResource(R.layout.multiline_item);
                hctype.setAdapter(hctAdapter);
                hctype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedHCT = hctList.get(position).getValue();
                        if(position > 1){
                            selectedEmpType = "0";
                            typeBlock.setVisibility(View.VISIBLE);
                            hcnumlable.setVisibility(View.VISIBLE);
                            hcnum.setVisibility(View.VISIBLE);
                        }else{
                            selectedEmpType = "0";
                            typeBlock.setVisibility(View.GONE);
                            hcnumlable.setVisibility(View.GONE);
                            hcnum.setVisibility(View.GONE);
                        }
                        if(position == 4){
                            hcnumlable.setVisibility(View.GONE);
                            hcnum.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        //DiseaseDropdown
        String diseaseJson = sf.getString("disease",null);
        List<String> diseaseStrList = new ArrayList<String>();
        vi = new ValueId();
        vi.setValue("0");
        vi.setLabel("Select");
        diseaseList.add(vi);
        diseaseStrList.add(vi.getLabel());
        if(null != diseaseJson){
            try{
                JSONArray ar = new JSONArray(diseaseJson);
                for(int i = 0; i < ar.length(); i++){
                    JSONObject o = ar.getJSONObject(i);
                    vi = new ValueId();
                    vi.setValue(o.getString("id"));
                    vi.setLabel(o.getString("name"));
                    diseaseStrList.add(vi.getLabel());
                    diseaseList.add(vi);
                }
                ArrayAdapter<String> diseaseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, diseaseStrList);
                diseaseAdapter.setDropDownViewResource(R.layout.multiline_item);
                disease.setAdapter(diseaseAdapter);
                disease.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedDisease = diseaseList.get(position).getValue();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        //StateDropdown
        String stateJson = sf.getString("state",null);
        List<String> stateStrList = new ArrayList<String>();
        vi = new ValueId();
        vi.setValue("0");
        vi.setLabel("Select");
        stateList.add(vi);
        stateStrList.add(vi.getLabel());
        if(null != stateJson){
            try{
                JSONArray ar = new JSONArray(stateJson);
                for(int i = 0; i < ar.length(); i++){
                    JSONObject o = ar.getJSONObject(i);
                    vi = new ValueId();
                    vi.setValue(o.getString("id"));
                    vi.setLabel(o.getString("name"));
                    stateStrList.add(vi.getLabel());
                    stateList.add(vi);
                }
                ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stateStrList);
                stateAdapter.setDropDownViewResource(R.layout.multiline_item);
                state.setAdapter(stateAdapter);
                state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                       selectedState = stateList.get(position).getValue();
                       new ClinicAction().execute(selectedState);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        if(sf.getString("rid",null) != null){
            rid = sf.getString("rid","0");
            new LoadURData().execute();
        }
    }

    private class LoadURData extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(UserRegister.this);
            progressDialog.setMessage(getResources().getString(R.string.processing));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(Config.ureqdata);
                JSONObject cred = new JSONObject();
                try {
                    cred.put("rid", rid);
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
                        mobile.setText(obj.getString("mobile"));
                        mobile2.setText(obj.getString("mobile_2"));
                        firstname.setText(obj.getString("first_name"));
                        lastname.setText(obj.getString("last_name"));
                        careof.setText(obj.getString("care_of"));
                        age.setText(obj.getString("age"));
                        address.setText(obj.getString("address"));
                        city.setText(obj.getString("city"));
                        remark.setText(obj.getString("remark"));
                        hcnum.setText(obj.getString("health_card_number"));
                        apt.setText(obj.getString("appointment_datetime"));
                        selectedAPT = obj.getString("appointment_datetime");
                        int countryIndex = 0;
                        for(int i=0; i < countryList.size();i++){
                            if(countryList.get(i).getValue().equalsIgnoreCase(obj.getInt("country")+"")){
                                countryIndex = i;
                                break;
                            }
                        }
                        country.setSelection(countryIndex);
                        int stateIndex = 0;
                        for(int i=0; i < stateList.size();i++){
                            if(stateList.get(i).getValue().equalsIgnoreCase(obj.getInt("state")+"")){
                                stateIndex = i;
                                break;
                            }
                        }
                        state.setSelection(stateIndex);
                        int careOfIndex = 0;
                        for(int i=0; i < coTypeList.size();i++){
                            if(coTypeList.get(i).getValue().equalsIgnoreCase(obj.getInt("care_of_type")+"")){
                                careOfIndex = i;
                                break;
                            }
                        }
                        coType.setSelection(careOfIndex);
                        int genderIndex = 0;
                        for(int i=0; i < genderList.size();i++){
                            if(genderList.get(i).equalsIgnoreCase(obj.getString("gender"))){
                                genderIndex = i;
                                break;
                            }
                        }
                        gender.setSelection(genderIndex);
                        int clinicIndex = 0;
                        for(int i=0; i < clinicList.size();i++){
                            if(clinicList.get(i).getValue().equalsIgnoreCase(obj.getInt("clinic")+"")){
                                clinicIndex = i;
                                break;
                            }
                        }
                        clinic.setSelection(clinicIndex);
                        int hctIndex = 0;
                        for(int i=0; i < hctList.size();i++){
                            if(hctList.get(i).getValue().equalsIgnoreCase(obj.getInt("health_card_type")+"")){
                                hctIndex = i;
                                break;
                            }
                        }
                        hctype.setSelection(hctIndex);
                        int hctCatIndex = 0;
                        for(int i=0; i < empTypeList.size();i++){
                            if(empTypeList.get(i).getValue().equalsIgnoreCase(obj.getInt("health_card_type_cat")+"")){
                                hctCatIndex = i;
                                break;
                            }
                        }
                        empType.setSelection(hctCatIndex);
                        if(hctIndex > 1){
                            selectedEmpType = "0";
                            typeBlock.setVisibility(View.VISIBLE);
                            hcnumlable.setVisibility(View.VISIBLE);
                            hcnum.setVisibility(View.VISIBLE);
                        }else{
                            selectedEmpType = "0";
                            typeBlock.setVisibility(View.GONE);
                            hcnumlable.setVisibility(View.GONE);
                            hcnum.setVisibility(View.GONE);
                        }
                        if(hctIndex == 4){
                            hcnumlable.setVisibility(View.GONE);
                            hcnum.setVisibility(View.GONE);
                        }
                        pageTitle.setText("Update User Request");
                        if(obj.getInt("is_clinic_visit") == 1){
                            submit.setVisibility(View.GONE);
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
        }
    }

    public void openDateSelector(){
        final Dialog dialogDate = new Dialog(UserRegister.this);
        dialogDate.setContentView(R.layout.date_picker);
        dialogDate.setCancelable(true);
        dialogDate.setTitle("Custom Dialog");
        WindowManager.LayoutParams lpl = new WindowManager.LayoutParams();
        lpl.copyFrom(dialogDate.getWindow().getAttributes());
        lpl.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lpl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lpl.gravity = Gravity.CENTER;
        dialogDate.getWindow().setAttributes(lpl);
        final DatePicker datePicker = (DatePicker) dialogDate.findViewById(R.id.datePicker);
        Button set = (Button) dialogDate.findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int m = datePicker.getMonth();
                String month =(m+1)+"";
                String year = datePicker.getYear()+"";
                String day = datePicker.getDayOfMonth()+"";
                if(month.length() == 1){
                    month = "0"+month;
                }
                if(day.length() == 1){
                    day = "0"+day;
                }
                selectedDOB = year+"-"+month+"-"+day;
                dob.setText(selectedDOB);
                dialogDate.dismiss();
            }
        });
        dialogDate.show();
    }

    public void openDateTimeSelector(){
        final Dialog dialogDate = new Dialog(UserRegister.this);
        dialogDate.setContentView(R.layout.date_picker);
        dialogDate.setCancelable(true);
        dialogDate.setTitle("Custom Dialog");
        WindowManager.LayoutParams lpl = new WindowManager.LayoutParams();
        lpl.copyFrom(dialogDate.getWindow().getAttributes());
        lpl.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lpl.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lpl.gravity = Gravity.CENTER;
        dialogDate.getWindow().setAttributes(lpl);
        final DatePicker datePicker = (DatePicker) dialogDate.findViewById(R.id.datePicker);
        Button set = (Button) dialogDate.findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int m = datePicker.getMonth();
                String month =(m+1)+"";
                String year = datePicker.getYear()+"";
                String day = datePicker.getDayOfMonth()+"";
                if(month.length() == 1){
                    month = "0"+month;
                }
                if(day.length() == 1){
                    day = "0"+day;
                }
                selectedAPT = year+"-"+month+"-"+day;
                apt.setText(selectedAPT);
                dialogDate.dismiss();
            }
        });
        dialogDate.show();
    }

    private class ClinicAction extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(UserRegister.this);
            progressDialog.setMessage(getResources().getString(R.string.processing));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(Config.clinic);
                JSONObject cred = new JSONObject();
                try {
                    cred.put("state", params[0]);
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
                editor.putString("clinic",buffer.toString());
                editor.commit();
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
            populateClink();
        }
    }

    public void populateClink(){
        //ClinicDropdown
        String clinicJson = sf.getString("clinic",null);
        List<String> clinicStrList = new ArrayList<String>();
        ValueId vi = new ValueId();
        vi.setValue("0");
        vi.setLabel("Select");
        clinicList.add(vi);
        clinicStrList.add(vi.getLabel());
        if(null != clinicJson){
            try{
                JSONArray ar = new JSONArray(clinicJson);
                for(int i = 0; i < ar.length(); i++){
                    JSONObject o = ar.getJSONObject(i);
                    vi = new ValueId();
                    vi.setValue(o.getString("id"));
                    vi.setLabel(o.getString("name"));
                    clinicStrList.add(vi.getLabel());
                    clinicList.add(vi);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        ArrayAdapter<String> clinicAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, clinicStrList);
        clinicAdapter.setDropDownViewResource(R.layout.multiline_item);
        clinic.setAdapter(clinicAdapter);
        clinic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClinic = clinicList.get(position).getValue();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void insertUserRequest(){
        try {
            String mobilestr = mobile.getText().toString();
            String mobile2str = mobile2.getText().toString();
            String fname = firstname.getText().toString();
            String lname = lastname.getText().toString();
            String addres = address.getText().toString();
            String citystr = city.getText().toString();
            String remarkstr = remark.getText().toString();
            String hcnstr = hcnum.getText().toString();
            String careofstr = careof.getText().toString();
            String agestr = age.getText().toString();
            if(agestr.length() == 0){
                agestr = "0";
            }
            if(careofstr.length() == 0){
                careofstr = "-";
            }
            if(fname.length() == 0){
                fname = "-";
            }
            if(lname.length() == 0){
                lname = "-";
            }
            if(addres.length() == 0){
                addres = "-";
            }
            if(citystr.length() == 0){
                citystr = "-";
            }
            if(remarkstr.length() == 0){
                remarkstr = "-";
            }
            if(hcnstr.length() == 0){
                hcnstr = "0";
            }
            if(mobile2str.length() == 0){
                mobile2str = "0";
            }
            if(hcnstr.equalsIgnoreCase("0") && selectedHCT.equalsIgnoreCase("0")){
                selectedHCT = hctList.get(1).getValue();
            }
            cred.put("rid",rid);
            cred.put("mobile",mobilestr);
            cred.put("mobile_2",mobile2str);
            cred.put("datetime",sf.getString("logindate",selectedAPT)+" 00:00:00");
            cred.put("appointment_datetime",selectedAPT+" 00:00:00");
            cred.put("dob",selectedDOB);
            cred.put("care_of",careofstr);
            cred.put("first_name",fname);
            cred.put("last_name",lname);
            cred.put("address",addres);
            cred.put("city",citystr);
            cred.put("remark",remarkstr);
            cred.put("health_card_number",hcnstr);
            cred.put("age",agestr);
            cred.put("is_clinic_visit",0);
            cred.put("is_treatment_start",0);
            cred.put("gender",selectedGender);
            cred.put("state",Integer.parseInt(selectedState));
            cred.put("country",Integer.parseInt(selectedCountry));
            cred.put("clinic",Integer.parseInt(selectedClinic));
            cred.put("disease",0);
            cred.put("health_card_type",Integer.parseInt(selectedHCT));
            cred.put("employee",Integer.parseInt(sf.getString("employee_id","0")));
            //new field
            if(Integer.parseInt(selectedHCT) == 1){
                selectedEmpType = "0";
            }
            cred.put("care_of_type",Integer.parseInt(selectedCoType));
            cred.put("health_card_type_cat",Integer.parseInt(selectedEmpType));
            boolean isErrorInput = true;
            if(mobilestr.length() == 10){
                isErrorInput = false;
            }
            if(!isErrorInput){
                new RegisterAction().execute();
            }else{
                alterError();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private class RegisterAction extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(UserRegister.this);
            progressDialog.setMessage(getResources().getString(R.string.processing));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(Config.register_request);
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
                        if(obj.has("success")){
                            isSucess = true;
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
            if(isSucess){
                Intent intent = new Intent(getApplicationContext(), Sucess.class);
                startActivity(intent);
            }else{
                alterError();
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

    public void alterError(){
        new AlertDialog.Builder(UserRegister.this)
                .setTitle("Error").setMessage("Please complete all mandatory fields marked with a * which are the minimum required fields")
                .setNegativeButton("OK", null).show();
    }
}