package com.jarvis.instarepost.classes;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.jarvis.instarepost.R;
import com.jarvis.instarepost.activities.MainActivity;
import com.securepreferences.SecurePreferences;
import java.util.Objects;

public class ClipBoardService extends Service {

    int flag = 1;
    public ClipboardManager clipboardManager;
    private OnPrimaryClipChangedListener mOnPrimaryClipChangedListener = new OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            String str;
            ClipData primaryClip = ClipBoardService.this.clipboardManager.getPrimaryClip();
            if (primaryClip != null) {
                Item itemAt = primaryClip.getItemAt(0);
                if (itemAt != null) {
                    CharSequence text = itemAt.getText();
                    if (text != null) {
                        str = text.toString();
                        if (str != null && str.matches("https://www.instagram.com/p/(.*)") && flag == 1) {
                            Log.e("insideIF","insideIf");
                            Intent intent = new Intent(ClipBoardService.this, MainActivity.class);
                            intent.setFlags(268468224);
                            intent.putExtra("from_service", true);
                            try {
                                ClipBoardService.this.startActivity(intent);
                                return;
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    }
                }
            }
            str = null;
            if (str != null) {
            }
        }
    };

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        this.clipboardManager = (ClipboardManager) getSystemService("clipboard");
        ClipboardManager clipboardManager2 = this.clipboardManager;
        if (clipboardManager2 != null) {
            clipboardManager2.addPrimaryClipChangedListener(this.mOnPrimaryClipChangedListener);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy","onDestroy");
        flag = 0;
        ((NotificationManager) Objects.requireNonNull((NotificationManager) getSystemService("notification"))).cancel(0);
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        SecurePreferences securePreferences = new SecurePreferences(this);
        if (securePreferences.getBoolean("notification_bar", false) && securePreferences.getBoolean("auto_download", true)) {
            new NotificationHelper(this).createNotification(getString(R.string.auto_download), getString(R.string.auto_download_enabled));
        }
        flag = 1;
        Log.e("onStart","onStart");
        return START_STICKY;
    }
}
