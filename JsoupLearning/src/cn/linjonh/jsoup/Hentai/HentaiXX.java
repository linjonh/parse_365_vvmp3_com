package cn.linjonh.jsoup.Hentai;

import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.Utils;
import org.jsoup.nodes.Document;

/**
 * @author jaysen.lin@foxmail.com
 * @since 2015/10/29
 * Project: JsoupLearning
 * package: cn.linjonh.jsoup.Hentai
 */
public class HentaiXX {
	public static void main(String[] args) {
		Document doc=ConnUtil.getHtmlDocument("http://im.dcf82700.00f365c.cdnb.movies.hostedtube.com/1/1045/1045091/NOWATERMARK_240.mp4?s=1446084428&e=1446091628&ri=2063&rs=50&ip=220.248.39.54&h=249554a47aeb1749cb91dc55912865c7");
		Utils.print(doc.toString());
	}
}
