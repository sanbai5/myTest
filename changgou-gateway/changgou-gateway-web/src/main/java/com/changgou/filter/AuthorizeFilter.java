package com.changgou.filter;

import com.changgou.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @ClassName AuthorizeFilter
 * @Description 配置全局过滤器
 * @Author sanbai5
 * @Date 20:38 2019/9/28
 * @Version 2.1
 **/
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    private static final String AUTHORIZE_TOKEN = "Authorization";
    //登陆页面
    private static final String LOGIN_URL = "http://localhost:9001/oauth/login";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取request，response
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 1、用户登录操作：放行   /api/user/login
        String path = request.getURI().getPath();
        if (URLFilter.hasAuthorize(path)){
            // 用户登录操作，放行
            return chain.filter(exchange);
        }

        // 2、用户访问其他资源：拦截（判断是否有token）
        // 2.1 判断请求参数是否有token   /api/user/login?Authorization=xxxx
        String token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);

        // 2.2 判断cookie是否有token
        if (StringUtils.isEmpty(token)){
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if (cookie != null){
                token = cookie.getValue();
            }
        }

        // 2.3 判断请求头是否有token
        if (StringUtils.isEmpty(token)){
            token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        }

        // 3、判断token是否存在
        if (StringUtils.isEmpty(token)){

            // 既不是登录或者注册的操作，并且访问受保护的资源，需要踢到登录页面。
            String uri = request.getURI().toString();
            String url = LOGIN_URL + "?FROM=" + uri;

            // 没有token，说明没有登录，不能放行
//            response.setStatusCode(HttpStatus.UNAUTHORIZED); // 无效认证
            response.setStatusCode(HttpStatus.SEE_OTHER);   // 重定向到某个资源
            response.getHeaders().add("Location", url);
            return response.setComplete();
        }
        //token存在，判断否正确
        try {
            //Claims claims = JwtUtil.parseJWT(token);
            //request.mutate().header(AUTHORIZE_TOKEN,token);
            request.mutate().header(AUTHORIZE_TOKEN, "bearer " + token);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
