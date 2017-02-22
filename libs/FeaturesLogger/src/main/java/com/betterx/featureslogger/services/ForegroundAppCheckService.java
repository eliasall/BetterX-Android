package com.betterx.featureslogger.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.betterx.featureslogger.data.FeatureLogger;
import com.betterx.featureslogger.datamodel.DeviceInfoStats;
import com.betterx.featureslogger.datamodel.ForegroundAppStats;
import com.jaredrummler.android.processes.ProcessManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ForegroundAppCheckService extends Service {

    public static final int AID_APP = 10000;
    public static final int AID_USER = 100000;

    private static final int UPDATE_TIME = 1000;


    private Timer timer;
    private String lastForegroundApp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startChecking();
        return START_STICKY;
    }

    private void startChecking() {
        cancelTimer();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    final String uglyForegroundAppPkg = getForegroundApp();
                    for (ActivityManager.RunningAppProcessInfo info : ProcessManager
                            .getRunningAppProcessInfo(getBaseContext())) {
                        if (info != null && uglyForegroundAppPkg!= null && uglyForegroundAppPkg.contains(info.processName)) {
                            if (lastForegroundApp != null && lastForegroundApp
                                    .equalsIgnoreCase(info.processName)) {
                                return;
                            }

                            final ForegroundAppStats stats = ForegroundAppStats
                                    .generate(getApplicationContext(), info.processName);
                            lastForegroundApp = info.processName;
                            SaveStatsService.saveForegroundAppStats(ForegroundAppCheckService.this, stats);
                            checkIsCurrentAppUpdated(info.processName);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 0, UPDATE_TIME);
    }

    private void cancelTimer() {
        if(timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    public static String getForegroundApp() {
        File[] files = new File("/proc").listFiles();
        int lowestOomScore = Integer.MAX_VALUE;
        String foregroundProcess = "";

        for (File file : files) {
            if (!file.isDirectory()) {
                continue;
            }

            int pid;
            try {
                pid = Integer.parseInt(file.getName());
            } catch (NumberFormatException e) {
                continue;
            }

            try {
                String cgroup = read(String.format("/proc/%d/cgroup", pid));

                String[] lines = cgroup.split("\n");

                if (lines.length != 2) {
                    continue;
                }

                String cpuSubsystem = lines[0];
                String cpuaccctSubsystem = lines[1];

                if (!cpuaccctSubsystem.endsWith(Integer.toString(pid))) {
                    // not an application process
                    continue;
                }

                if (cpuSubsystem.endsWith("bg_non_interactive")) {
                    // background policy
                    continue;
                }

                String cmdline = read(String.format("/proc/%d/cmdline", pid));

                if (cmdline.contains("com.android.systemui")) {
                    continue;
                }

                int uid = Integer.parseInt(
                        cpuaccctSubsystem.split(":")[2].split("/")[1].replace("uid_", ""));
                if (uid >= 1000 && uid <= 1038) {
                    // system process
                    continue;
                }

                int appId = uid - AID_APP;
                int userId = 0;
                // loop until we get the correct user id.
                // 100000 is the offset for each user.
                while (appId > AID_USER) {
                    appId -= AID_USER;
                    userId++;
                }

                if (appId < 0) {
                    continue;
                }

                // u{user_id}_a{app_id} is used on API 17+ for multiple user account support.
                // String uidName = String.format("u%d_a%d", userId, appId);

                File oomScoreAdj = new File(String.format("/proc/%d/oom_score_adj", pid));
                if (oomScoreAdj.canRead()) {
                    int oomAdj = Integer.parseInt(read(oomScoreAdj.getAbsolutePath()));
                    if (oomAdj != 0) {
                        continue;
                    }
                }


                int oomscore = Integer.parseInt(read(String.format("/proc/%d/oom_score", pid)));
                if (oomscore < lowestOomScore) {
                    lowestOomScore = oomscore;
                    foregroundProcess = cmdline;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return foregroundProcess;
    }

    private static String read(String path) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        output.append(reader.readLine());
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            output.append('\n').append(line);
        }
        reader.close();
        return output.toString();
    }

    private void checkIsCurrentAppUpdated(String foregroundAppPackage) {
        final String packageName = getApplicationContext().getPackageName();
        if(packageName.equalsIgnoreCase(foregroundAppPackage)) {
            try {
                final PackageInfo pInfo = getPackageManager().getPackageInfo(packageName, 0);
                final String lastSavedAppVersion =
                        FeatureLogger.getLastDetectedAppVersion(getApplicationContext());
                final String currentAppVersion = pInfo.versionName;
                if(lastSavedAppVersion == null ||
                        !lastSavedAppVersion.equalsIgnoreCase(currentAppVersion)) {
                    final DeviceInfoStats stats = DeviceInfoStats
                            .generate(getApplicationContext(), currentAppVersion);
                    FeatureLogger.saveAppVersion(getApplicationContext(), currentAppVersion);
                    SaveStatsService.saveDeviceStats(this, stats);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
