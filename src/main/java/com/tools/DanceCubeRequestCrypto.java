package com.tools;

import com.google.gson.JsonObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

public class DanceCubeRequestCrypto {
    public static final String KEY = "3339363237333738";
    public static final String IV = "3339363237333738";


    public static String dataBuild(String path,
                                   Map<String, String> params,
                                   String method,
                                   JsonObject data,
                                   String contentType) {
        String paramsStr = convertMapToString(params);

        String result = "{\"path\":\"%s\",\"param\":\"%s\",\"data\":%s,\"method\":\"%s\",\"contentType\":\"%s\"}"
                .formatted(path, paramsStr, method, data.toString(), contentType);
        return result;

    }

    public static String decryptWithDesCbc(String cipherText, byte[] key, byte[] iv) {
        Cipher cipher = null;
        String result = null;
        try {
            cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "DES"), new IvParameterSpec(iv));

            byte[] ciphertext = Base64.getDecoder().decode(cipherText);
            byte[] plaintext = cipher.doFinal(ciphertext);
            result = new String(plaintext, StandardCharsets.UTF_8);
        } catch(NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("No such algorithm or padding", e);
        } catch(InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Invalid key or iv", e);
        } catch(IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Illegal block size or bad padding in doFinal()", e);
        }
        return result;
    }

    public static String cryptoWithDesCbc(String plainText, byte[] key, byte[] iv) {
        Cipher cipher = null;
        String result = null;
        try {
            cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "DES"), new IvParameterSpec(iv));

            byte[] ciphertext = Base64.getDecoder().decode(plainText);
            byte[] plaintext = cipher.doFinal(ciphertext);
            result = new String(plaintext, StandardCharsets.UTF_8);
        } catch(NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("No such algorithm or padding", e);
        } catch(InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Invalid key or iv", e);
        } catch(IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Illegal block size or bad padding in doFinal()", e);
        }
        return result;
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for(int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }

    public static String convertMapToString(Map<String, String> map) {
        StringBuilder result = new StringBuilder();
        for(Map.Entry<String, String> entry : map.entrySet()) {
            if(!result.isEmpty()) {
                result.append("&");
            }
            result.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return result.toString();
    }
}
