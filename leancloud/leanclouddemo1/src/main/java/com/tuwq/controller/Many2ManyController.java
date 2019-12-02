package com.tuwq.controller;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("many2many")
public class Many2ManyController {

    @GetMapping("create")
    public String create() {
        AVObject studentTom = new AVObject("Student");// 学生 Tom
        studentTom.put("name", "Tom");

        AVObject courseLinearAlgebra = new AVObject("Course");
        courseLinearAlgebra.put("name", "线性代数");

        AVObject studentCourseMapTom = new AVObject("StudentCourseMap");// 选课表对象

        // 设置关联
        studentCourseMapTom.put("student", studentTom);
        studentCourseMapTom.put("course", courseLinearAlgebra);

        // 设置学习周期
        studentCourseMapTom.put("duration", Arrays.asList("2016-02-19", "2016-04-21"));
        // 获取操作平台
        studentCourseMapTom.put("platform", "iOS");

        // 保存选课表对象
        studentCourseMapTom.saveInBackground();
        return "success";
    }

    /**
     * 查询选修了某一课程的所有学生：
     */
    @GetMapping("select1")
    public String select1() {
        // 微积分课程
        AVObject courseCalculus = AVObject.createWithoutData("Course", "562da3fdddb2084a8a576d49");
        // 构建 StudentCourseMap 的查询
        AVQuery<AVObject> query = new AVQuery<>("StudentCourseMap");
        // 查询所有选择了线性代数的学生
        query.whereEqualTo("course", courseCalculus);
        // 执行查询
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(List<AVObject> list) {
                // list 是所有 course 等于线性代数的选课对象
                // 然后遍历过程中可以访问每一个选课对象的 student,course,duration,platform 等属性
                for (AVObject studentCourseMap : list) {
                    AVObject student = studentCourseMap.getAVObject("student");
                    AVObject course = studentCourseMap.getAVObject("course");
                    ArrayList duration = (ArrayList) studentCourseMap.getList("duration");
                    String platform = studentCourseMap.getString("platform");
                }
                // 同样我们也可以很简单地查询某一个学生选修的所有课程，只需将上述代码变换查询条件即可：
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
