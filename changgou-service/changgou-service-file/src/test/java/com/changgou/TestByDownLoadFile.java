package com.changgou;

import com.changgou.util.FastDFSClient;
import org.apache.commons.io.IOUtils;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestByDownLoadFile {
    @Test
    public void test01() throws IOException {
        String group = "group1";
        String remote_filename = "M00/00/00/wKjThF2DSQ-EUUmZAAAAAD9zVuA257.png";
        byte[] file = FastDFSClient.downLoadFile(group, remote_filename);
        IOUtils.write(file, new FileOutputStream("C:\\Users\\ASUS\\Desktop\\tttt\\aaaaaaaa.jpg"));
    }

    @Test
    public void testDelete02() throws IOException {
        String group = "group1";
        String remote_filename = "M00/00/00/wKjThF2DSQ-EUUmZAAAAAD9zVuA257.png";
        FastDFSClient.deleteFile(group, remote_filename);
    }

   /**
    * @Author mqy
    * @Description
    * @Date
    * @Param
    * @return
    **/

    public static FileInfo getFileInfo(String group_name, String remote_filename) {
        try {
            // 1、创建跟踪服务器客户端
            TrackerClient trackerClient = new TrackerClient();
            // 2、获取跟踪服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            // 3、创建存储服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);
            // 4、获取文件信息
            FileInfo fileInfo = storageClient.get_file_info(group_name, remote_filename);
            return fileInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
