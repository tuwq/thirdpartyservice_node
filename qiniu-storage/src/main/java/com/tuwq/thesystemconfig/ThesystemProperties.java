package com.tuwq.thesystemconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ConfigurationProperties(prefix="thesystem.config")
@Component
public class ThesystemProperties {
	
	private QiniuProperties qiniu;
	
}
