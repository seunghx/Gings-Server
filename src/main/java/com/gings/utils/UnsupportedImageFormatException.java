package com.gings.utils;

import java.util.List;

import lombok.Getter;

/**
 * 
 * 이미지 포맷으로 지원하지 않는 확장자를 갖는 파일이 전달되었을 때 던져지는 예외이다.
 * 
 * @author seunghyun
 *
 */
@Getter
public class UnsupportedImageFormatException extends RuntimeException {

    private static final long serialVersionUID = -2983016173279497580L;

    private String fieldName;
    private String fileName;
    private final List<String> supportedExtensions;

    public UnsupportedImageFormatException(String message, String fieldName, String fileName,
                                                            List<String> supportedExtensions) {
        super(message);
        this.fieldName = fieldName;
        this.fileName = fileName;
        this.supportedExtensions = supportedExtensions;
    }
}
