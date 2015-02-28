package cn.linjonh.jsoup.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

	public Utils() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		print(getFileName("http://xz1.mm667.com/s/009.jpg"));
	}

	/**
	 * @param imgFileUrl
	 * @return
	 */
	public static String getFileName(String imgFileUrl) {
		String imgFileName = imgFileUrl.replace("http://", "");
		imgFileName = imgFileName.substring(imgFileName.indexOf("/") + 1);
		imgFileName = imgFileName.replace("/", "_");
		return imgFileName;

	}

	public static void print(String str) {
		System.out.println(str);
	}

	public static String getFormatedTime() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		String appedDateInfo = dateFormat.format(date);
		return appedDateInfo;
	}
}
