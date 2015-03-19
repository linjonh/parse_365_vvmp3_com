package cn.linjonh.jsoup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.linjonh.jsoup.util.ConnUtil;

/**
 * <a href="http://www.mm131.com/">棣栭〉</a> <a
 * href="http://www.mm131.com/xinggan/">鎬ф劅缇庡コ</a> <a
 * href="http://www.mm131.com/qingchun/">娓呯函缇庣湁</a> <a
 * href="http://www.mm131.com/xiaohua/">缇庡コ鏍¤姳</a> <a
 * href="http://www.mm131.com/chemo/">鎬ф劅杞︽ā</a> <a
 * href="http://www.mm131.com/qipao/">鏃楄缇庡コ</a> <a
 * href="http://www.mm131.com/mingxing/">鏄庢槦鍐欑湡</a>
 * 
 * @author linjonh
 * 
 */
public class Mm131 {

	public Mm131() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Document doc = null;
		do {
			try {
				doc = ConnUtil.getHtmlDocument("http://www.mm131.com/");
			} catch (Exception e) {
			}
		} while (doc == null);
		// System.out.println(doc.toString());
		Elements navLists = doc.select("div.nav a[href]");

		ArrayList<HashMap<String, String>> navNameURLlist = new ArrayList<HashMap<String, String>>();
		for (Element el : navLists) {
			String name = el.text();
			String refUrl = el.attr("href");
			print(name + "==>" + refUrl);
			HashMap<String, String> map = new HashMap<String, String>();

			map.put(name, refUrl);
			navNameURLlist.add(map);
		}

		// for (int i = 1; i < navNameURLlist.size(); i++) {
		HashMap<String, String> map = navNameURLlist.get(1);// http://www.mm131.com/xinggan/}
		Iterator<String> ite = map.keySet().iterator();// map key
		while (ite.hasNext()) {
			String htmlUrl = map.get(ite.next());
			Document childHtmlDoc = ConnUtil.getHtmlDocument(htmlUrl);// Module
			Elements childEls = childHtmlDoc.select("div.main  dl dd a[href]");
			// System.out.println(childHtmlDoc.toString());
			System.out.println(childEls.toString());// 锟斤拷女图片锟斤拷锟斤拷

			/*
			 * <a href="list_6_11.html" class="page-en">11</a> <a
			 * href="list_6_2.html" class="page-en">锟斤拷一页</a>
			 */
			Elements pageEls = childEls.select("a[href][class]");// 页锟斤拷锟角�
			// System.out.println(cEls.toString());
			int index = childEls.indexOf(pageEls.get(0));// 页锟斤拷锟角╋拷锟絚hidEls锟斤拷始锟斤拷位锟斤拷
			List<Element> imgHtmlLists = childEls.subList(0, index);// 去锟斤拷锟斤拷锟斤拷要锟斤拷,锟斤拷锟�
																	// 锟斤拷女图片锟斤拷锟斤拷href锟斤拷锟斤拷应name锟斤拷签锟叫憋拷
			visitWeb(htmlUrl, imgHtmlLists);

		}
		// }

		// System.out.println(navLists.toString());// 锟斤拷锟斤拷锟斤拷
		// Elements as = lists.select("a");
		// System.out.println(as.toString());
	}

	/**
	 * @param htmlUrl
	 * @param imgHtmlLists
	 * @throws IOException
	 */
	public static void visitWeb(String htmlUrl, List<Element> imgHtmlLists) throws IOException {
		ArrayList<HashMap<String, String>> Items = new ArrayList<HashMap<String, String>>();// 为锟剿猴拷锟斤拷站顺锟斤拷一直锟斤拷list锟斤拷锟絤ap锟斤拷每锟斤拷map锟斤拷一锟斤拷item锟斤拷目
		for (Element el : imgHtmlLists) {
			// <a target="_blank"
			// href="http://www.mm131.com/xinggan/1415.html"><img
			// src="http://img1.mm131.com/pic/1415/0.jpg"
			// alt="锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷女Doris私锟斤拷锟斤拷" width="120"
			// height="160" />锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷女Doris私锟斤拷</a>
			// System.out.println(el);
			String imgHtmlUrl = el.attr("href");
			String imgHtmlName = el.text();
			/*
			 * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷女Doris私锟斤拷==>http://www.mm131.com/xinggan/1415.html
			 */
			// System.out.println(imgHtmlName + "==>" + imgHtmlUrl);
			HashMap<String, String> itemMap = new HashMap<String, String>();
			itemMap.put(imgHtmlName, imgHtmlUrl);// 锟斤拷住锟斤拷锟斤拷锟斤拷某锟斤拷锟斤拷色锟斤拷片锟斤拷锟斤拷页锟斤拷址
			Items.add(itemMap);// 锟斤拷锟斤拷锟斤拷薪锟缴拷锟斤拷锟揭筹拷锟街�
		}
		/**
		 * 锟斤拷锟斤拷某锟斤拷锟斤拷色锟斤拷片锟斤拷页锟斤拷址
		 */
		HashMap<String, String> personMap = Items.get(0);// 锟斤拷锟绞碉拷0锟斤拷item锟斤拷色
		Iterator<String> personIterator = personMap.keySet().iterator();

		if (personIterator.hasNext()) {
			String key = personIterator.next();
			String personHtmlUrl = personMap.get(key);
			// while (!personHtmlUrl.equals("")) {
			Document personHtmlDoc = ConnUtil.getHtmlDocument(personHtmlUrl);
			// System.out.println(personHtmlDoc);
			Elements contentPicEls = personHtmlDoc.select("div.content-pic");
			/**
			 * <div class="content-pic"> <a href="1415_2.html"><img
			 * alt="锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷女Doris私锟斤拷锟斤拷(图1)"
			 * src="http://img1.mm131.com/pic/1415/1.jpg" /></a> </div>
			 */
			Element imgEl = contentPicEls.get(0);
			String imgUrl = imgEl.select("img").attr("src");
			String imgBaseUri = htmlUrl;
			String imgRelativeUri = imgEl.select("a").get(0).attr("href");
			String imgNextHtmlUrl = imgBaseUri + imgRelativeUri;
			// System.out.println("imgElement" + imgEl);
			// System.out.println("imgBaseUri===" + imgBaseUri);
			// System.out.println("imgRelativeUri==" + imgRelativeUri);

			System.out.println("imgUrl==" + imgUrl);
			System.out.println("imgNextHtmlUrl==" + imgNextHtmlUrl);
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY_MM_dd_kk_mm_", Locale.CHINA);
			String appedDateInfo = dateFormat.format(date);
			String imgFileName = appedDateInfo + imgRelativeUri.substring(0, imgRelativeUri.lastIndexOf(".")) + "_"
					+ imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
			MainClass.saveUrlAs(imgUrl, "c:/Img/" + imgFileName);
			// personHtmlUrl = imgNextHtmlUrl;
			// }
		}
	}

	private static void print(String str) {
		System.out.println(str);
	}
}
