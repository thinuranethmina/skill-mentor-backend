package com.skillmentor.root.service.impl;

import com.skillmentor.root.service.FileService;
import com.skillmentor.root.service.R2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    R2Service r2Service;

    @Override
    public String uploadFile(String path, MultipartFile file, String prefix) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = prefix + UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + extension;

        String key = path + filename;

        try {
            r2Service.uploadFile(key, file.getInputStream(), file.getSize());
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to R2", e);
        }

        return filename;
    }


    @Override
    public boolean deleteFile(String path, String fileName) {
        r2Service.deleteFile(path+fileName);
        return true;
    }

    @Override
    public InputStream getResourceFile(String path, String fileName) throws FileNotFoundException {
        String fullPath = path + File.separator + fileName;
        return new FileInputStream(fullPath);
    }
}
