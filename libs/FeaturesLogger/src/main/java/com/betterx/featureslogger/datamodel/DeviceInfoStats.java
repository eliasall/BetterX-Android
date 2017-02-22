package com.betterx.featureslogger.datamodel;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.betterx.featureslogger.data.UIDGenerator;

import java.io.Serializable;
import java.lang.reflect.Method;

public class DeviceInfoStats implements Serializable {

    public long timestamp;
    public String uid;
    public String manufacturer;
    public String model;
    public String version;
    public ScreenSize screensize;


    @Override
    public String toString() {
        return "DeviceInfoStats{" +
                "timestamp=" + timestamp +
                ", uid='" + uid + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", version='" + version + '\'' +
                ", screensize='" + screensize + '\'' +
                '}';
    }

    /**
     * create device info stats object, which contains information about device
     */
    public static DeviceInfoStats generate(Context context, String version) {
        final DeviceInfoStats stats = new DeviceInfoStats();
        stats.timestamp = System.currentTimeMillis();
        stats.uid = UIDGenerator.getUID(context);
        stats.manufacturer = Build.MANUFACTURER;
        stats.model = Build.MODEL;
        stats.version = version;
        stats.screensize = getScreenSize(context);
        return stats;
    }

    private static ScreenSize getScreenSize(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= 17){
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            return new ScreenSize(realMetrics.heightPixels, realMetrics.widthPixels);
        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                return new ScreenSize((Integer) mGetRawH.invoke(display), (Integer) mGetRawW.invoke(display));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //this may not be 100% accurate, but it's all we've got
        return new ScreenSize(display.getHeight(), display.getWidth());

    }

}
