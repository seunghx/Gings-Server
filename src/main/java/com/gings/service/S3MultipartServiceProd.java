package com.gings.service;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * {@link MultipartFile}을 aws s3(Simple Storage Service)에 추가.
 * 
 * 
 * 1. 전체적으로 예외 처리를 위한 try-catch 문이 많기 때문에 나중에 aop 추가 예정.
 * 2. 업로드시 이미지 리사이징 기능 추가 에정.
 * 
 * @author seunghyun
 *
 */
@Slf4j
@Service
public class S3MultipartServiceProd implements MultipartService {

    @Value("${cloud.aws.s3.default-url}")
    private String defaultUrl;
    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;
    
    private final AmazonS3Client s3Client;
    private final TransferManager transferManager;
    
    /**
     *  현재는 default 설정.
     * 
     */
    public S3MultipartServiceProd(AmazonS3Client s3Client) {
        this.s3Client = s3Client;
        this.transferManager = TransferManagerBuilder.standard()
                                                     .withS3Client(s3Client)
                                                     .build();
    }
    
    /**
     * 
     * @param path - {@code filie}의 각 element가 s3 상에 등록될 위치. (버켓 최상위에서 디렉토리를 포함한 경로를 의미)
     *               필요시 각 file 당 path를 다르게 갖도록 변경 가능
     * 
     * @throws IllegalArgumentException 파라미터 {@code path}가 empty String 이거나 {@code files}가 null 
     *                                  또는 empty 리스트일 경우 이 예외가 던져진다.
     *                                  업로드 할 이미지가 없을 경우 사전에 검사해서 이 메서드를 호출하는 것이 바람직.
     */
    @Override
    public List<String> uploadMultipleFiles(List<MultipartFile> files) {
       
        if (files == null || files.isEmpty()) {
            log.info("Empty parameter files detected. while trying to upload multi-part files");
            
            throw new IllegalArgumentException("Argumet files is empty.");
        }
        
        try {
            List<String> uploaded = new ArrayList<>();
            
            files.stream()
                 .forEach(file -> {
                     try {
                         uploaded.add(upload(file));
                     }
                     catch(InterruptedException e) {
                         log.info("InterruptedException occurred while trying to upload multiple images");
                         
                         throw new RuntimeException(e);
                     }  
                 });

            log.info("Upload file succeeded.");

            return uploaded;
        } catch(AmazonServiceException e) {
            log.info("AmazonServiceException occurred while trying to upload multiple images");
            
            throw e;
        } catch(AmazonClientException e) {
            log.info("AmazonClientException occurred while trying to upload multiple images");
            
            throw e;
        }         
    }
    
   /**
    * 
    *   @throws 
    *       IllegalArgumentException 업로드 할 이미지가 없을 경우 사전에 검사해서 이 메서드를 호출하는 것이 바람직.
    *
    */
    @Override
    public String uploadSingleFile(MultipartFile file) {
        if(file == null) {
            log.info("Empty parameter files detected. while trying to upload multi-part files");
            
            throw new IllegalArgumentException("Argumet files is empty.");
        }
        try {
            String uploaded =  upload(file);
            
            log.info("Upload file succeeded.");

            return uploaded;
        } catch(AmazonServiceException e) {
            log.info("AmazonServiceException occurred while trying to upload multiple images");
            
            throw e;
        } catch(AmazonClientException e) {
            log.info("AmazonClientException occurred while trying to upload multiple images");
            
            throw e;
        } catch(InterruptedException e) {
            log.info("InterruptedException occurred while trying to upload multiple images");
            
            throw new RuntimeException(e);
        }     
    }
    


    /**
     * 
     * 한 개의 파일에 대한 삭제 연산을 수행.
     * 
     * {@link AmazonClientException} 타입 예외가 발생할 경우 예외를 밖으로 전달하지 않는다.
     * 
     * 명시적인 삭제 로직이 필요. 
     * (스케쥴러를 이용해 삭제 가능하게 삭제할 이미지 명을 리스트에 담거나 이벤트 방식으로 처리할 예정. 후에 추가.)
     * 
     */
    @Override
    public void deleteSingleFile(String fileName) {
        
        log.info("Deteting single file object from S3. Deleting file name : {}", fileName);
        
        try {
            s3Client.deleteObject(bucketName, parseFileName(fileName));

            log.info("Deleting file from S3 succeeded.");

        } catch(AmazonClientException e) {
            if(log.isInfoEnabled()) {
                log.info("Error occurred while trying to delete object.");

                log.error("{}", e);
                // 후에 삭제 로직 추가
            }
        }
    }
    

    /**
     * 
     * 여러 개의 파일에 대한 삭제 연산을 수행.
     * {@link MultiObjectDeletionException}이 발생할 경우 예외를 밖으로 전달하지 않는다.
     * 명시적인 삭제 로직이 필요. 
     * (스케쥴러를 이용해 삭제 가능하게 삭제할 이미지 명을 리스트에 담거나 이벤트 방식으로 처리할 예정. 후에 추가.)
     *      
     */
    @Override
    public void deleteMultipleFiles(List<String> fileNames) {
        if (fileNames == null || fileNames.size() == 0) {
            throw new IllegalArgumentException("Arguments fileNames is empty.");
        }

        log.info("Deteting multiple file objects from S3.");
        
        List<String> deletingFileNames = parseFileName(fileNames);
        
        DeleteObjectsRequest dor = new DeleteObjectsRequest(bucketName)
                                        .withKeys(deletingFileNames.toArray(new String[] {}));
        try {
            
            s3Client.deleteObjects(dor);
            
            log.info("Deleting multiple files from S3 succeeded.");

        }catch(MultiObjectDeleteException e) {
            
            if(log.isInfoEnabled()) {
                log.info("Error occurred while trying to delete multiple objects.");
                
                log.info("Error message from aws s3 : {}", e.getErrorMessage());
                log.info("Error is due to {}", e.getErrorType());
                log.info("Successfully deleted files : {}.", e.getDeletedObjects());
                
                log.error("{}", e);
                // 후에 삭제 로직 추가
            }
         }
    }    
    
    private String upload(MultipartFile file) throws InterruptedException {
        
        ObjectMetadata metaData = getObjectMetadata(file);
        
        String uploadingFileName = newFileName(file.getOriginalFilename());

        try(InputStream ins = file.getInputStream()) {
            PutObjectRequest request = 
                    new PutObjectRequest(bucketName, uploadingFileName, ins, metaData);
            
            request.setCannedAcl(CannedAccessControlList.PublicRead);

            transferManager.upload(request)
                           .waitForCompletion();
                                    
        }catch(IOException e) {
            log.info("IOException occurred while trying to open InputStream.");
            
            throw new RuntimeException(e);
        }
        
        return uploadedFileUrl(uploadingFileName);
    }
    
    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata metaData = new ObjectMetadata();
        
        metaData.setContentType(file.getContentType());
        metaData.setContentLength(file.getSize());

        return metaData;
    }
    
    
    /**
     * 
     * s3에 저장될 새 파일이름을 반환.
     * 
     * @throws UnsupportedFileFormatException 파일 확장자가 없을 때 이 예외가 발생됨. 
     *                                        bean validation을 통해 올바른 이미지 파일 포맷 검사가 진행되었을텐데 
     *                                        이 예외가 던져진다는 것은 서버 코드의 잘못이므로 이 예외는 서버 에러로 
     *                                        처리되어야함.
     */
    private String newFileName(String fileName) {
        
        String randomStr = UUID.randomUUID().toString().replaceAll("-", "");
        
        return randomStr + fileName;
     
    }
    

    private String uploadedFileUrl(String fileName) {
        
        return new StringBuilder(defaultUrl).append(bucketName)
                                            .append("/")
                                            .append(fileName)
                                            .toString();
        
    }
    
    private List<String> parseFileName(List<String> fileNames) {
        List<String> replaced = new ArrayList<>();
        
        fileNames.stream().forEach(file -> {
            replaced.add(parseFileName(file));
        });
        
        return replaced;
    }
    
    private String parseFileName(String fileName) {
        return fileName.replace(defaultUrl + bucketName + "/", "");
    }
    
}
