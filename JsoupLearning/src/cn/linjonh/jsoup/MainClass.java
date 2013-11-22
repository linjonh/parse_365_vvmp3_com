package cn.linjonh.jsoup;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class MainClass {

	public MainClass() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String html = "<html><head><title>First parse</title></head>"
				+ "<body><p>Parsed HTML into a doc.</p></body></html>";
		Document mainHtmDoc = // Jsoup.parse(html);
		Jsoup.connect("http://365.vvmp3.com/").get();
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
		Iterator<String> iterator=imgNameUrl.keySet().iterator();
		int count = 1;
		while (iterator.hasNext()) {
			String imgName = iterator.next();
			String imgUrl = imgNameUrl.get(imgName);
			/*
			 * ����ĳ������ͼƬ��
			 */
			Document aImgdocument = Jsoup.connect(imgUrl).get();
			for (Element imgElement : aImgdocument.select("img[src]")) {
				// ������0��img[src]����Ϊ���Ǹ�htmlͷ�����
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
				} else {
					saveUrlAs(baseUrl + uri, "C:/Img/" + imgFileName);
				}
			}
			count++;
		}
		// System.out.println(hrefs.toString());
		// System.out.println(relativeURL);
		// System.out.println(baseUrl);
		// System.out.println(imgTables);
		



	}
	public static boolean saveUrlAs(String fileUrl, String savePath)/* fileUrl������Դ��ַ */
	{

		try {
			URL url = new URL(fileUrl);/* ��������Դ��ַ����,����ֵ��url */
			/* ��Ϊ��ϵ���������Դ�Ĺ̶���ʽ�÷����Ա�����in�������url��ȡ������Դ�������� */
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			DataInputStream in = new DataInputStream(connection.getInputStream());
			/* �˴�Ҳ����BufferedInputStream��BufferedOutputStream */
			DataOutputStream out = new DataOutputStream(new FileOutputStream(savePath));
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
