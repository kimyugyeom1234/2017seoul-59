package com.oneproject.www.smartoday;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class weather_select extends AppCompatActivity {
    SpinnerAdapter spinnerAdapter;
    SharedPreferences weather;
    SharedPreferences.Editor editor;
    String regin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_select);
        setTitle("날씨 지역 설정");
        weather=getSharedPreferences("weather",0);
        regin=weather.getString("name","");
        Toast.makeText(getApplicationContext(),"현재 지역 : "+regin,Toast.LENGTH_SHORT).show();
        Spinner spinner=(Spinner)findViewById(R.id.spinner);
        spinnerAdapter= ArrayAdapter.createFromResource(this, R.array.region, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        Button btn=(Button)findViewById(R.id.btn_weathersubmit);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String region_id[]={"01150615","02820250","03880370", "04920310","05110590","06140132","07140106","08110141","09380104","10110620","11260630","12730310","13710253","14110130","15210510","16745250","17110109"};
                editor=weather.edit();
                editor.putString("name",spinnerAdapter.getItem(position).toString());
                editor.putString("id",region_id[position]);
                editor.putBoolean("changed",true);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
