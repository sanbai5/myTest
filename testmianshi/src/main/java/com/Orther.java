package com;

/**
 * @ClassName Orther
 * @Description
 * @Author sanbai5
 * @Date 15:44 2019/11/3
 * @Version 2.1
 **/
public class Orther {
    private static Orther orther = new Orther();

    private Orther() {
    }

    public static Orther getInstance() {
        return orther;
    }
}

class TestOne {
    private static TestOne testOne = null;

    private TestOne() {
    }

    public static TestOne getInstance() {
        if (testOne == null) {
            synchronized (TestOne.class) {
                if (testOne == null) {
                    testOne = new TestOne();
                }
                return testOne;
            }
        }
        return testOne;
    }
}

class TestTwo {
    public static void main(String[] args) {
        TestOne instance = TestOne.getInstance();
        TestOne instance2 = TestOne.getInstance();
        TestOne instance3 = TestOne.getInstance();
        TestOne instance4 = TestOne.getInstance();
        System.out.println(instance);
        System.out.println(instance2);
        System.out.println(instance3);
        System.out.println(instance4);
    }
}