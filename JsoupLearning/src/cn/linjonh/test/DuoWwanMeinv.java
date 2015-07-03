package cn.linjonh.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.linjonh.data.BasePreviewImageData;
import cn.linjonh.jsoup.util.ConnUtil;

/**
 * 多玩美女
 * 
 * @author john
 *
 */
public class DuoWwanMeinv {
	public static final String duowanMeinvMaxOffsetUrl = "http://tu.duowan.com/m/meinv?offset=1195&order='created'&math=50";
	// public static final String imageItemUrl =
	// "http://tu.duowan.com/g/01/96/7d.html";
	public static final String imageMaxItemUrl = "http://tu.duowan.com/gallery/104493.html";
	public static final String meinvImgMaxItemUrl = "http://tu.duowan.com/g/01/4f/c2.html";
	public static final String gif = "http://tu.duowan.com/m/meinv?offset=0&order='created'&math=50";

	// 85954 01/4f/c2
	public static void main(String[] args) {
		// getOffsetItems();
		// getAlbumsImages("http://tu.duowan.com/g/01/97/f6.html");//common
		// image
		getAlbumsImages("http://tu.duowan.com/g/01/98/05.html");// GIF image
	}

	private static void getOffsetItems() {
		try {
			URL url = new URL(gif);
			URLConnection conn = url.openConnection();
			InputStream in = conn.getInputStream();
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = r.readLine()) != null) {
				builder.append(line);
			}
			// Document doc=ConnUtil.getHtmlDocument(gif);
			JSONObject json = new JSONObject(builder.toString());
			BasePreviewImageData data = new BasePreviewImageData();
			int nextOffset = json.getInt("offset");
			data.next = nextOffset;
			String html = json.getString("html");
			Document lis = Jsoup.parse(html);
			Elements els = lis.select("li");
			int i = 1;
			for (Element el : els) {
				Elements alinks = el.select("a");

				showMsg(i++ + "-----------------------------------------");
				data.previewImgUrl = alinks.select("img").attr("src");
				data.albumSetUrl = alinks.get(1).attr("href");
				data.title = alinks.text();
				data.timeline = el.select("em").text();
				// showMsg(data.toString());
				// getAlbumsImages(data.albumSetUrl);
//				break;
			}
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	public static List<String> getAlbumsImages(String link) {
		List<String> dataSets = new ArrayList<String>();
		link = link.replace("http://tu.duowan.com/g/", "").replace(".html", "")
				.replace("/", "");
		// showMsg(link);
		// showMsg("parsedInt :"+Integer.parseInt(link, 16));
		int num = -1;
		try {
			num = Integer.parseInt(link, 16);
		} catch (Exception e) {
		}
		if (num == -1) {
			return dataSets;
		}
		String srollUrl = "http://tu.duowan.com/scroll/" + num + ".html";
		Document doc = ConnUtil.getHtmlDocument(srollUrl);
		if(doc==null){
			return dataSets;
		}
		loadPage(doc, dataSets);
		Elements pages = doc.select("div.mod-page");
		if (pages != null && pages.size() > 0) {
			showMsg(pages.toString());
			for (Element el : pages) {
				Elements pageLinks = el.select("a");
				for (int i = 1; i < pageLinks.size(); i++) {
					String pageUrl = pageLinks.get(i).attr("href");
					srollUrl = "http://tu.duowan.com/" + pageUrl;
					Document pagedoc = ConnUtil.getHtmlDocument(srollUrl);
					if (pagedoc != null) {
						loadPage(pagedoc, dataSets);
					}
				}
			}
		}
		showMsg("size" + dataSets.size());
		return dataSets;
	}

	private static void loadPage(Document doc, List<String> dataSets) {
		Elements els = doc.select("div.pic-box");

		BasePreviewImageData data = new BasePreviewImageData();
		int i = 0;
		for (Element el : els) {
			String previewImageUrl = el.child(0).child(0).attr("src");
			data.previewImgUrl = previewImageUrl;
			dataSets.add(previewImageUrl);
		}
		// showMsg(els.toString());
	}

	public static void showMsg(String str) {
		System.out.println(str);
	}
}
