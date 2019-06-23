package com.android_aes.administrator.androidaes.Cipher;

public class Pkcs7 {
    public static byte[] pkcs7_pad(byte[] source, int blocksize) {
        int sourceLength = source.length;
        int padDataLen = blocksize - (sourceLength % blocksize);
        int afterPadLen = sourceLength + padDataLen;
        byte[] paddingResult = new byte[afterPadLen];
        System.arraycopy(source, 0, paddingResult, 0, sourceLength);
        for (int i = sourceLength; i < afterPadLen; i++) {
            paddingResult[i] = (byte)padDataLen;
        }
        return paddingResult;
    }

    public static byte[] pkcs7_unpad(byte[] data) {
        int lastValue = data[data.length-1];
        byte[] unpad = new byte[data.length - lastValue];
        System.arraycopy(data, 0, unpad, 0, unpad.length);
        return unpad;
    }
}
