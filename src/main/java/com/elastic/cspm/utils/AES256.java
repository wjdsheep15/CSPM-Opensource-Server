package com.elastic.cspm.utils;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AES256 {

    private final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private final String aesEncryptionKey = "ASAC!_KYOBO_DTS_CSPM_ENCRYPT_KEY";
    private byte[] initVector;

    public void init() {
        // AES 암호화를 위해 IV는 항상 16바이트로 설정
        initVector = aesEncryptionKey.substring(0, 16).getBytes();
    }

    /**
     * 암호화 키가 유효한지 검사
     * 유효 크기 : 128비트(16바이트), 192비트(24바이트), 256비트(32바이트)
     */
    private boolean isValidKeySize(String key) {
        if (key == null) {
            return false;
        }

        int length = key.length();
        return length == 16 || length == 24 || length == 32;
    }

    /**
     * task : 입력 받은 'size' 크기만큼의 암호화 난수 생성
     */
    private String generateRandomOfSize(int size) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomArr = new byte[size];
        secureRandom.nextBytes(randomArr);
        String ets = Base64.getEncoder().encodeToString(randomArr);
        return ets.length() <= size ? ets : ets.substring(0, size);
    }

    /**
     * task : 암호화
     */
    public String encrypt(String value) {
        try {
            /*
             * IvParameterSpec : 암호화 알고리즘에서 초기화 벡터(IV)로 사용
             * SecretKeySpec   : 암호화 및 복호화에 사용되는 비밀키
             * Cipher : 실제 암호화 및 복호화 작업을 수행
             */
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec secretKeySpec = new SecretKeySpec(aesEncryptionKey.getBytes("UTF-8"), "AES");

            // 암호화 수행
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * task : 복호화
     */
    public String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec secretKeySpec = new SecretKeySpec(aesEncryptionKey.getBytes("UTF-8"), "AES");

            // 복호화 수행
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
