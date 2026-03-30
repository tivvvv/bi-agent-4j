package com.tiv.bi.util;

import com.tiv.bi.common.BusinessCodeEnum;
import com.tiv.bi.exception.BusinessException;

/**
 * 异常处理工具类
 */
public class ThrowUtils {

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param businessCodeEnum
     */
    public static void throwIf(boolean condition, BusinessCodeEnum businessCodeEnum) {
        throwIf(condition, new BusinessException(businessCodeEnum));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param businessCodeEnum
     * @param message
     */
    public static void throwIf(boolean condition, BusinessCodeEnum businessCodeEnum, String message) {
        throwIf(condition, new BusinessException(businessCodeEnum, message));
    }

}
