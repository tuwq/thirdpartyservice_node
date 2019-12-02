package com.tuwq.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类,配置本项目的一些常量
 */
@Configuration
@EnableConfigurationProperties(TheProjectProperties.class)
public class TheProjectPropertiesConfiguration {
}
