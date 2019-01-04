package com.gings.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.gings.model.ApiError;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author seunghyun
 *
 */
@Slf4j
@Order(2)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource msgSource;

    /**
     * 
     * 컨트롤러 메서드에 전달되는 인자에 문제가 있을 때 던져지는 예외에 대한 예외 핸들러이다. 예를 들면
     * {@code @PathVariable}인자에 다른 타입의 값이 들어왔다거나 할 때 이 예외가 발생한다.
     * 
     * @param status
     *            - 400 - Bad Request 를 의미.
     * 
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        
        log.error("An exception occurred associated with controller method parameter.", ex);

        ApiError apiError = new ApiError(status.value(),
                msgSource.getMessage("response.exception.MethodArgumentNotValidException", null, request.getLocale()));

        ex.getBindingResult().getFieldErrors()
                             .stream()
                             .forEach(e -> apiError.addDetail(e.getField(), 
                                                              msgSource.getMessage(e, request.getLocale())));

        ex.getBindingResult().getGlobalErrors()
                             .stream()
                             .forEach(e -> apiError.addDetail(e.getObjectName(), 
                                                              msgSource.getMessage(e, request.getLocale())));

        return handleExceptionInternal(ex, apiError, headers, status, request);
    }

    /**
     * 
     * 컨트롤러에 전달되는 오브젝트 인자에 대한 바인딩이 실패할 경우 이 메서드에서 해당 예외를 처리한다.
     * 
     * @param status
     *            - 400 - Bad Request 를 의미.
     */
    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.error("An Exception occurred while trying to bind object", ex);

        String message = msgSource.getMessage("response.exception.BindException", null, request.getLocale());
        
        ApiError apiError = new ApiError(status.value(), message);
                
        ex.getBindingResult().getFieldErrors()
                             .stream()
                             .forEach(e -> apiError.addDetail(e.getField(), message));

        ex.getBindingResult().getGlobalErrors().stream()
                .forEach(e -> apiError.addDetail(e.getObjectName(), msgSource.getMessage(e, request.getLocale())));

        return handleExceptionInternal(ex, apiError, headers, status, request);
    }

    /**
     * 
     * {@link HttpServletRequest}에 원하는 파라미터가 들어있지 않을 경우 발생하는 예외를 처리한다.
     * 
     * @param status
     *            - 400 - Bad Request 를 의미.
     *
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("An exception occurred because request parameter doesn't exist.", ex);

        ApiError apiError = new ApiError(status.value(),
                msgSource.getMessage("response.exception.MissingServletRequestParameterException",
                        new String[] { ex.getParameterName() }, request.getLocale()));

        return handleExceptionInternal(ex, apiError, headers, status, request);
    }

    /**
     * 
     * 요구한 multipart file이 전달되지 않은 경우 발생하는 예외를 처리한다.
     * 
     * @param status
     *            - 400 - Bad Request 를 의미.
     * 
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("An exception occurred because request multipart data doesn't exist.", ex);

        ApiError apiError = new ApiError(status.value(),
                msgSource.getMessage("response.exception.MissingServletRequestPartException",
                        new String[] { ex.getRequestPartName() }, request.getLocale()));

        return handleExceptionInternal(ex, apiError, headers, status, request);
    }
    
    /**
     * 
     * 페이지를 찾지 못할 때 발생하는 예외에 대한 처리를 수행하는 메서드.
     * 
     * @param status
     *            - 404 - Not Found
     * 
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {

        ApiError apiError = new ApiError(status.value(),
                msgSource.getMessage("response.exception.NoHandlerFoundException", new String[] { ex.getRequestURL() },
                        request.getLocale()));
        return handleExceptionInternal(ex, apiError, headers, status, request);
    }

    /**
     * 
     * 지원되지 않는 HTTP method가 컨트롤러에 전달되었을 때 발생하는 예외를 처리하는 메서드.
     * 
     * @param status
     *            - 405 - Method Not Allowed
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("An exception occurred associated with unsupported type request.", ex);

        StringBuilder builder = new StringBuilder();

        ex.getSupportedHttpMethods().forEach(method -> builder.append(method.name() + ", "));
        int lastCommaIdx = builder.lastIndexOf(",");
        builder.replace(lastCommaIdx, lastCommaIdx + 1, "");

        ApiError apiError = new ApiError(status.value(),
                msgSource.getMessage("response.exception.HttpRequestMethodNotSupportedException",
                        new String[] { ex.getMethod(), builder.toString() }, request.getLocale()));
        
        return handleExceptionInternal(ex, apiError, headers, status, request);
    }


    /**
     * 
     * 이 클래스가 계승하는 {@link ResponseEntityExceptionHandler}에 정의된 메서드에서 처리할 수 있는 예외가 아닌
     * 그 외의 예외에 대한 처리를 수행하는 메서드이다.
     * 
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleSystemException(Exception ex, WebRequest request) {
        log.error("An system exception occurred.", ex);

        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                msgSource.getMessage("response.exception.SystemException", null, request.getLocale()));

        return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
