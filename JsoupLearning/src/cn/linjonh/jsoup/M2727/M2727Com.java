package cn.linjonh.jsoup.M2727;

import cn.linjonh.data.BasePreviewImageData;
import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.Utils;
import org.jsoup.nodes.Document;

import java.util.List;

/**
 * @author jaysen.lin@foxmail.com
 * @since 2015/11/3
 * Project: JsoupLearning
 * package: cn.linjonh.jsoup.M2727
 */
public class M2727Com {
	public static final String url = "http://m.27270.com";

	public static void main(String[] args) {
		getImageGridList();
	}

	public static List<BasePreviewImageData> getImageGridList() {
		Document document = ConnUtil.getHtmlDocument(url);
		Utils.print(document.toString());
		return null;
	}
}
