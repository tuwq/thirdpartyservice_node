package com.alipay.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *������AlipayConfig
 *���ܣ�����������
 *��ϸ�������ʻ��й���Ϣ������·��
 *�޸����ڣ�2017-04-05
 *˵����
 *���´���ֻ��Ϊ�˷����̻����Զ��ṩ���������룬�̻����Ը����Լ���վ����Ҫ�����ռ����ĵ���д,����һ��Ҫʹ�øô��롣
 *�ô������ѧϰ���о�֧�����ӿ�ʹ�ã�ֻ���ṩһ���ο���
 */

public class AlipayConfig {
	
//�����������������������������������Ļ�����Ϣ������������������������������

	// Ӧ��ID,����APPID���տ��˺ż�������APPID��Ӧ֧�����˺�
	public static String app_id = "2016092500592648";
	
	// �̻�˽Կ������PKCS8��ʽRSA2˽Կ
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCQZzWlyRyxkUgcl6VpJ4hbpMVd2DZkH+Om0DPjxTsSAaI5mCPrDrT1jr9AoBcE3YN/0bYW5SePnJ3IIZMrUkKsWq2pfRTB4GACNNUUMkEFGh+7zSL6FlDjIUGJcWxUgSlgm39EAOgMXDA6vDQ9KTXvy73W6770k5ADzRzQSS2JwbzUkg+2pYSUd/kQaDFaodQmxJvKG5fE7Qv0nc0jghG/rgGULAW8ugGH2IOhdKx6br4AjytXPrJy7CdZ9Je3oQvQva0E1fo2C/uid1VarA95CG2q2Am6rk7riDwT1HRuHtO8X8WTW0anFlxgDY4Ybs7Ea0NOjJdzgjhvN54vLsdLAgMBAAECggEAe6tYn644W76fspURPZh0yH+55CAqnN9OuWtwNS6R417wz0CD6miDBBaHptI0RJ9tYkd5GeyRUAYCfwZ6IszlevSB7DjKUSxXLSyQHCbZ1oPED7Oks+fuyEaP+SThcRHZHPqvKM9Z7FP7niHW31Zu8fpckDcAKDhES20bsyoozOOK7aMeODOFL09np/vkGExKM8ljlfe27E4Y69Y8QitHP7hZzocc0NgqVJVVi/gjfxDaP0PkWo74+2NIloz7zoSzmVnor3LSTo20nB3ByuJBr809GUX+Fcs9ubxotLwVYp+834HhiUdxuY0DbDNFh1lYkJxQzNupn/cUlLUCJ5h+YQKBgQDXESIBLjZwjolu7taslsDMjIqSOocoWCpFdUq7BEE+3Fb5hz9NkFY8HY00FWJAo45ZBTQOT/lWrDL3rgkUk3GOxby/ryVM69idJDM6SuuwMZiXye+2prsMJ9J7Ro3P42XRQHtVgDUbd+I1YhvjjfIzwH2tEbdQNkZetOOdRKgYEQKBgQCr4xK81VXA2RbOE2mzMjW8Yp/X93BctMC+omTaoU0M1i2yTXpjxCMZRsYj4MEmU9bsmcHXAlbC2b8XylzTg7XmBUpznIaqdv65baqk1Kq8geIII61EFiyv4kzxI7t18CR1HK0e6gRvVOJQGZzp0GZNXh1tHAqXMTB2qqdP+tHlmwKBgA10YPA59+XqxOZPwtZhSAaD9FdZmgMIIPzfbaFtjrdkCXzq3Sb5FKwGHUNY4yCwGsOu86bBkhO+CiI8nQzQfJY2AKztRCQfzRlnnlzTOxN2xtkYJQsEO0dzZlOYAl8zFreUlSKwNqJVSa2VSbFeu/xI2pPtO86k1pouLt8z4GBBAoGAHBhJf8P++D6Dk7tok3mpftk/9zTikMSanI4+nd2vkmGPpo0zbAsrEX6X9FVQPMuHXbTkEhi3xbGBoonZEll86dQDj1IOUN6SVAywz7BTF+zC3UG3mGFnfHgoy2FjyqstyladlWf6wZfGRgFpvG3rysmPVb43nO7VVRAfuNruY/ECgYEAgTlPbZ7LoYZfuQj42SkQd6M+NhTzMoQfl2l2GwQbg/M4kJrOYvIN5yZDidB2BIk0yvWtW4VmrWfVav1RUCtzSLCY+M008fpyDhRGY+X7z8PpkfyND5r3zJ4CJU8xHsjmC7lW89GYj1gIqTFjXeX7hiEDqPaF1CnNIJTiRlzMbpc=";
	
	// ֧������Կ,�鿴��ַ��https://openhome.alipay.com/platform/keyManage.htm ��ӦAPPID�µ�֧������Կ��
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkgkLVQyPAL4fkVm34YzTURseeEjKOs2b/GgmHguYtpia3YaZreFrtqDy4b+jSqb7y9jri3QateU+MR8z+l3F/jWxMBseol49bznJFA+zZg8LmwIVHKaXvesNRdzvJtXIoEO94h0APP/pZWsieAopA/+uOQjHdxg0gc7sb0Mfh/PLvhToEEuYqNzIOve2QMKBEHiTgX7Zc3TX17oUZcEZbIpsOTWli//ZdgxjFRvCYWP1T1jk4yA67iWOq5UHZloxw3eKM+QdlcSoic/H1cAdtAzIH+bOSAZijYrHbzglpoe2cN7HX4aCX8yleytWo7wRA/3ypVbYtvH4jms8QioD8wIDAQAB";

	// �������첽֪ͨҳ��·��  ��http://��ʽ������·�������ܼ�?id=123�����Զ����������������������������
	public static String notify_url = "http://qianyue.natapp1.cc/AlipayTest/notify_url.jsp";

	// ҳ����תͬ��֪ͨҳ��·�� ��http://��ʽ������·�������ܼ�?id=123�����Զ����������������������������
	public static String return_url = "http://qianyue.natapp1.cc/AlipayTest/return_url.jsp";

	// ǩ����ʽ
	public static String sign_type = "RSA2";
	
	// �ַ������ʽ
	public static String charset = "utf-8";
	
	// ֧��������
	public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
	
	// ֧��������
	public static String log_path = "C:\\";


//�����������������������������������Ļ�����Ϣ������������������������������

    /** 
     * д��־��������ԣ�����վ����Ҳ���ԸĳɰѼ�¼�������ݿ⣩
     * @param sWord Ҫд����־����ı�����
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

