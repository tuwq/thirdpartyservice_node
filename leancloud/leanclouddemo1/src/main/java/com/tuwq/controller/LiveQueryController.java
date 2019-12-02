package com.tuwq.controller;

import cn.leancloud.AVException;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import cn.leancloud.livequery.AVLiveQuery;
import cn.leancloud.livequery.AVLiveQueryEventHandler;
import cn.leancloud.livequery.AVLiveQuerySubscribeCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/liveQuery")
public class LiveQueryController {

    private String SUCCESS = "success";

    public String create() throws InterruptedException {
        AVQuery<AVObject> query = new AVQuery<>("Todo");
        AVLiveQuery liveQuery = AVLiveQuery.initWithQuery(query);
        liveQuery.subscribeInBackground(new AVLiveQuerySubscribeCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    // 订阅成功
                }
            }
        });
        // 订阅成功后，就可以接收到和 AVObject 相关的更新了。
        liveQuery.setEventHandler(new AVLiveQueryEventHandler() {
            @Override
            public void onObjectCreated(AVObject newTodo) {
                System.out.println(newTodo.getString("title")); // 更新作品集
            }

            @Override
            public void onObjectUpdated(AVObject avObject, List<String> updateKeyList) {
                System.out.println("对象被更新。");
            }
            @Override
            public void onObjectEnter(AVObject object, List<String> updatedKeys) {
                // 注意区分 create 和 enter 的不同行为。如果一个对象已经存在，在更新之前不符合查询条件，而在更新之后符合查询条件，
                // 那么 enter 事件会被触发。如果一个对象原本不存在，后来被构建了出来，那么 create 事件会被触发。
                System.out.println("对象进入。");
            }
            @Override
            public void onObjectLeave(AVObject object, List<String> updatedKeys) {
                System.out.println("对象离开。");
            }
            @Override
            public void onObjectDeleted(String object) {
                System.out.println("对象被删除。");
            }
            @Override
            public void onUserLogin(AVUser user) {
                System.out.println("用户登录。");
            }
        });
        Thread.sleep(60000);
        liveQuery.unsubscribeInBackground(new AVLiveQuerySubscribeCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    // 成功取消订阅
                }
            }
        });
        return SUCCESS;
    }


}
