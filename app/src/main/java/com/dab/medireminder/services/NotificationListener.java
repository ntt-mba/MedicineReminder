package com.dab.medireminder.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;

import com.dab.medireminder.data.model.IgnoreReason;
import com.dab.medireminder.utils.AlarmUtils;
import com.dab.medireminder.utils.AppUtils;
import com.dab.medireminder.utils.TextSpeechUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@SuppressLint("OverrideAbstract")
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService {

    private TextToSpeech textToSpeech;
    private String appID = "com.dab.medireminder";

    private static boolean isInitialized, isSuspended, isScreenOn;
    private AudioManager audioMan;
    private TelephonyManager telephony;
    private final DeviceStateReceiver stateReceiver = new DeviceStateReceiver();
    private Shake shake;
    private final OnStatusChangeListener statusListener = new OnStatusChangeListener() {
        @Override
        public void onStatusChanged() {
            if (isSuspended && textToSpeech != null) {
                textToSpeech.stop();
            }
        }
    };
    private static final List<OnStatusChangeListener> statusListeners = new ArrayList<>();

    @Override
    public void onCreate() {
        initTextToSpeech(this);
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (sbn != null) {
            if (sbn.getPackageName().equals(appID)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    audioMan.requestAudioFocus(null, AudioManager.STREAM_RING,
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

                    Notification notification = sbn.getNotification();
                    CharSequence mContent = notification.extras.getString(Notification.EXTRA_TEXT);
                    String speakWord = mContent == null ? "Đã đến giờ uống thuốc rồi bạn nhé" : mContent.toString();

                    textToSpeech.speak(speakWord, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        if (sbn != null && sbn.getPackageName().equals(appID)) {
            AlarmUtils.getAlarmUtils().cancelAlarm(this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (isInitialized) return super.onBind(intent);
        audioMan = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(stateReceiver, filter);
        shake = new Shake(this);
        shake.setOnShakeListener(new Shake.OnShakeListener() {
            @Override
            public void onShake() {
                if (textToSpeech != null) textToSpeech.stop();
            }
        });
        registerOnStatusChangeListener(statusListener);
        setInitialized(true);
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (isInitialized) {
            if (textToSpeech != null) textToSpeech.stop();
            unregisterReceiver(stateReceiver);
            setInitialized(false);
            unregisterOnStatusChangeListener(statusListener);
        }
        return false;
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void initTextToSpeech(Context context) {
        textToSpeech = new TextToSpeech(context, (TextToSpeech.OnInitListener) status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(new Locale("vi", "VN"));
                if (!TextSpeechUtils.checkLanguageAvailable(textToSpeech, Locale.JAPAN)) {
                    TextSpeechUtils.notifySetupTextToSpeech(context, textToSpeech);
                }
            } else {
                TextSpeechUtils.notifySetupTextToSpeech(context, textToSpeech);
            }
        });
    }

    public static boolean isRunning() {
        return isInitialized;
    }

    private void setInitialized(boolean initialized) {
        isInitialized = initialized;
        onStatusChanged();
    }

    public static boolean isSuspended() {
        return isSuspended;
    }

    public static boolean toggleSuspend() {
        isSuspended ^= true;
        onStatusChanged();
        return isSuspended;
    }

    public static boolean toggleSuspend(boolean status) {
        isSuspended = status;
        onStatusChanged();
        return isSuspended;
    }

    private boolean isScreenOn() {
        isScreenOn = CheckScreen.isScreenOn(this);
        return isScreenOn;
    }

    private boolean isHeadsetOn() {
        return (audioMan.isBluetoothA2dpOn() || audioMan.isWiredHeadsetOn());
    }

    public static void registerOnStatusChangeListener(OnStatusChangeListener listener) {
        statusListeners.add(listener);
    }

    public static void unregisterOnStatusChangeListener(OnStatusChangeListener listener) {
        statusListeners.remove(listener);
    }

    public interface OnStatusChangeListener {
        /**
         * Called when the service status has changed.
         *
         * @see Service#isRunning()
         * @see Service#isSuspended()
         */
        void onStatusChanged();
    }

    private static void onStatusChanged() {
        for (OnStatusChangeListener l : statusListeners) {
            l.onStatusChanged();
        }
    }

    private static class CheckScreen {
        private static PowerManager powerMan;

        private static boolean isScreenOn(Context c) {
            if (powerMan == null) {
                powerMan = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
            }
            assert powerMan != null; // Prevent Lint warning. Should never be null, I want a crash report if it is.
            return powerMan.isScreenOn();
        }
    }

    private Set<IgnoreReason> ignore() {
        Set<IgnoreReason> ignoreReasons = new HashSet<>();
        if (isSuspended) {
            ignoreReasons.add(IgnoreReason.SUSPENDED);
        }
        if ((audioMan.getRingerMode() == AudioManager.RINGER_MODE_SILENT
                || audioMan.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)) {
            ignoreReasons.add(IgnoreReason.SILENT);
        }
        if (telephony.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK) {
            ignoreReasons.add(IgnoreReason.CALL);
        }
        if (!isScreenOn()) {
            ignoreReasons.add(IgnoreReason.SCREEN_OFF);
        }
        if (isScreenOn()) {
            ignoreReasons.add(IgnoreReason.SCREEN_ON);
        }
        if (!isHeadsetOn()) {
            ignoreReasons.add(IgnoreReason.HEADSET_OFF);
        }
        if (isHeadsetOn()) {
            ignoreReasons.add(IgnoreReason.HEADSET_ON);
        }
        return ignoreReasons;
    }

    private class DeviceStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean interruptIfIgnored = true;
            assert action != null;
            switch (action) {
                case Intent.ACTION_SCREEN_ON:
                    isScreenOn = true;
                    interruptIfIgnored = false;
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    isScreenOn = false;
                    interruptIfIgnored = false;
                    break;
            }
            if (interruptIfIgnored && textToSpeech != null) {
                Set<IgnoreReason> ignoreReasons = ignore();
                if (!ignoreReasons.isEmpty()) {
                    textToSpeech.stop();
                }
            }
        }
    }
}
