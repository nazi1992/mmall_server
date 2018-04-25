package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2018/4/24 0024.
 */
@Service("IFileService")
public class IFileServiceImpl implements IFileService {
    private Logger logger = LoggerFactory.getLogger(IFileServiceImpl.class);
    @Override
    public String upload(MultipartFile multipartFile, String path) {

        String fileName = multipartFile.getOriginalFilename();
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".")+1);
        String realFileName = UUID.randomUUID()+"."+fileSuffix;
        logger.info("文件上传路径：{}，文件名：{} 储存的文件名：{}",path,fileName,realFileName);
        File filePath = new File(path);
        if(!filePath.exists())
        {
            filePath.setWritable(true);//允许写入操作
            filePath.mkdirs();
        }
        File file = new File(filePath,realFileName);

        try {
            multipartFile.transferTo(file);
            //将文件上传到ftp服务器
            FtpUtil.uploadFile(Lists.newArrayList(file));
            //将文件从服务器目录中删除
            file.delete();
        } catch (IOException e) {
            logger.error("上传失败",e);
            return  null;
        }

        return file.getName();
    }
}
