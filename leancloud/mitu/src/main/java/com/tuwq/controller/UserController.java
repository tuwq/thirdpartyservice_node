package com.tuwq.controller;

import com.tuwq.base.BaseController;
import com.tuwq.common.JsonResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController extends BaseController {

    @PutMapping("put")
    public JsonResult<Void> put() {
        return this.success();
    }
}
