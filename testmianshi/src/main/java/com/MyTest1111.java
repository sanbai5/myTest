package com;

/**
 * @ClassName MyTest1111
 * @Description
 * @Author sanbai5
 * @Date 15:53 2019/10/30
 * @Version 2.1
 **/
public class MyTest1111 {
    void test(int i) {
        System.out.println("I am an int.");
    }

    void test(String s) {
        System.out.println("I am a string.");
    }

    public static void main(String[] args) {
        String a = "My field1";
        String b = "My field1";
        String c = new String("My field1");
        String d = new String("My field1");
        System.out.println("a==b:"+(a==b));
        System.out.println("a==c:"+(a==c));
        System.out.println("c==d:"+(c==d));
        System.out.println("a.equals(b):"+a.equals(b));
        System.out.println("a.equals(c):"+a.equals(c));
        System.out.println("c.equals(d):"+c.equals(d));
    }
}
