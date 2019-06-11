package com.android_aes.administrator.androidaes.Cipher;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.android_aes.administrator.androidaes.AesUtils;
import com.android_aes.administrator.androidaes.ParseSystemUtil;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;


@SuppressLint("Registered")
public class AESCipher implements CipherService {

    public static void fileEncrpty(String password, String sourceFile, String encodeFile) throws Exception{
        //根据给定的字节数组(密钥数组)构造一个AES密钥。
        byte[] raw = AesUtils.getRawKey(password.getBytes());
        SecretKeySpec key = new SecretKeySpec(raw, AES);

        @SuppressLint("GetInstance")
        Cipher cipher = Cipher.getInstance ( "AES" );// 创建密码器
        cipher.init(Cipher.ENCRYPT_MODE, key);//初始化密码器
        //读取要加密的文件流
        FileInputStream fileinputStream = new FileInputStream (sourceFile);
        //输出加密后的文件流
        FileOutputStream fileoutputStream = new FileOutputStream(encodeFile);
        //以加密流写入文件
        CipherInputStream cipherInputStream = new CipherInputStream(fileinputStream, cipher);
        byte[] b = new byte[1024];
        int len;
        //没有读到文件末尾一直读
        while((len = cipherInputStream.read(b)) != -1) {
            fileoutputStream.write(b, 0, len);
            fileoutputStream.flush();
        }
        cipherInputStream.close();
        fileinputStream.close();
        fileoutputStream.close();
    }

    /**
     * 将最终解密的short数组还原为字符串
     */
    private String getOrigin(short[][] decryptState) {
        StringBuilder builder = new StringBuilder();
        for (short[] shorts : decryptState) {
            for (short s : shorts) {
                builder.append( (char) s );
            }
        }
        return builder.toString();
    }

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

    /**
     * [解密] 将解密扩展密钥数组逆转
     */
    private short[][][] inverseRoundKeys(short[][][] roundKeys) {
        short[][][] result = new short[roundKeys.length][4][4];
        int length = roundKeys.length;
        for (int i = 0; i < roundKeys.length; i++) {
            result[i] = roundKeys[length - 1 - i];
        }
        return result;
    }

    /**
     * 对外的解密接口
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decrypt(String encryptedText, String key,int rounds,String privatepath,boolean PrintFile ,boolean R) throws Exception {
        System.out.println("\n\n#####################  decryption  #####################");
        System.out.println ( encryptedText );
        short[][] initialTextState = transfer ( ArrayUtil.byteToShorts ( ParseSystemUtil.parseHexStr2Byte(encryptedText) ));
        ArrayUtil.printInfo("密文",encryptedText , false);
        ArrayUtil.printInfo("密码", key, false);

        short[][] initialKeyState = transfer(ArrayUtil.transferToShorts(key));

        ArrayUtil.printInfo("初始加密矩阵", getStateHex(initialTextState), false);
        ArrayUtil.printInfo("初始密钥矩阵", getStateHex(initialKeyState), true);

        short[][] decryptState = coreDecrypt(initialTextState, initialKeyState , rounds ,privatepath,PrintFile);
        String plaintext = getOrigin(decryptState);
        ArrayUtil.printInfo("plaintext", plaintext, false);
        return plaintext;
    }

    /**
     * 解密逻辑：将可逆操作抽取
     */
    private short[][] coreDecrypt(short[][] encryptedTextState, short[][] keyState,int rounds,String privatePath,boolean printRawkey) throws IOException {
        int MODE = 1 ;
        short[][] rawRoundKeys = generateRoundKeys(keyState);
        System.out.println("RoundKeys");

        boolean print = true;
        if (print == printRawkey){
            printRoundKeys(rawRoundKeys,privatePath);
        }


        short[][][] roundKeys = transfer(rawRoundKeys);

        for (int i = 1; i < roundKeys.length - 1; i++) {
            roundKeys[i] = mixColumns(roundKeys[i], AESConstants.INVERSE_CX);
        }

        short[][][] inverseRoundKeys = inverseRoundKeys(roundKeys);
        System.out.println("inverse roundKeys");
        printRoundKeys(inverseRoundKeys,privatePath);
        return coreEncrypt(encryptedTextState, inverseRoundKeys, AESConstants.
                INVERSE_SUBSTITUTE_BOX, AESConstants.INVERSE_CX, AESConstants.INVERSE_SHIFTING_TABLE,MODE,rounds,privatePath,printRawkey);
    }

    /**
     * 将加密后得到的状态数组转成字节数组，便于进行Base64编码
     */
    public static byte[] transfer2Bytes(short[][] finalState) {
        byte[] result = new byte[finalState.length * 4];
        for (int i = 0;i < finalState.length; i++) {
            for (int j = 0; j < 4; j++) {
                result[i * 4 + j] = (byte) (finalState[i][j] & 0xff);
            }
        }
        return result;
    }

    /**
     * 列混合变换：状态数组与多项式等价矩阵进行有限域GF(2)上的矩阵乘法
     */
    public static short[][] mixColumns(short[][] state, short[][] table) {
        short[][] result = new short[state.length][4];
        for (int i = 0; i < state.length; i++) {
            result[i] = matrixMultiply(state[i], table);
        }
        return result;
    }

    /**
     * 一个字与多项式等价数组在有限域GF(2)上的乘法操作
     */
    private static short[] matrixMultiply(short[] aWord, short[][] table) {
        short[] result = new short[4];
        for (int i = 0; i < 4; i++) {
            result[i] = wordMultiply(table[i], aWord);
        }
        return result;
    }

    /**
     * 两个字在有限域GF(2)上的乘法操作
     */
    private static short wordMultiply(short[] firstWord, short[] secondWord) {
        short result = 0;
        for (int i=0; i < 4; i++) {
            result ^= multiply(firstWord[i], secondWord[i]);
        }
        return result;
    }

    /**
     * 有限域GF(2)上的乘法操作，通过分解操作数将之转化成有限域GF(2)上的倍乘操作
     */
    private static short multiply(short a, short b) {
        short temp = 0;
        while (b != 0) {
            if ((b & 0x01) == 1) {
                temp ^= a;
            }
            a <<= 1;
            if ((a & 0x100) > 0) {
                a ^= 0x1b;
            }
            b >>= 1;
        }
        return (short) (temp & 0xff);
    }

    /**
     * 轮密钥扩展：将1个状态长度的主密钥扩展成rounds + 1个状态长度的轮密钥数组
     */
    private short[][] generateRoundKeys(short[][] originalKey) {
        short[][] roundKeys = new short[44][4];
        int keyWordCount = originalKey.length;
        System.arraycopy(originalKey, 0, roundKeys, 0, keyWordCount);
        for (int i = keyWordCount; i < keyWordCount * 11; i++) {
            short[] temp = roundKeys[i - 1];
            if (i % keyWordCount == 0) {
                temp = xor(substituteWord(leftShift(temp)), AESConstants.R_CON[i / keyWordCount]);
            }
            roundKeys[i] = xor(roundKeys[i - keyWordCount], temp);
        }
        return roundKeys;
    }

    /**
     * 状态替代：对状态中的每个字进行字替代
     */
    public static short[][] substituteState(short[][] state, short[][] substituteTable) {
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < 4 ; j++) {
                state[i][j] = substituteByte(state[i][j], substituteTable);
            }
        }
        return state;
    }

    /**
     * 字替代：对字中每个字节进行字节替代
     */
    private short[] substituteWord(short[] aWord) {
        for (int i = 0; i < 4; i++) {
            aWord[i] = substituteByte(aWord[i], AESConstants.SUBSTITUTE_BOX);
        }
        return aWord;
    }

    /**
     * 字节替代： 取一个字的高四位和低四位分别作为S盒的行号和列号，
     *          通过行列号取S盒中的字节替代原字节
     */
    private static short substituteByte(short originalByte, short[][] substituteTable) {
        int low4Bits = originalByte & 0x000f;
        int high4Bits = (originalByte >> 4) & 0x000f;
        return substituteTable[high4Bits][low4Bits];
    }

    /**
     * 行移位变换：对状态的行进行循环左移，左移规则在<tt>shiftingTable</tt>中定义
     */
    public static short[][] shiftRows(short[][] state, short[][] shiftingTable) {
        short[][] result = new short[state.length][4];
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < state.length; i++) {
                result[i][j] = state[shiftingTable[i][j]][j];
            }
        }
        return result;
    }

    /**
     * 以字节为单位循环左移1个单位, <tt>LEFT_SHIFT_TABLE</tt> 决定移动的规则
     */
    private short[] leftShift(short[] aWord) {
        short[] result = new short[4];
        for (int i = 0; i < 4; i++) {
            result[i] = aWord[AESConstants.LEFT_SHIFT_TABLE[i]];
        }
        return result;
    }

    /**
     * 对外的加密接口
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String encrypt(String content, String password, int rounds, String privatePath, boolean printEncrypt, boolean printRawkey) throws IOException {
        int MODE = 0;
        System.out.println("#####################  加密  #####################");
        ArrayUtil.printInfo("明文", content, false);
        ArrayUtil.printInfo("密钥", password, false);
        //将明文转换成short类型
        short[][] text = transfer( ArrayUtil.transferToShorts(content));
        ArrayUtil.printInfo("初始16进制明文", getStateHex(text), false);
        short[][] initialpassword = transfer( ArrayUtil.transferToShorts(password));
        ArrayUtil.printInfo("初始16进制密钥", getStateHex(initialpassword), true);

        short[][] rawRoundKeys = generateRoundKeys(initialpassword);
        System.out.println("RoundKeys");
        File file = new File(privatePath+"/"+"加密过程.txt");
        if (file.exists ()) {
            file.delete ();
        }
        File file1 = new File(privatePath+"/"+"AES密钥扩展.txt");
        if (file1.exists ()) {
            file1.delete ();
        }

        boolean print = true;
        if (print == printRawkey){
            printRoundKeys(rawRoundKeys,privatePath);
        }

        short[][][] roundKeys = transfer(rawRoundKeys);

        short[][] finalState = coreEncrypt(text, roundKeys, AESConstants.SUBSTITUTE_BOX,
                AESConstants.CX, AESConstants.SHIFTING_TABLE,MODE,rounds,privatePath,printEncrypt);
        System.out.println ("abc"+transfer2Bytes(finalState) );
        return Base64Util.encode(transfer2Bytes(finalState));
}

    /**
     * 将<tt>1 x modeSize</tt>矩阵转成<tt>Nb x 4</tt>状态矩阵，通常用在获取明文和密钥的初始状态数组
     */
    public static short[][] transfer(short[] origin) {
        short[][] result = new short[origin.length / 4][4];
        for (int i = 0; i < result.length; i++) {
            System.arraycopy(origin, i * 4, result[i], 0, 4);
        }
        return result;
    }

    /**
     * 有限域GF(2)上的加法，<tt>modeSize</tt>位的异或操作
     */
    public static short[][] xor(short[][] first, short[][] second) {
        short[][] result = new short[first.length][4];
        int length = first.length;
        for (short i = 0; i < length; i++) {
            for (short j = 0; j < length; j++) {
                result[i][j] = (short) (first[i][j] ^ second[i][j]);
            }
        }
        return result;
    }

    /**
     * 两个字间的异或操作
     */
    private short[] xor(short[] first, short[] second) {
        short[] result = new short[4];
        for (short i = 0; i < 4; i++) {
            result[i] = (short) (first[i] ^ second[i]);
        }
        return result;
    }

    /**
     * 状态数组的十六进制串
     */
    public static String getStateHex(short[][] state) {
        StringBuilder builder = new StringBuilder();
        for (short[] aWord : state) {
            builder.append(toHexString(aWord));
        }
        return builder.toString();
    }

    /**
     * AES核心操作，通过将可逆操作抽取成可逆矩阵作为参数，使该方法能在加/解密操作中复用
     * @param initialPTState    明文或密文的状态数组
     * @param roundKeys     加/解密要用到的轮密钥数组
     * @param substituteTable   加/解密要用到的S盒
     * @param mixColumnTable    列混合中用来取代既约多项式的数组
     * @param shiftingTable    行变换中用来决定字间左移的位数的数组
     * @param MODE 判断加密/解密模式
     * @param P
     * @return 加/解密结果
     */

    private short[][] coreEncrypt(short[][] initialPTState,
                                  short[][][] roundKeys, short[][] substituteTable,
                                  short[][] mixColumnTable, short[][] shiftingTable, int MODE, int rounds, String privatePath, boolean P) throws IOException {

        File fileexist = new File ( privatePath+"/"+"加密过程.txt" );
        FileWriter file = new FileWriter (privatePath+"/"+"加密过程.txt",true);
//        FileWriter file = new FileWriter ("C://Users//Administrator/Desktop//AES加密流程.txt",true);
        String M = "";
        switch (MODE){
            case 0:
                file.write (  "------------------加密过程------------------"+ "\r\n\r\n");
                M="";
                break;
            case 1:
                file.write (  "------------------解密过程------------------"+ "\r\n\r\n");
                M = "Inv";
                break;
        }


        // 初始轮密钥加，异或操作
        short[][] state = xor(roundKeys[0], initialPTState);
        file.write (  "AddRoundKeys:"+getStateHex(state) + "\r\n\r\n");
        // 处理前九轮变换
        for (int i = 0; i < rounds - 1; i++) {
                int N = i + 1;
                file.write ( "第"+N+"轮\r\n");
                System.out.println("N = " + N);
                // 字节替代
                state = substituteState(state, substituteTable);
                ArrayUtil.printInfo("SubBytes", getStateHex(state), false);
                file.write (  M + "SubBytes:"+getStateHex(state) + "\r\n");
                // 行移位变换
                state = shiftRows(state, shiftingTable);
                ArrayUtil.printInfo("ShiftRows", getStateHex(state), false);
                file.write (  M + "ShiftRows:"+getStateHex(state) + "\r\n");
                // 列混合变换
                state = mixColumns(state, mixColumnTable);
                ArrayUtil.printInfo("MixColumns", getStateHex(state), false);
                file.write (  M + "MixColumns:"+getStateHex(state) + "\r\n");
                // 轮密钥加变换
                ArrayUtil.printInfo("RoundKey", getStateHex(roundKeys[i + 1]), false);
                switch (MODE){
                    case 0:
                        state = xor(roundKeys[ i + 1], state);
                        break;
                    case 1:
                        state = xor(roundKeys[ i + 11 - rounds ], state);
                }
                ArrayUtil.printInfo("AddRoundKeys", getStateHex(state), true);
                file.write (  M + "AddRoundKeys:"+getStateHex(state) + "\r\n\r\n");
            }

            // 处理最后一轮
            file.write ( "最终轮\r\n" );
            System.out.println("N = last");
            state = substituteState(state, substituteTable);
            ArrayUtil.printInfo("SubBytes", getStateHex(state), false);
            file.write (  M+"SubBytes:"+getStateHex(state) +"\r\n");

            state = shiftRows(state, shiftingTable);
            ArrayUtil.printInfo("ShiftRows", getStateHex(state), false);
            file.write (  M+"ShiftRows:"+getStateHex(state) + "\r\n");


            ArrayUtil.printInfo("RoundKey", getStateHex(roundKeys[roundKeys.length - 1]), false);
            state = xor(roundKeys[roundKeys.length - 1], state);
            ArrayUtil.printInfo("AddRoundKeys", getStateHex(state), false);
            file.write (  M+"AddRoundKeys:"+getStateHex(state) + "\r\n\r\n");
            file.close ();
            boolean print = false;
            if(print == P){
                    fileexist.delete ();
            }
        return state;
    }

    /**
     * 将二维数组转成三维数组，方便在轮变换中通过轮下标获取 Nk x 4 的轮密钥矩阵
     */
    private short[][][] transfer(short[][] origin) {
        short[][][] result = new short[origin.length / 4][4][4];
        for (int i = 0; i < origin.length / 4; i++) {
            short[][] temp = new short[4][4];
            System.arraycopy(origin, i * 4, temp, 0, 4);
            result[i] = temp;
        }
        return result;
    }

    /**
     * 将一个字以字节为单位转换成十六进制形式字符串
     */
    private static String toHexString(short[] aWord) {
        StringBuilder builder = new StringBuilder();
        for (short aByte : aWord) {
            builder.append(toHexString(aByte));
        }
        return builder.toString();
    }

    /**
     * 获取一个字节的十六进制串
     */
    private static String toHexString(short value) {
        String hexString = Integer.toHexString(value);
        if (hexString.toCharArray().length == 1) {
            hexString = "0" + hexString;
        }
        return hexString;
    }

    private static final String AES = "AES";

    /**
     * 打印轮密钥数组
     */
    private void printRoundKeys(short[][] roundKeys,String privatePath) throws IOException {
        FileWriter fileWriter = new FileWriter (privatePath+"/"+"AES密钥扩展.txt",true);
//        FileWriter fileWriter = new FileWriter ("C://Users//Administrator/Desktop//AES密钥扩展.txt",true);
        for (int i = 0, keyOrder = 1; i < roundKeys.length; i += 4, keyOrder++) {
            String infoKValue = getStateHex(new short[][]{
                    roundKeys[i], roundKeys[i + 1],
                    roundKeys[i + 2], roundKeys[i + 3]
            });
            ArrayUtil.printInfo("[RoundKey " + keyOrder + "]", infoKValue , false);
            fileWriter.write ( "[RoundKey " + keyOrder + "]" + infoKValue+ "\r\n" );
        }
        System.out.println();
        fileWriter.close ();
    }

    /**
     * 打印三维轮密钥数组
     */
    private void printRoundKeys(short[][][] roundKeys,String privatePath) throws IOException {
        FileWriter fileWriter = new FileWriter (privatePath+"/"+"AES密钥扩展.txt",true);
//        FileWriter fileWriter = new FileWriter ( "C://Users//Administrator/Desktop//AES密钥扩展.txt",true);

        for (int i = 0; i < roundKeys.length; i++) {
            String infoKValue = getStateHex(roundKeys[i]);
            ArrayUtil.printInfo("[RoundKey " + (i + 1) + "]", infoKValue, false);
            fileWriter.write ( "[RoundKey " + infoKValue + "]"+infoKValue+"\r\n");
        }
        System.out.println();
        fileWriter.close ();

    }
}
