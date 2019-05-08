package com.tuwq.thesystemconfig;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ThesystemProperties.class)
public class ThesystemPropertiesConfiguration {

}
