package com.tuwq.init;

import cn.leancloud.AVLogger;
import cn.leancloud.core.AVOSCloud;
import com.tuwq.config.LeanCloudProperties;
import com.tuwq.config.TheProjectProperties;
import com.tuwq.util.ApplicationContextUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 初始化LeanCloud
 */
@Component
public class LeanCloudBooter implements ApplicationListener<ContextRefreshedEvent> {
    /**
     * 该方法是SpringBoot成功启动后的回调函数钩子
     * 该方法初始化LeanCloud
     * {@link AVOSCloud#setLogLevel(AVLogger.Level)}  开启leanCloud的调试日志,生产环境关闭此调试
     * {@link AVOSCloud#initialize(String, String)}   初始化leanCloud,需要初始化参数
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            AVOSCloud.setLogLevel(AVLogger.Level.DEBUG);// 生产环境关闭此日志调试
            TheProjectProperties theProjectProperties = ApplicationContextUtil.popBean(TheProjectProperties.class);
            LeanCloudProperties leancloud = theProjectProperties.getLeancloud();
            AVOSCloud.initialize(leancloud.getAppId(), leancloud.getAppKey());
            System.err.println("leanCloud init Success!");
        }
    }
}
