package com.tuwq.controller;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("one2many")
public class One2ManyController {

    /**
     * post与commit 持一对多关系
     * @return
     */
    @GetMapping("create")
    public String create() {
        // 创建 post
        AVObject post = new AVObject("Post");
        post.put("title", "饿了……");
        post.put("content", "中午去哪吃呢？");
        // 创建 comment
        AVObject comment = new AVObject("Comment");
        comment.put("content", "当然是肯德基啦！");
        // 将 post 设为 comment 的一个属性值
        comment.put("parent", post);
        // 保存 comment 会同时保存 post
        comment.save();
        // 云端存储时，会将被指向的对象用 Pointer 的形式存起来。你也可以用 objectId 来指向一个对象：
//        AVObject post = AVObject.createWithoutData("Post", "57328ca079bc44005c2472d0");
//        comment.put("post", post);
        return "success";
    }

    /**
     * Pointers 存储
     * 中国的「省份」与「城市」具有典型的一对多的关系。
     * 深圳和广州（城市）都属于广东省（省份），而朝阳区和海淀区（行政区）只能属于北京市（直辖市）。
     * 广东省对应着多个一级行政城市，北京对应着多个行政区。下面我们使用 Pointers 来存储这种一对多的关系。
     * 为了表述方便，后文中提及城市都泛指一级行政市以及直辖市行政区，而省份也包含了北京、上海等直辖市。
     */
    @GetMapping("pointersOne2Many")
    public String pointersOne2Many() {
        AVObject guangZhou = new AVObject("City");// 广州
        guangZhou.put("name", "广州");
        AVObject guangDong = new AVObject("Province");// 广东
        guangDong.put("name", "广东");
        guangZhou.put("dependent", guangDong);// 为广州设置 dependent 属性为广东
        guangZhou.saveInBackground().subscribe(new Observer<AVObject>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(AVObject avObject) {
                // 广州被保存成功
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onComplete() {
            }
        });
        // 广东无需被单独保存，因为在保存广州的时候已经上传到云端。
        // 注意：保存关联对象的同时，被关联的对象也会随之被保存到云端。
        // 要关联一个已经存在于云端的对象，例如将「东莞市」添加至「广东省」，方法如下：
        // 假设 GuangDong 的 objectId 为 56545c5b00b09f857a603632
        guangDong = AVObject.createWithoutData("Province", "56545c5b00b09f857a603632");
        AVObject dongGuan = new AVObject("City");// 东莞
        dongGuan.put("name", "东莞");

        dongGuan.put("dependent", guangDong);// 为东莞设置 dependent 属性为广东
        return "success";
    }

    /**
     * Pointers 查询
     * 假如已知一个城市，想知道它的上一级的省份：
     * @return
     */
    @GetMapping("pointersSelect1")
    public String pointersSelect1() {
        // 假设东莞作为 City 对象存储的时候它的 objectId 是 568e743c00b09aa22162b11f，这个 objectId 可以在控制台查看
        AVObject dongGuan = AVObject.createWithoutData("City", "568e743c00b09aa22162b11f");
        dongGuan.fetchInBackground("dependent").subscribe(new Observer<AVObject>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(AVObject avObject) {
                // 获取广东省
                AVObject province = avObject.getAVObject("dependent");
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onComplete() {
            }
        });
        return "success";
    }

    /**
     * 假如查询结果中包含了城市，并想通过一次查询同时把对应的省份也一并加载到本地：
     * @return
     */
    @GetMapping("pointersSelect2")
    public String pointersSelect2() {
        AVQuery<AVObject> query = new AVQuery<>("City");
        // 查询名字是广州的城市
        query.whereEqualTo("name", "广州");
        // 找出对应城市的省份
        query.include("dependent");
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(List<AVObject> list) {
                // list 的结果为 name 等于广州的城市的集合，当然我们知道现实中只存在一个广州市
                for (AVObject city : list) {
                    // 并不需要网络访问
                    // 获取对应的省份
                    AVObject province = city.getAVObject("dependent");
                }
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onComplete() {
            }
        });
        return "success";
    }

    /**
     * 假如已知一个省份，要找出它的所有下辖城市：
     * @return
     */
    @GetMapping("pointersSelect3")
    public String pointersSelect3() {
        // 假设 GuangDong 的 objectId 为 56545c5b00b09f857a603632
        AVObject guangDong = AVObject.createWithoutData("Province", "56545c5b00b09f857a603632");
        AVQuery<AVObject> query = new AVQuery<>("City");
        query.whereEqualTo("dependent", guangDong);
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(List<AVObject> list) {
                for (AVObject city : list) {
                    // list 的结果为广东省下辖的所有城市
                }
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onComplete() {
            }
        });
        return "success";
    }
}
