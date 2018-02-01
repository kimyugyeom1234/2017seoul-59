package com.oneproject.www.smartoday;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SubwaySearch extends AppCompatActivity {

    SubwayAPI subwayAPI;


    //역
    ArrayList<SubwayResult> list;
    ArrayList<SubwayResult> list2;
    ListView lvSubway;
    EditText edtSubway;
    Button btnSubway;
    ArrayList<String> subwayStation_cd, subwayStation_nm, subwayStation_lm;

    SubwayAdapter subwayAdapter;

    //도착

    String subwayArriveTime, subwayStartTime, subwayStationWay;
    TextView tv_ArriveTime, tv_StartTime, tv_StationWay;
    RadioGroup rgp;
    RadioButton rbt1, rbt2;
    int inout = 1;

    SharedPreferences subway;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subway_search);
        setTitle(R.string.title_subway);
        subway = getSharedPreferences("transit", 0);
        editor = subway.edit();
        lvSubway = (ListView) findViewById(R.id.lvSubway);
        edtSubway = (EditText) findViewById(R.id.edtSubway);
        btnSubway = (Button) findViewById(R.id.btnSubway);
        list = new ArrayList<SubwayResult>();
        doSubway();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                subwayStation_lm = subwayAPI.getStationLineNum();
                subwayStation_cd = subwayAPI.getStationCode();
                subwayStation_nm = subwayAPI.getStationName();
                int i = 0;
                list.clear();

                SubwayResult sb;
                while (i < subwayStation_nm.size()) {
                    sb = new SubwayResult(subwayStation_cd.get(i), subwayStation_nm.get(i), subwayStation_lm.get(i));
                    list.add(sb);
                    i++;
                }
                if(i>0)
                Toast.makeText(SubwaySearch.this, "finish", Toast.LENGTH_SHORT).show();

            }
        }, 1000);
        btnSubway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int temp = 0;
                list2 = new ArrayList<SubwayResult>();
                list2.clear();
                String edt = edtSubway.getText().toString().trim();
                for (int i = 0; i < 683; i++) {
                    SubwayResult temp_result = list.get(i);
                    //Toast.makeText(SubwaySearch.this,  temp_result.getSubwayName(), Toast.LENGTH_SHORT).show();
                    if (edt.length() > 0 && temp_result.getSubwayName().trim().contains(edt)) {
                        list2.add(temp_result);
                        temp++;
                    }
                }
                if (temp > 0)
                    subwayAdapter = new SubwayAdapter(getApplication(), list2);
                else
                    subwayAdapter = new SubwayAdapter(getApplication(), list);
                lvSubway.setAdapter(subwayAdapter);
                subwayAdapter.notifyDataSetChanged();
            }
        });


        tv_ArriveTime = (TextView) findViewById(R.id.tv_ArriveTime);
        tv_StartTime = (TextView) findViewById(R.id.tv_StartTime);
        tv_StationWay = (TextView) findViewById(R.id.tv_StationWay);
        rgp = (RadioGroup) findViewById(R.id.rgp);
        rbt1 = (RadioButton) findViewById(R.id.rbt1);
        rbt2 = (RadioButton) findViewById(R.id.rbt2);

        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if (checkedId == R.id.rbt1) {
                    inout = 1;
                } else if (checkedId == R.id.rbt2) {
                    inout = 2;
                }
            }
        });

    }

    public void doSubway() {
        subwayAPI = new SubwayAPI();
    }

    public class SubwayAdapter extends BaseAdapter {

        Context context;
        ArrayList<SubwayResult> post;

        public SubwayAdapter(Context c, ArrayList<SubwayResult> post) {
            this.context = c;
            this.post = post;
        }

        @Override
        public int getCount() { // adpter총길이
            return post.size();
        }

        @Override
        public SubwayResult getItem(int position) {
            return post.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;

        }

        @Override
        public View getView(final int position, View convertview, ViewGroup viewGroup) {
            LayoutInflater lay = LayoutInflater.from(context);
            View view = lay.inflate(R.layout.item_subway, null);

            final TextView tvSubway = (TextView) view.findViewById(R.id.tvSubway);
            final String code = post.get(position).getSubwayCode();
            final String name = post.get(position).getSubwayName();
            final String lineNum = post.get(position).getSubwayLinenum();
            tvSubway.setText(name + "  " + lineNum + "호선");

            tvSubway.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.putString("subway_name", name);
                    editor.putString("subway_num", lineNum);
                    editor.putString("code", code);
                    editor.putInt("inout", inout);
                    System.out.print("tesss" + code);
                    //Toast.makeText(getApplicationContext(),code+"  "+inout,Toast.LENGTH_SHORT).show();
                    editor.commit();
                    setResult(RESULT_OK);
                    finish();
                    //tv_StationWay.append("행");
                }
            });

            return view;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}