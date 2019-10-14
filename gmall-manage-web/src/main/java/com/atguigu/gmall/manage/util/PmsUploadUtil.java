package com.atguigu.gmall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.IOException;

public class PmsUploadUtil {

    public static String uploadImage(MultipartFile multipartFile) throws IOException, MyException {


        String imgUrl="http://192.168.125.131";

        // 配置fdfs的全局链接地址
        String tracker = PmsUploadUtil.class.getResource("/tracker.conf").getPath();// 获得配置文件的路径

        ClientGlobal.init(tracker);

        TrackerClient trackerClient = new TrackerClient();

        // 获得一个trackerServer的实例
        TrackerServer trackerServer = trackerClient.getConnection();

        // 通过tracker获得一个Storage链接客户端
        StorageClient storageClient = new StorageClient(trackerServer,null);

        //获得上传的二进制对象
        byte[] bytes=multipartFile.getBytes();

        //获取文件后缀
        String or=multipartFile.getOriginalFilename();
        int i=or.lastIndexOf("1");
        String extName=or.substring(i+1);

        String[] uploadInfos = storageClient.upload_file(bytes, extName, null);


        for (String uploadInfo : uploadInfos) {
            imgUrl += "/"+uploadInfo;

            //url = url + uploadInfo;
        }


        return imgUrl;
    }
}
