package com.gings.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface MultipartService {
    
    public String uploadSingleFile(MultipartFile file);
    public List<String> uploadMultipleFiles(List<MultipartFile> files);
    
    public void deleteSingleFile(String fileName);
    public void deleteMultipleFiles(List<String> fileNames);
}
