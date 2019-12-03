package com.tuwq.controller;

import cn.leancloud.AVException;
import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("external")
public class ExternalServiceController {

    /**
     * 第三方登录
     * LeanCloud 允许应用将用户账户与微信、QQ 等第三方平台关联起来，这样用户就可以直接用第三方账户登录应用。
     * 比如说允许用户使用微信登录，那么你的代码会像这样：
     * @return
     */
    @GetMapping("login")
    public String login() {
        Map<String, Object> authData = new HashMap<String, Object>();
        // 必须
        authData.put("expires_in", 7200);
        authData.put("openid", "OPENID");
        authData.put("access_token", "ACCESS_TOKEN");
        //可选
        authData.put("refresh_token", "REFRESH_TOKEN");
        authData.put("scope", "SCOPE");
        AVUser.loginWithAuthData(authData, "weixin").subscribe(new Observer<AVUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVUser avUser) {
                System.out.println("成功登录");
            }
            public void onError(Throwable throwable) {
                System.out.println("尝试使用第三方账号登录，发生错误。");
            }
            public void onComplete() {}
        });
        /**
         * 云端会验证传入的 authData 是否合法，并且查询是否已经存在与之关联的用户。
         *    如果有的话，则返回 200 OK 状态码，同时附上用户的信息（包括 sessionToken）
         * 如果 authData 没有和任何用户关联，客户端会收到 201 Created 状态码，意味着新用户被创建，
         *   同时附上用户的 objectId、createdAt、sessionToken 和一个自动生成的 username，例如：
         */
        /**
         * {
        *    "username":     "k9mjnl7zq9mjbc7expspsxlls",
        *    "objectId":     "5b029266fb4ffe005d6c7c2e",
        *    "createdAt":    "2018-05-21T09:33:26.406Z",
        *    "updatedAt":    "2018-05-21T09:33:26.575Z",
        *    "sessionToken": "…",
        *    // authData 通常不会返回
        *    // 继续阅读以了解其中原因
        *    "authData": {
        *         // …
         * }
         */
        return "success";
    }

    /**
     * 扩展需求
     * 新用户登录时必须填用户信息
     * 满足需求：一个新用户使用第三方账号授权拿到相关信息后，仍然需要设置账号相关的用户名、手机号、密码等重要信息后，才被允许登录成功。
     *
     * 这时要使用 loginWithauthData 登录接口的 failOnNotExist 参数并将其设置为 ture。服务端会判断是否已存在能匹配上的 authData，否的话，返回 211 错误码和 Could not find user 报错信息。
     * 开发者根据这个 211 错误码，跳转到要求输入用户名、密码、手机号等信息的页面，
     * 实例化一个 AVUser 对象，保存上述补充数据，再次调用 loginWithauthData 接口进行登录，并 不再传入 failOnNotExist 参数。示例代码如下：
     * @return
     */
    @GetMapping("extension")
    public String extension() {
        Map<String, Object> authData = new HashMap<String, Object>();
        authData.put("expires_in", 7200);
        authData.put("openid", "OPENID");
        authData.put("access_token", "ACCESS_TOKEN");
        authData.put("refresh_token", "REFRESH_TOKEN");
        authData.put("scope", "SCOPE");
        Boolean failOnNotExist = true;

        AVUser user = new AVUser();
        user.loginWithAuthData(authData,"weixin",failOnNotExist).subscribe(new Observer<AVUser>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(AVUser avUser) {
                System.out.println("存在匹配的用户，登录成功");
            }
            @Override
            public void onError(Throwable e) {
                AVException avException = new AVException(e);
                int code = avException.getCode();
                if (code == 211){
                    // 跳转到输入用户名、密码、手机号等业务页面
                } else {
                    System.out.println("发生错误:" + e.getMessage());
                }
            }
            @Override
            public void onComplete() {
            }
        });

        // 跳转到输入用户名、密码、手机号等业务页面之后
        AVUser avUser = new AVUser();
        avUser.setUsername("Tom");
        avUser.setMobilePhoneNumber("+8618200008888");
        avUser.loginWithAuthData(authData, "weixin").subscribe(new Observer<AVUser>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(AVUser avUser) {
                System.out.println("登录成功");
            }
            @Override
            public void onError(Throwable e) {
                System.out.println("登录失败：" + e.getMessage());
            }
            @Override
            public void onComplete() {
            }
        });
        return "success";
    }
}
