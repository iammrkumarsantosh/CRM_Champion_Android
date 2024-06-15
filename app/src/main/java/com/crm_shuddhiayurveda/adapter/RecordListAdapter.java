package com.crm_shuddhiayurveda.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crm_shuddhiayurveda.MainActivity;
import com.crm_shuddhiayurveda.R;

import com.crm_shuddhiayurveda.UserRegister;
import com.crm_shuddhiayurveda.utils.Config;
import com.crm_shuddhiayurveda.utils.RecordEntity;

import java.util.ArrayList;

public class RecordListAdapter extends ArrayAdapter<RecordEntity> {
    Context context;
    int layoutResourceId;
    SharedPreferences sf;
    SharedPreferences.Editor editor;
    ArrayList<RecordEntity> data = new ArrayList<RecordEntity>();
    public RecordListAdapter(Context context, int layoutResourceId, ArrayList<RecordEntity> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.sf = context.getSharedPreferences(Config.api_key, Context.MODE_PRIVATE);
        this.editor = sf.edit();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public RecordEntity getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.indexOf(data.get(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        final RecordEntity item = data.get(position);
        RecordListAdapter.ViewHolder holder;
        convertView = inflater.inflate(R.layout.record_list, parent, false);
        convertView.setTag(new RecordListAdapter.ViewHolder(convertView));
        holder = (RecordListAdapter.ViewHolder) convertView.getTag();
        String name = item.getFirstname()+" "+item.getLastname();
        if(name.trim().length() == 0){
            name = "-";
        }
        String appointment = item.getAppointment().substring(0,10);
        holder.mobile.setText("Mobile : +91-"+item.getMobile());
        holder.date.setText("Request Date : "+item.getRequestdate().substring(0,10));
        holder.fullname.setText(name);
        holder.careof.setText("Care of : "+item.getCareof());
        holder.aptdate.setText("Appointment Date : "+appointment);
        holder.clinic.setText("Clinic : "+item.getClinic());
        holder.state.setText(item.getState()+", India");
        if(appointment.equalsIgnoreCase("1999-01-01")){
            holder.aptdate.setVisibility(View.GONE);
        }
        if(item.getClinic().equalsIgnoreCase("0")){
            holder.clinic.setVisibility(View.GONE);
        }
        if(item.getCareof().equalsIgnoreCase("0")){
            holder.careof.setVisibility(View.GONE);
        }
        if(item.getFirstname().equalsIgnoreCase("0") && item.getLastname().equalsIgnoreCase("0")){
            holder.fullname.setVisibility(View.GONE);
        }
        holder.clinic.setVisibility(View.GONE);
        holder.state.setVisibility(View.GONE);
        holder.editicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("rid",item.getId());
                editor.commit();
                Intent intent = new Intent(context.getApplicationContext(), UserRegister.class);
                context.startActivity(intent);
            }
        });
        if(item.getVisited().equalsIgnoreCase("1")){
            holder.aptdate.setText("Clinic Visited");
            holder.editicon.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView mobile,date,fullname,careof,aptdate,clinic,state;
        ImageView editicon;
        ViewHolder(View root) {
            editicon = (ImageView)root.findViewById(R.id.editicon);
            mobile = (TextView)root.findViewById(R.id.mobile);
            date = (TextView)root.findViewById(R.id.date);
            fullname = (TextView)root.findViewById(R.id.fullname);
            careof = (TextView)root.findViewById(R.id.careof);
            aptdate = (TextView)root.findViewById(R.id.aptdate);
            clinic = (TextView)root.findViewById(R.id.clinic);
            state = (TextView)root.findViewById(R.id.state);
        }
    }
}
