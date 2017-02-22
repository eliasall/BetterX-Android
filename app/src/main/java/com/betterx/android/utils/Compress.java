package com.betterx.android.utils;

import android.content.Context;

import com.betterx.featureslogger.data.UIDGenerator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import timber.log.Timber;

public class Compress {

    private static final int BUFFER = 2048;

    private List<File> files; //zip entries
    private String zipFilePath; //zip file path

    public Compress(List<File> files, String zipFile) {
        this.files = files;
        this.zipFilePath = zipFile;
    }

    /**
     * Add files to zip archive. add .json in the end off zip entry name.
     * zipFilePath - path of zip archive.
     */
    public File zip(Context context) {
        try  {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFilePath);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            for(File file : files) {
                final String zipEntryName = getFilename(file.getName(), context);
                Timber.d("Adding: " + zipEntryName);
                FileInputStream fi = new FileInputStream(file);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(zipEntryName);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
            return new File(zipFilePath);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFilename(String filename, Context context ) {
        final String[] nameParts = filename.split("_");
        if(nameParts.length >= 3) {
            return String.format("%s_%s_%s.json", UIDGenerator.getUID(context), nameParts[1], nameParts[2]);
        }
        return filename;
    }

}
