package com.tuwq.controller;

import cn.leancloud.AVException;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVSaveOption;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("cloud")
public class SimpleController {

    /**
     * 创建对象并添加数据至云端
     * @return
     */
    @GetMapping("put")
    public String put() {
        // 构建对象
        AVObject todo = new AVObject("Todo");
        // 为属性赋值
        todo.put("title", "马拉松报名");
        todo.put("priority", 2);
        // 将对象保存到云端
        todo.saveInBackground().subscribe(new Observer<AVObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVObject todo) {
                // 成功保存之后，执行其他逻辑
                System.out.println("保存成功。objectId：" + todo.getObjectId());
            }
            public void onError(Throwable throwable) {
                // 异常处理
            }
            public void onComplete() {}
        });
        return "success";
    }

    /**
     * 通过objectId获取云端对象
     * @return
     */
    @GetMapping("query")
    public String query() {
        AVQuery<AVObject> query = new AVQuery<>("Todo");
        query.getInBackground("5de4788f21b47e006c9d3118").subscribe(new Observer<AVObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVObject todo) {
                // todo 就是 objectId 为 5de4788f21b47e006c9d3118 的 Todo 实例
                // 如果你试图获取一个不存在的属性，SDK 不会报错，而是会返回 null
                String title    = todo.getString("title");
                int priority    = todo.getInt("priority");

                // 获取内置属性
                String objectId = todo.getObjectId();
                Date updatedAt  = todo.getUpdatedAt();
                Date createdAt  = todo.getCreatedAt();
                System.out.println("title: " + title + " priority: " + priority);
                System.out.println("objectId: " + objectId);
                System.out.println("updatedAt: " + updatedAt + " createAt: " + createdAt);
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
        return "success";
    }

    /**
     * 更新对象
     * 要更新一个对象，只需指定需要更新的属性名和属性值，然后调用 saveInBackground 方法。例如：
     * LeanCloud 会自动识别需要更新的属性并将对应的数据发往云端，未更新的属性会保持原样。
     * @return
     */
    @GetMapping("update")
    public String update() {
        AVObject todo = AVObject.createWithoutData("Todo", "5de4788f21b47e006c9d3118");
        todo.put("content", "这周周会改到周三下午三点。");
        todo.save(); // 同步更新
        return "success";
    }

    /**
     * 有条件更新对象
     *  通过传入 query 选项，可以按照指定条件去更新对象——当条件满足时，执行更新；条件不满足时，不执行更新并返回 305 错误。
     *  例如，用户的账户表 Account 有一个余额字段 balance，同时有多个请求要修改该字段值。
     *  为避免余额出现负值，只有当金额小于或等于余额的时候才能接受请求：
     *  query 选项只对已存在的对象有效，不适用于尚未存入云端的对象。
     * @return
     */
    @GetMapping("update/option")
    public String updateOption() {
        AVObject account = AVObject.createWithoutData("Account", "5de4788f21b47e006c9d3118");
        // 对 balance 原子减少 100
        final int amount = -100;
        account.increment("balance", amount);
        // 设置条件
        AVSaveOption option = new AVSaveOption();
        option.query(new AVQuery<>("Account").whereGreaterThanOrEqualTo("balance", -amount)); // 比100大或等于
        // 操作结束后，返回最新数据。
        // 如果是新对象，则所有属性都会被返回，
        // 否则只有更新的属性会被返回。
        option.setFetchWhenSave(true);
        account.saveInBackground(option).subscribe(new Observer<AVObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVObject account) {
                System.out.println("当前余额为：" + account.get("balance"));
            }
            public void onError(Throwable throwable) {
                System.out.println("余额不足，操作失败！");
            }
            public void onComplete() {}
        });
        return "success";
    }

    /**
     * 更新数组
     * 更新数组也是原子操作。使用以下方法可以方便地维护数组类型的数据：
     * add() 将指定对象附加到数组末尾。
     * addUnique() 如果数组中不包含指定对象，则将该对象加入数组。对象的插入位置是随机的。
     * removeAll() 从数组字段中删除指定对象的所有实例。
     * @return
     */
    @GetMapping("/updateArray")
    public String updateArray() {
        // 例如，Todo 用一个 alarms 属性保存所有闹钟的时间。下面的代码将多个时间加入这个属性：
        Date alarm1 = getDateWithDateString("2018-04-30 07:10:00");
        Date alarm2 = getDateWithDateString("2018-04-30 07:20:00");
        Date alarm3 = getDateWithDateString("2018-04-30 07:30:00");

        AVObject todo = new AVObject("Todo");
        todo.addAllUnique("alarms", Arrays.asList(alarm1, alarm2, alarm3));
        todo.save();
        return "success";
    }

    @GetMapping("remove")
    public String remove() {
        AVObject todo = AVObject.createWithoutData("Todo", "582570f38ac247004f39c24b");
        todo.delete();
        return "success";
    }

    // 虽然上述方法可以在一次请求中包含多个操作，
    // 每一个分别的保存或同步操作在计费时依然会被算作一次请求，而所有的删除操作则会被合并为一次请求。
    @GetMapping("batch")
    public String batch() {
        AVQuery<AVObject> query = new AVQuery<>("Todo");
        // 获取所有
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(List<AVObject> todos) {
                // 获取需要更新的 todo
                for (AVObject todo : todos) {
                    // 更新属性值
                    todo.put("isComplete", true);
                }
                // 批量更新
                try {
                    AVObject.saveAll(todos);
                } catch (AVException e) {
                    e.printStackTrace();
                }
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
        return "success";
    }

    // fetchInBackground 当云端数据发生更改时，你可以调用 fetchInBackground 方法来刷新对象，使之与云端数据同步：
    @GetMapping("fetchInBackground")
    public String fetchInBackground() {
        AVObject todo = AVObject.createWithoutData("Todo", "5de4788f21b47e006c9d3118");
        todo.fetchInBackground().subscribe(new Observer<AVObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVObject todo) {
                // todo 已刷新
                System.out.println(todo);
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
        // 刷新操作会强行使用云端的属性值覆盖本地的属性。因此如果本地有属性修改，fetchInBackground 操作会丢弃这些修改。
        // 为避免这种情况，你可以在刷新时指定 需要刷新的属性，这样只有指定的属性会被刷新（包括内置属性 objectId、createdAt 和 updatedAt），其他属性不受影响
        /*AVObject todo = AVObject.createWithoutData("Todo", "5de4788f21b47e006c9d3118");
        String keys = "priority, location";
        todo.fetchInBackground(keys).subscribe(new Observable<AVObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVObject todo) {
                // 只有 priority 和 location 会被获取和刷新
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });*/
        return "success";
    }

    /**
     * 基本的put操作
     * @return
     */
    @GetMapping("test/put1")
    public String put1() {
        AVObject testObject = new AVObject("TestObject");
        testObject.put("words", "Hello world!");
        testObject.saveInBackground().blockingSubscribe();
        return "success";
    }

    /**
     * subscribe形式的put
     * 如果 class 不存在，它将自动创建。
     * leancloud会自动创建并添加不存在的字段
     * @return
     */
    @GetMapping("test/put2")
    public String put2() {
        final AVObject todo = new AVObject("Todo");
        todo.put("title", "工程师周会"); // 只要添加这一行代码，服务端就会自动添加这个字段
        todo.put("content", "每周工程师会议，周一下午2点");
        todo.put("location", "会议室");
        todo.saveInBackground().subscribe(new Observer<AVObject>() {
            public void onSubscribe(Disposable disposable) {
            }
            public void onNext(AVObject avObject) {
                System.out.println("put field finished.");
            }
            public void onError(Throwable throwable) {
            }
            public void onComplete() {
            }
        });
        return "success";
    }

    Date getDateWithDateString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
