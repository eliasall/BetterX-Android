package com.betterx.android.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.betterx.android.dataModel.Message;
import com.betterx.android.dataModel.TransmissionSettings;
import com.betterx.android.dataModel.UserData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * this class provides ability to work with shared preferences easily
 */
@Singleton
public class PersistentDataStore {

    private static final String PERSISTENT_PREFERENCES = "persistent_prefs";
    private static final String PERSISTENT_USER_DATA = "user_data";
    private static final String PERSISTENT_SHARE_COUNT = "share_count";
    private static final String PERSISTENT_TRANSMISSION_SETTINGS = "transmission_settings";
    private static final String PERSISTENT_MESSAGES = "messages";
    private static final String PERSISTENT_START_COLLECTING_DATE = "start_collecting_date";
    private static final String PERSISTENT_GCM_REG_ID = "gcm_reg_id";
    private static final String PERSISTENT_IS_REGISTERED = "is_registered";

    private final SharedPreferences preferences;
    private TransmissionSettings transmissionSettings;
    private UserData userData;
    private List<Message> messages;

    @Inject
    public PersistentDataStore(Context context) {
        preferences = context.getSharedPreferences(PERSISTENT_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * put user data, which will be received from user during registration, to shared preferences
     */
    public void saveUserData(UserData userData) {
        final Gson gson = new Gson();
        final String userDataJson = gson.toJson(userData);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PERSISTENT_USER_DATA, userDataJson);
        editor.apply();
        this.userData = userData;
    }

    /**
     * put transmission settings, which will be received from user during registration, to shared preferences
     */
    public void saveTransmissionSettings(TransmissionSettings transmissionSettings) {
        final Gson gson = new Gson();
        final String transmissionSettingsJson = gson.toJson(transmissionSettings);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PERSISTENT_TRANSMISSION_SETTINGS, transmissionSettingsJson);
        editor.apply();

        this.transmissionSettings = transmissionSettings;
    }

    /**
     *  put incoming and outcoming messages to sharedpreferences
     */
    public void saveMessage(Message msg) {
        final List<Message> messageList = getMessages();
        messageList.add(msg);

        final Gson gson = new Gson();
        final String messagesJson = gson.toJson(messageList);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PERSISTENT_MESSAGES, messagesJson);
        editor.apply();

        this.messages = messageList;
    }

    /**
     *  mark all incoming messages as read
    */
    public void markAllMessagesAsReaded() {
        final List<Message> messageList = getMessages();
        if(messageList != null && messageList.size() > 0) {
            for(Message message : messageList) {
                message.isReaded = true;
            }
        }

        final Gson gson = new Gson();
        final String messagesJson = gson.toJson(messageList);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PERSISTENT_MESSAGES, messagesJson);
        editor.apply();
        this.messages = messageList;
    }

    /**
     * @return unread incoming messages count
     */
    public int getUnreadMsgCount() {
        int total = 0;
        final List<Message> messageList = getMessages();
        if(messageList != null && messageList.size() > 0) {
            for(Message message : messageList) {
                if(!message.isReaded) {
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * save user tickets count
     */
    public void saveShareCount(int count) {
        final int previousCount = getSharesCount();
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PERSISTENT_SHARE_COUNT, previousCount + count);
        editor.apply();
    }

    /**
     * save start collecting data time
     */
    public void saveStartCollectingDate(long time) {
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(PERSISTENT_START_COLLECTING_DATE, time);
        editor.apply();
    }

    /**
     * save google cloud messaging registration id
     */
    public void saveGcmRegId(String regId) {
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PERSISTENT_GCM_REG_ID, regId);
        editor.apply();
    }

    public void saveRegistrationStatuse(boolean isRegistered) {
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PERSISTENT_IS_REGISTERED, isRegistered);
        editor.apply();
    }

    /**
     * @return true, if user finish registration
     */
    public boolean isRegistrationFinished() {
        return preferences.getBoolean(PERSISTENT_IS_REGISTERED, false);
    }

    /**
     * @return count of user shares. now this value and tickets value are the same
     */
    public int getSharesCount() {
        return preferences.getInt(PERSISTENT_SHARE_COUNT, 1);
    }

    /**
     * @return saved user data
     */
    public UserData getUserData() {
        if (userData == null) {
            userData = new UserData();
            final String userDataJson = preferences.getString(PERSISTENT_USER_DATA, "");
            if (!TextUtils.isEmpty(userDataJson)) {
                final Gson gson = new Gson();
                userData = gson.fromJson(userDataJson, UserData.class);
            }
        }
        return userData;
    }

    /**
     * @return incoming and outcoming message list
     */
    public List<Message> getMessages() {
//        if (messages == null) {
            final String messagesJson = preferences.getString(PERSISTENT_MESSAGES, "");
            messages = new ArrayList<>();
            if (!TextUtils.isEmpty(messagesJson)) {
                Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
                final Gson gson = new Gson();
                messages = gson.fromJson(messagesJson, listType);
            }
//        }
        return messages;
    }

    /**
     * @return start collecting data time
     */
    public long getStartCollectingDate() {
        return preferences.getLong(PERSISTENT_START_COLLECTING_DATE, 0);
    }

    /**
     * @return google cloud message reg id
     */
    public String getGcmRegId() {
        return preferences.getString(PERSISTENT_GCM_REG_ID, "");
    }

    /**
     * @return transmission settings
     */
    public TransmissionSettings getTransmissionSettings() {
        if (transmissionSettings == null) {
            final String transmissionSettingsJson = preferences.getString(PERSISTENT_TRANSMISSION_SETTINGS, "");
            if (!TextUtils.isEmpty(transmissionSettingsJson)) {
                final Gson gson = new Gson();
                transmissionSettings = gson.fromJson(transmissionSettingsJson, TransmissionSettings.class);
            }
        }
        return transmissionSettings;
    }

}
