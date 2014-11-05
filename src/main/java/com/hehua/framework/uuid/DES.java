package com.hehua.framework.uuid;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 基于base64的des加密解密
 * 
 * @author Chenlei <chenlei@diandian.com>
 * @createDate 2013-11-18 下午8:29:32
 */
public class DES {

    private Cipher encrpytCipher;

    private Cipher decrpytCipher;

    private final static long LONG_BYTES = 8;

    public DES(byte[] secretStr) {
        try {
            DESKeySpec desKeySpec = new DESKeySpec(secretStr);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Key secretKey = keyFactory.generateSecret(desKeySpec);
            encrpytCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            encrpytCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            decrpytCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            decrpytCipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
                | NoSuchPaddingException e) {
            throw new RuntimeException("cannot generate secret key.", e);
        }
    }

    public String encodeBase64URLSafeStringLong(long value) {
        byte[] rawData = ByteBuffer.allocate(8).putLong(value).array();
        return encodeBase64URLSafeString(rawData);
    }

    public long decodeBase64Long(String value) {
        byte[] bytes = decodeBase64(value);
        if (bytes == null || bytes.length != LONG_BYTES) {
            throw new RuntimeException("fail to decode: " + value);
        }
        return ByteBuffer.wrap(bytes).getLong();
    }

    public String encodeBase64URLSafeString(String value) {
        byte[] rawData = value.getBytes();
        return encodeBase64URLSafeString(rawData);
    }

    public String decodeBase64String(String value) {
        byte[] bytes = decodeBase64(value);
        //        if (bytes == null || bytes.length != LONG_BYTES) {
        //            throw new RuntimeException("fail to decode: " + value);
        //        }
        return new String(bytes);
    }

    public String encodeBase64URLSafeString(byte[] rawData) {
        byte[] encrypted;
        synchronized (encrpytCipher) {
            try {
                encrypted = encrpytCipher.doFinal(rawData);
                return Base64.encodeBase64URLSafeString(encrypted);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException("fail to encrypt:" + Arrays.toString(rawData), e);
            }
        }
    }

    public byte[] decodeBase64(String value) {
        byte[] encrypted = Base64.decodeBase64(value);
        synchronized (decrpytCipher) {
            try {
                return decrpytCipher.doFinal(encrypted);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException("fail to decode:" + value, e);
            }
        }
    }
}
