package com.skillmentor.root.controller;

import com.skillmentor.root.service.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping(value = "/file")
@Tag(name = "File Management", description = "Endpoints for managing files and their related data")
public class FileController {

    @Autowired
    private final FileService fileService;

    @Value("${project.images}")
    private String path;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(@RequestPart MultipartFile file) throws IOException {
//        String uploadedFileName = fileService.uploadFile(path, file);
//        return ResponseEntity.ok("File uploaded is : " + uploadedFileName);
//    }

    @GetMapping(value = "/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public void getResource(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        InputStream resourceFile = fileService.getResourceFile(path, fileName);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(resourceFile, response.getOutputStream());
    }

}
