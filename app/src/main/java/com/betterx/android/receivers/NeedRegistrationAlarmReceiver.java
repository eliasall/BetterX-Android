package com.betterx.android.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.betterx.android.R;
import com.betterx.android.app.BetterxApp;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.ui.activities.StartActivity;

import javax.inject.Inject;

public class NeedRegistrationAlarmReceiver extends BroadcastReceiver {

    @Inject
    PersistentDataStore dataStore;

    @Override
    public void onReceive(Context context, Intent intent) {
        BetterxApp.component(context).inject(this);
        if(!dataStore.isRegistrationFinished()) {
            //if user still not registered, notify
            sendNotification(context);
        }
    }

    /**
     * notify user so he need to finish registration
     * show notification in statusbar
     */
    private void sendNotification(Context context) {
        final PendingIntent pendingIntent = getNotificationIntent(context);

        final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentTitle(context.getString(R.string.app_name));
        notificationBuilder.setContentText(context.getString(R.string.finish_registration_notification));
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);

        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private PendingIntent getNotificationIntent(Context context) {
        final Intent intent = new Intent(context, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }

}
