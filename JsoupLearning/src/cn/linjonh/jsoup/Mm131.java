package cn.linjonh.jsoup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 没法下载图片
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
		// TODO Auto-generated method stub
		Document doc = Jsoup.connect("http://www.mm131.com/").get();
		// System.out.println(doc.toString());
		Elements navLists = doc.select("div.nav a[href]");
		ArrayList<HashMap<String, String>> navNameURLlist = new ArrayList<HashMap<String, String>>();
		for (Element el : navLists) {
			String name = el.text();
			String refUrl = el.attr("href");
			// System.out.println(name + "==>" + refUrl);
			HashMap<String, String> map = new HashMap<String, String>();

			map.put(name, refUrl);
			navNameURLlist.add(map);
		}
		// for (int i = 1; i < navNameURLlist.size(); i++) {
		HashMap<String, String> map = navNameURLlist.get(1);// 以性感美女图片为例{性感美女=http://www.mm131.com/xinggan/}
		Iterator<String> ite = map.keySet().iterator();// 预先只放了一个map key即某个版块
			while (ite.hasNext()) {
			String htmlUrl = map.get(ite.next());
			Document childHtmlDoc = Jsoup.connect(htmlUrl).get();// 访问版块网页
			Elements childEls = childHtmlDoc.select("div.main  dl dd a[href]");
			// System.out.println(childHtmlDoc.toString());
			// System.out.println(childEls.toString());// 美女图片方格

			/*
			 * <a href="list_6_11.html" class="page-en">11</a> <a href="list_6_2.html"
			 * class="page-en">下一页</a>
			 */
			Elements pageEls = childEls.select("a[href][class]");// 页码标签
			// System.out.println(cEls.toString());
			int index = childEls.indexOf(pageEls.get(0));// 页码标签在chidEls开始的位置
			List<Element> imgHtmlLists = childEls.subList(0, index);// 去除不需要的,获得
																	// 美女图片方格href及对应name标签列表
			visitWeb(htmlUrl, imgHtmlLists);

			}
		// }

		// System.out.println(navLists.toString());// 导航栏
		// Elements as = lists.select("a");
		// System.out.println(as.toString());
	}

	/**
	 * @param htmlUrl
	 * @param imgHtmlLists
	 * @throws IOException
	 */
	public static void visitWeb(String htmlUrl, List<Element> imgHtmlLists) throws IOException {
		ArrayList<HashMap<String, String>> Items = new ArrayList<HashMap<String, String>>();// 为了和网站顺序一直用list存放map，每个map放一个item条目
		for (Element el : imgHtmlLists) {
			// <a target="_blank" href="http://www.mm131.com/xinggan/1415.html"><img
			// src="http://img1.mm131.com/pic/1415/0.jpg" alt="宝岛美腿美女Doris私房照" width="120"
			// height="160" />宝岛美腿美女Doris私房</a>
			// System.out.println(el);
			String imgHtmlUrl = el.attr("href");
			String imgHtmlName = el.text();
			/*
			 * 宝岛美腿美女Doris私房==>http://www.mm131.com/xinggan/1415.html
			 */
			// System.out.println(imgHtmlName + "==>" + imgHtmlUrl);
			HashMap<String, String> itemMap = new HashMap<String, String>();
			itemMap.put(imgHtmlName, imgHtmlUrl);// 记住板块里的某个角色照片集网页地址
			Items.add(itemMap);// 存放所有角色的网页地址
		}
		/**
		 * 访问某个角色照片网页地址
		 */
		HashMap<String, String> personMap = Items.get(0);// 访问第0个item角色
		Iterator<String> personIterator = personMap.keySet().iterator();

		if (personIterator.hasNext()) {
			String key = personIterator.next();
			String personHtmlUrl = personMap.get(key);
//				while (!personHtmlUrl.equals("")) {
			Document personHtmlDoc = Jsoup.connect(personHtmlUrl).get();
			// System.out.println(personHtmlDoc);
			Elements contentPicEls = personHtmlDoc.select("div.content-pic");
			/**
			 * <div class="content-pic"> <a href="1415_2.html"><img alt="宝岛美腿美女Doris私房照(图1)"
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
			String imgFileName = appedDateInfo
					+ imgRelativeUri.substring(0, imgRelativeUri.lastIndexOf(".")) + "_"
					+ imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
			MainClass.saveUrlAs(imgUrl, "c:/Img/" + imgFileName);
//					personHtmlUrl = imgNextHtmlUrl;
			// }
		}
	}

}
