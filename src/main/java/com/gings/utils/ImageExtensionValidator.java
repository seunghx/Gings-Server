package com.gings.utils;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final MessageSource messageSource;
    
    public ImageExtensionValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void initialize(ImageExtension contactNumber) {
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if (Objects.isNull(file) || file.isEmpty()) {
            log.info("Empty multipart file detected in {}.", this);

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    messageSource.getMessage("response.exception.multipart.empty", null, 
                                             LocaleContextHolder.getLocale()))
                   .addConstraintViolation();

            return false;
        }

        log.debug("Delegating image file name validation to {}.", ImageOperationProvider.class);

        try {
            ImageOperationProvider.validateImage(file.getOriginalFilename());
        } catch (UnsupportedImageFormatException ex) {
            if (log.isDebugEnabled()) {
                log.debug("Validating image file name failed due to {}.", ex.toString());
                log.debug("Invalid file name : {}", ex.getFileName());
            }
            return false;
        }

        log.debug("Validating image file name succeeded.");
        
        return true;

    }

}
