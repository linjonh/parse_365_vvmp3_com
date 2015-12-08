package cn.linjonh.jsoup.Bouncebreak;

import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @author jaysen.lin@foxmail.com
 * @since 2015/9/23
 * Project: JsoupLearning
 * package: cn.linjonh.jsoup.Bouncebreak
 */
public class Bounce {
	//aHR0cDovL2JvdW5jZWJyZWFrLmNvbS9wYWdlLw==
	static final String url = "http://bouncebreak.com/page/";

	public static void main(String[] args) {
		Document doc = ConnUtil.getHtmlDocument(url + 1);
//		Elements els = doc.select("p img");
		Elements pagination =doc.select(".pagination span");
//		Utils.print(els.toString());
//		Utils.print(pagination.toString());
//		Utils.print(pagination.text());
		String[] arr=pagination.get(0).text().split("of");
		Utils.print(arr[1].trim());
	}


}
