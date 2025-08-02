package com.skillmentor.root.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FileService {
    public String uploadFile(String path, MultipartFile file, String prefix) throws IOException;
    public boolean deleteFile(String path, String fileName);

    public InputStream getResourceFile(String path, String fileName) throws FileNotFoundException;

}
