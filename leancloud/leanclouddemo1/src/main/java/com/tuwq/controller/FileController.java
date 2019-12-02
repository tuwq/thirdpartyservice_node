package com.tuwq.controller;

import cn.leancloud.AVFile;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.callback.ProgressCallback;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("file")
public class FileController {

    private String SUCCESS = "success";

    /**
     * 构建文件
     * 可以通过字符串构建文件：
     * @return
     */
    @GetMapping("/create")
    public String create() throws FileNotFoundException {
        // resume.txt 是文件名,字符串是数据内容
        AVFile file = new AVFile("resume.txt", "LeanCloud".getBytes());
        // 除此之外，还可以通过 URL 构建文件：
        // 通过 URL 构建文件时，SDK 并不会将原本的文件转储到云端，而是会将文件的物理地址存储为字符串，
        // 这样也就不会产生任何文件上传流量。使用其他方式构建的文件会被保存在云端。
        AVFile file2 = new AVFile(
                "logo.png",
                "https://leancloud.cn/assets/imgs/press/Logo%20-%20Blue%20Padding.a60eb2fa.png",
                new HashMap<String, Object>()
        );
        // LeanCloud 会根据文件扩展名自动检测文件类型。如果需要的话，也可以手动指定 Content-Type（一般称为 MIME 类型）：
        Map<String, Object> meta = new HashMap<String, Object>();
        meta.put("mime_type", "application/json");
        AVFile file3 = new AVFile("resume.txt", "LeanCloud".getBytes());
        file3.setMetaData(meta);
        // 与前面提到的方式相比，一个更常见的文件构建方式是从本地路径上传：
        /**
         * 这里上传的文件名字叫做 avatar.jpg。需要注意：
         * 每个文件会被分配到一个独一无二的 objectId，所以在一个应用内是允许多个文件重名的。
         * 文件必须有扩展名才能被云端正确地识别出类型。比如说要用 AVFile 保存一个 PNG 格式的图像，那么扩展名应为 .png。
         * 如果文件没有扩展名，且没有手动指定类型，那么 LeanCloud 将默认使用 application/octet-stream。
         */
        AVFile file4 = AVFile.withAbsoluteLocalPath("avatar.jpg", "/tmp/avatar.jpg");
        return SUCCESS;
    }

    /**
     * 存文件
     * 将文件保存到云端后，便可获得一个永久指向该文件的 URL：
     * 文件上传后，可以在 _File class 中找到。已上传的文件无法再被修改。
     * 如果需要修改文件，只能重新上传修改过的文件并取得新的 objectId 和 URL。
     * @return
     */
    @GetMapping("save")
    public String save() throws FileNotFoundException {
        AVFile file = AVFile.withAbsoluteLocalPath("avatar.jpg", "/tmp/avatar.jpg");
        file.saveInBackground().subscribe(new Observer<AVFile>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVFile file) {
                System.out.println("文件保存完成。objectId：" + file.getObjectId());
            }
            public void onError(Throwable throwable) {
                // 保存失败，可能是文件无法被读取，或者上传过程中出现问题
            }
            public void onComplete() {}
        });
        return SUCCESS;
    }

    /**
     * 已经保存到云端的文件可以关联到 AVObject：
     * @return
     */
    @GetMapping("get")
    public String get() {
        AVFile file = new AVFile(
                "logo.png",
                "https://leancloud.cn/assets/imgs/press/Logo%20-%20Blue%20Padding.a60eb2fa.png",
                new HashMap<String, Object>()
        );
        AVObject todo = new AVObject("Todo");
        todo.put("title", "买蛋糕");
        // attachments 是一个 Array 属性
        todo.add("attachments", file);
        todo.save();
        return SUCCESS;
    }

    /**
     * 注意，如果文件被保存到了 AVObject 的一个数组属性中，那么在查询 AVObject 时如果需要包含文件，
     * 则要用到 AVQuery 的 include 方法。比如说，在获取所有标题为 买蛋糕 的 todo 的同时获取附件中的文件：
     * @return
     */
    @GetMapping("get2")
    public String get2() {
        // 获取同一标题且包含附件的 todo
        AVQuery<AVObject> query = new AVQuery<>("Todo");
        query.whereEqualTo("title", "买蛋糕");
        query.whereExists("attachments");
        // 同时获取附件中的文件
        query.include("attachments");
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(List<AVObject> todos) {
                for (AVObject todo : todos) {
                    // 获取每个 todo 的 attachments 数组
                }
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
        return SUCCESS;
    }

    @GetMapping("listenUpdatePercent")
    public String listenUpdatePercent() {
        AVFile file = new AVFile("logo.png", "https://leancloud.cn/assets/imgs/press/Logo%20-%20Blue%20Padding.a60eb2fa.png", new HashMap<String, Object>());
        file.saveInBackground(new ProgressCallback() {
            @Override
            public void done(Integer percent) {
                // percent 是一个 0 到 100 之间的整数，表示上传进度
            }
        });
        return SUCCESS;
    }

    /**
     * 文件元数据
     * 上传文件时，可以用 metaData 添加额外的属性。文件一旦保存，metaData 便不可再修改。
     * @return
     */
    @GetMapping("sourceData")
    public String sourceData() {
        AVFile file = new AVFile("resume.txt", "LeanCloud".getBytes());
        // 设置元数据
        file.addMetaData("author", "LeanCloud");
        file.saveInBackground().subscribe(new Observer<AVFile>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVFile file) {
                // 获取 author 属性
                String author = (String) file.getMetaData("author");
                // 获取文件名
                String fileName = file.getName();
                // 获取大小（不适用于通过 base64 编码的字符串或者 URL 保存的文件）
                int size = file.getSize();
            }
            public void onError(Throwable throwable) {}
            public void onComplete() {}
        });
        return SUCCESS;
    }

    /**
     * 保存图像时，如果想在下载原图之前先得到缩略图，方法如下：
     * 图片最大不超过 20 MB 才可以获取缩略图。
     * 国际版不支持图片缩略图。
     * @return
     */
    @GetMapping("thumbnail")
    public String thumbnail() {
        AVFile file = new AVFile("test.jpg", "文件-url", new HashMap<String, Object>());
        file.getThumbnailUrl(true, 100, 100);
        return SUCCESS;
    }

    @GetMapping("remove")
    public String remove() {
        AVObject file = AVObject.createWithoutData("_File", "552e0a27e4b0643b709e891e");
        file.delete();
        return SUCCESS;
    }
}
