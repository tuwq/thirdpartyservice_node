package com.xcyy.controller;

import com.xcyy.common.JsonResult;
import com.xcyy.exception.CheckParamErrorException;
import com.xcyy.model.Users;
import com.xcyy.param.TestParam;
import com.xcyy.properties.TheProjectProperties;
import com.xcyy.redis.RedisOperator;
import com.xcyy.service.TestService;
import com.xcyy.util.*;
import com.xcyy.vo.TestVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Api(description = "测试接口")
@RestController
@RequestMapping("/test")
public class TestController extends BaseController {

    @Autowired
    private TestService testService;
    @Autowired
    private RedisOperator redis;
    @Autowired
    private TheProjectProperties theProjectProperties;

    @ApiOperation(value = "简单测试接口", notes = "返回ok")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="msg", value = "消息信息", paramType = "path", required = false, dataType = "String")})
    @GetMapping("/string")
    public JsonResult<String> string(@RequestParam("msg") String msg) {
        System.out.println(msg);
        return JsonResult.<String>success("ok");
    }

    @ApiOperation(value = "获取数据", notes = "返回数据列表")
    @GetMapping("/all")
   public JsonResult<List<Users>> allUser() {
        System.out.println(ThreadUtil.getCurrentRequest());
        System.out.println(ThreadUtil.getCurrentResponse());
        System.out.println(ApplicationContextUtil.popBean(TestService.class)==testService);
        redis.set("aaa", "111");
        System.out.println(redis.get("aaa"));
        redis.del("aaa");
        System.out.println(redis.get("aaa"));
        return testService.allUser();
    }

    @ApiOperation(value = "测试返回vo对象json数据", notes = "返回数据")
    @GetMapping("/build")
    public JsonResult<TestVo> build() {
        Users user = Users.builder().name("nickname").id(1).age(20).build();
        return JsonResult.<TestVo>success(DtoUtil.adapt(new TestVo(), user));
    }

    @ApiOperation(value = "测试直接返回json数据对象", notes = "返回数据")
    @GetMapping("/json")
    public JsonResult<Users> json() {
        Users build = Users.builder().name("nickname").id(1).age(22).build();
        String json = JsonUtils.objectToJson(build);
        Users user = JsonUtils.jsonToPojo(json, Users.class);
        return JsonResult.<Users>success(user);
    }

    @ApiOperation(value = "测试接收异常错误信息", notes = "抛出异常")
    @GetMapping("/exception")
    public JsonResult<Void> exception() {
        System.out.println(TimeAgoUtils.format(new Date()));
        System.out.println(MD5Util.encrypt("aaa"));
        throw new CheckParamErrorException("参数错误");
    }

    @ApiOperation(value = "测试手机格式校验", notes = "无返回")
    @GetMapping("/paramError")
    public JsonResult<Void> paramError(@RequestBody TestParam param) {
        ValidatorUtil.check(param);
        return this.success();
    }

    @ApiOperation(value = "测试spring自定义配置获取", notes = "无返回")
    @GetMapping("/config")
    public JsonResult<Void> config() {
        System.out.println(theProjectProperties.getTest().getName());
        return this.success();
    }
}
