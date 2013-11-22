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

		Element element = hrefs.get(5);// 图片标题
		String relativeURL = element.attr("href");
		String baseUrl = element.baseUri();
		String text = element.ownText();

		Document imgHtmlDoc = Jsoup.connect(baseUrl + relativeURL).get();
		Elements imgTables = imgHtmlDoc.getElementsByTag("table");
		Element allImgTable = imgTables.get(2);
		Elements hrefsList = allImgTable.select("a[href]");// 获取得到了图片主题的所有链接
		System.out.println(text + "共" + hrefsList.size() + "条");// 版块主题
		System.out.println("==============================");
		HashMap<String, String> imgNameUrl = new HashMap<String, String>();
		for (Element first : hrefsList) {
			String aBaseUrl = first.baseUri();
			String aHref = first.attr("href");// 相对地址
			String aText = first.child(0).child(0).ownText();// 对应名称
			// System.out.println(aText + ":" + aBaseUrl + aHref);
			imgNameUrl.put(aText, aBaseUrl + aHref);
		}
		Iterator<String> iterator=imgNameUrl.keySet().iterator();
		int count = 1;
		while (iterator.hasNext()) {
			String imgName = iterator.next();
			String imgUrl = imgNameUrl.get(imgName);
			/*
			 * 解析某条标题图片集
			 */
			Document aImgdocument = Jsoup.connect(imgUrl).get();
			for (Element imgElement : aImgdocument.select("img[src]")) {
				// 跳过第0个img[src]，以为这是个html头条广告
				String uri = imgElement.attr("src");// 图片地址，有些事该网站的相对地址，有些是其他网站的绝对地址，所以要判断
				// System.out.println(aImgdocument.toString());
				// System.out.println(imgElement.toString());
				Date date = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY_MM_dd_kk_mm_",
						Locale.CHINA);
				String appedDateInfo = dateFormat.format(date);
				String imgFileName = appedDateInfo + count + "_"
						+ uri.substring(uri.lastIndexOf("/") + 1);
				if (uri.contains("http")) {
					// 直接URL。loadImage（）
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
	public static boolean saveUrlAs(String fileUrl, String savePath)/* fileUrl网络资源地址 */
	{

		try {
			URL url = new URL(fileUrl);/* 将网络资源地址传给,即赋值给url */
			/* 此为联系获得网络资源的固定格式用法，以便后面的in变量获得url截取网络资源的输入流 */
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			DataInputStream in = new DataInputStream(connection.getInputStream());
			/* 此处也可用BufferedInputStream与BufferedOutputStream */
			DataOutputStream out = new DataOutputStream(new FileOutputStream(savePath));
			/* 将参数savePath，即将截取的图片的存储在本地地址赋值给out输出流所指定的地址 */
			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = in.read(buffer)) > 0)/* 将输入流以字节的形式读取并写入buffer中 */
			{
				out.write(buffer, 0, count);
			}
			out.close();/* 后面三行为关闭输入输出流以及网络资源的固定格式 */
			in.close();
			connection.disconnect();
			System.out.println(fileUrl + "\n" + savePath);
			return true;/* 网络资源截取并存储本地成功返回true */

		} catch (Exception e) {
			System.out.println(e + "\n" + fileUrl + "\n" + savePath);
			return false;
		}
	}

}
