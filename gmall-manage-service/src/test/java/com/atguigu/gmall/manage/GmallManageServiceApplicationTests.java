package com.atguigu.gmall.manage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageServiceApplicationTests {

    public static void main(String[] args) {
        List<String> list = Collections.singletonList( "data" );
        for (String s : list) {
            System.out.println(s);
        }
    }

    @Test
    public void contextLoads() {

    }

}
