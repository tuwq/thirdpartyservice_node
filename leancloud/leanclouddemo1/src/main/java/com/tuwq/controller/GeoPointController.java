package com.tuwq.controller;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.types.AVGeoPoint;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("geoPoint")
public class GeoPointController {

    /**
     * LeanCloud 允许你通过将 AVGeoPoint 关联到 AVObject 的方式存储折射真实世界地理位置的经纬坐标，
     * 这样做可以让你查询包含一个点附近的坐标的对象。常见的使用场景有「查找附近的用户」和「查找附近的地点」。\
     * 要构建一个包含地理位置的对象，首先要构建一个地理位置。
     * 下面的代码构建了一个 AVGeoPoint 并将其纬度（latitude）设为 39.9，经度（longitude）设为 116.4：
     * @return
     */
    @GetMapping("create")
    public String create() {
        AVGeoPoint point = new AVGeoPoint(39.9, 116.4);
        // 现在可以将这个地理位置存储为一个对象的属性：
        // 构建对象
        AVObject todo = new AVObject("Todo");
        todo.put("location", point);
        todo.save();
        return "success";
    }

    /**
     * 给定一些含有地理位置的对象，可以从中找出离某一点最近的几个，或者处于某一范围内的几个。
     * 要执行这样的查询，可以向普通的 AVQuery 添加 whereNear 条件。
     * 下面的代码查找 location 属性值离某一点最近的 Todo 对象：
     * @return
     */
    @GetMapping("select")
    public String select() {
        AVQuery<AVObject> query = new AVQuery<>("Todo");
        AVGeoPoint point = new AVGeoPoint(39.9, 116.4);
        query.whereNear("location", point);
        // 限制为 10 条结果
        query.limit(10);
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(List<AVObject> todos) {
                // todos 是包含满足条件的 Todo 对象的数组
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
        // 像 orderByAscending 和 orderByDescending 这样额外的排序条件会获得比默认的距离排序更高的优先级。
        return "success";
    }

    // 若要限制结果和给定地点之间的距离，可以参考 API 文档中的 whereWithinKilometers、whereWithinMiles 和 whereWithinRadians
    // 若要查询在某一矩形范围内的对象，可以用 whereWithinGeoBox：

    /**
     * GeoPoint 的注意事项
     * 使用地理位置需要注意以下方面：
     * +
     *
     * 每个 AVObject 数据对象中只能有一个 AVGeoPoint 对象的属性。
     * 地理位置的点不能超过规定的范围。纬度的范围应该是在 -90.0 到 90.0 之间，经度的范围应该是在 -180.0 到 180.0 之间。
     * 如果添加的经纬度超出了以上范围，将导致程序错误。
     * @return
     */
    @GetMapping("positionApi")
    public String positionApi() {
        AVQuery<AVObject> query = new AVQuery<>("Todo");
        AVGeoPoint southwest = new AVGeoPoint(30, 115);
        AVGeoPoint northeast = new AVGeoPoint(40, 118);
        query.whereWithinGeoBox("location", southwest, northeast);
        return "success";
    }
}
