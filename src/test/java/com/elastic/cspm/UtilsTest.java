package com.elastic.cspm;

import com.elastic.cspm.utils.AES256;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UtilsTest {

    @Autowired
    private AES256 utilAES256;

    @Test
    void testAES256(){

        String str = "1111";
        // 암호화
        String encrypted = utilAES256.encrypt(str);
        System.out.println("암호화 값 : " + encrypted);

        // 복호화
        String decrypted = utilAES256.decrypt(encrypted);
        System.out.println("복호화 값 : " + decrypted);

        if (str.equals(decrypted)){
            System.out.println("======= True =======");
        } else {
            System.out.println("======= False =======");
        }
    }


    @Value(value = "${DEV_DB_PASSWORD}")
    private String envValue;

    @Test
    void envfile(){
        System.out.println(envValue);
    }
}
