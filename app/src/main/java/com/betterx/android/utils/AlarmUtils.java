package com.betterx.android.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.betterx.android.dataModel.TransmissionSettings;
import com.betterx.android.receivers.NeedRegistrationAlarmReceiver;
import com.betterx.android.receivers.UploadReceiver;
import com.betterx.android.receivers.ZipAndEncryptReceiver;

import java.util.Calendar;

public class AlarmUtils {

    public static final long REPEATING_TIME = 1000*60*60*24;

    /**
     * function to start logged file upload on s3 server
     * @param settings - uploading user settings
     */
    public static void startUploadAlarm(Context context, TransmissionSettings settings) {
        stopUploadAlarm(context);

        final AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, UploadReceiver.class);
        final PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, settings.transmissionTime.getTime());
        calendar.set(Calendar.MINUTE, 0);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), REPEATING_TIME, alarmIntent);
    }

    /**
     * function to start alarm, which notify user that he need to complete the registration
     */
    public static void startRegistrationNotificationAlarm(Context context) {
        stopRegistrationNotificationAlarm(context);

        final AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, NeedRegistrationAlarmReceiver.class);
        final PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
//        calendar.set(Calendar.MINUTE, 0);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), REPEATING_TIME, alarmIntent);

    }

    /**
     * function to start alarm, which notify user that he need to complete the registration
     */
    public static void startFileZippingAlarm(Context context) {
        stopFileZippingAlarm(context);

        final AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, ZipAndEncryptReceiver.class);
        final PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 10);
        calendar.set(Calendar.SECOND, 0);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), REPEATING_TIME, alarmIntent);

    }

    /**
     * function to stop registration notifications
     */
    public static void stopRegistrationNotificationAlarm(Context context) {
        final AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel pendingIntent for NeedRegistrationAlarmReceiver
        final Intent intent = new Intent(context, NeedRegistrationAlarmReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmMgr.cancel(pendingIntent);
    }

    /**
     * function to stop log upload to server
     */
    public static void stopUploadAlarm(Context ctx) {
        final AlarmManager alarmMgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        // Cancel pendingIntent for UploadReceiver
        final Intent intent = new Intent(ctx, UploadReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        alarmMgr.cancel(pendingIntent);
    }

    public static void stopFileZippingAlarm(Context ctx) {
        final AlarmManager alarmMgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        // Cancel pendingIntent for UploadReceiver
        final Intent intent = new Intent(ctx, ZipAndEncryptReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        alarmMgr.cancel(pendingIntent);
    }

}
