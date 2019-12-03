package com.tuwq.service;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import cn.leancloud.types.AVNull;
import com.tuwq.base.BaseService;
import com.tuwq.common.ResultCode;
import com.tuwq.config.TheProjectProperties;
import com.tuwq.exception.CheckParamException;
import com.tuwq.model.UserDo;
import com.tuwq.param.registration.RegisterParam;
import com.tuwq.util.MD5Util;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class RegistrationService extends BaseService {

    @Autowired
    private TheProjectProperties theProjectProperties;

    /**
     * 注册用户并发送手机验证码
     *    1. 该手机号码是否已被注册过
     *      (1) 注册过且已被激活   -> 手机号码已被注册 终止
     *      (2) 注册过但未被重新激活 -> 重新发送验证短信 终止
     *      (3) 未注册过,新用户注册,leancloud会自动发送验证短信
     * @param param
     */
    public void registerSmsCode(RegisterParam param) throws InterruptedException {
        AVQuery query = new AVQuery(UserDo.modelName);
        query.whereEqualTo(UserDo.mobilePhoneNumber, param.getPhonePrefix() + param.getPhoneNumber());
        List<AVObject> dbUserList = query.find();
        if (dbUserList.size() > 0) {
            AVObject avObject = dbUserList.get(0);
            boolean mobilePhoneVerified = avObject.getBoolean(UserDo.mobilePhoneVerified);
            if (mobilePhoneVerified) {
                throw new CheckParamException(ResultCode.PARAM_ERROR, "该手机号已注册");
            } else {
                AVUser.requestMobilePhoneVerifyInBackground(param.getPhonePrefix()+param.getPhoneNumber()).blockingSubscribe();
                System.out.println("未激活状态的手机号,重新发送一次验证码");
                return;
            }
        }
        // 注册新用户
        AVUser user = new AVUser();
        user.setUsername(param.getPhoneNumber());
        user.setPassword(MD5Util.encrypt(param.getPhoneNumber()));
        user.setMobilePhoneNumber(param.getPhonePrefix() + param.getPhoneNumber());
        user.signUpInBackground().blockingSubscribe(dbUser->{
            System.out.println("注册成功.会自动发送一份短信到用户");
        },throwable -> {
            throw new CheckParamException(ResultCode.PARAM_ERROR, "该手机号码已被注册使用");
        });
        System.out.println("返回程序结果");
    }

    /**
     *  1. 验证手机验证码是否正确
     *  2. 修改用户名为手机号码
     * @param param
     */
    public void register(RegisterParam param) {
        /*try {
            AVUser.signUpOrLoginByMobilePhoneInBackground(param.getPhonePrefix()+param.getPhoneNumber(),
                    param.getSmsCode()).blockingSubscribe();
            System.out.println("注册或登录成功。验证成功");
        } catch (Exception e) {
            System.out.println("into error");
            throw new CheckParamException(ResultCode.PARAM_ERROR, "手机验证码错误 注册登录失败");
        }
        try {
            AVUser.verifyMobilePhoneInBackground(param.getSmsCode()).blockingSubscribe();
            System.out.println("注册或登录成功。验证成功");
        } catch (Exception e) {
            System.out.println("into error");
            throw new CheckParamException(ResultCode.PARAM_ERROR, "手机验证码错误 验证失败");
        }*/
        AVUser.signUpOrLoginByMobilePhoneInBackground(param.getPhonePrefix()+param.getPhoneNumber(),
                param.getSmsCode()).blockingSubscribe(user -> {
            System.out.println("注册或登录成功。验证成功");
        }, throwable -> {
                    System.out.println("into error");
            throw new CheckParamException(ResultCode.PARAM_ERROR, "手机验证码错误 注册登录失败");
        });

        AVUser.verifyMobilePhoneInBackground(param.getSmsCode()).blockingSubscribe(object -> {
            System.out.println("mobilePhoneVerified 将变为 true");
        }, throwable -> {
            System.out.println("into error");
            throw new CheckParamException(ResultCode.PARAM_ERROR, "手机验证码错误 验证失败");
        });

        System.out.println("返回结果");
    }
}
