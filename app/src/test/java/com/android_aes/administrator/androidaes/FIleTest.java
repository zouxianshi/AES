package com.android_aes.administrator.androidaes;

import java.util.Scanner;
public class FIleTest {
    static int number=10;
    static int[] t1 = new int[number];
    static int[] t2 = new int[number];
    static void input()
    {
        System.out.println("请输入T1序列：");
        Scanner in_t1 = new Scanner(System.in);//循环输入T1数组
        for(int i=0;i<number;i++){
            t1[i]=in_t1.nextInt();}
        System.out.println("请输入T2序列：");
        Scanner in_t2 = new Scanner(System.in);//循环输入T2数组
        for(int i=0;i<number;i++){
            t2[i]=in_t2.nextInt();}
        System.out.println("T1数组：");
        for(int i=0;i<number;i++){              //输出两个数组
            System.out.print("["+t1[i]+"]"); }
        System.out.println("\nT2数组：");
        for(int i=0;i<number;i++){
            System.out.print("["+t2[i]+"]"); }

    }
    public static void main(String[] args){
        input();
    }
}