package com.example.petcare;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //get id,message
        int notificationId=intent.getIntExtra("notificationId",0);
        String message=intent.getStringExtra("todo");

        Intent Reminders=new Intent(context,Reminders.class);
        PendingIntent contentIntent =PendingIntent.getActivity(context,0,Reminders,0);

        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //prepare notification
        Notification.Builder builder=new Notification.Builder(context);
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("זה הזמן!")
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(contentIntent);

        notificationManager.notify(notificationId,builder.build());
    }
}
