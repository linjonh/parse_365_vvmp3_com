/**
 * 性感美女图吧
 * http://tu.meinvdd.com/meinv/
 */
package cn.linjonh.jsoup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.DonwloadUtil;

/**
 * @author linjonh
 *
 */
public class SexyMeiNv {
	public static int COUNT = 1;

	/**
	 * 
	 */
	public SexyMeiNv() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// String mainHtmlUrl = "http://tu.meinvdd.com/meinv/";
		String mainHtmlUrl = "http://pic.psstone.net/meinv/";
		Document doc = Jsoup.connect(mainHtmlUrl).get();
		// System.out.println(doc.toString());
		Elements mainLists = doc.select("div.mainlist");
		
		Elements pageLists = mainLists.select("div.listpages a[href]");
		ArrayList<HashMap<String, String>> pageMapLists = createNameValueMapLists(pageLists);
		Elements imgHtmlLists = mainLists.select("ul li a[href]");

		ArrayList<HashMap<String, String>> imgHtmlMapLists = createNameValueMapLists(imgHtmlLists);
		/*
		 * loop and downlod images
		 */
		// lookImg(imgHtmlMapLists);
		
		for (int i = 1; i < pageMapLists.size() - 1; i++) {// i遍历去掉最后一个和第一个重复的
			HashMap<String, String> page = pageMapLists.get(i);
			Iterator<String> iterator=page.keySet().iterator();
			if(iterator.hasNext()){
				String htmlUrl=page.get(iterator.next());
				Document document = ConnUtil.getHtmlDocument(mainHtmlUrl + htmlUrl);

				// System.out.println(document);
				// System.out.println("==================================================");
				Elements nextMainLists = document.select("div.mainlist");// 图片列表
				Elements nextImgHtmlLists = nextMainLists.select("ul li a[href]");

				ArrayList<HashMap<String, String>> nextImgHtmlMapLists = createNameValueMapLists(nextImgHtmlLists);
				lookImg(nextImgHtmlMapLists);
			}
			
		}

		

		// System.out.println(mainLists.toString());

		// System.out.println("imgLists\n" + imgHtmlLists.toString());
		System.out.println("pageLists\n" + pageLists.toString());
	}

	/**
	 * 创建角色图片主题Name-htmlUrl lists
	 * 
	 * @param imgHtmlLists
	 *            形如：<a href="http://tu.meinvdd.com/meinv/5977.html" target="_blank"><img
	 *            alt="美艳模特袒胸露乳写真照" src="http://a.6544.cc/uploads/d131103/10571234_139_162.jpg"
	 *            width="139" height="162" />美艳模特袒胸露乳写真照</a>
	 */
	public static ArrayList<HashMap<String, String>> createNameValueMapLists(Elements imgHtmlLists) {
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
		return Items;
	}

	/**
	 * 再创建了name-htmlUrl后，查看img链接
	 * 
	 * @param imgHtmlMapLists
	 */
	public static void lookImg(ArrayList<HashMap<String, String>> imgHtmlMapLists) {
		for (int i = 0; i < imgHtmlMapLists.size(); i++) {
			HashMap<String, String> map = imgHtmlMapLists.get(i);
			Iterator<String> iterator = map.keySet().iterator();
			if (iterator.hasNext()) {
				String key = iterator.next();// 在createNameValueMapLists方法中只放了一个key
				String anImgHtmUrl = map.get(key);
				Document document = ConnUtil.getHtmlDocument(anImgHtmUrl);
				if(document!=null){
					Elements mainPics = document.select("#oldp a[href] img");
					// System.out.println("mainPics==>\n" + mainPics);
					/*
					 * 遍历图片并保存
					 */
					for (int j = 0; j < mainPics.size(); j++) {
						String imgFileUrl = mainPics.get(j).attr("src");

						String imgFileName = getFileName(imgFileUrl, COUNT);
						COUNT++;
						DonwloadUtil.DonwloadImg(imgFileUrl, "C:/Img/" + imgFileName, key);
					}
				}
			}
		}
	}

	/**
	 * @param imgFileUrl
	 * @return
	 */
	public static String getFileName(String imgFileUrl, int count) {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY_MM_dd_HH_mm_",
				Locale.CHINA);
		String appedDateInfo = dateFormat.format(date);
		String imgFileName = appedDateInfo + count + "_"
				+ imgFileUrl.substring(imgFileUrl.lastIndexOf("/") + 1);
		return imgFileName;

	}

}
