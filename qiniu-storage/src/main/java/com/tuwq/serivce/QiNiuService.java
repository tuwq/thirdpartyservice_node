package com.tuwq.serivce;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.tuwq.thesystemconfig.ThesystemProperties;

@Service
public class QiNiuService {
	
	@Resource
	private ThesystemProperties thesystemProperties;
	
	private Configuration cfg = new Configuration(Zone.zone2());
	
	private UploadManager uploadManager = new UploadManager(cfg);
	
	/**
	 * 上传头像
	 * @param file
	 * @return
	 */
	public String avatar(MultipartFile file) {
		String originName = file.getOriginalFilename();
		String key = thesystemProperties.getQiniu().getQiniuUserAvatarPrefix() + originName + originName.substring(originName.lastIndexOf("."));
		byte[] uploadBytes = null;
		try {
			uploadBytes = file.getBytes();
		} catch (IOException e) {
			throw new RuntimeException("读取文件字节失败");
		}
		Auth auth = Auth.create(thesystemProperties.getQiniu().getQiniuAcKey(), thesystemProperties.getQiniu().getQiniuSeKey());
		long expireSeconds = 3600;
		String upToken = auth.uploadToken(thesystemProperties.getQiniu().getQiniuImgBucket(), key, expireSeconds, new StringMap().put("insertOnly",0));
		try {
		    Response response = uploadManager.put(uploadBytes, key, upToken);
		    //解析上传成功的结果
		    DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
		    System.out.println(putRet);
		    return response.bodyString();
		} catch (QiniuException ex) {
		    Response r = ex.response;
		    System.err.println(r.toString());
		    try {
		        System.err.println(r.bodyString());
		    } catch (QiniuException ex2) {
		    	throw new RuntimeException("上传文件至七牛云失败");
		    }
		}
		return null;
	}
	
	
}
