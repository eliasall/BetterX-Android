package com.betterx.android.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.betterx.android.app.Constants;
import com.betterx.android.utils.Compress;
import com.betterx.android.utils.FileEncryption;
import com.betterx.featureslogger.data.FeatureLogger;
import com.betterx.featureslogger.data.UIDGenerator;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class DataBackupService extends IntentService {

    private static final String FILES_TO_UPLOAD_FOLDER_NAME = "filesToUpload";

    private static final int CONNECTION_TIMEOUT = 50 * 1000; // 10 sec
    private static final int MAX_CONNECTIONS = 3;
    private static final int ERROR_RETRY = 3;

    private static final String ACTION_UPLOAD_STATS = "com.betterx.android.services.action.UPLOAD_STATS";

    public DataBackupService() {
        super("DataBackupService");
    }

    /**
     * statick method, which start data uploading
     */
    public static void uploadStats(Context context) {
        final Intent intent = new Intent(context, DataBackupService.class);
        intent.setAction(ACTION_UPLOAD_STATS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_UPLOAD_STATS.equals(intent.getAction())) {
            uploadFileToServer();
        }
    }

    private void uploadFileToServer() {
        final List<File> fileList = getFilesToUpload();
        if (fileList != null && fileList.size() > 0) {
            for(File file : fileList) {
                uploadLogToS3(file, file.getName());
            }
        }
    }

    private List<File> getFilesToUpload() {
        final File filesToUploadParentDir = new File(FeatureLogger.getStatsDir() + "/" + FILES_TO_UPLOAD_FOLDER_NAME);
        final List<File> filesToUpload = new ArrayList<>();
        if(filesToUploadParentDir.exists() && filesToUploadParentDir.isDirectory()) {
            Collections.addAll(filesToUpload, filesToUploadParentDir.listFiles());
        }
        return filesToUpload;
    }

    /**
     * upload file to the amazon s3 server
     *
     */
    private void uploadLogToS3(final File file, String name) {
        try {
            Timber.d("Uploading file " + name + "; real name " + file.getName());
            final AmazonS3 s3 = createClient();
            s3.putObject(new PutObjectRequest(Constants.AWS_BUCKET_KEY, name, file));
            file.delete();
        } catch (AmazonClientException ace) {
            ace.printStackTrace();
        }
    }

    /**
     * create and setup amazon s3 client
     */
    private AmazonS3 createClient() {
        final BasicAWSCredentials credentials = new BasicAWSCredentials(Constants.AWS_ACCESS_KEY, Constants.AWS_SECRET_KEY);
        final AmazonS3 s3 = new AmazonS3Client(credentials, createClientConfiguration());
        s3.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
        return s3;
    }

    /**
     * @return amazon s3 client configuration
     */
    private ClientConfiguration createClientConfiguration() {
        final ClientConfiguration configuration = new ClientConfiguration();
        configuration.setConnectionTimeout(CONNECTION_TIMEOUT);
        configuration.setMaxConnections(MAX_CONNECTIONS);
        configuration.setMaxErrorRetry(ERROR_RETRY);
        return configuration;
    }

}
