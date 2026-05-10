package com.atguigu.handler;

import com.atguigu.enums.ResultCode;
import com.atguigu.exception.BusinessException;
import com.atguigu.result.Result;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
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
 * 全局异常处理器
 * <p>
 * 统一处理控制器层和 Feign 调用异常，返回标准 Result 响应。
 * 覆盖：参数校验、类型转换、请求方法/媒体类型错误、404、
 * 业务异常、Feign 调用异常及未知异常。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===================== Bean Validation 校验异常 =====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn("请求体参数校验失败: {}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.error(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /*@ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e) {
        log.warn("表单绑定参数校验失败: {}", e.getMessage());
        String message = e.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.error(ResultCode.PARAM_ERROR.getCode(), message);
    }*/

    /**
     * 处理 @Validated 在类上或方法参数（如 @Min）触发的约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("方法参数校验失败: {}", e.getMessage());
        String message = e.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining("; "));
        return Result.error(ResultCode.PARAM_ERROR.getCode(), message);
    }

    // ===================== Spring MVC 常见异常 =====================

    /*@ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        log.warn("缺少必填参数: {}", e.getMessage());
        return Result.error(ResultCode.PARAM_ERROR.getCode(), "参数 '" + e.getParameterName() + "' 是必需的");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型不匹配: name={}, value={}, requiredType={}", e.getName(), e.getValue(), e.getRequiredType());
        String typeName = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown";
        return Result.error(ResultCode.PARAM_ERROR.getCode(), "参数 '" + e.getName() + "' 类型错误，期望类型: " + typeName);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体无法解析: {}", e.getMessage());
        return Result.error(ResultCode.PARAM_ERROR.getCode(), "请求体格式错误，无法解析");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("不支持的请求方法: {}", e.getMessage());
        return Result.error(ResultCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Result<?> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        log.warn("不支持的媒体类型: {}", e.getMessage());
        return Result.error(415, "不支持的媒体类型");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("接口不存在: {}", e.getRequestURL());
        return Result.error(ResultCode.NOT_FOUND);
    }*/

    // ===================== 业务异常 =====================

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    // ===================== OpenFeign 调用异常 =====================

    /*@ExceptionHandler(FeignException.class)
    public Result<?> handleFeignException(FeignException e) {
        log.error("Feign 调用异常: status={}, message={}", e.status(), e.getMessage());
        // 可以尝试从响应体中提取下游服务的业务错误信息，这里简化处理
        return Result.error(ResultCode.ERROR.getCode(), "内部服务调用失败，请稍后重试");
    }*/

    // ===================== 兜底异常 =====================

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("未知系统异常: ", e);
        return Result.error(ResultCode.ERROR);
    }
}