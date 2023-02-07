package cn.linjonh.test;

import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.DownloadUtil;
import org.jsoup.nodes.Document;

public class Test1 {

	public static void changeStr(String str){
		str="welcome";
	}

	public static void main(String[] args) {
//		String str="1234";
//		changeStr(str);
//		System.out.println(str);

//		DownloadUtil.donwloadImg("http://img1.mm131.me/pic/4900/1.jpg","E:/MM131/");
		Document htmlDocument = ConnUtil.getHtmlDocument("https://www.yhvips1.com/index.php?m=&c=mh&a=inforedit&mhid=651&ji_no=3", "D:/yhvips1/log.txt");
		System.out.println(htmlDocument.toString());
	}
}
