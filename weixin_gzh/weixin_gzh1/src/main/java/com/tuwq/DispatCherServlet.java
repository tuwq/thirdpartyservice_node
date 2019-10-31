package com.tuwq;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 微信对接步骤
 *  1. 填写服务器地址
 *  2. 验证服务器地址是否正确
 *  3. 任何微信动作,都会以post请求通知到该地址
 */
@RestController
public class DispatCherServlet {

    @GetMapping("dispatCherServlet")
    public String getDispatCherServlet(String signature, String timestamp, String nonce, String echostr) {
        if (!CheckUtil.checkSignature(signature, timestamp, nonce)) {
            return null;
        }
        return echostr;
    }

    @PostMapping("dispatCherServlet")
    public void postDispatCherServlet(HttpServletRequest request, HttpServletResponse response, String signature, String timestamp, String nonce, String echostr) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Map<String, String> result = XmlUtils.parseXml(request);
        String toUserName = result.get("ToUserName");
        String fromUserName = result.get("FromUserName");
        String createTime = result.get("CreateTime");
        String msgType = result.get("MsgType");
        String content = result.get("Content");
        String msgId = result.get("MsgId");
        switch (msgType) {
            case "text":
                String resultXml = null;
                PrintWriter out = response.getWriter();
                TextMessage textMessage = new TextMessage();
                textMessage.setToUserName(fromUserName);
                textMessage.setFromUserName(toUserName);
                textMessage.setCreateTime(System.currentTimeMillis());
                textMessage.setMsgType("text");
                if (content.equals("你好")) {
                    textMessage.setContent("给予回复");
                } else {
                    String resultApi = HttpClientUtil.doGet("http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + content);
                    JSONObject jsonObject = new JSONObject().parseObject(resultApi);
                    Integer state = jsonObject.getInteger("result");
                    if (state != null || state == 0) {
                        String content1Api = jsonObject.getString("content");
                        textMessage.setContent(content1Api);
                    }
                }
                resultXml = XmlUtils.messageToXml(textMessage);
                out.println(resultXml);
                out.close();
                break;
            default:
                break;
        }

    }

    @GetMapping("/test")
    public String test() {
        return "success";
    }
}
