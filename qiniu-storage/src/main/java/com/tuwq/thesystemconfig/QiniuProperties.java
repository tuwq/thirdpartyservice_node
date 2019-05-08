package com.tuwq.thesystemconfig;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QiniuProperties {
	private String qiniuAcKey;
	private String qiniuSeKey;
	private String qiniuImgBucket;
	private String qiniuUserAvatarPrefix;
}
