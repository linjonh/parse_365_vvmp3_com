package cn.linjonh.jsoup.HDYouMa;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.Utils;

public class NvYouCollection {

	public static void main(String[] args) {
		Document docs = ConnUtil.getHtmlDocument("http://66.bbs333.com/av/");
		Elements els = docs.select("td a");
		els.remove(0);
		els.remove(0);
		for (Element el : els) {
			String url = "http://66.bbs333.com/av/" + el.attr("href");
			Document doc = ConnUtil.getHtmlDocument(url);

			Elements tdEls = doc.select("td");
			// AV person description
			String description = tdEls.get(0).html();
			println("<div>");
			println(description);
			Element imgSrcEl = tdEls.get(1);
			getImageUrlList(imgSrcEl);
			getVedioList(doc);
			println("</div>");
		}
		// System.out.println(els.toString());
	}

	private static ArrayList<String> getImageUrlList(Element imgSrcEl) {
		String imageHtmlurl = imgSrcEl.baseUri()
				+ imgSrcEl.select("iframe ").attr("src");
		ArrayList<String> list = new ArrayList<String>();
		Document doc = ConnUtil.getHtmlDocument(imageHtmlurl);
		Elements els = doc.select("img");
		for (Element el : els) {
			String imgUrl =el.attr("src");
			list.add(imgUrl);
			println("<img src=\"" + imgUrl + "\"/>");
		}
		return list;
	}

	private static ArrayList<String> getVedioList(Document doc) {
		Elements els = doc.select("a");
		ArrayList<String> list = new ArrayList<String>();
		for (Element el : els) {
			String video = el.attr("href");
			list.add(video);
			println(el.toString());
		}
		return list;
	}

	static void println(String str) {
		Utils.writeLog("E:/workspace/",str);
	}
}
