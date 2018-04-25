package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Administrator on 2018/4/24 0024.
 */
public interface IFileService {
    public String upload(MultipartFile multipartFile,String path);
}
