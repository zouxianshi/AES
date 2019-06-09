package com.android_aes.administrator.androidaes;

import android.annotation.SuppressLint;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class Decrypt {
    private static final String AES = "AES";
    private static Logger logger = Logger.getLogger(String.valueOf(Decrypt.class));
    /**
     * 解密AES加密过的字符串
     *
     * @param content
     *            AES加密过过的内容
     * @param password
     *            加密时的密码
     * @return 明文
     */
     public String decrypt(String content, String password) {
        try {

            byte[] raw = com.android_aes.administrator.androidaes.AesUtils.getRawKey(password.getBytes());
            SecretKeySpec key = new SecretKeySpec(raw, AES);

            @SuppressLint("GetInstance")
            Cipher cipher = Cipher.getInstance ( "AES" );// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);//初始化解密密码器
            byte[] bytecontent = ParseSystemUtil.parseHexStr2Byte(content);
            System.out.println ( "需要解密的内容："+bytecontent );
            byte[] result = cipher.doFinal ( bytecontent );

            String message = new String(result, StandardCharsets.UTF_8);
            logger.info("加解密结果:"+ message);
            return message; // 明文


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     * 使用对称密钥进行加密
     * keyFilePath 密钥存放路径
     * encodeFile 要解密的文件
     * decodeFile 解密后的文件
     */
    public static void fileDecrpty(String password, String encodeFile, String decodeFile) throws Exception{
        //根据给定的字节数组(密钥数组)构造一个AES密钥。
        byte[] raw = com.android_aes.administrator.androidaes.AesUtils.getRawKey(password.getBytes());
        SecretKeySpec key = new SecretKeySpec(raw, AES);

        @SuppressLint("GetInstance")
        //创建密码器
        Cipher cipher = Cipher.getInstance ( "AES" );
        //初始化密码器
        cipher.init(Cipher.DECRYPT_MODE, key);
        //读取要加密的文件流
        FileInputStream fileinputStream = new FileInputStream (encodeFile);
        //输出加密后的文件流
        FileOutputStream fileoutputStream = new FileOutputStream(decodeFile);
        //以解密流写入文件
        CipherInputStream cipherInputStreamut = new CipherInputStream(fileinputStream, cipher);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = cipherInputStreamut.read(buffer)) !=-1) {
            fileoutputStream.write(buffer, 0, r);
            fileoutputStream.flush();
        }
        cipherInputStreamut.close ();
        fileoutputStream.close();
        fileinputStream.close();
    }
}
