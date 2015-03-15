package cn.linjonh.jsoup;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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

public class MainClass {
	private static int count = 1;
	public MainClass() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// touBai();
		photoSelf();

	}
	public static void photoSelf() throws IOException {
		Document doc = Jsoup.connect("http://365.vvmp3.com/xtu").get();
		Elements lists = doc.select("a[href]");
		ArrayList<HashMap<String, String>> tempList = new ArrayList<HashMap<String, String>>();

		for (int i = 1; i < lists.size(); i++) {// i��ʼΪ1���˵���һ��i=0��"������ҳ"Ԫ��
			Element aElement = lists.get(i);
			HashMap<String, String> map = new HashMap<String, String>();
			String key = aElement.text();
			String value = aElement.attr("href");
			String baseUrl = aElement.baseUri();
			map.put(key, baseUrl + value);
			tempList.add(map);
		}
		for (HashMap<String, String> m : tempList) {
			Iterator<String> iterator = m.keySet().iterator();
			// while (iterator.hasNext()) {
			// String imgTitle = iterator.next();
			// }
			parseAndDownloadImg("http://365.vvmp3.com/", m, iterator);
		}
		// System.out.println(tempList);


	}
	/**
	 * @throws IOException
	 */
	public static void touBai() throws IOException {
		Document mainHtmDoc = Jsoup.connect("http://365.vvmp3.com/").get();
		Elements tables = mainHtmDoc.getElementsByTag("table");

		Elements hrefs = tables.get(2).select("a[href]");

		Element element = hrefs.get(5);// ͼƬ����
		String relativeURL = element.attr("href");
		String baseUrl = element.baseUri();
		String text = element.ownText();

		Document imgHtmlDoc = Jsoup.connect(baseUrl + relativeURL).get();
		Elements imgTables = imgHtmlDoc.getElementsByTag("table");
		Element allImgTable = imgTables.get(2);
		Elements hrefsList = allImgTable.select("a[href]");// ��ȡ�õ���ͼƬ�������������
		System.out.println(text + "��" + hrefsList.size() + "��");// �������
		System.out.println("==============================");
		HashMap<String, String> imgNameUrl = new HashMap<String, String>();
		for (Element first : hrefsList) {
			String aBaseUrl = first.baseUri();
			String aHref = first.attr("href");// ��Ե�ַ
			String aText = first.child(0).child(0).ownText();// ��Ӧ����
			// System.out.println(aText + ":" + aBaseUrl + aHref);
			imgNameUrl.put(aText, aBaseUrl + aHref);
		}
		Iterator<String> iterator = imgNameUrl.keySet().iterator();

		parseAndDownloadImg(baseUrl, imgNameUrl, iterator);
		// System.out.println(hrefs.toString());
		// System.out.println(relativeURL);
		// System.out.println(baseUrl);
		// System.out.println(imgTables);
	}

	/**
	 * @param baseUrl
	 * @param imgNameUrl
	 * @param iterator
	 * @throws IOException
	 */
	public static void parseAndDownloadImg(String baseUrl, HashMap<String, String> imgNameUrl,
			Iterator<String> iterator) throws IOException {

		while (iterator.hasNext()) {
			String imgName = iterator.next();
			String imgUrl = imgNameUrl.get(imgName);
			/*
			 * ����ĳ������ͼƬ��
			 */
			Document aImgdocument = Jsoup.connect(imgUrl).get();
			Elements lists =aImgdocument.select("img[src]");
			/*
			 * i��ʼΪ1���˵���һ��i=0��"html_titleͼƬ"Ԫ�� ,������0��img[src]����Ϊ���Ǹ�htmlͷ�����
			 */
			for (int i = 1; i < lists.size(); i++) {
				Element imgElement = lists.get(i);
				String uri = imgElement.attr("src");// ͼƬ��ַ����Щ�¸���վ����Ե�ַ����Щ��������վ�ľ��Ե�ַ������Ҫ�ж�
				// System.out.println(aImgdocument.toString());
				// System.out.println(imgElement.toString());
				Date date = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY_MM_dd_kk_mm_",
						Locale.CHINA);
				String appedDateInfo = dateFormat.format(date);
				String imgFileName = appedDateInfo + count + "_"
						+ uri.substring(uri.lastIndexOf("/") + 1);

				if (uri.contains("http")) {
					// ֱ��URL��loadImage����
					saveUrlAs(uri, "C:/Img/" + imgFileName);
					count++;
				} else {
					if (baseUrl.endsWith("/"))
						baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
					if (imgFileName.endsWith("fengexian.gif"))
						continue;
					else {
						saveUrlAs(baseUrl + uri, "C:/Img/" + imgFileName);
						count++;
					}
				}
			}
		}
	}
	public static boolean saveUrlAs(String fileUrl, String savePath)/* fileUrl������Դ��ַ */
	{

		try {
			URL url = new URL(fileUrl);/* ��������Դ��ַ����,����ֵ��url */
			/* ��Ϊ��ϵ���������Դ�Ĺ̶���ʽ�÷����Ա�����in�������url��ȡ������Դ�������� */
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			DataInputStream in = new DataInputStream(connection.getInputStream());
			/* �˴�Ҳ����BufferedInputStream��BufferedOutputStream */
			String dirP=savePath.substring(0, savePath.lastIndexOf("/"));
			File dir=new File(dirP);
			if(!dir.exists()){
				dir.mkdirs();
			}
			File file = new File(savePath);
			
			DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
			/* ������savePath��������ȡ��ͼƬ�Ĵ洢�ڱ��ص�ַ��ֵ��out�������ָ���ĵ�ַ */
			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = in.read(buffer)) > 0)/* �����������ֽڵ���ʽ��ȡ��д��buffer�� */
			{
				out.write(buffer, 0, count);
			}
			out.close();/* ��������Ϊ�ر�����������Լ�������Դ�Ĺ̶���ʽ */
			in.close();
			connection.disconnect();
			System.out.println(fileUrl + "\n" + savePath);
			return true;/* ������Դ��ȡ���洢���سɹ�����true */

		} catch (Exception e) {
			System.out.println(e + "\n" + fileUrl + "\n" + savePath);
			return false;
		}
	}

}
