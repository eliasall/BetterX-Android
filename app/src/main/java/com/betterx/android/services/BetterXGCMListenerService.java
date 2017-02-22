package com.betterx.android.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.betterx.android.R;
import com.betterx.android.app.BetterxApp;
import com.betterx.android.app.events.MessageReceivedEvent;
import com.betterx.android.classes.MainThreadBus;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.dataModel.Message;
import com.betterx.android.dataModel.MessageType;
import com.betterx.android.ui.activities.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class BetterXGCMListenerService extends GcmListenerService {

    private static final String KEY_MSG = "price";

    @Inject
    PersistentDataStore dataStore;
    @Inject
    MainThreadBus bus;


    @Override
    public void onCreate() {
        super.onCreate();
        BetterxApp.component(getApplicationContext()).inject(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if(data != null && data.containsKey(KEY_MSG)) {
            // parse message
            final String msg = data.getString(KEY_MSG);
            final List<Message> messages = dataStore.getMessages();
            if(messages == null || messages.size() == 0 || !messages.get(messages.size() - 1).msg.equals(msg)) {
                //create message object, which contains some additional information about message
                final Message message = new Message();
                message.messageType = MessageType.RECEIVED;
                message.msg = msg;
                message.isReaded = false;
                message.date = System.currentTimeMillis();
                Timber.d("Received message %s", data.toString());
                dataStore.saveMessage(message);
                sendNotification(message.msg);
                bus.post(new MessageReceivedEvent());
            }

        }
    }

    /**
     * notify user that new message received
     * @param message - received message
     */
    private void sendNotification(String message) {
        final PendingIntent pendingIntent = getNotificationIntent();

        final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentTitle(getString(R.string.notification_msg_title));
        notificationBuilder.setContentText(message);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);

        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    /**
     * @return intent, which will be done, if user click notification
     */
    private PendingIntent getNotificationIntent() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }
}