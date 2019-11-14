package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: shenkunlin
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

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
