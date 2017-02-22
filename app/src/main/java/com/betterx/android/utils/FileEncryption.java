package com.betterx.android.utils;

import android.content.Context;

import com.amazonaws.util.Base64;
import com.betterx.featureslogger.data.FeatureLogger;

import org.apache.commons.codec.DecoderException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;

import static org.apache.commons.codec.binary.Hex.decodeHex;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.apache.commons.io.FileUtils.writeStringToFile;

public class FileEncryption {

    public static final int AES_Key_Size = 256;
    public static final String ALGO = "AES/CBC/PKCS5Padding";


    Cipher aesCipher;
    SecretKeySpec aeskeySpec;

    /**
     * Constructor: creates ciphers
     */
    public FileEncryption(Context context) throws GeneralSecurityException {
        // create AES shared key cipher
        aesCipher = Cipher.getInstance(ALGO);
        prepareKey(context);
//        makeKey(); //lock previous line and unlock this line, to save and load key from file
    }

    /**
     * Creates a new AES key
     */
    public void makeKey() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance(ALGO);
        kgen.init(AES_Key_Size);
        SecretKey key = loadKey();
        if (key == null) {
            key = kgen.generateKey();
            saveKey(key);
        }
        aeskeySpec = new SecretKeySpec(key.getEncoded(), ALGO);
    }

    /**
     * Encrypts and then copies the contents of a given file.
     */
    public void encrypt(Context context, File in, File out) throws IOException, InvalidKeyException {
        try {
            final IvParameterSpec ivSpec = getIVSpec(context);
            aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec, ivSpec);
            FileInputStream is = new FileInputStream(in);
            CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), aesCipher);
            copy(is, os);
            os.close();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Decrypts and then copies the contents of a given file.
     */
    public void decrypt(Context context, File in, File out) throws IOException, InvalidKeyException {
        try {
            final IvParameterSpec ivSpec = getIVSpec(context);
            aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec, ivSpec);
            CipherInputStream is = new CipherInputStream(new FileInputStream(in), aesCipher);
            FileOutputStream os = new FileOutputStream(out);
            copy(is, os);
            is.close();
            os.close();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies a stream.
     */
    private void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] b = new byte[1024];
        while ((i = is.read(b)) != -1) {
            os.write(b, 0, i);
        }
    }

    /**
     * save key to file
     */
    public static void saveKey(SecretKey key) {
        try {
            final File file = new File(FeatureLogger.getStatsDir() + "/" + "ase_code");
            final byte[] encoded = key.getEncoded();
            final String data = Base64.encodeAsString(encoded);
            writeStringToFile(file, data);
            Timber.d("Kode: " + file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load key from file
     */
    public static SecretKey loadKey() {
        try {
            final File file = new File(FeatureLogger.getStatsDir() + "/" + "ase_code");
            if (file.exists()) {
                final String data = new String(readFileToByteArray(file));
                Timber.d("Kode: " + data);
                final byte[] encoded = Base64.decode(data);
                return new SecretKeySpec(encoded, ALGO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * prepare secret key, using key file from assets
     */
    private void prepareKey(Context context) {
            final String data = loadStringFromAssets(context, "pass");
            final byte[] encoded = Base64.decode(data);
            SecretKey key = new SecretKeySpec(encoded, ALGO);
            aeskeySpec = new SecretKeySpec(key.getEncoded(), ALGO);
    }

    private IvParameterSpec getIVSpec(Context context) {
        try {
            final String ivStr = loadStringFromAssets(context, "init_vector");
            final byte[] encoded = decodeHex(ivStr.toCharArray());
            return new IvParameterSpec(encoded);
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Load string from assets file
     */
    private String loadStringFromAssets(Context context, String filename) {
        StringBuilder buf = new StringBuilder();
        try {
            InputStream pass = context.getAssets().open(filename);
            BufferedReader in = new BufferedReader(new InputStreamReader(pass, "UTF-8"));
            String data;

            while ((data = in.readLine()) != null) {
                buf.append(data);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

//    public void saveBytesToFile(byte[] encoded, String fileName) throws IOException{
//
//        final File file = new File(fileName);
//        final char[] hex = encodeHex(encoded);
//        final String data = String.valueOf(hex);
//        writeStringToFile(file, data);
//        Timber.v("File " + file.getName() + " = " + data);
//    }
//
//    private String getFilePath(String filename) {
//        return FeatureLogger.getStatsDir() + "/" + filename;
//    }

}
