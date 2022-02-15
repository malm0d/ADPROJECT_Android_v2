package iss.nus.adproject_android_v2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlertReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "888888";
    private static final String CHANNEL_NAME = "Message Notification Channel";
    private static final String CHANNEL_DESCRIPTION = "This channel is for displaying message";

    @Override
    public void onReceive(Context context, Intent intent) {

        createNotificationChannel(context);
        createNotification(context);

        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager= context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification(Context context){
        Intent intent = new Intent(context, Settings.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.meal)
                .setContentTitle("Food Diary")
                .setContentText("Have you logged in your entry today?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        int notificationId=11111;
        NotificationManagerCompat mgr = NotificationManagerCompat.from(context);
        Notification notification = builder.build();
        mgr.notify(notificationId, notification);
    }
}
