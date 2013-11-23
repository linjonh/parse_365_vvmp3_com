package cn.linjonh.jsoup.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ConnUtil {

	public ConnUtil() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 链接网页获取html文档
	 * 
	 * @param htmlUrl
	 *            http协议地址
	 * @return Document
	 */
	public static Document getHtmlDocument(String htmlUrl) {
		try {
			Document document = Jsoup.connect(htmlUrl).get();
			return document;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
}
