package com.tuwq.controller;

import com.tuwq.param.UserParam;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(description = "用户相关接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @ApiOperation(value="获取用户列表", notes="获取用户列表")
    @GetMapping("/getUserList")
    public List<UserParam> getUserList() {
        List<UserParam> r = new ArrayList<UserParam>();
        return r;
    }

    @ApiOperation(value="注册", notes="用户注册接口")
    @PostMapping("/register")
    public Integer register(@RequestBody @ApiParam(name = "注册参数", value = "", required = true) UserParam param) {
        return 1;
    }

    @ApiOperation(value="获取用户信息", notes="根据id获取用户信息")
    @GetMapping("/info")
    @ApiImplicitParam(name ="id",value = "学生id",paramType = "path",required = true,dataType = "String")
    @ApiResponses({
            @ApiResponse(code=401,message = "请求参数格式错误"),
            @ApiResponse(code=402,message="请求参数不正确")
    })
    public String info(@RequestParam("id") String id) {
        return "info";
    }
}
