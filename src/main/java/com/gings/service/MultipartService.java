package com.gings.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface MultipartService {
    public String uploadSingleFile(MultipartFile file, String path);
    public List<String> uploadMultipleFiles(List<MultipartFile> files, String path);
    public void delete(List<String> fileNames, String path);
}
