package com.changgou.token;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName CreateJwt66Test
 * @Description
 * @Author 传智播客
 * @Date 18:40 2019/8/21
 * @Version 2.1
 **/
public class CreateJwt66Test {

    /***
     * 创建令牌测试
     */
    @Test
    public void testCreateToken(){
        //证书文件路径
        String key_location="changgou71.jks";
        //秘钥库密码
        String key_password="changgou71";
        //秘钥密码
        String keypwd = "changgou71";
        //秘钥别名
        String alias = "changgou71";

        //访问证书路径
        ClassPathResource resource = new ClassPathResource(key_location);

        //创建秘钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource,key_password.toCharArray());

        //读取秘钥对(公钥、私钥)
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias,keypwd.toCharArray());

        //获取私钥
        RSAPrivateKey rsaPrivate = (RSAPrivateKey) keyPair.getPrivate();

        //定义Payload
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "1");
        tokenMap.put("name", "itheima");
        tokenMap.put("roles", "ROLE_VIP,ROLE_USER");

        //生成Jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(rsaPrivate));

        //取出令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }


    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.iz1fiNlnObSLXH1oLPRfebrViYVAr8R7sIVk9oYZgqJnj8lup_gGfN2VbtLHoiu30SPq8mQ-C7Ay5z1utL29GJIo6ooe8HzsCj0XoZU-rfOrwgjbqIIgwXedEtlPCJUenjegIj6wKCSF84wolMJaQddmp6sUSyOmkvTqECj5dJEApG8AxJT4R7jOsNZlttvaooQxdkVp98wLZ_bdmxn2MbRFZ5xXP8882rbtFWNdh3YUsR7Q3j3h3aKLLuMIPZ2nGnxwirRy5aOL63bsGLhcGkXPDJUCQSEDYxs4kKw8n8rT8SQlbMzubB2T-SEbVD091YI3AyEvTymBhYMYVwRWtw";
        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyuy+hCykjbd1c11BMjkxg/zZivxJ9vBB8VFra0MF9hhi2VwSysTkFPTa5/VpeVnVaIezqolMTT4j8wiMt8pX6gzzzeM8rReh6gVEQktiYzwJ8hmYAWjamX1id8vRjY95JPwfHzVq4c6JWFdJSNMRNo5dy3LIcqAyNGRat9qUbUQ3d/g3fdQdI8nOE52DQTl4MkwhtfbbyDCSDNxHmrL+PFR8bwWYzFWSeJu45Ktnfp8I/gdd0xnUbOPmloCfef6DMsze4AYeVYCgnBY3T7RUBKcEX7T4DzFq9s1sp3CTxzf1JaMdb/Ef/4EvrXBtrmqmcHfSpAuG7lxmMCIKj053awIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }

}
