package com.jarvis.instarepost.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import com.securepreferences.SecurePreferences;
import java.util.Objects;

public class NotificationActivity extends Activity {
    public static final String AUTO_DOWNLOAD_NOTIFICATION_ID = "AUTO_DOWNLOAD";

    public static PendingIntent getDismissIntent(int i, Context context) {
        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(268468224);
        intent.putExtra(AUTO_DOWNLOAD_NOTIFICATION_ID, i);
        return PendingIntent.getActivity(context, 0, intent, 268435456);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ((NotificationManager) Objects.requireNonNull((NotificationManager) getSystemService("notification"))).cancel(getIntent().getIntExtra(AUTO_DOWNLOAD_NOTIFICATION_ID, -1));
        Editor edit = new SecurePreferences(this).edit();
        edit.apply();
        edit.putBoolean("notification_bar", false).apply();
        finish();
    }
}
