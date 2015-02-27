package cn.linjonh.jsoup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.DonwloadUtil;
import cn.linjonh.jsoup.util.Utils;

public class XieZhengTaoTu {

	public XieZhengTaoTu() {
		// TODO Auto-generated constructor stub
	}

	static final String mUrl = "http://77.bbs560.com/xz/cn/";
	static final String mip = "http://198.44.250.182/xz/cn/";
	static final String filePathDir = "C:/XieZhenTaoTu";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("start");
		// final Document doc = ConnUtil.getHtmlDocument(mUrl);
		File in = new File(filePathDir + "/xz.html");
		try {
			final Document doc = Jsoup.parse(in, "gb2312");
			Elements alinks = doc.select("a");
			System.out.println(alinks);
			download(alinks);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param alinks
	 * @return
	 */
	private static List<String> getAlinks(Elements alinks) {
		List<String> girdItemLinks = new ArrayList<String>();
		for (Element el : alinks) {
			System.out.println("getAlinks: " + el.attr("href"));
			girdItemLinks.add(el.attr("href"));
			print("baseUri:" + el.baseUri());
		}
		return girdItemLinks;
	}

	/**
	 * 
	 * @param imglinks
	 * @return
	 */
	private List<String> getImglinks(Elements imglinks) {
		List<String> girdItemImgLinks = new ArrayList<String>();

		for (Element el : imglinks) {
			System.out.println("getImglinks: " + el.attr("src"));
			girdItemImgLinks.add(el.attr("src"));
			print("baseUri:" + el.baseUri());
		}
		return girdItemImgLinks;
	}

	private static void download(Elements alinks) {
		List<String> alinklist = getAlinks(alinks);
		int count = 1;
		for (String link : alinklist) {
			if (link.contains("jpg")) {
				// DonwloadUtil.DonwloadImg(link, filePathDir);
				System.out.println("skip " + count++ + " images here");
			} else {
				// http://xz5.mm667.com/tnl45/
				downloadDetailAlbum(link);
			}
		}

		// Elements imglinks = alinks.select("img");
		// getImglinks(imglinks);

	}

	private static void downloadDetailAlbum(String url) {
		Document document = ConnUtil.getHtmlDocument(url);
		Elements elements = document.select("option");
		for (Element element : elements) {
//			print("baseUri: " + element.baseUri());
			String link = element.baseUri() + element.attr("value");
			print("downloadDetailAlbum:"+link);
			DonwloadUtil.DonwloadImg(link, filePathDir);
		}
	}

	private static void print(String str) {
		System.out.println(str);
	}
}
