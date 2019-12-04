package com.xcyy.advice;

import com.xcyy.common.ResultCode;
import com.xcyy.exception.CheckParamErrorException;
import com.xcyy.exception.CheckParamFormatException;
import com.xcyy.exception.ExceptionResult;
import com.xcyy.exception.TokenException;
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
     * 检查参数格式出错,如果空串,手机号码格式错误
     * @param e
     * @return
     */
    @ExceptionHandler(CheckParamFormatException.class)
    public ResponseEntity<ExceptionResult> handlerParamException(CheckParamFormatException e) {
        return new ResponseEntity<ExceptionResult>(ExceptionResult.error(e.getResultCode(), e.getMsg()), HttpStatus.OK);
    }

    /**
     * 检查参数出错,如密码错误等异常
     * @param e
     * @return
     */
    @ExceptionHandler(CheckParamErrorException.class)
    public ResponseEntity<ExceptionResult> handlerException(CheckParamErrorException e) {
        return new ResponseEntity<ExceptionResult>(ExceptionResult.error(e.getResultCode(), e.getMsg()), HttpStatus.OK);
    }

    /**
     * token相关出错
     * @param e
     * @return
     */
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ExceptionResult> handlerException(TokenException e) {
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
