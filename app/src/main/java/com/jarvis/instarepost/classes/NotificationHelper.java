package com.jarvis.instarepost.classes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import androidx.core.app.NotificationCompat.Builder;
import com.jarvis.instarepost.R;
import com.jarvis.instarepost.activities.MainActivity;
import com.jarvis.instarepost.activities.NotificationActivity;
import java.util.Objects;

public class NotificationHelper {
    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    static final boolean a = false;
    private Context context;

    public NotificationHelper(Context context2) {
        this.context = context2;
    }

    public void createNotification(String str, String str2) {
        PendingIntent dismissIntent = NotificationActivity.getDismissIntent(0, this.context);
        Intent intent = new Intent(this.context, MainActivity.class);
        intent.addFlags(268435456);
        PendingIntent activity = PendingIntent.getActivity(this.context, 0, intent, 134217728);
        Builder builder = new Builder(this.context);
        builder.setSmallIcon(R.drawable.ic_download_notification);
        builder.setContentTitle(str).setContentText(str2).setAutoCancel(false).setOngoing(true).addAction(R.drawable.ic_close, App.getContext().getString(R.string.dismiss_notification), dismissIntent).setContentIntent(activity);
        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService("notification");
        if (VERSION.SDK_INT >= 26) {
            String str3 = NOTIFICATION_CHANNEL_ID;
            NotificationChannel notificationChannel = new NotificationChannel(str3, "Auto Download", 3);
            builder.setChannelId(str3);
            ((NotificationManager) Objects.requireNonNull(notificationManager)).createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(0, builder.build());
    }
}
