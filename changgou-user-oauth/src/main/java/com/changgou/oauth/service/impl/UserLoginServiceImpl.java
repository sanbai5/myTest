package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.UserLoginService;
import com.changgou.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Base64;
import java.util.Map;

/**
 * @ClassName UserLoginServiceImpl
 * @Description
 * @Author sanbai5
 * @Date 11:24 2019/10/9
 * @Version 2.1
 **/
@Service
public class UserLoginServiceImpl implements UserLoginService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret, String grant_type) throws UnsupportedEncodingException {
        ServiceInstance serviceInstance = loadBalancerClient.choose("user-auth");
        String uri = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort();
        String url = uri + "/oauth/token";
        MultiValueMap body = new LinkedMultiValueMap();
        body.set("grant_type", grant_type);
        body.set("username", username);
        body.set("password", password);
        MultiValueMap headers = new LinkedMultiValueMap();
        byte[] encode = Base64.getEncoder().encode((clientId + ":" + clientSecret).getBytes());
        String encodeMsg = new String(encode, "UTF-8");
        headers.add("Authorization", "Basic " + encodeMsg);
        HttpEntity httpEntity = new HttpEntity(body, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        Map<String, String> map = responseEntity.getBody();
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(map.get("access_token"));
        authToken.setJti(map.get("jti"));
        authToken.setRefreshToken(map.get("refresh_token"));

        return authToken;
    }
}
