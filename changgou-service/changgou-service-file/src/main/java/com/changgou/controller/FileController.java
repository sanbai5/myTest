package com.changgou.controller;

import com.changgou.util.FastDFSClient;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
public class FileController {
    /*
     * 文件上传
     * */
    @PostMapping("/upload")
    public String upload(@PathVariable(value = "file") MultipartFile file) throws IOException {

        String[] strings = FastDFSClient.uploadFile(file.getBytes(),
                StringUtils.getFilenameExtension(file.getOriginalFilename()),
                file.getOriginalFilename());
        String trackerUrl = FastDFSClient.getTrackerUrl();
        return trackerUrl + "/" + strings[0] + "/" + strings[1];
    }
}
