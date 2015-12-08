package cn.linjonh.jsoup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class XZtaoTu {

	public XZtaoTu() {
		// TODO Auto-generated constructor stub
	}

	static final String mUrl = "http://77.bbs560.com/xz/cn/";
	static final String mip = "http://198.44.250.182/xz/cn/";
	static final String filePathDir = "C:/XieZhenTaoTu";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("start");
//		final Document doc = ConnUtil.getHtmlDocument(mUrl);
		File in=new File(filePathDir+"/xz.html");
		try {
			final Document doc=Jsoup.parse(in,"gb2312");
			Elements alinks = doc.select("a");
			System.out.println(alinks);
			download(alinks);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 * @param alinks
	 * @return
	 */
	private static List<String> getAlinks(Elements alinks) {
		List<String> girdItemLinks = new ArrayList<String>();
		for (Element el : alinks) {
			System.out.println(el.attr("href"));
			girdItemLinks.add(el.attr("href"));
		}
		return girdItemLinks;
	}

	/**
	 * 
	 * @param imglinks
	 * @return
	 */
	private List<String> getImglinks(Elements imglinks) {
		List<String> girdItemImgLinks = new ArrayList<String>();

		for (Element el : imglinks) {
			System.out.println(el.attr("src"));
			girdItemImgLinks.add(el.attr("src"));
		}
		return girdItemImgLinks;
	}

	private static void download(Elements alinks) {
		List<String> alinklist = getAlinks(alinks);

		for (String link : alinklist) {
			if (link.contains("jpg")) {
//				DownloadUtil.DonwloadImg(link, filePathDir);
				System.out.println("skip 29 images here");
			} else {

			}
		}

		// Elements imglinks = alinks.select("img");
		// getImglinks(imglinks);

	}
}
