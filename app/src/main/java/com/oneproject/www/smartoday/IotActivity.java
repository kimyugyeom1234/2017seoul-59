package com.oneproject.www.smartoday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.oneproject.www.smartoday.DBHelper.TABLE_NAME3;
import static com.oneproject.www.smartoday.DBHelper._CHECK;
import static com.oneproject.www.smartoday.DBHelper._ID;
import static com.oneproject.www.smartoday.DBHelper._LOGO;
import static com.oneproject.www.smartoday.DBHelper._NAME;
import static com.oneproject.www.smartoday.DBHelper._NIC;
import static com.oneproject.www.smartoday.DBHelper._PKG;

public class IotActivity extends AppCompatActivity {
    ImageView icon_check, icon_iot, icon_setting, imageView20;
    public static final int iotactcode = 1002;


    JSONArray JsonArray = null;

    ArrayList<Iotstatus> personList;
    DBHelper mHelper;
    SQLiteDatabase db;
    Cursor cursor;
    MyCursorAdapter mAdapter;
    BackPressCloseHandler b;

    String id;
    SharedPreferences login;

    @Override
    public boolean supportRequestWindowFeature(int featureId) {
        return super.supportRequestWindowFeature(featureId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.activity_iot);
        setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.settingon);

        login = getSharedPreferences("appData", Activity.MODE_PRIVATE);
        id = login.getString("ID", "");

        icon_check = (ImageView) findViewById(R.id.check);
        icon_iot = (ImageView) findViewById(R.id.iot);
        icon_setting = (ImageView) findViewById(R.id.setting);
        imageView20 = (ImageView) findViewById(R.id.imageView20);

        personList = new ArrayList<Iotstatus>();

        b=new BackPressCloseHandler(this,getString(R.string.btn_close));


        try {
            mHelper = new DBHelper(this);
            db = mHelper.getWritableDatabase();
            cursor = db.rawQuery(String.format("select * from %s", TABLE_NAME3), null);
            mAdapter = new MyCursorAdapter(IotActivity.this, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ListView lvHome = (ListView) findViewById(R.id.lvHome);
        lvHome.setAdapter(mAdapter);

        imageView20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //https://play.google.com/store/search?q=iot
                Intent it = new Intent(IotActivity.this, IotListActivity.class);
                startActivityForResult(it, iotactcode);
            }
        });
        icon_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icon_check.setImageResource(R.drawable.checkon);
                icon_iot.setImageResource(R.drawable.wifi);
                icon_setting.setImageResource(R.drawable.settingoff);
                Intent it = new Intent(IotActivity.this, CheckActivity.class);
                startActivity(it);
                finish();
            }
        });
        icon_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icon_check.setImageResource(R.drawable.check);
                icon_iot.setImageResource(R.drawable.wifi);
                icon_setting.setImageResource(R.drawable.settingon);
                Intent it = new Intent(IotActivity.this, SettingActivity.class);
                startActivity(it);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        b.onBackPressed();
    }


    class MyCursorAdapter extends CursorAdapter {
        @SuppressWarnings("deprecation")
        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.iotpkglist, parent, false);
            return v;
        }


        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ImageView ivlogo=(ImageView)view.findViewById(R.id.applogo);
            TextView tvIot1 = (TextView) view.findViewById(R.id.appname);
            TextView tvIot2 = (TextView) view.findViewById(R.id.pkgname);
            CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
            cb.setVisibility(View.GONE);
            //TextView tvIot3 = (TextView) view.findViewById(R.id.iot_text3);
            //TextView tvIot4 = (TextView) view.findViewById(R.id.iot_text4);

            LinearLayout lliotHome = (LinearLayout) view.findViewById(R.id.lliot);

            final int id = cursor.getInt(cursor.getColumnIndex(_ID));
            final String pkg = cursor.getString(cursor.getColumnIndex(_PKG));
            final String name = cursor.getString(cursor.getColumnIndex(_NAME));
            //final String check = cursor.getString(cursor.getColumnIndex(_CHECK));
            try{
                ivlogo.setImageDrawable(getPackageManager().getApplicationIcon(pkg));
            }catch (PackageManager.NameNotFoundException e){}
            tvIot1.setText(name);
            tvIot2.setText(pkg);

            lliotHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(IotActivity.this, name, Toast.LENGTH_SHORT).show();
                    try {
                        if (getPackageList(pkg)) {
                            Intent intent = getPackageManager().getLaunchIntentForPackage(pkg);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else{
                            String url = "https://play.google.com/store/search?q="+name;
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(i);
                        }
                    }catch (Exception e){
                    }
                }
            });
            lliotHome.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    db.execSQL(String.format("delete from %s where %s=%s",TABLE_NAME3,_ID,id));
                    refreshDB();
                    Toast.makeText(IotActivity.this,R.string.alert_delete,Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }

    }
    public boolean getPackageList(String pkg) {
        boolean isExist = false;

        PackageManager pkgMgr = getPackageManager();
        List<ResolveInfo> mApps;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = pkgMgr.queryIntentActivities(mainIntent, 0);

        try {
            for (int i = 0; i < mApps.size(); i++) {
                if(mApps.get(i).activityInfo.packageName.startsWith(pkg)){
                    isExist = true;
                    break;
                }
            }
        }
        catch (Exception e) {
            isExist = false;
        }
        return isExist;
    }
    public void refreshDB() {
        cursor = db.rawQuery(String.format("select * from %s", TABLE_NAME3), null);
        mAdapter.changeCursor(cursor);
    }

    ArrayList<IotList> discoveryResults;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            discoveryResults = new ArrayList<>();
            //Toast.makeText(getApplicationContext(), "recieved", Toast.LENGTH_SHORT).show();
            ArrayList<String> hostname = data.getStringArrayListExtra("name");
            ArrayList<String> ipaddress = data.getStringArrayListExtra("pkg");
            int count = data.getIntExtra("count", 0);
            for (int i = 0; i < count; i++) {
                IotList discoveryResult = new IotList();
                discoveryResult.name = hostname.get(i);
                discoveryResult.pkg = ipaddress.get(i);
                String query = String.format("insert into %s values (null,'%s','%s')", TABLE_NAME3, hostname.get(i), ipaddress.get(i));
                db.execSQL(query);
                discoveryResults.add(i, discoveryResult);
            }
            refreshDB();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.


        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.title, null);
        ImageView ivIcon = (ImageView) actionbar.findViewById(R.id.title_icon);
        TextView ivLetter = (TextView) actionbar.findViewById(R.id.title_letter);
        Button btn_logout = (Button) actionbar.findViewById(R.id.logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logout
                Intent intent = new Intent(IotActivity.this, MainActivity.class);
                startActivity(intent);
                SharedPreferences auto = getSharedPreferences("appData", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = auto.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(IotActivity.this, getString(R.string.logout), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        ivIcon.setImageResource(R.drawable.wifion);
        ivLetter.setText(R.string.title_iot);
        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        return true;
    }


}