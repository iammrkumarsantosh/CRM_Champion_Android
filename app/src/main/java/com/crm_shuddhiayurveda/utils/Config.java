package com.crm_shuddhiayurveda.utils;

public interface Config {
    String api_key = "ProHealth";
    String api_domain = "http://192.168.1.8:8000/api";
    String healthcardtype = api_domain+"/healthcardtype/?format=json";
    String country = api_domain+"/country/?format=json";
    String state = api_domain+"/state/?format=json";
    String disease = api_domain+"/disease/?format=json";
    String login = api_domain+"/login/?format=json";
    String attendance = api_domain+"/attendance/?format=json";
    String clinic = api_domain+"/clinic/?format=json";
    String register_request = api_domain+"/registerrequest/?format=json";
    String viewuserrequest = api_domain+"/viewuserrequest/?format=json";
    String careof = api_domain+"/careof/?format=json";
    String healthcardTypecategory = api_domain+"/healthcardTypecategory/?format=json";
    String ureqdata = api_domain+"/ureqdata/?format=json";
    String searchbymobile = api_domain+"/searchbymobile/?format=json";
}
