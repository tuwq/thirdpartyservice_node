package com.tuwq.controller;

import cn.leancloud.AVUser;
import com.tuwq.base.BaseController;
import com.tuwq.common.JsonResult;
import com.tuwq.param.registration.RegisterParam;
import com.tuwq.service.RegistrationService;
import com.tuwq.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("registration")
public class RegistrationController extends BaseController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("registerSmsCode")
    public JsonResult<Void> registerSmsCode(@RequestBody RegisterParam param) throws InterruptedException {
        ValidatorUtil.check(param);
        registrationService.registerSmsCode(param);
        return this.success();
    }

    @PostMapping("register")
    public JsonResult<Void> register(@RequestBody RegisterParam param) {
        ValidatorUtil.check(param);
        registrationService.register(param);
        return this.success();
    }

    @PostMapping("test")
    public JsonResult<Void> test() {
        AVUser.requestLoginSmsCodeInBackground("+8613077959253").blockingSubscribe();
        return this.success();
    }
}
