package com.atguigu.gmall.manage;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageWebApplicationTests {

    public static void main(String[] args)throws IOException, MyException {
        // 配置fdfs的全局链接地址
        String tracker = GmallManageWebApplicationTests.class.getResource("/tracker.conf").getPath();// 获得配置文件的路径

        ClientGlobal.init(tracker);

        TrackerClient trackerClient = new TrackerClient();

        // 获得一个trackerServer的实例
        TrackerServer trackerServer = trackerClient.getConnection();

        // 通过tracker获得一个Storage链接客户端
        StorageClient storageClient = new StorageClient(trackerServer,null);

        String[] uploadInfos = storageClient.upload_file("d:/a.jpg", "jpg", null);

        String url = "http://192.168.125.131";

        for (String uploadInfo : uploadInfos) {
            url += "/"+uploadInfo;

            //url = url + uploadInfo;
        }

        System.out.println(url);

    }
    @Test
    public void contextLoads() throws IOException, MyException {


    }

}
