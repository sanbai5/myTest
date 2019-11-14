package com.changgou.util;


import com.changgou.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/*
 * 封装FastDFS的api工具类
 * */
public class FastDFSClient {
    /*
     *初始化FastDFS配置
     * */
    static {
        String path = "fdfs_client.conf";
        ClassPathResource classPathResource = new ClassPathResource(path);
        String conf_file = classPathResource.getPath();
        try {
            ClientGlobal.init(conf_file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /*
     *文件上传
     * */
    public static String[] uploadFile(FastDFSFile fastDFSFile) {
        try {
            //获取文件相关属性
            byte[] file_buff = fastDFSFile.getContent();//文件内容
            String file_ext_name = fastDFSFile.getExt();//文件扩展名
            NameValuePair[] meta_list = new NameValuePair[1];
            meta_list[0] = new NameValuePair(fastDFSFile.getAuthor());
            //创建跟踪器客户端
            TrackerClient trackerClient = new TrackerClient();
            //获取跟踪服务器
            TrackerServer connection = trackerClient.getConnection();
            //创建储存器客户端
            StorageClient storageClient = new StorageClient(connection, null);
            //文件上传
            String[] uploadResult = storageClient.upload_appender_file(file_buff, file_ext_name, meta_list);
            return uploadResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     *文件上传
     * */
    public static String[] uploadFile(byte[] file_buff, String file_ext_name, String des) {
        try {
            NameValuePair[] meta_list = new NameValuePair[1];
            meta_list[0] = new NameValuePair(des);
            //创建跟踪器服务器客户端
            TrackerClient trackerClient = new TrackerClient();
            //获取跟踪器服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            //创建储存服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);
            //文件上传
            String[] strings = storageClient.upload_appender_file(file_buff, file_ext_name, meta_list);
            return strings;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
     *获取tracker服务器地址
     * */

    public static String getTrackerUrl() {
        try {
            //创建跟踪服务器
            TrackerClient trackerClient = new TrackerClient();
            //获取跟踪服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取跟踪服务器地址url
            String hostAddress = trackerServer.getInetSocketAddress().getAddress().getHostAddress();
            int port = ClientGlobal.getG_tracker_http_port();
            String url = "http://" + hostAddress + ":" + port;
            return url;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
     * 文件下载
     * */
    public static byte[] downLoadFile(String group_name, String remote_filename) {
        try {
            //创建跟踪服务器
            TrackerClient trackerClient = new TrackerClient();
            //获取跟踪服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            //创建储存服务器
            StorageClient storageClient = new StorageClient(trackerServer, null);
            byte[] file = storageClient.download_file(group_name, remote_filename);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 文件删除
     * */
    public static void deleteFile(String group_name, String remote_filename) {
        try {
            //创建跟踪服务器
            TrackerClient trackerClient = new TrackerClient();
            //获取跟踪服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            //创建储存服务器
            StorageClient storageClient = new StorageClient(trackerServer, null);
            storageClient.delete_file(group_name, remote_filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
