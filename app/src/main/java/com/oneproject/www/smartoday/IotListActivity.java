package com.oneproject.www.smartoday;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.oneproject.www.smartoday.DBHelper.TABLE_NAME3;
import static com.oneproject.www.smartoday.DBHelper._LOGO;
import static com.oneproject.www.smartoday.DBHelper._NIC;
import static com.oneproject.www.smartoday.DBHelper._PKG;

public class IotListActivity extends AppCompatActivity {
    ArrayList<IotList> apps,tempList;
    SQLiteDatabase db;
    DBHelper mHelper;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_list);
        apps = new ArrayList<IotList>();
        tempList=new ArrayList<>();
        mHelper=new DBHelper(this);
        db=mHelper.getWritableDatabase();

        List<PackageInfo> pack = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < pack.size(); i++) {
            IotList iotList = new IotList();
            String pkg = pack.get(i).packageName;
            iotList.pkg = pkg;
            try {
// 패키지명을 이용해 아이콘 가져오기
                iotList.icon = getPackageManager().getApplicationIcon(pkg);
// 패키지명을 이용해 앱 이름 가져오기
                iotList.name = getPackageManager().getApplicationLabel
                        (getPackageManager().getApplicationInfo
                                (pkg, PackageManager.GET_UNINSTALLED_PACKAGES))
                        .toString();
            } catch (PackageManager.NameNotFoundException e) {
            }
            String pkgs[] = {"ohman", "ohcam", "skt.sh", "kiturami","iot","ht.cctv2"};
            for (int k = 0; k < pkgs.length; k++) {
                if (pkg.contains(pkgs[k])) {
                    apps.add(iotList);
                    break;
                }
            }
        }
        ListView listView = (ListView) findViewById(R.id.lvIot);
        TextView tvnone=(TextView)findViewById(R.id.tvnone);
        IotAdapter iotadapter = new IotAdapter(IotListActivity.this, apps);
        listView.setAdapter(iotadapter);
        if (apps.size()==0){
            listView.setVisibility(View.GONE);
            tvnone.setVisibility(View.VISIBLE);
        }else{
            listView.setVisibility(View.VISIBLE);
            tvnone.setVisibility(View.GONE);
        }
        Button btn_submit=(Button)findViewById(R.id.btnSubmit_iot);
        Button btn_cancel=(Button)findViewById(R.id.btnCancel_iot);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IotList iotList;
                ArrayList<String> temp1 = new ArrayList<String>();
                ArrayList<String> temp2 = new ArrayList<String>();
                int t = 0;

                for (int i = 0; i < tempList.size(); i++) {
                    if (tempList.get(i).check) {
                        iotList = tempList.get(i);
                        temp1.add(t, iotList.name);
                        temp2.add(t, iotList.pkg);
                        t++;
                    }
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra("name", temp1);
                intent.putStringArrayListExtra("pkg", temp2);
                intent.putExtra("count", t);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private class IotAdapter extends BaseAdapter {
        ArrayList<IotList> apps;
        Context c;

        public IotAdapter(Context c, ArrayList<IotList> personList) {
            this.c = c;
            this.apps = personList;
        }

        @Override
        public Object getItem(int position) {
            return apps.get(position);
        }

        @Override
        public int getCount() {
            return apps.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            final IotList iotList;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(c);
                convertView = inflater.inflate(R.layout.iotpkglist, parent, false);
                viewHolder.lliot = (LinearLayout) convertView.findViewById(R.id.lliot);
                viewHolder.appname = (TextView) convertView.findViewById(R.id.appname);
                viewHolder.pkgname = (TextView) convertView.findViewById(R.id.pkgname);
                viewHolder.applogo = (ImageView) convertView.findViewById(R.id.applogo);
                viewHolder.cb = (CheckBox) convertView.findViewById(R.id.cb);
                viewHolder.cb.setFocusable(false);
                viewHolder.cb.setClickable(false);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            iotList = apps.get(position);
            viewHolder.applogo.setImageDrawable(iotList.icon);
            viewHolder.appname.setText(iotList.name);
            viewHolder.pkgname.setText(iotList.pkg);
            String pkg="";
            try {
                cursor = db.rawQuery(String.format("select %s from %s where %s='%s'", _PKG, TABLE_NAME3, _PKG, iotList.pkg), null);
                while (cursor.moveToNext()) {
                    pkg = cursor.getString(cursor.getColumnIndex(_PKG));
                }
            } catch (Exception e) {
                pkg= "";
            }
            if(pkg.length()>0){
                iotList.check=true;
            }
            viewHolder.lliot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.cb.setChecked(!iotList.check);
                }
            });
            viewHolder.cb.setChecked(iotList.check);
            viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    iotList.check = viewHolder.cb.isChecked();
                }
            });
            try {
                tempList.get(position);
            } catch (Exception e) {
                tempList.add(position,iotList);
            } finally {
                tempList.set(position, iotList);
            }
            return convertView;
        }

        class ViewHolder {
            LinearLayout lliot;
            ImageView applogo;
            TextView appname;
            TextView pkgname;
            CheckBox cb;
        }
    }
}
