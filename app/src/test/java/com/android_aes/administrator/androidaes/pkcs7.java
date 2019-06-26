package com.android_aes.administrator.androidaes;

import com.android_aes.administrator.androidaes.Cipher.Pkcs7;

public class pkcs7 {
    public static void main(String[] args){
        String  A = "7a6f750d0d0d0d0d0d0d0d0d0d0d0d0d";
        byte[] B = ParseSystemUtil.parseHexStr2Byte ( A );
        System.out.println (  Pkcs7.pkcs7_unpad ( B ) );
    }
}
