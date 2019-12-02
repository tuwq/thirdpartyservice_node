package com.tuwq.controller;

import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import cn.leancloud.types.AVNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 注册
     * 用户第一次打开应用的时候，可以让用户注册一个账户。下面的代码展示了一个典型的使用用户名和密码注册的流程：
     * 新建 AVUser 的操作应使用 signUpInBackground 而不是 saveInBackground，但以后的更新操作就可以用 saveInBackground 了。
     * 如果收到 202 错误码，意味着 _User 表里已经存在使用同一 username 的账号，此时应提示用户换一个用户名。
     * 除此之外，每个用户的 email 和 mobilePhoneNumber 也需要保持唯一性，否则会收到 203 或 214 错误。
     * 可以考虑在注册时把用户的 username 设为与 email 相同，这样用户可以直接 用邮箱重置密码。
     *
     * 采用「用户名 + 密码」注册时需要注意：密码是以明文方式通过 HTTPS 加密传输给云端，云端会以密文存储密码（云端对密码的长度、复杂度不作限制），
     * 并且我们的加密算法是无法通过所谓「彩虹表撞库」获取的，这一点请开发者放心。
     * 换言之，用户的密码只可能用户本人知道，开发者不论是通过控制台还是 API 都是无法获取。
     * 另外我们需要强调 在客户端，应用切勿再次对密码加密，这会导致 重置密码 等功能失效。
     * @return
     */
    @GetMapping("register")
    public String register() {
        // 创建实例
        AVUser user = new AVUser();
        // 等同于 user.put("username", "Tom")
        user.setUsername("Tom2");
        user.setPassword("cat!@#123");
        // 可选
        user.setEmail("tom2@leancloud.rocks");
        user.setMobilePhoneNumber("+8618200007777");
        // 设置其他属性的方法跟 AVObject 一样
        user.put("gender", "secret");
        user.signUp();
        System.out.println("objectId: " + user.getObjectId());
//        user.signUpInBackground().subscribe(new Observer<AVUser>() {
//            public void onSubscribe(Disposable disposable) {}
//            public void onNext(AVUser user) {
//                // 注册成功
//                System.out.println("注册成功。objectId：" + user.getObjectId());
//            }
//            public void onError(Throwable throwable) {
//                // 注册失败（通常是因为用户名已被使用）
//            }
//            public void onComplete() {}
//        });
        return "success";
    }

    /**
     * 对于移动应用来说，允许用户以手机号注册是个很常见的需求。
     * 实现该功能大致分两步，第一步是让用户提供手机号，点击「获取验证码」按钮后，该号码会收到一个六位数的验证码：
     * 用户填入验证码后，用下面的方法完成注册：
     * username 将与 mobilePhoneNumber 相同，password 会由云端随机生成。
     *
     * 手机号格式
     * AVUser 接受的手机号以 + 和国家代码开头，后面紧跟着剩余的部分。
     * 手机号中不应含有任何划线、空格等非数字字符。
     * 例如，+19490008888 是一个合法的美国或加拿大手机号（1 是国家代码），+8618200008888 是一个合法的中国手机号（86 是国家代码）。
     * 请参阅短信 SMS 服务使用指南中的 服务覆盖区域和价格 以了解 LeanCloud 支持的国家和地区。
     * @return
     */
    @GetMapping("registerByPhone")
    public String registerByPhone() {
        AVUser.requestLoginSmsCodeInBackground("+8613077959253").blockingSubscribe();
        AVUser.signUpOrLoginByMobilePhoneInBackground("+8613077959253", "123456").subscribe(new Observer<AVUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVUser user) {
                // 注册成功
                System.out.println("注册成功。objectId：" + user.getObjectId());
            }
            public void onError(Throwable throwable) {
                // 验证码不正确
            }
            public void onComplete() {}
        });
        return "success";
    }

    @GetMapping("login")
    public String login() {
        AVUser.logIn("Tom", "cat!@#123").subscribe(new Observer<AVUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVUser user) {
                // 登录成功
            }
            public void onError(Throwable throwable) {
                // 登录失败（可能是密码错误）
            }
            public void onComplete() {}
        });
        return "success";
    }

    /**
     * 下面的代码用邮箱和密码登录一个账户：
     * @return
     */
    @GetMapping("loginEmail")
    public String loginEmail() {
        AVUser.loginByEmail("tom@leancloud.rocks", "cat!@#123").subscribe(new Observer<AVUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVUser user) {
                // 登录成功
            }
            public void onError(Throwable throwable) {
                // 登录失败（可能是密码错误）
            }
            public void onComplete() {}
        });
        return "success";
    }

    /**
     * 手机号登录
     * 如果应用允许用户以手机号注册，那么也可以让用户以手机号配合密码或短信验证码登录。下面的代码用手机号和密码登录一个账户：
     * 默认情况下，LeanCloud 允许所有关联了手机号的用户直接以手机号登录，无论手机号是否 通过验证。
     * 为了让应用更加安全，你可以选择只允许验证过手机号的用户通过手机号登录。可以在 控制台 > 存储 > 设置 里面开启该功能。
     * @return
     */
    @GetMapping("loginPhone")
    public String loginPhone() {
        AVUser.loginByMobilePhoneNumber("+8618200008888", "cat!@#123").subscribe(new Observer<AVUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVUser user) {
                // 登录成功
            }
            public void onError(Throwable throwable) {
                // 登录失败（可能是密码错误）
            }
            public void onComplete() {}
        });
        /**
         * 除此之外，还可以让用户通过短信验证码登录，适用于用户忘记密码且不愿重置密码的情况。和 通过手机号注册 的步骤类似，
         * 首先让用户填写与账户关联的手机号码，然后在用户点击「获取验证码」后调用下面的方法：
         */
        AVUser.requestLoginSmsCodeInBackground("+8618200008888").blockingSubscribe();
        // 用户填写收到的验证码后，用下面的方法完成登录：
        AVUser.signUpOrLoginByMobilePhoneInBackground("+8618200008888", "123456").subscribe(new Observer<AVUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVUser user) {
                // 登录成功
            }
            public void onError(Throwable throwable) {
                // 验证码不正确
            }
            public void onComplete() {}
        });
        // 测试手机号和固定验证码
        /**
         * 在开发过程中，可能会因测试目的而需要频繁地用手机号注册登录，然而运营商的发送频率限制往往会导致测试过程耗费较多的时间。
         * 为了解决这个问题，可以在 控制台 > 消息 > 短信 > 设置 里面设置一个测试手机号，而云端会为该号码生成一个固定验证码。以后进行登录操作时，只要使用的是这个号码，云端就会直接放行，无需经过运营商网络。
         * 测试手机号还可用于将 iOS 应用提交到 App Store 进行审核的场景，因为审核人员可能因没有有效的手机号码而无法登录应用来进行评估审核。如果不提供一个测试手机号，应用有可能被拒绝。
         * 可参阅 短信 SMS 服务使用指南 来了解更多有关短信发送和接收的限制。
         */
        // 单设备登录
        /**
         * 某些场景下需要确保用户的账户在同一时间只在一台设备上登录，也就是说当用户在一台设备上登录后，其他设备上的会话全部失效。可以按照以下方案来实现：
         * 新建一个专门用于记录用户登录信息和当前设备信息的 class。
         * 每当用户在新设备上登录时，将该 class 中该用户对应的设备更新为该设备。
         * 在另一台设备上打开客户端时，检查该设备是否与云端保存的一致。若不一致，则将用户 登出。
         */
        // 账户锁定
        /**
         * 输入错误的密码或验证码会导致用户登录失败。如果在 15 分钟内，同一个用户登录失败的次数大于 6 次，该用户账户即被云端暂时锁定，此时云端会返回错误码 { "code": 1, "error": "You have exceeded the maximum number of login attempts, please try again later, or consider resetting your password." }，
         * 开发者可在客户端进行必要提示。
         * 锁定将在最后一次错误登录的 15 分钟之后由云端自动解除，开发者无法通过 SDK 或 REST API 进行干预。
         * 在锁定期间，即使用户输入了正确的验证信息也不允许登录。这个限制在 SDK 和云引擎中都有效。
         */
        // 验证邮箱
        /**
         * 可以通过要求用户在登录或使用特定功能之前验证邮箱的方式防止恶意注册。
         * 默认情况下，当用户注册或变更邮箱后，emailVerified 会被设为 false。在应用的 控制台 > 存储 > 设置 中，可以开启 从客户端注册邮箱或者更新邮箱时，发送验证邮件 选项，
         * 这样当用户注册或变更邮箱时，会收到一封含有验证链接的邮件。在同一设置页面还可找到阻止未验证邮箱的用户登录的选项。
         */
        // 如果用户忘记点击链接并且在未来某一时刻需要进行验证，可以用下面的代码发送一封新的邮件：
        AVUser.requestEmailVerifyInBackground("tom@leancloud.rocks").blockingSubscribe();
        /**
         * 用户点击邮件内的链接后，emailVerified 会变为 true。如果用户的 email 属性为空，则该属性永远不会为 true。
         */
        return "success";
    }

    @GetMapping("mobilePhoneVerify")
    public String mobilePhoneVerify() {
        /**
         * 和 验证邮箱 类似，应用还可以要求用户在登录或使用特定功能之前验证手机号。
         * 默认情况下，当用户注册或变更手机号后，mobilePhoneVerified 会被设为 false。在应用的 控制台 > 存储 > 设置 中，
         * 可以开启 从客户端注册或更新手机号时，向注册手机号码发送验证短信 选项，
         * 这样当用户注册或变更手机号时，会收到一条含有验证码的短信。
         * 在同一设置页面还可找到阻止未验证手机号的用户登录的选项。
         */
        // 可以随时用下面的代码发送一条新的验证码：
        AVUser.requestMobilePhoneVerifyInBackground("+8618200008888").blockingSubscribe();
        // 用户填写验证码后，调用下面的方法来完成验证。mobilePhoneVerified 将变为 true：
        AVUser.verifyMobilePhoneInBackground("123456").subscribe(new Observer<AVNull>() {
            public void onSubscribe(Disposable disposable) {

            }
            public void onNext(AVNull avNull) {
                // mobilePhoneVerified 将变为 true
            }
            public void onError(Throwable throwable) {
                // 验证码不正确
            }
            public void onComplete() {}
        });
        return "success";
    }

    @GetMapping("currentUser")
    public String currentUser() {
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            // 跳到首页
        } else {
            // 显示注册或登录页面
        }
        // 会话信息会长期有效，直到用户主动登出：
        AVUser.logOut();
        // currentUser 变为 null
        // AVUser currentUser = AVUser.getCurrentUser();
        return "success";
    }

    /**
     * 设置当前用户
     * 用户登录后，云端会返回一个 session token 给客户端，它会由 SDK 缓存起来并用于日后同一 AVUser 的鉴权请求。
     * session token 会被包含在每个客户端发起的 HTTP 请求的 header 里面，这样云端就知道是哪个 AVUser 发起的请求了。
     * 以下是一些应用可能需要用到 session token 的场景：
     *  应用根据以前缓存的 session token 登录（可以用 AVUser.getCurrentUser().getSessionToken() 获取到当前用户的 session token）。
     *  应用内的某个 WebView 需要知道当前登录的用户。
     *  在服务端登录后，返回 session token 给客户端，客户端根据返回的 session token 登录。
     * @return
     */
    @GetMapping("setCurrentUser")
    public String setCurrentUser() {
        // 下面的代码使用 session token 登录一个用户（云端会验证 session token 是否有效）：
        AVUser.becomeWithSessionTokenInBackground("anmlwi96s381m6ca7o7266pzf").subscribe(new Observer<AVUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVUser user) {
                // 登录成功
            }
            public void onError(Throwable throwable) {
                // session token 无效
            }
            public void onComplete() {}
        });
        return "success";
    }

    /**
     * 重置密码
     * 我们都知道，应用一旦加入账户密码系统，那么肯定会有用户忘记密码的情况发生。对于这种情况，我们为用户提供了多种重置密码的方法。
     * 邮箱重置密码的流程如下：
     *      用户输入注册的电子邮箱，请求重置密码；
     *      LeanCloud 向该邮箱发送一封包含重置密码的特殊链接的电子邮件；
     *      用户点击重置密码链接后，一个特殊的页面会打开，让他们输入新密码；
     *      用户的密码已被重置为新输入的密码。
     * @return
     */
    @GetMapping("resetPassword")
    public String resetPassword() {
        // 首先让用户填写注册账户时使用的邮箱，然后调用下面的方法：
        AVUser.requestPasswordResetInBackground("tom@leancloud.rocks").blockingSubscribe();
        // 上面的代码会查询 _User 表中是否有对象的 email 属性与前面提供的邮箱匹配。如果有的话，则向该邮箱发送一封密码重置邮件。
        // 之前提到过，应用可以让 username 与 email 保持一致，也可以单独收集用户的邮箱并将其存为 email。
        // 密码重置邮件的内容可在应用的 控制台 > 设置 > 邮件模版 中自定义。更多关于自定义邮件模板和验证链接的内容，请参考 自定义应用内用户重设密码和邮箱验证页面。
        return "success";
    }

    /**
     * 除此之外，还可以用手机号重置密码：
     *      用户输入注册的手机号，请求重置密码；
     *      LeanCloud 向该号码发送一条包含验证码的短信；
     *      用户输入验证码和新密码。
     * @return
     */
    @GetMapping("resetPasswordByPhone")
    public String resetPasswordByPhone() {
        // 下面的代码向用户发送含有验证码的短信：
        AVUser.requestPasswordResetBySmsCodeInBackground("+8618200008888").blockingSubscribe();
        // 上面的代码会查询 _User 表中是否有对象的 mobilePhoneNumber 属性与前面提供的手机号匹配。
        // 如果有的话，则向该号码发送验证码短信。
        // 可以在 控制台 > 存储 > 设置 中设置只有在 mobilePhoneVerified 为 true 的情况下才能用手机号重置密码。
        // 用户输入验证码和新密码后，用下面的代码完成密码重置：
        AVUser.resetPasswordBySmsCodeInBackground("123456", "cat!@#123").subscribe(new Observer<AVNull>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVNull aVNull) {
                // 密码重置成功
            }
            public void onError(Throwable throwable) {
                // 验证码不正确
            }
            public void onComplete() {}
        });
        return "success";
    }

    /**
     * 用户对象的安全
     * AVUser 类自带安全保障，只有通过 logIn 或者 signUpInBackground 这种经过鉴权的方法获取到的 AVUser 才能进行保存或删除相关的操作，保证每个用户只能修改自己的数据。
     * 这样设计是因为 AVUser 中存储的大多数数据都比较敏感，包括手机号、社交网络账号等等。为了用户的隐私安全，即使是应用的开发者也应避免直接接触这些数据。
     * 下面的代码展现了这种安全措施：
     * 通过 AVUser.getCurrentUser() 获取的 AVUser 总是经过鉴权的。
     * 要查看一个 AVUser 是否经过鉴权，可以调用 isAuthenticated 方法。通过经过鉴权的方法获取到的 AVUser 无需进行该检查。
     * 注意，用户的密码只能在注册的时候进行设置，日后如需修改，只能通过 重置密码 的方式进行。
     * 密码不会被缓存在本地。如果尝试直接获取已登录用户的密码，会得到 null
     * @return
     */
    public String securityUser() {
        AVUser.logIn("Tom", "cat!@#123").subscribe(new Observer<AVUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVUser user) {
                // 试图修改用户名
                user.put("username", "Jerry");
                // 密码已被加密，这样做会获取到空字符串
                String password = user.getString("password");
                // 可以执行，因为用户已鉴权
                user.save();
                // 绕过鉴权直接获取用户
                AVQuery<AVUser> query = new AVQuery<>("_User");
                query.getInBackground(user.getObjectId()).subscribe(new Observer<AVUser>() {
                    public void onSubscribe(Disposable disposable) {}
                    public void onNext(AVUser unauthenticatedUser) {
                        unauthenticatedUser.put("username", "Toodle");
                        // 会出错，因为用户未鉴权
                        unauthenticatedUser.save();
                    }
                    public void onError(Throwable throwable) {}
                    public void onComplete() {}
                });
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
        return "success";
    }

    /**
     * 其他对象的安全
     * 对于给定的一个对象，可以指定哪些用户有权限读取或修改它。
     * 为实现该功能，每个对象都有一个由 AVACL 对象组成的访问控制表。
     * 请参阅 ACL 权限管理开发指南。
     * @return
     */
    public String otherSecurity() {
        return "success";
    }
    

}
