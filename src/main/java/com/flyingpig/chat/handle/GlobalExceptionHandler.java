package com.flyingpig.chat.handle;


import com.flyingpig.chat.dataobject.common.Result;
import com.flyingpig.chat.dataobject.constant.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.BindException;

//全局异常处理器
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 请求参数异常/缺少--400
     *
     * @param e
     * @return
     */
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            BindException.class}
    )
    public Result missingServletRequestParameterException(Exception e) {
        return Result.error(StatusCode.PARAMETERERROR, "缺少参数或参数错误");
    }

    /**
     * 请求方法错误--405
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result httpRequestMethodNotSupportedExceptionHandler(Exception e){
        log.error("请求方法错误");
        return Result.error(StatusCode.METHODERROR,"请求方法错误");
    }



    /**
     * 全局异常处理
     * @param ex
     * @return
     */

    @ExceptionHandler(RedisConnectionFailureException.class)
    public Result redisConnectionFailureExceptionHandler(RedisConnectionFailureException ex) {
        log.error("redis未启动");
        return Result.error("redis未启动");
    }

    /**
     * 全局异常处理
     * @param ex
     * @return
     */

    @ExceptionHandler(Exception.class)
    public Result ex(Exception ex) {
        ex.printStackTrace();
        return Result.error(ex.getMessage());
    }
}