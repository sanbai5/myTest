package com;

import java.util.*;

/**
 * @ClassName MyList
 * @Description
 * @Author sanbai5
 * @Date 15:21 2019/10/30
 * @Version 2.1
 **/
public class MyList {
        String str = new String("good");
        char[] ch = {'a', 'b', 'c'};
        public static void main (String args[]){
            MyList ex = new MyList();
            ex.change(ex.str, ex.ch);
            System.out.print(ex.str +"and");
            System.out.print(ex.ch);
        }
        public void change(String str,char ch[]){
            str = "test ok";
            ch[0] = 'g';
        }
}
