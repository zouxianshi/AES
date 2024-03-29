package com.android_aes.administrator.androidaes;


import com.android_aes.administrator.androidaes.Cipher.AESCipher;

import java.io.File;


public class ExampleUnitTest{

    public static void main(String[] args) throws Exception {
        String content = "zouxianshizouxia1241345135";
        String password = "12345678901";
        String rounds = "10";
        int round = Integer.parseInt(rounds);

        System.out.println("加解密之前：" + content);
        System.out.println("密码：" +password);
        // 加密
        File file1 = new File ( "C://Users//Administrator/Desktop//AES加密流程.txt" );
        String filepath = "C://Users//Administrator/Desktop//AES加密流程.txt";
        if (file1.exists ()){
            file1.delete ();
        }
        AESCipher AESCipher = new AESCipher ();
        String encrypt = AESCipher.encrypt ( content ,password,round,filepath,true,true);
        assert encrypt != null;
        System.out.println("加密后的内容：" + encrypt);
        String decrypt = AESCipher.decrypt ( encrypt,password,round,filepath,true,true );
        System.out.println("解密后的内容：" + decrypt );
    }

//    public static void main(String[] args){
//        String subbyte= "d9c7d56954307ea95d3cc54f7b93bb1e";//d9c7d56954307ea95d3cc54f7b93bb1e//e531b5e4fd088ab78d6d07920322fee9
//        short[][] initialTextState = AESCipher.transfer ( ArrayUtil.byteToShorts ( ParseSystemUtil.parseHexStr2Byte(subbyte) ));
//        short[][]  result = AESCipher.substituteState(initialTextState, AESConstants.INVERSE_SUBSTITUTE_BOX);
//        ArrayUtil.printInfo("Subbyte", AESCipher.getStateHex(result), false);
//
//        String shiftrow = "d930c51e543cbb695d93d5a97bc77e4f";//d930c51e543cbb695d93d5a97bc77e4f//d9c7d56954307ea95d3cc54f7b93bb1e
//        initialTextState = AESCipher.transfer ( ArrayUtil.byteToShorts ( ParseSystemUtil.parseHexStr2Byte(shiftrow) ));
//        result = AESCipher.shiftRows(initialTextState, AESConstants.INVERSE_SHIFTING_TABLE);
//        ArrayUtil.printInfo("shiftrow", AESCipher.getStateHex(result), false);
//
//
//
//        String mixcolumns = "22f35ab93e93bea968ad9fe8952391aa";//22f35ab93e93bea968ad9fe8952391aa//d930c51e543cbb695d93d5a97bc77e4f
//        initialTextState = AESCipher.transfer ( ArrayUtil.byteToShorts ( ParseSystemUtil.parseHexStr2Byte(mixcolumns) ));
//        result = AESCipher.mixColumns (initialTextState, AESConstants.INVERSE_CX);
//        ArrayUtil.printInfo("mixcolumns", AESCipher.getStateHex(result), false);
//
//
//        String addrawkey = "6598c89cdea504440b643d3062832e8c";//22f35ab93e93bea968ad9fe8952391aa
//        String roundkey = "476b9225e036baed63c9a2d8f7a0bf26";//6598c89cdea504440b643d3062832e8c
//        initialTextState = AESCipher.transfer ( ArrayUtil.byteToShorts ( ParseSystemUtil.parseHexStr2Byte(addrawkey) ));
//        short [][] initialroundkey = AESCipher.transfer ( ArrayUtil.byteToShorts ( ParseSystemUtil.parseHexStr2Byte(roundkey) ));
//        result = AESCipher.xor (initialTextState,initialroundkey);//50344e67aca26fc20f7cb29bf05e4a3a
//        ArrayUtil.printInfo("addrawkey", AESCipher.getStateHex(result), false);
//       }
    }