package com.factory;

/**
 * @ClassName CarFactory
 * @Description
 * @Author sanbai5
 * @Date 16:13 2019/11/3
 * @Version 2.1
 **/
public class CarFactory {
    public static void getCar(String car) {
        HongQi hongQi = new HongQi();
        BenTian benTian = new BenTian();
        if ("红旗".equals(car)) {
            hongQi.car();
        } else {
            benTian.car();
        }
    }
}
