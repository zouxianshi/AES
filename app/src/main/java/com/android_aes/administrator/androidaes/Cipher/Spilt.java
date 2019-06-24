package com.android_aes.administrator.androidaes.Cipher;


public class Spilt {
    //将字符串按照一定长度分割
    public static String[] stringSpilt(String s,int len){
        int spiltNum;//len->想要分割获得的子字符串的长度
        int i;
        int cache = len;//存放需要切割的数组长度
        spiltNum = (s.length())/len;
        String[] subs;//创建可分割数量的数组
        if((s.length()%len)>0){
            subs = new String[spiltNum+1];
        }else{
            subs = new String[spiltNum];
        }

//可用一个全局变量保存分割的数组的长度
        int start = 0;
        if(spiltNum>0){
            for(i=0;i<subs.length;i++){
                if(i==0){
                    subs[0] = s.substring(start, len);
                    start = len;
                }else if(i>0 && i<subs.length-1){
                    len = len + cache ;
                    subs[i] = s.substring(start,len);
                    start = len ;
                }else{
                    subs[i] = s.substring(len);
                }
            }
        }
        return subs;
    }
}
