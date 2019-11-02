1. 银联文档
	https://open.unionpay.com/tjweb/acproduct/dictionary
2. 银联网关支付
	https://open.unionpay.com/tjweb/acproduct/list?apiservId=448

3. 银联商户平台注册商户账号,使用ie浏览器
	https://merchant.unionpay.com/join/

4. 下载账号对应的商户证书放入D:/certs
	acp_test_enc.cer
	acp_test_middle.cer
	acp_test_root.cer
	acp_test_sign.p12

5. 修改acp_sdk.properties配置文件
	acpsdk.signCert.path=D:/certs/acp_test_sign.p12

6. 访问demo项目
	http://127.0.0.1:9001/ACPSample_B2C/

7. 检查demo是否正常
	消费样例-消费-提交,成功出现支付页面说明成功

8. 支付订单
	输入信息支付订单

9. 修改后台通知地址,需要外网能够访问
	acpsdk.backUrl=http://localhost:8080/ACPSample_B2C/backRcvResponse

10. 修改前台通知地址,需要外网能够访问
	acpsdk.frontUrl=http://localhost:8080/ACPSample_B2C/frontRcvResponse

## 银联支付提交原理
	1. 封装支付请求参数
	2. 将参数验证签名,以html表单形式提交给银联支付接口,含加签内容resp.getWriter().write(html);


## 接收银联回调
	1. 同步回调(前台通知),第三方支付系统以浏览器重定向形式将支付结果通知商户端,跳转提示页面告诉用户支付成功
	2. 异步回调(后台通知),第三方支付系统以http告诉商户端结果,处理系统订单逻辑,注意最终一致性和幂等性

## 对接时注意
	更换相关参数信息,orderId传入业务订单的支付id
	启动加载证书



