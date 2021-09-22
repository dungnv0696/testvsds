package com.lifesup.gbtd.util;

import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Component
public class Crypto {

    private static Crypto instance;
    private static SecretKeySpec secretKey;

    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;

    public static Crypto getInstance() {
        if (null == instance) {
            instance = new Crypto();
        }
        return instance;
    }

    private Crypto() {
        secretKey = setKey();
    }

    private static SecretKeySpec setKey() {
        byte[] key;
        MessageDigest sha;
        try {
            key = Const.AES_KEY.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            return new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public String encrypt(String strToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            //Cipher cipher = Cipher.getInstance("AES/ECB");
            byte[] IV = new byte[GCM_IV_LENGTH];
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public String decrypt(String strToDecrypt) {
        if (strToDecrypt == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] IV = new byte[GCM_IV_LENGTH];
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            return null;
        }
    }
}
