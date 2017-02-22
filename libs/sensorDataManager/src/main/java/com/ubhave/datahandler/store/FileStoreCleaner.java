package com.ubhave.datahandler.store;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;

import android.util.Log;

import com.ubhave.datahandler.config.DataHandlerConfig;
import com.ubhave.datahandler.config.DataStorageConfig;
import com.ubhave.datahandler.config.DataStorageConstants;
import com.ubhave.datahandler.except.DataHandlerException;

public class FileStoreCleaner {
    private static final String TAG = "LogFileDataStorage";

    private final Object fileTransferLock;
    private final DataHandlerConfig config;

    public FileStoreCleaner(final Object fileTransferLock) {
        this.config = DataHandlerConfig.getInstance();
        this.fileTransferLock = fileTransferLock;
    }

    public void moveDirectoryContentsForUpload(String directoryFullPath) throws DataHandlerException, IOException {
        if (DataHandlerConfig.shouldLog()) {
            Log.d(TAG, "moveDirectoryContentsForUpload() " + directoryFullPath);
        }

        File directory = new File(directoryFullPath);
        if (directory != null && directory.exists()) {
            File[] fileList = directory.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    if (isMediaFile(file.getName()) || isLogFileDueForUpload(file)) {
                        if (file.length() <= 0) {
                            file.delete();
                        } else {
                            moveFileToUploadDir(file);
                        }
                    }
                }
            }
        }
    }

    private File getUploadDirectory() throws DataHandlerException {
        String uploadDir = (String) config.get(DataStorageConfig.LOCAL_STORAGE_UPLOAD_DIRECTORY_PATH);
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    private void moveFileToUploadDir(final File file) {
        new Thread() {
            private void removeDirectoryIfEmpty(final File directory) {
                if (directory != null) {
                    File[] fileList = directory.listFiles();
                    if (fileList != null && fileList.length == 0) {
                        boolean deleted = directory.delete();
                        if (DataHandlerConfig.shouldLog()) {
                            Log.d(TAG, "removeDirectoryIfEmpty() " + directory.getAbsolutePath() + " = " + deleted);
                        }
                    }
                }
            }

            public void run() {
                try {
                    if (DataHandlerConfig.shouldLog()) {
                        Log.d(TAG, "gzip file " + file);
                    }
                    final File uploadDirectory = getUploadDirectory();
                    synchronized (fileTransferLock) {
                        try {
                            encryptFile(file);
                            gzipFile(file, uploadDirectory);
                            if (DataHandlerConfig.shouldLog()) {
                                String abs = file.getAbsolutePath();
                                Log.d(TAG, "moved file " + abs + " to server upload dir");
                                Log.d(TAG, "deleting file: " + abs);
                            }
                            File parentDirectory = file.getParentFile();
                            file.delete();

                            removeDirectoryIfEmpty(parentDirectory);
                        } catch (FileNotFoundException e) {
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private boolean isMediaFile(final String fileName) {
        return fileName.contains(DataStorageConstants.ZIP_FILE_SUFFIX) || fileName.contains(DataStorageConstants.AUDIO_FILE_SUFFIX) || fileName.contains(DataStorageConstants.IMAGE_FILE_SUFFIX);
    }

    private long getDurationLimit() {
        try {
            return (Long) config.get(DataStorageConfig.FILE_LIFE_MILLIS);
        } catch (Exception e) {
            e.printStackTrace();
            return DataStorageConfig.DEFAULT_FILE_LIFE_MILLIS;
        }
    }

    private boolean isLogFileDueForUpload(File file) {
        try {
            if (file != null) {
                String fileName = file.getName();
                if (fileName.contains(DataStorageConstants.LOG_FILE_SUFFIX)) {
                    Calendar curCal = Calendar.getInstance();
                    curCal.setTimeInMillis(System.currentTimeMillis());
                    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy", Locale.US);

                    String timeStr = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.indexOf(DataStorageConstants.LOG_FILE_SUFFIX));
                    String curStr = formatter.format(curCal.getTime());

                    if (timeStr.equalsIgnoreCase(curStr) == false) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void encryptFile(File inputFile) {
        //Load Public Key File
        String file = "res/raw/betterxpubkey.asc";
        InputStream keyStream = this.getClass().getClassLoader().getResourceAsStream(file);
        //InputStream keyStream = ctx.getResources().openRawResource(R.raw.betterxpubkey);
        FileOutputStream outfile = null;
        try {
            //Load public key
            PGPPublicKey pubKey = KeyBasedFileProcessorUtil.readPublicKey(keyStream);

            //Output file
            outfile = new FileOutputStream(inputFile.getAbsoluteFile() + "1");

            //Other settings
            boolean armor = false;
            boolean integrityCheck = false;

            KeyBasedFileProcessorUtil.encryptFile(outfile, inputFile.getAbsolutePath(),
                    pubKey, armor, integrityCheck);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (outfile != null) {
            String tempPath = inputFile.getAbsolutePath();
            File newfile = new File(inputFile.getAbsolutePath() + "1");
            if (newfile != null && newfile.exists()) {
                long size = newfile.length();
                if (size > 0) {
                    try {
                        //inputFile.delete();
                        //newfile.renameTo(new File(tempPath));
                    } catch (Exception e) {
                    }
                }
            }
            inputFile = newfile;
        }
    }

    private void gzipFile(final File inputFile, final File uploadDirectory) throws IOException, DataHandlerException {
        FileInputStream in = new FileInputStream(inputFile);
        byte[] buffer = new byte[1024];
        String fileName = inputFile.getName();
        String gzipFileName = fileName.substring(0, fileName.indexOf(".")) + DataStorageConstants.ZIP_FILE_SUFFIX;

        int len;
        File outputFile = new File(uploadDirectory, gzipFileName);
        GZIPOutputStream gzipOS = new GZIPOutputStream(new FileOutputStream(outputFile));
        while ((len = in.read(buffer)) > 0) {
            gzipOS.write(buffer, 0, len);
        }
        in.close();
        gzipOS.finish();
        gzipOS.close();
    }

    private String getIdentifier() throws DataHandlerException {
        String device_id = (String) config.get(DataStorageConfig.UNIQUE_DEVICE_ID);
        if (device_id == null) {
            String user_id = (String) config.get(DataStorageConfig.UNIQUE_USER_ID);
            if (user_id == null) {
                if (DataHandlerConfig.shouldLog()) {
                    Log.d(TAG, "Error: user identifier is: " + user_id + ", device identifier is: " + device_id);
                }
                throw new DataHandlerException(DataHandlerException.CONFIG_CONFLICT);
            }
            return user_id;
        }
        return device_id;
    }
}
