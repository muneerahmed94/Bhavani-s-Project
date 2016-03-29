package com.example.ruchithaprasad.ui;

/**
 * Created by Ruchitha prasad on 29-03-2016.
 */
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NLService extends NotificationListenerService {
    int k;
    private String TAG = this.getClass().getSimpleName();
    private NLServiceReceiver nlservicereciver;
    @Override
    public void onCreate() {
        super.onCreate();
        nlservicereciver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kpbird.nlsexample.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlservicereciver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Intent i = new  Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event"," " + sbn.getPackageName() + "\n");
        i.putExtra("package",""+sbn.getPackageName());
        i.putExtra("notification_event","\nNotification "+k);
        i.putExtra("package",""+sbn.getPackageName());
        Notification mNotification=sbn.getNotification();
        Bundle extras = mNotification.extras;
        i.putExtra("title",""+extras.getString("android.title"));
        i.putExtra("text",""+extras.getCharSequence("android.text"));
        sendBroadcast(i);
        k++;

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

    class NLServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("command").equals("clearall")){
                Intent i = new  Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
                NLService.this.cancelAllNotifications();
                i.putExtra("notification_event","clear");

                sendBroadcast(i);
            }
            else if(intent.getStringExtra("command").equals("list")){
                Intent i1 = new  Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
                i1.putExtra("notification_event","clear");
                sendBroadcast(i1);
                k=1;
                for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
                    String check="posted";
                    Intent i2 = new  Intent("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
                    i2.putExtra("notification_event","\nNotification "+k);
                    i2.putExtra("package",""+sbn.getPackageName());
                    i2.putExtra("pack",""+check);
                    Notification mNotification=sbn.getNotification();
                    Bundle extras = mNotification.extras;
                    i2.putExtra("title",""+extras.getString("android.title"));
                    i2.putExtra("text",""+extras.getCharSequence("android.text"));
                    sendBroadcast(i2);
                    k++;
                }


            }

        }
    }

}
