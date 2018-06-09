package com.example.hwarang.threemealsdev.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.example.hwarang.threemealsdev.R;
import com.example.hwarang.threemealsdev.main.MainActivity;

public class NotificationReceiver extends BroadcastReceiver{

    //private NotificationManager mManager;
    private Notification.Builder mBuilder;
    //private final String mChannelId = "default";

    public NotificationReceiver(){

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm Received!", Toast.LENGTH_LONG).show();
        Log.d("notifi test", "noti test1");
        //Uri notificatin =
        //TODO notification
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d("notifi test", "noti test2");

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            mBuilder = new Notification.Builder(context);
            //Toast.makeText(context, "Alarm Received!", Toast.LENGTH_LONG).show();
        }else{
            NotificationChannel mChannel = new NotificationChannel("andokdcapp", "andokdcapp", NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(mChannel);

            mBuilder = new Notification.Builder(context, mChannel.getId());
            //Toast.makeText(context, "Alarm Received!", Toast.LENGTH_LONG).show();
        }
        mBuilder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_mom)
                .setTicker("철수야 밥 먹자")
                .setContentTitle("식사 알리미")
                .setContentText("식사는 하셨나요?, 하셨다면 식단을 등록해주세요~!");

        mBuilder.setWhen(System.currentTimeMillis());

        Log.d("notifi test", "noti test3");
        mBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        Log.d("notifi test", "noti test4");
        nm.notify(111, mBuilder.build());
        Log.d("notifi test", "noti test5");


        //Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        //vibrator.vibrate(5000);
    }
}
