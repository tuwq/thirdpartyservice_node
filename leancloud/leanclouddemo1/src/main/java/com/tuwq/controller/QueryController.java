package com.tuwq.controller;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
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
@RequestMapping("query")
public class QueryController {
    /**
     * 执行一次基础查询通常包括这些步骤：
     * 构建 AVQuery；
     * 向其添加查询条件；
     * 执行查询并获取包含满足条件的对象的数组。
     * 下面的代码获取所有 lastName 为 Smith 的 Student：
     * @return
     */
    @GetMapping("query1")
    public String query1() {
        AVQuery<AVObject> query = new AVQuery<>("Student");
        query.whereEqualTo("lastName", "Smith");
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }
            @Override
            public void onNext(List<AVObject> students) {
                // students 是包含满足条件的 Student 对象的数组
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

    @GetMapping("optionQuery2")
    public String optionQuery2() {
        AVQuery<AVObject> query = new AVQuery<>("Student");
        query.whereNotEqualTo("firstName", "Jack");
        // 限制 age < 18
        query.whereLessThan("age", 18);
        // 限制 age <= 18
        query.whereLessThanOrEqualTo("age", 18);
        // 限制 age > 18
        query.whereGreaterThan("age", 18);
        // 限制 age >= 18
        query.whereGreaterThanOrEqualTo("age", 18);
        // 可以在同一个查询中设置多个条件，这样可以获取满足所有条件的结果。可以理解为所有的条件是 AND 的关系：
        query.whereEqualTo("firstName", "Jack");
        query.whereGreaterThan("age", 18);
        // 可以通过指定 limit 限制返回结果的数量（默认为 100）：
        // 最多获取 10 条结果
        query.limit(10);
        // 可以通过设置 skip 来跳过一定数量的结果：
        // 跳过前 20 条结果
        query.skip(20);
        // 把 skip 和 limit 结合起来，就能实现翻页功能：
        query = new AVQuery<>("Todo");
        query.whereEqualTo("priority", 2);
        query.limit(10);
        query.skip(20);
        // 需要注意的是，skip 的值越高，查询所需的时间就越长。
        // 作为替代方案，可以通过设置 createdAt 或 updatedAt 的范围来实现更高效的翻页，因为它们都自带索引。
        return "success";
    }

    @GetMapping("query3")
    public String query3() {
        // 如果只需要一条结果，可以直接用 getFirst：
        AVQuery<AVObject> query = new AVQuery<>("Todo");
        query.whereEqualTo("priority", 2);
        query.getFirstInBackground().subscribe(new Observer<AVObject>() {
            public void onSubscribe(Disposable disposable) {}

            @Override
            public void onNext(AVObject avObject) {
                // todo 是第一个满足条件的 Todo 对象
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
        return "success";
    }

    /**
     * 字符串查询
     * 可以用 whereStartsWith 来查找某一属性值以特定字符串开头的对象。和 SQL 中的 LIKE 一样，你可以利用索引带来的优势：
     * @return
     */
    @GetMapping("strSelect")
    public String strSelect() {
        AVQuery<AVObject> query = new AVQuery<>("Todo");
        // 相当于 SQL 中的 title LIKE 'lunch%'
        query.whereStartsWith("title", "lunch");
        // 可以用 whereContains 来查找某一属性值包含特定字符串的对象：
        query = new AVQuery<>("Todo");
        // 相当于 SQL 中的 title LIKE '%lunch%'
        query.whereContains("title", "lunch");
        // 和 whereStartsWith 不同，whereContains 无法利用索引，因此不建议用于大型数据集。
        // 注意 whereStartsWith 和 whereContains 都是 区分大小写 的，所以上述查询会忽略 Lunch、LUNCH 等字符串。
        // 如果想查找某一属性值不包含特定字符串的对象，可以使用 whereMatches 进行基于正则表达式的查询：
        query = new AVQuery<>("Todo");
        // "title" 不包含 "ticket"（区分大小写）
        query.whereMatches("title","^((?!ticket).)*$");
        // 不过我们并不推荐大量使用这类查询，尤其是对于包含超过 100,000 个对象的 class，因为这类查询无法利用索引，
        // 实际操作中云端会遍历所有对象来获取结果。
        // 如果有进行全文搜索的需求，可以了解一下 应用内搜索 功能。
        return "success";
    }

    /**
     * whereContainedIn 如需获取某一属性值包含一列值中任意一个值的对象，可以直接用 whereContainedIn 而无需执行多次查询。
     * 反过来，还可以用 whereNotContainedIn 来获取某一属性值不包含一列值中任何一个的对象。
     * @return
     */
    @GetMapping("arrQuery")
    public String arrQuery() {
        AVQuery<AVObject> query = new AVQuery<>("Todo");
        // 下面的代码查找所有数组属性 tags 包含 工作 的对象：
        query.whereEqualTo("tags", "工作");
        // 下面的代码查找所有数组属性 tags 同时包含 工作、销售 和 会议 的对象：
        query.whereContainsAll("tags", Arrays.asList("工作", "销售", "会议"));
        // 如需获取某一属性值包含一列值中任意一个值的对象，可以直接用 whereContainedIn 而无需执行多次查询。
        // 下面的代码构建的查询会查找所有 priority 为 1 或 2 的 todo 对象：
        // 单个查询
        AVQuery<AVObject> priorityOneOrTwo = new AVQuery<>("Todo");
        priorityOneOrTwo.whereContainedIn("priority", Arrays.asList(1, 2));
        // 这样就可以了 :)
        // ---------------
        //       vs.
        // ---------------
        // 多个查询
        final AVQuery<AVObject> priorityOne = new AVQuery<>("Todo");
        priorityOne.whereEqualTo("priority", 1);
        final AVQuery<AVObject> priorityTwo = new AVQuery<>("Todo");
        priorityTwo.whereEqualTo("priority", 2);
        // AVQuery<AVObject> priorityOneOrTwo = AVQuery.or(Arrays.asList(priorityOne, priorityTwo));
        // 好像有些繁琐 :(
        // 反过来，还可以用 whereNotContainedIn 来获取某一属性值不包含一列值中任何一个的对象。
        return "success";
    }

    /**
     * 关系查询
     * @return
     */
    @GetMapping("contactQuery")
    public String contactQuery() {
        // 查询关联数据有很多种方式，常见的一种是查询某一属性值为特定 AVObject 的对象，这时可以像其他查询一样直接用 whereEqualTo。比如说，
        // 如果每一条博客评论 Comment 都有一个 post 属性用来存放原文 Post，则可以用下面的方法获取所有与某一 Post 相关联的评论：
        AVObject post = AVObject.createWithoutData("Post", "57328ca079bc44005c2472d0");
        AVQuery<AVObject> query = new AVQuery<>("Comment");
        query.whereEqualTo("post", post);
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(List<AVObject> comments) {
                // comments 包含与 post 相关联的评论
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });

        // 如需获取某一属性值为另一查询结果中任一 AVObject 的对象，可以用 whereMatchesQuery。
        // 下面的代码构建的查询可以找到所有包含图片的博客文章的评论
        AVQuery<AVObject> innerQuery = new AVQuery<>("Post");
        innerQuery.whereExists("image");

        query = new AVQuery<>("Comment");
        query.whereMatchesQuery("post", innerQuery);

        // 如需获取某一属性值不是另一查询结果中任一 AVObject 的对象，则使用 whereDoesNotMatchQuery
        // 有时候可能需要获取来自另一个 class 的数据而不想进行额外的查询，
        // 此时可以在同一个查询上使用 include。下面的代码查找最新发布的 10 条评论，并包含各自对应的博客文章：
        query = new AVQuery<>("Comment");
        // 获取最新发布的
        query.orderByDescending("createdAt");
        // 只获取 10 条
        query.limit(10);
        // 同时包含博客文章
        query.include("post");
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(List<AVObject> comments) {
                // comments 包含最新发布的 10 条评论，包含各自对应的博客文章
                for (AVObject comment : comments) {
                    // 该操作无需网络连接
                    AVObject post = comment.getAVObject("post");
                }
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
        return "success";
    }

    /**
     * 可以用 dot 符号（.）来获取多级关系。比如说，在获取评论对应文章的同时获取文章作者：
     * @return
     */
    @GetMapping("countQuery2")
    public String countQuery2() {
        AVQuery<AVObject> query = null;
        query.include("post.author");
        // 可以在同一查询上应用多次 include 以包含多个属性。通过这种方法获取到的对象同样接受 getFirst 等 AVQuery 辅助方法。
        // 通过 include 进行多级查询的方式不适用于数组属性内部的 AVObject，只能包含到数组本身。
        // dot 符号（.）还可用于 selectKeys 以限制返回的关联对象的属性：
        query.selectKeys(Arrays.asList("post.author.firstName"));
        /**
         * LeanCloud 云端使用的并非关系型数据库，无法做到真正的联表查询，所以实际的处理方式是：先执行内嵌/子查询（和普通查询一样，
         * limit 默认为 100，最大 1000），然后将子查询的结果填入主查询的对应位置，再执行主查询。如果子查询匹配到的记录数量超出 limit，
         * 且主查询有其他查询条件，那么可能会出现没有结果或结果不全的情况，因为只有 limit 数量以内的结果会被填入主查询。
         */
        // 统计总数量
        // 如果只需知道有多少对象匹配查询条件而无需获取对象本身，可使用 count 来代替 findInBackground。比如说，查询有多少个已完成的 todo
        query = new AVQuery<>("Todo");
        query.whereEqualTo("isComplete", true);
        query.countInBackground().subscribe(new Observer<Integer>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(Integer count) {
                System.out.println(count + " 个 todo 已完成。");
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
        return "success";
    }

    @GetMapping("combination")
    public String combination() {
        // 组合查询就是把诸多查询条件用一定逻辑合并到一起（OR 或 AND）再交给云端去查询。
        // 组合查询不支持在子查询中包含 GeoPointController 或其他非过滤性的限制（例如 near、withinGeoBox、limit、skip、ascending、descending、include）。
        // OR 查询
        // 使用 OR 查询时，子查询中不能包含 GeoPointController 相关的查询。
        final AVQuery<AVObject> priorityQuery = new AVQuery<>("Todo");
        priorityQuery.whereGreaterThanOrEqualTo("priority", 3);
        final AVQuery<AVObject> isCompleteQuery = new AVQuery<>("Todo");
        isCompleteQuery.whereEqualTo("isComplete", true);
        AVQuery<AVObject> query = AVQuery.or(Arrays.asList(priorityQuery, isCompleteQuery));
        // AND 查询
        // 使用 AND 查询的效果等同于往 AVQuery 添加多个条件。
        // 下面的代码构建的查询会查找创建时间在 2016-11-13 和 2016-12-02 之间的 todo：
        final AVQuery<AVObject> startDateQuery = new AVQuery<>("Todo");
        startDateQuery.whereGreaterThanOrEqualTo("createdAt", getDateWithDateString("2016-11-13"));

        final AVQuery<AVObject> endDateQuery = new AVQuery<>("Todo");
        endDateQuery.whereLessThan("createdAt", getDateWithDateString("2016-12-03"));

        query = AVQuery.and(Arrays.asList(startDateQuery, endDateQuery));
        return "success";
    }

    /**
     * 单独使用 AND 查询跟使用基础查询相比并没有什么不同，不过当把它和 OR 查询结合在一起的时候就不一样了。
     * 下面的代码构建的查询可以查找所有今天创建的 todo 中没有 location 的或 priority 为 3 的：
     * @return
     */
    @GetMapping("queryNowDay")
    public String queryNowDay() {
        final AVQuery<AVObject> createdAtQuery = new AVQuery<>("Todo");
        createdAtQuery.whereGreaterThanOrEqualTo("createdAt", getDateWithDateString("2018-04-30"));
        createdAtQuery.whereLessThan("createdAt", getDateWithDateString("2018-05-01"));

        final AVQuery<AVObject> locationQuery = new AVQuery<>("Todo");
        locationQuery.whereDoesNotExist("location");

        final AVQuery<AVObject> priorityQuery = new AVQuery<>("Todo");
        priorityQuery.whereEqualTo("priority", 3);

        AVQuery<AVObject> orQuery = AVQuery.or(Arrays.asList(locationQuery, priorityQuery));
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(createdAtQuery, orQuery));
        return "success";
    }

    @GetMapping("orderByTime")
    public String orderByTime() {
        AVQuery<AVObject> query = new AVQuery<>("Todo");
        // 按 createdAt 升序排列
        query.orderByAscending("createdAt");
        // 按 createdAt 降序排列
        query.orderByDescending("createdAt");
        // 还可以为同一个查询添加多个排序规则；
        query.addAscendingOrder("priority");
        query.addDescendingOrder("createdAt");

        // 下面的代码可用于查找包含或不包含某一属性的对象：
        // 查找包含 "images" 的对象
        query.whereExists("images");
        // 查找不包含 "images" 的对象
        query.whereDoesNotExist("images");
        return "success";
    }

    @GetMapping("queryFieldExist")
    public String queryFieldExist() {
        AVQuery<AVObject> query = new AVQuery<>("Todo");
        // 查找包含 "images" 的对象
        query.whereExists("images");
        // 查找不包含 "images" 的对象
        query.whereDoesNotExist("images");
        return "success";
    }

    @GetMapping("serialization")
    public String serialization() {
        // 序列化
        AVObject todo = new AVObject("Todo"); // 构建对象
        todo.put("title", "马拉松报名"); // 设置名称
        todo.put("priority", 2); // 设置优先级
        todo.put("owner", AVUser.getCurrentUser()); // 这里就是一个 Pointer 类型，指向当前登录的用户
        String serializedString = todo.toString();
        // 反序列化
        AVObject deserializedObject = AVObject.parseAVObject(serializedString);
        deserializedObject.save(); // 保存到服务端
        return "success";
    }

    Date getDateWithDateString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
