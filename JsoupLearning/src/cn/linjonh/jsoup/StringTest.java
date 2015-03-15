package cn.linjonh.jsoup;

import javax.print.Doc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.linjonh.jsoup.util.ConnUtil;

public class StringTest {

	public StringTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// String t="共100页/";
		// // t=t.substring(1, t.length()-1);//100
		// t=t.substring(1, t.lastIndexOf("/"));//100页 [)
		// String docs=ConnUtil.decode("D:/detailImageItem.html", (byte)0);
		// // Document
		// doc=ConnUtil.getHtmlDocument("http://www.22mm.cc/mm/bagua/PmaHPHdPabJaHPbHb.html");
		// Document doc=Jsoup.parse(docs);
		// Elements els=doc.select("div#box-inner script");
		// String html=els.get(1).html();
		// int first=html.indexOf("\"");
		// int last=html.lastIndexOf("\"");
		// html=html.substring(first+1,last);
		//
		// // printStr(els.toString());
		// printStr("image url: "+html);
		// String union = "\u70B9\u51FB\u8FDB\u5165\u4E0B\u4E00\u9875";
		// printStr(union);
		testSplit();
	}

	private static void testSplit() {
		String src = "var arrayImg = new Array();"
				+ "arrayImg[0] = \"http://srimg1.meimei22.com/big/suren/2014-8-22/1/407899112014050822400403_640.jpg\";"
				+ "arrayImg[0] = \"http://srimg1.meimei22.com/big/suren/2014-8-22/1/4078991120140508224023013_640.jpg\";"
				+ "arrayImg[0] = \"http://srimg1.meimei22.com/big/suren/2014-8-22/1/407899112014050822404702_640.jpg\";"
				+ "arrayImg[0] = \"http://srimg1.meimei22.com/big/suren/2014-8-22/1/4078991120140508224114010_640.jpg\";"
				+ "getImgString()";
		String[] arrs = src.split(";");
		for (String arr : arrs) {
			printStr(arr);
		}
	}

	private static void printStr(String str) {
		System.out.println(str);
	}
}
