package com.ran.hub.config;

//全局异常处理器

import com.ran.commons.constant.ResponseEnum;
import com.ran.commons.exception.BusinessException;
import com.ran.commons.response.ApiResult;
import com.ran.hub.exception.DistributedLockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 使用 @RestControllerAdvice + @ExceptionHandler 拦截所有 Controller 抛出的异常
 * 统一包装成 ApiResult 返回，避免异常直接暴露给前端。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String LOG_STRING = "RequestURL: {}, Timestamp: {}, {}: {}";
    /** Handle business exceptions */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<Void> handleBusinessException(HttpServletRequest request, BusinessException e) {
        ApiResult<Void> result = ApiResult.error(e);
        log.error(LOG_STRING, request.getRequestURL(), result.timestamp(), e.getClass().getSimpleName(), e.getMessage(), e);
        return result;
    }

    /** Handle parameter validation exceptions */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String messageCode = fieldError != null ? fieldError.getDefaultMessage() : "param.invalid";
        ApiResult<Void> result = ApiResult.error(ResponseEnum.PARAMETER_ERROR.getCode(), messageCode);
        log.warn(LOG_STRING, request.getRequestURL(), result.timestamp(), e.getClass().getSimpleName(), e.getMessage(), e);
        return result;
    }

    /** 表单绑定失败 */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleBindException(HttpServletRequest request, BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String messageCode = fieldError != null ? fieldError.getDefaultMessage() : "param.invalid";
        ApiResult<Void> result = ApiResult.error(ResponseEnum.PARAMETER_ERROR.getCode(), messageCode);
        log.warn(LOG_STRING, request.getRequestURL(), result.timestamp(), e.getClass().getSimpleName(), e.getMessage());
        return result;
    }

    /** Handle constraint violation exceptions */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException e) {
        String messageCode = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("; "));
        ApiResult<Void> result = ApiResult.error(ResponseEnum.VALIDATION_ERROR.getCode(), messageCode);
        log.warn(LOG_STRING, request.getRequestURL(), result.timestamp(), e.getClass().getSimpleName(), e.getMessage(), e);
        return result;
    }

    /** 参数类型不匹配 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleMethodArgumentTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        String messageCode = "parameter.error";
        ApiResult<Void> result = ApiResult.error(ResponseEnum.PARAMETER_ERROR.getCode(), messageCode);
        log.warn(LOG_STRING, request.getRequestURL(), result.timestamp(), e.getClass().getSimpleName(), e.getMessage());
        return result;
    }

    /** 	缺少必填参数 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleMissingServletRequestParameterException(HttpServletRequest request, MissingServletRequestParameterException e) {
        String messageCode = "parameter.missing";
        ApiResult<Void> result = ApiResult.error(ResponseEnum.PARAMETER_ERROR.getCode(), messageCode);
        log.warn(LOG_STRING, request.getRequestURL(), result.timestamp(), e.getClass().getSimpleName(), e.getMessage());
        return result;
    }

    /** 	请求体不可读（JSON格式错误等） */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleHttpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException e) {
        ApiResult<Void> result = ApiResult.error(ResponseEnum.BAD_REQUEST.getCode(), "parameter.illegal");
        log.warn(LOG_STRING, request.getRequestURL(), result.timestamp(), e.getClass().getSimpleName(), e.getMessage());
        return result;
    }

    /** 请求方法不对 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResult<Void> handleHttpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        String messageCode = "http.method.not.supported";
        ApiResult<Void> result = ApiResult.error(ResponseEnum.METHOD_NOT_ALLOWED.getCode(), messageCode);
        log.warn(LOG_STRING, request.getRequestURL(), result.timestamp(), e.getClass().getSimpleName(), e.getMessage());
        return result;
    }

    /** 接口不存在 */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResult<Void> handleNoHandlerFoundException(HttpServletRequest request, NoHandlerFoundException e) {
        String messageCode = "http.url.not.found";
        ApiResult<Void> result = ApiResult.error(ResponseEnum.NOT_FOUND.getCode(), messageCode);
        log.warn(LOG_STRING, request.getRequestURL(), result.timestamp(), e.getClass().getSimpleName(), e.getMessage());
        return result;
    }

    /** 分布式锁异常，携带 lockKey + errorType */
    @ExceptionHandler(DistributedLockException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<Void> handleDistributedLockException(HttpServletRequest request, DistributedLockException e) {
        ApiResult<Void> result = ApiResult.error(ResponseEnum.SYSTEM_ERROR.getCode(), "lock.error." + e.getErrorType().name().toLowerCase());
        log.error(LOG_STRING + ", lockKey={}, errorType={}", request.getRequestURL(), result.timestamp(), e.getClass().getSimpleName(), e.getMessage(), e.getLockKey(), e.getErrorType(), e);
        return result;
    }

    /** 兜底异常，不暴露内部细节 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<Void> handleException(HttpServletRequest request, Exception e) {
        ApiResult<Void> result = ApiResult.error(ResponseEnum.SYSTEM_ERROR.getCode(), "error.system");
        log.error(LOG_STRING, request.getRequestURL(), result.timestamp(), e.getClass().getSimpleName(), e.getMessage(), e);
        return result;
    }
}
