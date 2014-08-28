package com.github.andrdev.sc2gamer;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;


public class AlarmReceiver extends BroadcastReceiver {
    //build proper notification
    @Override
    public void onReceive(Context context, Intent intent) {
//        Notification mNotification = new Notification.Builder(context).
//                setAutoCancel(true).
//                setDefaults(Notification.DEFAULT_ALL).
//                build();
//        NotificationManager notificationManager = (NotificationManager)
//                context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, mNotification);
        Intent i = new Intent(context, AlarmDialogActivity.class);
        i.putExtra("AlarmId", intent.getIntExtra("AlarmId", 1));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
