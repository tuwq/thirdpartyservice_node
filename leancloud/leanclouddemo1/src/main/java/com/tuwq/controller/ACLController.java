package com.tuwq.controller;

import cn.leancloud.*;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("acl")
public class ACLController {

    /**
     * 角色创建
     * 首先，我们来创建一个 Administrator 的角色。
     * +
     *
     * 这里有一个需要特别注意的地方，因为 AVRole 本身也是一个 AVObject，它自身也有 ACL 控制，并且它的权限控制应该更严谨，
     * 如同「论坛的管理员有权力任命版主，而版主无权任命管理员」一样的道理，
     * 所以创建角色的时候需要显式地设定该角色的 ACL，而角色是一种较为稳定的对象：
     * @return
     */
    public String roleCreate() {
        // 新建一个针对角色本身的 ACL
        AVACL roleACL = new AVACL();
        roleACL.setPublicReadAccess(true);
        roleACL.setWriteAccess(AVUser.getCurrentUser(),true);

        // 新建一个角色，并把为当前用户赋予该角色
        AVRole administrator= new AVRole("Administrator",roleACL);//新建角色
        administrator.getUsers().add(AVUser.getCurrentUser());//为当前用户赋予该角色
        administrator.saveInBackground().blockingSubscribe();//保存到云端
        return "success";
    }

    /**
     * 对象设置角色权限
     * 我们现在已经创建了一个有效的角色，接下来为 Post 对象设置 Administrator 的访问「可读可写」的权限，
     * 设置成功以后，任何具备 Administrator 角色的用户都可以对 Post 对象进行「可读可写」的操作了：
     * @return
     */
    public String setAcl() {
        // 新建一个帖子对象
        final AVObject post = new AVObject("Post");
        post.put("title", "夏天吃什么夜宵比较爽？");
        post.put("content", "求推荐啊！");

        AVQuery<AVRole> roleQuery = new AVQuery<AVRole>("_Role");
        // 假设上一步创建的 Administrator 角色的 objectId 为 55fc0eb700b039e44440016c
        roleQuery.getInBackground("55fc0eb700b039e44440016c").subscribe(new Observer<AVRole>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(AVRole avRole) {
                //新建一个 ACL 实例
                AVACL acl = new AVACL();
                acl.setPublicReadAccess(true);// 设置公开的「读」权限，任何人都可阅读
                acl.setRoleWriteAccess(avRole.toString(), true);// 为 Administrator 「写」权限
                acl.setWriteAccess(AVUser.getCurrentUser(), true);// 为当前用户赋予「写」权限

                // 以上代码的效果就是：只有 Post 作者（当前用户）和拥有 Administrator 角色的用户可以修改这条 Post，而所有人都可以读取这条 Post
                post.setACL(acl);
                post.saveInBackground();
            }
            @Override
            public void onError(Throwable e) {
                System.out.println("errorMessage:" + e.getMessage());
            }
            @Override
            public void onComplete() {
            }
        });
        return "success";
    }

    /**
     * 用户角色的赋予和剥夺
     * 经过以上两步，我们还差一个给具体的用户设置角色的操作，这样才可以完整地实现基于角色的权限管理。
     * 在通常情况下，角色和用户之间本是多对多的关系，比如需要把某一个用户提升为某一个版块的版主，亦或者某一个用户被剥夺了版主的权力，以此类推。
     * 在应用的版本迭代中，用户的角色都会存在增加或者减少的可能，因此，LeanCloud 也提供了为用户赋予或者剥夺角色的方式。
     * 在代码级别，实现「为角色添加用户」与「为用户赋予角色」的代码是一样的。此类操作的逻辑顺序是：
     *
     * 赋予角色：首先判断该用户是否已经被赋予该角色，如果已经存在则无需添加，如果不存在则为该用户（AVUser）的 roles 属性添加当前角色实例。
     * 以下代码演示为当前用户添加 Administrator 角色：
     * @return
     */
    public String setRole() {
        final AVQuery<AVRole> roleQuery = new AVQuery<AVRole>("_Role");
        roleQuery.whereEqualTo("name","Administrator");
        roleQuery.findInBackground().subscribe(new Observer<List<AVRole>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(List<AVRole> avRoles) {
                // 如果角色存在
                if (avRoles.size() > 0){
                    final AVRole administratorRole = avRoles.get(0);
                    roleQuery.whereEqualTo("users",AVUser.getCurrentUser());
                    roleQuery.findInBackground().subscribe(new Observer<List<AVRole>>() {
                        @Override
                        public void onSubscribe(Disposable d) { }
                        @Override
                        public void onNext(List<AVRole> list) {
                            if (list.size()  == 0){
                                administratorRole.getUsers().add(AVUser.getCurrentUser());// 赋予角色
                                administratorRole.saveInBackground();
                            }else {
                                System.out.println("已经拥有 Administrator 角色了。");
                            }
                        }
                        @Override
                        public void onError(Throwable e) { }
                        @Override
                        public void onComplete() { }
                    });
                }else {
                    // 角色不存在，就新建角色
                    AVRole administratorRole = new AVRole("Administrator");
                    administratorRole.getUsers().add(AVUser.getCurrentUser());// 赋予角色
                    administratorRole.saveInBackground();
                }
            }
            @Override
            public void onError(Throwable e) {
                System.out.println("errorMessage:" + e.getMessage());
            }
            @Override
            public void onComplete() {
            }
        });
        return "success";
    }

    /**
     * 角色赋予成功之后，基于角色的权限管理的功能才算完成
     * 剥夺角色：首先判断该用户是否已经被赋予该角色，如果未曾赋予则不做修改，
     * 如果已被赋予，则从对应的用户（AVUser）的 roles 属性当中把该角色删除。
     * @return
     */
    public String removeRole() {
        final AVQuery<AVRole> roleQuery = new AVQuery<AVRole>("_Role");
        roleQuery.whereEqualTo("name","Moderator");
        roleQuery.findInBackground().subscribe(new Observer<List<AVRole>>() {
            @Override
            public void onSubscribe(Disposable d) { }
            @Override
            public void onNext(List<AVRole> avRoles) {
                if(avRoles.size() > 0){
                    final AVRole moderatorRole= avRoles.get(0);
                    roleQuery.whereEqualTo("users",AVUser.getCurrentUser());
                    roleQuery.findInBackground().subscribe(new Observer<List<AVRole>>() {
                        @Override
                        public void onSubscribe(Disposable d) { }
                        @Override
                        public void onNext(List<AVRole> list) {
                            // 如果该用户确实拥有该角色，那么就剥夺
                            if(list.size() > 0) {
                                moderatorRole.getUsers().remove(AVUser.getCurrentUser());
                                moderatorRole.saveInBackground();
                            }
                        }
                        @Override
                        public void onError(Throwable e) { }
                        @Override
                        public void onComplete() { }
                    });
                }
            }
            @Override
            public void onError(Throwable e) {
                System.out.println("errorMessage:" + e.getMessage());
            }
            @Override
            public void onComplete() {

            }
        });
        return "success";
    }

    /**
     * 查询某一个用户拥有哪些角色：
     * @return
     */
    public String select() {
        // 第一种方式是通过 AVUser 内置的接口：
        AVUser user = AVUser.getCurrentUser();
        user.getRolesInBackground().subscribe(new Observer<List<AVRole>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(List<AVRole> avRoles) {
                // avRoles 表示这个用户拥有的角色
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onComplete() {
            }
        });

        // 第二种方式是通过构建 AVQuery：
        AVQuery<AVRole> roleQuery = new AVQuery<AVRole>("_Role");
        roleQuery.whereEqualTo("users",user);
        roleQuery.findInBackground().subscribe(new Observer<List<AVRole>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(List<AVRole> list) {
                // list 就是一个 AVRole 的 List，这些 AVRole 就是当前用户所在拥有的角色
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
     * 查询都有哪些用户被赋予了 Moderator 角色：
     * @return
     */
    public String select2() {
        AVRole moderatorRole= new AVRole("Moderator"); //根据 id 查询或者根据 name 查询出一个实例
        AVRelation<AVUser> userRelation= moderatorRole.getUsers();
        AVQuery<AVUser> userQuery = userRelation.getQuery();
        userQuery.findInBackground().subscribe(new Observer<List<AVUser>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(List<AVUser> list) {
                // list 就是拥有该角色权限的所有用户了。
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
     * 角色的从属关系
     * 角色从属关系是为了实现不同角色的权限共享以及权限隔离。
     * 权限共享 很好理解，比如管理员拥有论坛所有板块的管理权限，而版主只拥有单一板块的管理权限，如果开发一个版主使用的新功能，都要同样的为管理员设置该项功能权限，代码就会冗余，
     * 因此，我们通俗的理解是：管理员也是版主，只是他是所有板块的版主。因此，管理员在角色从属的关系上是属于版主的，只不过 TA 是特殊的版主
     * @return
     */
    public String subordinate() {
        // 从服务端查询 Administrator 实例
        AVRole administratorRole = null;
        // 从服务端查询 Moderator 实例
        AVRole moderatorRole = null;
        // 向 moderatorRole 的 roles（AVRelation） 中添加 administratorRole
        moderatorRole.getRoles().add(administratorRole);
        moderatorRole.saveInBackground().blockingSubscribe();
        return "success";
    }

    /**
     * 权限隔离
     * 权限隔离也就是两个角色不存在从属关系，但是某些权限又是共享的，
     * 此时不妨设计一个中间角色，让前面两个角色从属于中间角色，这样在逻辑上可以很快梳理，其实本质上还是使用了角色的从属关系。
     * @return
     */
    public String isolation() {
        // 新建 3 个角色实例
        AVRole photographicRole = null; //创建或者从服务端查询出 Photographic 角色实例
        AVRole mobileRole = null; //创建或从服务端查询出 Mobile 角色实例
        AVRole digitalRole = null; //创建或从服务端查询出 Digital 角色实例
        // photographicRole 和 mobileRole 继承了 digitalRole
        digitalRole.getRoles().add(photographicRole);
        digitalRole.getRoles().add(mobileRole);

        digitalRole.saveInBackground().subscribe(new Observer<AVObject>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(AVObject avObject) {
                //新建 3 篇贴子，分别发在不同的板块上
                AVObject photographicPost= new AVObject ("Post");
                AVObject mobilePost = new AVObject("Post");
                AVObject digitalPost = new AVObject("Post");
                //.....此处省略一些具体的值设定

                AVACL photographicACL = new AVACL();
                photographicACL.setPublicReadAccess(true);
                photographicACL.setRoleWriteAccess(String.valueOf(photographicRole), true);
                photographicPost.setACL(photographicACL);

                AVACL mobileACL = new AVACL();
                mobileACL.setPublicReadAccess(true);
                mobileACL.setRoleWriteAccess(String.valueOf(mobileRole), true);
                mobilePost.setACL(mobileACL);

                AVACL digitalACL = new AVACL();
                digitalACL.setPublicReadAccess(true);
                digitalACL.setRoleWriteAccess(String.valueOf(digitalRole), true);
                digitalPost.setACL(digitalACL);

                // photographicPost 只有 photographicRole 可以读写
                // mobilePost 只有 mobileRole 可以读写
                // 而 photographicRole，mobileRole，digitalRole 均可以对 digitalPost 进行读写
                photographicPost.saveInBackground();
                mobilePost.saveInBackground();
                digitalPost.saveInBackground();
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
     * 获取对象的 ACL 值
     * @return
     */
    public String getObjectAcl() {
        AVQuery<AVObject> query = new AVQuery<>("Todo");
        query.includeACL(true);
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(List<AVObject> avObjects) {
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
