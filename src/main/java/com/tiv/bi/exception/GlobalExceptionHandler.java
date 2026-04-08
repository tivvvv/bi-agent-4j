package com.tiv.bi.exception;

import com.tiv.bi.common.BusinessCodeEnum;
import com.tiv.bi.common.BusinessResponse;
import com.tiv.bi.util.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public BusinessResponse<?> handleBusinessException(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BusinessResponse<String> handleValidationException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return ResultUtils.error(BusinessCodeEnum.PARAMS_ERROR, errorMsg);
    }

    /**
     * 运行时异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public BusinessResponse<?> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(BusinessCodeEnum.SYSTEM_ERROR);
    }

    /**
     * 异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public BusinessResponse<?> handleException(Exception e) {
        log.error("Exception", e);
        return ResultUtils.error(BusinessCodeEnum.SYSTEM_ERROR);
    }

}
