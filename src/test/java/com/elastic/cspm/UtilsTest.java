package com.elastic.cspm;

import com.elastic.cspm.utils.AES256;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UtilsTest {

    @Autowired
    private AES256 utilAES256;

    @Test
    void testAES256(){
        // 암호화
        String a = utilAES256.encrypt("1234567891023456");
        System.out.println(a);
        // 복호화
        System.out.println(utilAES256.decrypt(a));
    }
}
