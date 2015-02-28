package cn.linjonh.jsoup;

import org.jsoup.nodes.Document;

import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.Utils;

public class TuiGirl {

	public TuiGirl() {
		// TODO Auto-generated constructor stub
	}
	public static final String mUrl="http://www.tuigirl.com";
	public static void main(String[] args) {        
		Document document=ConnUtil.getHtmlDocument(mUrl);
		Utils.print(document.toString());
	}
}
