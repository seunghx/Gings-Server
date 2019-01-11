package com.gings.utils;



import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * {@link ImageExtension} 애노테이션이 붙은 {@link MultipartFile} 타입 프로퍼티에 대하여 올바른(지원하는) 
 * 이미지 확장자 인지를 검증한다.
 * 
 * @author seunghyun
 *
 */
@Slf4j
@Component
public class ImageExtensionValidator implements ConstraintValidator<ImageExtension, MultipartFile> {

    private MessageSource msgSource;
    
    public ImageExtensionValidator(MessageSource msgSource) {
        this.msgSource = msgSource;
    }
    
    @Override
    public void initialize(ImageExtension contactNumber) {
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if(file == null) return true;

        log.debug("Delegating image file name validation to {}.", ImageOperationProvider.class);
        
        try {
            ImageOperationProvider.validateImage(file.getOriginalFilename());
        } catch (UnsupportedImageFormatException ex) {
            if (log.isInfoEnabled()) {
                log.info("Validating image file name failed due to {}.", ex.toString());
                log.info("Invalid file name : {}", ex.getFileName());
            }
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    msgSource.getMessage("response.exception.multipart.invalid", null, LocaleContextHolder.getLocale()))
                    .addConstraintViolation();
            return false;
        } catch (Throwable throwable) {
            log.error("{}", throwable);
        }
        
        log.debug("Validating image file name succeeded.");
        
        return true;

    }

}
