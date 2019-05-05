package com.wdk.netty;

import io.netty.channel.AbstractChannel;

/**
 * @Description
 * @Author wangdk, wangdk@erongdu.com
 * @CreatTime 2019/4/24 15:03
 * @Since version 1.0.0
 */
public class Test{

    private String test1;

    public void method1(){
        String test1 = "wocao";
        this.test1 = test1 = "nimabi";

        System.out.println("test1_____"+test1);
        System.out.println("this.test1______"+this.test1);
    }
    class Test1{
        public void test2(){
            method1();
        }
    }


    //先执行一次do语句块  再判断while循环条件  条件满足继续执行do语句  不满足退出循环.
    public void testDoWhile(){
        int i=0;
        do{
            System.out.println("第"+i+"次执行...");
            i++;
        }while (i<0);
    }

    public static void main(String[] args) {
        new Test().testDoWhile();

        int write = 1<<2;
        // 1<<2 --> 100
        //~1<<2 ==> 0000 0100 --> 1111 1011

        //11111011 -->-1 -->1111 1010 -->反码 0000 0101 ----> -5
        //00000100 & 11111011 ==> 0
        System.out.println(Integer.toBinaryString(write));
        System.out.println(Integer.toBinaryString(~write));

        System.out.println(write & ~write);
    }
}


