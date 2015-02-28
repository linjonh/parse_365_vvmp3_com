package cn.linjonh.jsoup.util;

public class Utils {

	public Utils() {
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args){
		print(getFileName("http://xz1.mm667.com/s/009.jpg"));
	}
	/**
	 * @param imgFileUrl
	 * @return
	 */
	public static String getFileName(String imgFileUrl) {
		String imgFileName = imgFileUrl.replace("http://", "");
		imgFileName = imgFileName.substring(imgFileName.indexOf("/") + 1);
		imgFileName=imgFileName.replace("/", "_");
		return imgFileName;

	}
	private static void print(String str){
		System.out.println(str);
	}
}
