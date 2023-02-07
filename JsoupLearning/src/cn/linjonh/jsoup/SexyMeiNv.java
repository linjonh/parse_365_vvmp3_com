/**
 * �Ը���Ůͼ��
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

import cn.linjonh.jsoup.util.DownloadUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.linjonh.jsoup.util.ConnUtil;

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
		
		for (int i = 1; i < pageMapLists.size() - 1; i++) {// i����ȥ�����һ���͵�һ���ظ���
			HashMap<String, String> page = pageMapLists.get(i);
			Iterator<String> iterator=page.keySet().iterator();
			if(iterator.hasNext()){
				String htmlUrl=page.get(iterator.next());
				Document document = ConnUtil.getHtmlDocument(mainHtmlUrl + htmlUrl);

				// System.out.println(document);
				// System.out.println("==================================================");
				Elements nextMainLists = document.select("div.mainlist");// ͼƬ�б�
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
	 * ������ɫͼƬ����Name-htmlUrl lists
	 * 
	 * @param imgHtmlLists
	 *            ���磺<a href="http://tu.meinvdd.com/meinv/5977.html" target="_blank"><img
	 *            alt="����ģ��̻��¶��д����" src="http://a.6544.cc/uploads/d131103/10571234_139_162.jpg"
	 *            width="139" height="162" />����ģ��̻��¶��д����</a>
	 */
	public static ArrayList<HashMap<String, String>> createNameValueMapLists(Elements imgHtmlLists) {
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
		return Items;
	}

	/**
	 * �ٴ�����name-htmlUrl�󣬲鿴img����
	 * 
	 * @param imgHtmlMapLists
	 */
	public static void lookImg(ArrayList<HashMap<String, String>> imgHtmlMapLists) {
		for (int i = 0; i < imgHtmlMapLists.size(); i++) {
			HashMap<String, String> map = imgHtmlMapLists.get(i);
			Iterator<String> iterator = map.keySet().iterator();
			if (iterator.hasNext()) {
				String key = iterator.next();// ��createNameValueMapLists������ֻ����һ��key
				String anImgHtmUrl = map.get(key);
				Document document = ConnUtil.getHtmlDocument(anImgHtmUrl);
				if(document!=null){
					Elements mainPics = document.select("#oldp a[href] img");
					// System.out.println("mainPics==>\n" + mainPics);
					/*
					 * ����ͼƬ������
					 */
					for (int j = 0; j < mainPics.size(); j++) {
						String imgFileUrl = mainPics.get(j).attr("src");

						String imgFileName = getFileName(imgFileUrl, COUNT);
						COUNT++;
						DownloadUtil.downloadImg(imgFileUrl, "C:/Img/" + imgFileName, key);
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
