package com.tuwq.serivce;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tuwq.common.FileType;

@Service
public class UserService {
	
	@Autowired
	private QiNiuService qiNiuService;
	
	/**
	 * 上传文件前的检验工作
	 * 	判断文件是否为空
	 *  判断文件是否是jpg或png格式图片
	 * @param file 上传的文件
	 * @return
	 */
	public String avatar(MultipartFile file) {
		if (file.isEmpty()) {
			throw new RuntimeException("文件为空");
		}
		if (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
			throw new RuntimeException("文件类型错误,请上传jpg或png文件类型的文件");
		}
		if(this.checkInputStream(file)) {
			String resultBody = this.qiNiuService.avatar(file);
			return resultBody;
		}
		return "";
	}

	/**
	 * 检查流是否是木马文件
	 * @param file 文件
	 * @return
	 */
	private boolean checkInputStream(MultipartFile file) {
		try {
			InputStream inputStream = file.getInputStream();
			FileType fileType = getFileType(inputStream);
			if (fileType == null) {
				throw new RuntimeException("非图片文件");
			}
			String imgValue = fileType.getExt();
			System.out.println("图片格式:" + imgValue);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	// 判断文件是图片格式
	public static FileType getFileType(InputStream is) throws IOException {
		byte[] src = new byte[28];
		is.read(src, 0, 28);
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v).toUpperCase();
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		FileType[] fileTypes = FileType.values();
		for (FileType fileType : fileTypes) {
			if (stringBuilder.toString().startsWith(fileType.getValue())) {
				return fileType;
			}
		}
		return null;
	}
}
