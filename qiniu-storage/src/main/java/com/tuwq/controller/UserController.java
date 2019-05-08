package com.tuwq.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tuwq.common.JsonResult;
import com.tuwq.serivce.QiNiuService;
import com.tuwq.serivce.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	/**
	 * 上传一张图片并返回结果信息
	 * @param file
	 * @return
	 */
	@PostMapping(value="/avatar", headers="content-type=multipart/form-data")
	public JsonResult<String> avatar(@RequestParam("avatar_file") MultipartFile file, 
									@RequestParam("extra") String extra,HttpServletResponse response) {
		 String resultBody = this.userService.avatar(file);
		 response.setHeader("Access-Control-Allow-Origin", "*");
		 return JsonResult.<String>success(resultBody);
	}
}
