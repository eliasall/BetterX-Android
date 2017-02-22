package com.betterx.android.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.betterx.android.utils.Compress;
import com.betterx.android.utils.FileEncryption;
import com.betterx.featureslogger.data.FeatureLogger;
import com.betterx.featureslogger.data.UIDGenerator;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class ZipAndEncryptService extends IntentService {

    private static final String ACTION_ZIP_AND_ENCRYPT_STATS = "com.betterx.android.services.action.ZIP_AND_ENCRYPT_STATS";
    private static final String FILES_TO_UPLOAD_FOLDER_NAME = "filesToUpload";

    public ZipAndEncryptService() {
        super("ZipAndEncryptService");
    }

    public static void zipAndEncryptStats(Context context) {
        final Intent intent = new Intent(context, ZipAndEncryptService.class);
        intent.setAction(ACTION_ZIP_AND_ENCRYPT_STATS);
        context.startService(intent);
        Timber.v("Start Zipping");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_ZIP_AND_ENCRYPT_STATS.equals(intent.getAction())) {
            final List<File> files = getLogFileList();
            final File zipFile = zipAndEncrypt(files);
            Timber.v("files to zip found " + files.size());
            if (zipFile != null) {
                deleteFiles(files);
            }
        }
    }

    /**
     * zip log files, and encrypt zip archive
     *
     * @return encripted zip file, wich will be uploaded to the server
     */
    private File zipAndEncrypt(List<File> fileList) {
        if (fileList != null && fileList.size() > 0) {
            //prepare zip file path
            createParentFolder();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
            final String zipFileName = UIDGenerator.getUID(this) + "_" + dateFormat.format(getLogFileTime(fileList.get(0))) + ".zip";
            final String zipFilePath = FeatureLogger.getStatsDir() + "/" + zipFileName;
            final Compress compress = new Compress(fileList, zipFilePath);
            //zip log files
            try {
                final File zipped = compress.zip(this);
                if (zipped == null) {
                    return null;
                }

                File encrypted = new File(FeatureLogger.getStatsDir() + "/" + FILES_TO_UPLOAD_FOLDER_NAME + "/" + zipped.getName() + ".enc");
//            File dencripted = new File(FeatureLogger.getStatsDir()+"/decripted_" + zipped.getName());


                //encrypt zip archive
                FileEncryption encryption = new FileEncryption(this);
                encryption.encrypt(this, zipped, encrypted);
//                encryption.decrypt(this, encrypted, dencripted);
                if (encrypted.exists()) {
                    //delete zip archive after encryption
                    zipped.delete();
                    return encrypted;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void createParentFolder() {
        final File file = new File(FeatureLogger.getStatsDir() + "/" + FILES_TO_UPLOAD_FOLDER_NAME);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * @return log files list, which should be zipped and uploaded to the server
     */
    private ArrayList<File> getLogFileList() {
        final ArrayList<File> fileList = new ArrayList<>();

        try {
            final String rootPath = FeatureLogger.getStatsDir();
            final File directory = new File(rootPath);
            if (directory.exists()) {
                File[] files = directory.listFiles();
                if (files != null && files.length > 0) {
                    long nowTime = getCurrentTime().getTimeInMillis();
                    for (File file : files) {
                        final Date fileDate = getLogFileTime(file);
                        //check, that log file date less then today date(we should upload previous day logs)
                        if (fileDate != null && fileDate.getTime() < nowTime) {
                            fileList.add(file);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileList;
    }

    /**
     * @return log file date
     */
    private Date getLogFileTime(File file) {
        String fileName = file.getName().replace(".json", "");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        String fileDateStr = fileName.substring(fileName.lastIndexOf('_') + 1);
        try {
            return dateFormat.parse(fileDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return current date
     */
    private Calendar getCurrentTime() {
        long currentTime = System.currentTimeMillis();
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTimeInMillis(currentTime);
        nowCal.set(Calendar.HOUR, 0);
        nowCal.set(Calendar.HOUR_OF_DAY, 0);
        nowCal.set(Calendar.MINUTE, 0);
        nowCal.set(Calendar.SECOND, 0);
        nowCal.set(Calendar.MILLISECOND, 0);
        return nowCal;
    }

    /**
     * delete files
     */
    private void deleteFiles(List<File> files) {
        for (File file : files) {
            file.delete();
        }
    }

}
