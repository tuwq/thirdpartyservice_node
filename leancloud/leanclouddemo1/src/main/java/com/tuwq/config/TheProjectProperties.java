package com.tuwq.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;

@Setter
@Getter
@ConfigurationProperties(prefix="the.project.config")
@Primary
public class TheProjectProperties {

    // leancloud相关配置
    private LeanCloudProperties leancloud;
}
