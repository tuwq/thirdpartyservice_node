package com.tuwq.advice;


import com.tuwq.common.ResultCode;
import com.tuwq.exception.CheckParamException;
import com.tuwq.exception.ExceptionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常拦截器
 */
@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    /**
     * 检查参数出错的异常
     * @param e
     * @return
     */
    @ExceptionHandler(CheckParamException.class)
    public ResponseEntity<ExceptionResult> handlerParamException(CheckParamException e) {
        return new ResponseEntity<ExceptionResult>(ExceptionResult.error(e.getResultCode(), e.getMsg()), HttpStatus.OK);
    }
    /**
     * 接受全部异常
     * @param e
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResult> handlerParamException(Exception e) {
        System.out.println("捕获异常到全局异常");
        log.error(e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<ExceptionResult>(ExceptionResult.error(ResultCode.SERVER_ERROR,e.getMessage()), HttpStatus.OK);
    }
}
