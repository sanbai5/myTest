package com.changgou.filter;

import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * @ClassName URLFilter
 * @Description
 * @Author sanbai5
 * @Date 10:15 2019/10/10
 * @Version 2.1
 **/
public class URLFilter {

    private static String uri = "/api/user/add,/api/user/login";   // 无需拦截的url

    public static boolean hasAuthorize(String url){
        String[] uris = uri.split(",");
        for (String uri : uris) {
            if (url.startsWith(uri)){
                return true;    // 访问这些资源无需登录
            }
        }
        return false;
    }
}
