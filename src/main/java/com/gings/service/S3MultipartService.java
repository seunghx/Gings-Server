package com.gings.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
public class S3MultipartService implements MultipartService {
    
    @Value("${cloud.aws.s3.bucket-name}")
    private String BUCKET_NAME;

    
    private final AmazonS3Client s3Client;
    private final TransferManager transferManager;
    
    /**
     *  현재는 default 설정.
     * 
     */
    public S3MultipartService(AmazonS3Client s3Client) {
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
    public List<String> uploadMultipleFiles(List<MultipartFile> files, String path) {
       
        if (files == null || files.isEmpty()) {
            log.info("Empty parameter files detected. while trying to upload multi-part files");
            
            throw new IllegalArgumentException("Argumet files is empty.");
        }
        
        //else if(StringUtils.isEmpty(path)) {
        //    log.info("Empty path parameter detected while trying to upload multi-part files");
            
         //   throw new IllegalArgumentException("Argument path is empty.");
       // }
        try {
            return uploadMultiple(files, path);
            
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
    *   @throws 
    *       IllegalArgumentException 업로드 할 이미지가 없을 경우 사전에 검사해서 이 메서드를 호출하는 것이 바람직.
    *
    */
    @Override
    public String uploadSingleFile(MultipartFile file, String path) {
        if(file == null) {
            log.info("Empty parameter files detected. while trying to upload multi-part files");
            
            throw new IllegalArgumentException("Argumet files is empty.");
        }
        try {
            return uploadOne(file, path);
            
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
    
    @Override
    public void delete(List<String> fileNames, String path) {
        if (fileNames == null || fileNames.size() == 0) {
            throw new IllegalArgumentException("Arguments fileNames is empty.");
        }

        if (fileNames.size() == 1)
            deleteOne(fileNames.get(0));
        else
            deleteAll(fileNames);
    }
    
    private String uploadOne(MultipartFile file, String path) throws InterruptedException {
        
        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentLength(file.getSize());
        metaData.setContentType(file.getContentType());
        String uploadingFileName = path + getNewFileName(file.getOriginalFilename());

        try(InputStream ins = file.getInputStream()) {
            PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, uploadingFileName, 
                                                            ins, metaData);
            
            request.setCannedAcl(CannedAccessControlList.PublicRead);

            transferManager.upload(request).waitForCompletion();
                        
            log.info("Upload file succeeded.");
            
        }catch(IOException e) {
            log.info("IOException occurred while trying to open InputStream.");
            
            throw new RuntimeException(e);
        }
        
        return s3Client.getUrl(BUCKET_NAME, uploadingFileName).toString();
    }
    
    private List<String> uploadMultiple(List<MultipartFile> files, String path) 
                                                                    throws InterruptedException {
        List<String> uploaded = new ArrayList<>();
        
        ArrayList<File> uploadingFiles = new ArrayList<File>();

        files.stream()
             .forEach(file -> {
                 String uploadingFileName = file.getOriginalFilename();
            
                 File uploadingFile = new File("./"+uploadingFileName);
            
                 try {
                     file.transferTo(uploadingFile);
                 }catch(IOException e) {
                     log.info("IOException occurred while trying to transfer multi-part file to File object.");
                
                     throw new RuntimeException(e);
                 }
                 uploadingFiles.add(uploadingFile);
                 uploaded.add(getUploadedFileName(uploadingFileName, path));
             }); 
                   
            transferManager.uploadFileList(BUCKET_NAME, "/", new File("./"), uploadingFiles)
                           .waitForCompletion();
                                    
            log.info("Upload files succeeded.");

            return uploaded;
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
    private void deleteOne(String fileName) {

        log.info("Deteting single file object from S3. Deleting file name : {}", 
                                                                            fileName);
        try {
            s3Client.deleteObject(BUCKET_NAME, fileName);
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
    private void deleteAll(List<String> fileNames) {

        log.info("Deteting multiple file objects from S3.");

        fileNames.forEach(file -> log.debug("Deleting file name : {}", file));

        DeleteObjectsRequest dor = new DeleteObjectsRequest(BUCKET_NAME)
                                        .withKeys(fileNames.toArray(new String[] {}));
        try {
            s3Client.deleteObjects(dor);
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
    
    /**
     * 
     * s3에 저장될 새 파일이름을 반환.
     * 
     * @throws UnsupportedFileFormatException 파일 확장자가 없을 때 이 예외가 발생됨. 
     *                                        bean validation을 통해 올바른 이미지 파일 포맷 검사가 진행되었을텐데 
     *                                        이 예외가 던져진다는 것은 서버 코드의 잘못이므로 이 예외는 서버 에러로 
     *                                        처리되어야함.
     */
    private static String getNewFileName(String fileName) {
       
        String randomStr = UUID.randomUUID().toString().replaceAll("-", "");
        
        return randomStr + fileName;
    }
    

    private String getUploadedFileName(String fileName, String path) {
        
        return s3Client.getUrl(BUCKET_NAME, fileName)
                       .toString();
    }
    
}