package com.oneproject.www.smartoday;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by admin on 2017-08-03.
 */

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private String p;
    private Activity activity;

    public BackPressCloseHandler(Activity context,String p) {
        this.activity = context;
        this.p=p;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                activity.getString(R.string.alert_close,p), Toast.LENGTH_SHORT);
        toast.show();
    }
}
