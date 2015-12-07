package cn.linjonh.jsoup.M5442;

import cn.linjonh.data.BasePreviewImageData;
import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jaysen.lin@foxmail.com
 * @since 2015/11/27
 * Project: JsoupLearning
 * package: cn.linjonh.jsoup.M5442
 */
public class M5442Com {
	private static java.lang.String url = "http://m.5442.com";

	public static void main(String[] args) {
//		getImageGridList().get(0);
//		Document doc = ConnUtil.getHtmlDocument("http://m.5442.com/meinv/");
//		Document doc = ConnUtil.getHtmlDocument("http://www.5442.com/meinv/index.html");
//		Utils.print(doc.toString());
		getSectionPageUrl();
		getDetailList(null);
//		getImageGridList("http://www.5442.com/meinv/index.html");
	}

	public static List<String> getSectionPageUrl() {
		Document doc = ConnUtil.getHtmlDocument("http://www.5442.com/meinv/index.html");
		Elements pages = doc.select(".page a");
		if (pages != null) {
			Element lastpageEl = pages.get(pages.size() - 1);
			String pageUrlPattern = lastpageEl.attr("href");
			String prefix = pageUrlPattern.substring(0, pageUrlPattern.lastIndexOf("_") + 1);
			String count = pageUrlPattern.substring(pageUrlPattern.lastIndexOf("_")+1).replace(".html", "");
		}
		return null;
	}

	public static List<BasePreviewImageData> getImageGridList(String url) {
		Document document = ConnUtil.getHtmlDocument(url);
		Elements tj = document.select(".tjlist li a");
		Elements mainList = document.select(".imgList li a");
		ArrayList<BasePreviewImageData> list = new ArrayList<>();
		//		Utils.print(document.toString());
//		Utils.print(tj.toString());
//		Utils.print(mainList.toString());
		if (tj != null) {
			for (Element element : tj) {
				BasePreviewImageData data = new BasePreviewImageData();
				data.albumSetUrl = element.attr("href");
				data.previewImgUrl = element.child(0).attr("lazysrc");
				data.title = element.child(0).attr("alt");
				Utils.print(data.toString());
				list.add(data);
			}
		}
		if (mainList != null) {
			for (Element element : mainList) {
				BasePreviewImageData data = new BasePreviewImageData();
				if (element.children().size() > 0) {
					data.albumSetUrl = element.attr("href");
					data.previewImgUrl = element.child(0).attr("src");
					data.title = element.child(0).attr("alt");
					Utils.print(data.toString());
					list.add(data);
				}
			}
		}
//		Utils.print(list.toString());
		return list;
	}

	public static List<String> getDetailList(BasePreviewImageData data) {
//		String url=data.albumSetUrl;
		String url = "http://www.5442.com/meinv/20151203/28969.html";
		Document doc = ConnUtil.getHtmlDocument(url);
		Utils.print(url);
		Elements els = doc.select(".arcBody p img");
		ArrayList<String> imageUrls = new ArrayList<>();
		//pagecount
		Element page = doc.select(".page a").get(0);
		String pageStr = page.text();
		int pageCount = 0;
		try {
			String indexCount = pageStr.substring(1).trim();
			indexCount = indexCount.substring(0, indexCount.length() - 2);
			pageCount = Integer.valueOf(indexCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//url
		String templateUrl = els.get(0).attr("src");
		String[] namePattern = parseImageNameFormat(templateUrl);
		for (int i = 1; i <= pageCount * els.size(); i++) {
			String name = namePattern[0] + i + namePattern[1];
//			Utils.print(name);
			imageUrls.add(name);
		}
		String lastPageUrl = url.replace(".html", "_" + pageCount + ".html");
		Document latPageDoc = ConnUtil.getHtmlDocument(lastPageUrl);
		int size = latPageDoc.select(".tal img").size();
		if (size < 2) {
			imageUrls.remove(imageUrls.size() - 1);
		}
		Utils.print(imageUrls.toString());
		return imageUrls;
	}

	private static String[] parseImageNameFormat(String url) {
		Utils.print(url);
		String name[] = new String[2];
		name[0] = url.substring(0, url.lastIndexOf("/") + 1);
		String tmp = url.substring(url.lastIndexOf("/"));
		name[1] = tmp.substring(tmp.indexOf("."));
		return name;
	}
}
