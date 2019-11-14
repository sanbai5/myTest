package com;

/**
 * @ClassName maqiyun
 * @Description
 * @Author sanbai5
 * @Date 9:21 2019/10/30
 * @Version 2.1
 **/
public class maqiyun {
    public static void main(String[] args) {
        A a = new A();
        B b = new B();
        C c = new C();
        D d = new D();
        System.out.println(a.get(c));
    }
}

class A {
    String get(D d) {
        return "AD";
    }

    String get(A a) {
        return "AA";
    }

   /* String get(B b) {
        return "CC";
    }*/
}

class B extends A {
    String get(A a) {
        return "BA";
    }

    String get(B b) {
        return "BB";
    }
}

class C extends B {

}

class D extends B {

}

class Test {

}