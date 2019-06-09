package com.android_aes.administrator.androidaes;

import android.annotation.SuppressLint;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class Encrypt {
    private static final String AES = "AES";

    private static Logger logger = Logger.getLogger(String.valueOf(Encrypt.class));
    /**
     * AES加密字符串
     *
     * @param content  需要被加密的字符串
     * @param password 加密需要的密码
     * @return 密文
     */
    public String encrypt(String content, String password) {
        try {

            byte[] raw = AesUtils.getRawKey(password.getBytes());
            SecretKeySpec key = new SecretKeySpec(raw, AES);

            @SuppressLint("GetInstance")
            Cipher cipher = Cipher.getInstance ( "AES" );// 创建密码器
            byte[] byteContent = content.getBytes ( StandardCharsets.UTF_8 );
            cipher.init(Cipher.ENCRYPT_MODE, key);//初始化密码器
            byte[] result = cipher.doFinal ( byteContent );

            logger.info ( "加解密结果:" + ParseSystemUtil.parseByte2HexStr ( result ) );
            String Result = ParseSystemUtil.parseByte2HexStr ( result );
            return Result;


        } catch (Exception e) {
            e.printStackTrace ();
        }
        return null;
    }

    /**
     * 加密
     * 使用对称密钥进行加密
     * keyFilePath 密钥存放路径
     * sourseFile 要加密的文件
     * encodeFile 加密后的文件
     */
    public static void fileEncrpty(String password, String sourceFile, String encodeFile) throws Exception{
        //根据给定的字节数组(密钥数组)构造一个AES密钥。
        byte[] raw = com.android_aes.administrator.androidaes.AesUtils.getRawKey(password.getBytes());
        SecretKeySpec key = new SecretKeySpec(raw, AES);

        @SuppressLint("GetInstance")
        Cipher cipher = Cipher.getInstance ( "AES" );// 创建密码器
        cipher.init(Cipher.ENCRYPT_MODE, key);//初始化密码器
        //读取加密的文件流
        FileInputStream fileinputStream = new FileInputStream (sourceFile);
        //输出加密后的文件流
        FileOutputStream fileoutputStream = new FileOutputStream(encodeFile);
        //以加密流写入文件
        CipherInputStream cipherInputStream = new CipherInputStream(fileinputStream, cipher);
        byte[] b = new byte[1024];
//        byte[] content;
//        byte[] result;
        int len;
        //没有读到文件末尾一直读
        while((len = cipherInputStream.read(b)) != -1) {
//            content = fileinputStream.read (  )
//            result = cipher.doFinal ( file );
            fileoutputStream.write(b, 0, len);
            fileoutputStream.flush();
        }
        cipherInputStream.close();
        fileinputStream.close();
        fileoutputStream.close();
    }
}


