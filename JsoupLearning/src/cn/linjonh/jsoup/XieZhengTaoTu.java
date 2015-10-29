package cn.linjonh.jsoup;

import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.DonwloadUtil;
import cn.linjonh.jsoup.util.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class XieZhengTaoTu {

	static final String  mUrl        = "http://77.bbs560.com/xz/cn/";
	static final String  mip         = "http://198.44.250.182/xz/cn/";
	static final String  filePathDir = "D:/TuiGirlXieZhen";
	static final boolean isOmei      = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("start");
		final Document doc = ConnUtil.getHtmlDocument(mUrl);
		Elements alinks = doc.select("a");
//		download(alinks, isOmei);
		System.out.println(alinks);
//		readFileFromLocal();
//		readOmeiFileFromLocal();

	}

	private static void readOmeiFileFromLocal() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				File omin = new File(filePathDir + "/xzoumei.html");
				try {
					final Document doc = Jsoup.parse(omin, "gb2312");
					Elements alinks = doc.select("a");
					// System.out.println(alinks);
					download(alinks, true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private static void readFileFromLocal() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				File in = new File(filePathDir + "/xz.html");

				try {
					final Document doc = Jsoup.parse(in, "gb2312");
					Elements alinks = doc.select("a");
					// System.out.println(alinks);
					download(alinks, false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * @param alinks parse html to get all image album links
	 * @return
	 */
	private static List<String> getAlinks(Elements alinks) {
		List<String> girdItemLinks = new ArrayList<String>();
		for (Element el : alinks) {
			girdItemLinks.add(el.attr("href"));
			// System.out.println("getAlinks: " + el.attr("href"));
			// print("baseUri:" + el.baseUri());
		}
		return girdItemLinks;
	}

	/**
	 * @param imglinks
	 * @return
	 */
	private List<String> getImglinks(Elements imglinks) {
		List<String> girdItemImgLinks = new ArrayList<String>();

		for (Element el : imglinks) {
			System.out.println("getImglinks: " + el.attr("src"));
			girdItemImgLinks.add(el.attr("src"));
			print("baseUri:" + el.baseUri());
		}
		return girdItemImgLinks;
	}

	/**
	 * @param alinks all album links
	 */
	private static void download(Elements alinks, boolean isOuMei) {
		List<String> alinklist = getAlinks(alinks);
		int count = 1;
		CountDownLatch latch = new CountDownLatch(9);
		boolean flag = false;
		ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 16, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
		for (String link : alinklist) {
			if (link.contains("jpg")) {
				// DonwloadUtil.DonwloadImg(link, filePathDir);
				System.out.println("skip " + count++ + " images here");
			} else {
				// http://xz5.mm667.com/tnl45/
				print("downloadDetailAlbum:" + link);
				if (isOuMei) {
					downloadDetailAlbum(link);
				} else {
//					http://xz1.mm667.com/xz42/
//					http://xz1.mm667.com/xz43/
//					http://xz1.mm667.com/xz44/待续哎
//					http://xz1.mm667.com/xz21/待续哎
//					if ("http://xz1.mm667.com/xz42/".equals(link)) {
//						flag = true;
//					}
//					if (flag) {
//						downloadDetailAlbum(link);
//					}
					executor.execute(new Runnable() {
						@Override
						public void run() {
							downloadDetailAlbum(link);
							latch.countDown();
						}
					});
				}

			}
		}

		try {
			latch.await();
			executor.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param url detail album url
	 */
	private static void downloadDetailAlbum(String url) {
		Document document = ConnUtil.getHtmlDocument(url);
		if (document == null) {
			do {
				Utils.print(Utils.getFormatedTime() + "downloadDetailAlbum: doc is null, trying connect again...");
				document = ConnUtil.getHtmlDocument(url);
			} while (document == null);
			Utils.print(Utils.getFormatedTime() + "-->downloadDetailAlbum: document is not null now ");
		}
		Elements elements = document.select("option");
		Utils.print("image pageSize:" + elements.size());
		ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 16, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
		final CountDownLatch downLatch = new CountDownLatch(elements.size());
		for (Element element : elements) {
			// print("baseUri: " + element.baseUri());
			final String link = element.baseUri() + element.attr("value");
			// print("downloadDetailAlbum:"+link);
			Thread seed = new Thread(new Runnable() {
				@Override
				public void run() {
					Utils.print("currentThread id " + Thread.currentThread().getId());
					DonwloadUtil.donwloadImg(link, filePathDir, filePathDir);
					downLatch.countDown();
				}
			});
			executor.execute(seed);
		}
		try {
			downLatch.await();
			executor.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void print(String str) {
		System.out.println(str);
	}
}
