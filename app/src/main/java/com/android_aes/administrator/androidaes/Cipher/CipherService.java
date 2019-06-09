package com.android_aes.administrator.androidaes.Cipher;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface CipherService {
    /**
     * encrypt plaintext with key
     * @param plaintext text to be encrypted
     * @param key key
     * @return encrypted text
     */
    String encrypt(String plaintext, String key,int rounds) throws IOException;

    /**
     * decrypt encrypted text with key
     * @param encryptedText encrypted text
     * @param key key
     * @return origin plaintext
     */
    String decrypt(String encryptedText, String key,int rounds) throws Exception;
}
