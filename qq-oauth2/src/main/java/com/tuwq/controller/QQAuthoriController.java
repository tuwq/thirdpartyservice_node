package com.tuwq.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;

import lombok.extern.slf4j.Slf4j;

/**
 * QQ授权
 * @author tuwq
 */
@Controller
@Slf4j
public class QQAuthoriController {
	
	/**
	 * 生成前往授权链接
	 * @return
	 */
	
	@RequestMapping("/qqAuth")
	public void qqAuth(HttpServletRequest request, HttpServletResponse response) {
		try {
			String authorizeURL = new Oauth().getAuthorizeURL(request);
			log.info("authorizeURL:" + authorizeURL);
			response.sendRedirect(authorizeURL);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * QQ互联授权的回调地址
	 * 通过code获得accessToken
	 * 通过accessToken获得openId
	 * 通过openId+accessToken获得头像
	 * @return
	 */
	@RequestMapping("/mycb")
	@ResponseBody
	public String mycb(String code, HttpServletRequest request,  HttpServletResponse response) {
		System.out.println("code:" + code);
		try {
			AccessToken accessTokenObject = (new Oauth()).getAccessTokenByRequest(request);
			if (accessTokenObject == null) {
				return "500";
			}
			String accessToken = accessTokenObject.getAccessToken();
			if (StringUtils.isEmpty(accessToken)) {
				return "500";
			}
			System.out.println("accessToken:" + accessToken);
			OpenID openIDObject = new OpenID(accessToken);
			String openId = openIDObject.getUserOpenID();
			if (StringUtils.isEmpty(openId)) {
				return "500";
			}
			System.out.println("openId:" + openId);
			UserInfo userInfo = new UserInfo(accessToken, openId);
			UserInfoBean userInfoBean = userInfo.getUserInfo();
			if (userInfoBean == null) {
				return "500";
			}
			String avatarURL100 = userInfoBean.getAvatar().getAvatarURL100();
			System.out.println("avatarURL100" + avatarURL100);
		} catch (QQConnectException e) {
			e.printStackTrace();
		}
		return code;
	}
}
