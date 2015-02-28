package cn.linjonh.jsoup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.linjonh.jsoup.meizitu.MeizituEntity;

public class MeizituTestVersion {
	static final String web_2 = "http://www.meizitu.com";
	private static ArrayList<MeizituEntity> mLists = new ArrayList<MeizituEntity>();
	static Document meizitu = null;

	public MeizituTestVersion() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Document meizitu = ConnUtil.getHtmlDocument(web_2);

		try {
			meizitu = Jsoup.parse(new File("D:\\H5\\H5_Meizitu\\meiztu.html"),
					"utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (meizitu != null) {
			// System.out.println(meizitu.toString());
			Elements titles = meizitu.select(".metaRight a");
			Elements pics = meizitu.select("#picture a");

			// if (titles != null)
			System.out.println(titles.size());
			System.out.println(pics.size());
			if (pics.size() != 0) {
				// System.out.println(pic.toString());
				for (int i = 0; i < pics.size(); i++) {
					Element pic = pics.get(i);
					Element title = titles.get(i);

					MeizituEntity entity = new MeizituEntity();

					String detailHtmlURL = pic.attr("href");
					String picThumb = pic.select("img").attr("data-original");
					// System.out.println(detailHtmlURL+"===>"+picThumb);
					entity.setTitles(title.text());
					entity.setPicDetailURL(detailHtmlURL);
					entity.setPicThumbURL(picThumb);
					mLists.add(entity);
				}
			} else {
				System.out.println("empty");
			}

			Elements pgs = meizitu.select("#wp_page_numbers li a");
			for (Element el : pgs) {
				System.out.println(el.toString());
			}
		} else {
			System.out.println("document null");
		}
		// for (MeizituEntity en : mLists) {
		// System.out.println(en.getTitles());
		// System.out.println(en.getPicDetailURL());
		// System.out.println(en.getPicThumbURL() + "\n");
		// }

	}

	public static ArrayList<String> getNextHtmlPageLists() {
		ArrayList<String> list = new ArrayList<String>();

		if (meizitu != null) {
			Elements pgs = meizitu.select("li .thisclass");
		}

		return list;

	}
}
