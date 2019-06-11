package com.android_aes.administrator.androidaes.Cipher;

import java.io.IOException;

public interface CipherService {
    /**
     * encrypt plaintext with key
     * @param content text to be encrypted
     * @param password key
     * @param privatePath
     * @param PrintFile
     * @param printRawkey
     * @return encrypted text
     */
    String encrypt(String content, String password, int rounds, String privatePath, boolean PrintFile, boolean printRawkey) throws IOException;

    /**
     * decrypt encrypted text with key
     * @param encryptedText encrypted text
     * @param key key
     * @return origin plaintext
     */
    String decrypt(String encryptedText, String key,int rounds,String privatepath,boolean PrintFile,boolean printRawkey) throws Exception;
}
