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
 * û������ͼƬ
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
		HashMap<String, String> map = navNameURLlist.get(1);// ���Ը���ŮͼƬΪ��{�Ը���Ů=http://www.mm131.com/xinggan/}
		Iterator<String> ite = map.keySet().iterator();// Ԥ��ֻ����һ��map key��ĳ�����
			while (ite.hasNext()) {
			String htmlUrl = map.get(ite.next());
			Document childHtmlDoc = Jsoup.connect(htmlUrl).get();// ���ʰ����ҳ
			Elements childEls = childHtmlDoc.select("div.main  dl dd a[href]");
			// System.out.println(childHtmlDoc.toString());
			// System.out.println(childEls.toString());// ��ŮͼƬ����

			/*
			 * <a href="list_6_11.html" class="page-en">11</a> <a href="list_6_2.html"
			 * class="page-en">��һҳ</a>
			 */
			Elements pageEls = childEls.select("a[href][class]");// ҳ���ǩ
			// System.out.println(cEls.toString());
			int index = childEls.indexOf(pageEls.get(0));// ҳ���ǩ��chidEls��ʼ��λ��
			List<Element> imgHtmlLists = childEls.subList(0, index);// ȥ������Ҫ��,���
																	// ��ŮͼƬ����href����Ӧname��ǩ�б�
			visitWeb(htmlUrl, imgHtmlLists);

			}
		// }

		// System.out.println(navLists.toString());// ������
		// Elements as = lists.select("a");
		// System.out.println(as.toString());
	}

	/**
	 * @param htmlUrl
	 * @param imgHtmlLists
	 * @throws IOException
	 */
	public static void visitWeb(String htmlUrl, List<Element> imgHtmlLists) throws IOException {
		ArrayList<HashMap<String, String>> Items = new ArrayList<HashMap<String, String>>();// Ϊ�˺���վ˳��һֱ��list���map��ÿ��map��һ��item��Ŀ
		for (Element el : imgHtmlLists) {
			// <a target="_blank" href="http://www.mm131.com/xinggan/1415.html"><img
			// src="http://img1.mm131.com/pic/1415/0.jpg" alt="����������ŮDoris˽����" width="120"
			// height="160" />����������ŮDoris˽��</a>
			// System.out.println(el);
			String imgHtmlUrl = el.attr("href");
			String imgHtmlName = el.text();
			/*
			 * ����������ŮDoris˽��==>http://www.mm131.com/xinggan/1415.html
			 */
			// System.out.println(imgHtmlName + "==>" + imgHtmlUrl);
			HashMap<String, String> itemMap = new HashMap<String, String>();
			itemMap.put(imgHtmlName, imgHtmlUrl);// ��ס������ĳ����ɫ��Ƭ����ҳ��ַ
			Items.add(itemMap);// ������н�ɫ����ҳ��ַ
		}
		/**
		 * ����ĳ����ɫ��Ƭ��ҳ��ַ
		 */
		HashMap<String, String> personMap = Items.get(0);// ���ʵ�0��item��ɫ
		Iterator<String> personIterator = personMap.keySet().iterator();

		if (personIterator.hasNext()) {
			String key = personIterator.next();
			String personHtmlUrl = personMap.get(key);
//				while (!personHtmlUrl.equals("")) {
			Document personHtmlDoc = Jsoup.connect(personHtmlUrl).get();
			// System.out.println(personHtmlDoc);
			Elements contentPicEls = personHtmlDoc.select("div.content-pic");
			/**
			 * <div class="content-pic"> <a href="1415_2.html"><img alt="����������ŮDoris˽����(ͼ1)"
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
