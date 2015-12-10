package cn.linjonh.jsoup.M5442;


import cn.linjonh.data.BasePreviewImageData;
import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.DownloadUtil;
import cn.linjonh.jsoup.util.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jaysen.lin@foxmail.com
 * @since 2015/11/27
 * Project: JsoupLearning
 * package: cn.linjonh.jsoup.M5442
 */
public class M5442Com {
	private static final String path    = "D:\\M5442_IMG\\";
	private static final String LogPath = path + "Log/";
	//    private static String url = "http://m.5442.com";
	private static String prefix;
	private static int    pageCount;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		getImageGridList().get(0);
//		Document doc = ConnUtil.getHtmlDocument("http://m.5442.com/meinv/");
//		Document doc = ConnUtil.getHtmlDocument("http://www.5442.com/meinv/index.html");
//		Utils.print(doc.toString());
//		getDetailList(getImageGridList().get(0));
//        getImageGridList("http://www.5442.com/meinv/index.html");
		int index = 1;
		if (args != null && args.length > 0) {
			try {
				index = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		getSectionPageCount();
		for (int i = index; i <= pageCount; i++) {
			getImageGridList(i);
			Utils.print("main loop index: " + i);
			Utils.writeLog(path + "MainLoopIndexArgs/", "main loop index: " + i);
		}
	}

	public static int getSectionPageCount() {
		Document doc = ConnUtil.getHtmlDocument("http://www.5442.com/meinv/", LogPath);
		Elements pages = doc.select(".page a");
		int countNum = 0;
		if (pages != null) {
			Element lastpageEl = pages.get(pages.size() - 1);
			String pageUrlPattern = lastpageEl.attr("href");
			prefix = doc.baseUri() + pageUrlPattern
					.substring(0, pageUrlPattern.lastIndexOf("_") + 1);
			String count = pageUrlPattern.substring(pageUrlPattern.lastIndexOf("_") + 1)
					.replace(".html", "");

			try {
				pageCount = countNum = Integer.valueOf(count);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return countNum;
	}

	public static List<BasePreviewImageData> getImageGridList(int pageIndex) {
		String url = prefix + pageIndex + ".html";
		Document document = ConnUtil.getHtmlDocument(url, LogPath);
		Elements mainList = document.select(".imgList li a");

		ArrayList<BasePreviewImageData> list = new ArrayList<>();
		if (mainList != null) {
			Utils.print("mainList.size() " + mainList.size());
			ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 8, 2, TimeUnit.SECONDS,
					new LinkedBlockingQueue<>());
			CountDownLatch downLatch = new CountDownLatch(mainList.size());
			for (Element element : mainList) {
				BasePreviewImageData data = new BasePreviewImageData();
				if (element.children().size() > 0) {
					if (element.select("img").size() <= 0) {
						downLatch.countDown();
						continue;
					}
					data.albumSetUrl = element.attr("href");
					data.imgPreviewUri = element.child(0).attr("src");
					data.title = element.child(0).attr("alt");
					data.pageSize = pageCount;
//					Utils.print("M5442 pageSize:" + data.pageSize);
//					list.add(data);
					Thread itemThread = new Thread(() -> {
						getDetailList(data);
						downLatch.countDown();
						Utils.print("downLatch.countDown():" + downLatch.getCount());
					});
					executor.execute(itemThread);
				} else {
					downLatch.countDown();
				}
				Utils.print("mainList downLatch.countDown():" + downLatch.getCount());
			}
			try {
				downLatch.await();
				executor.shutdown();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
//		Utils.print(list.toString());
		return list;
	}

	public static List<String> getDetailList(BasePreviewImageData data) {
		String url = data.albumSetUrl;
//        String url = "http://www.5442.com/meinv/20151203/28969.html";
		Document doc = ConnUtil.getHtmlDocument(url, LogPath);
//        Utils.print(url);
		Elements els = doc.select(".arcBody p img");
		ArrayList<String> imageUrls = new ArrayList<>();
		//pagecount
		int pageCount = 0;
		int imgCountPerPage = els.size();
		try {
			Element page = doc.select(".page a").get(0);
			String pageStr = page.text();
			String indexCount = pageStr.substring(1).trim();
			indexCount = indexCount.substring(0, indexCount.length() - 2);
			pageCount = Integer.valueOf(indexCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/**
		 * add adopted Image url to list
		 */
		String templateUrl = els.get(0).attr("src");
		String[] namePattern = parseImageNameFormat(templateUrl);
		if (pageCount <= 0) {//详细页是否有切页码
			for (int i = 1; i <= imgCountPerPage; i++) {
				String name;
				if (Boolean.parseBoolean(namePattern[2]) && i < 10) {
					name = namePattern[0] + "0" + i + namePattern[1];
				} else {
					name = namePattern[0] + i + namePattern[1];
				}
//			Utils.print(name);
				imageUrls.add(name);
			}
		} else {
			for (int i = 1; i <= pageCount * imgCountPerPage; i++) {
				String name;
				if (Boolean.parseBoolean(namePattern[2]) && i < 10) {
					name = namePattern[0] + "0" + i + namePattern[1];
				} else {
					name = namePattern[0] + i + namePattern[1];
				}
//			Utils.print(name);
				imageUrls.add(name);
			}
			//删除不存在的image url
			String lastPageUrl = url.replace(".html", "_" + pageCount + ".html");
			Document latPageDoc = ConnUtil.getHtmlDocument(lastPageUrl, LogPath);
			int size = latPageDoc.select(".tal img").size();
			if (size < imgCountPerPage) {
				int delCount = imgCountPerPage - size;
				for (int i = 0; i < delCount; i++) {
					imageUrls.remove(imageUrls.size() - 1);
				}
			}
		}

		/**
		 * download image
		 */
//		ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 8, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
//		CountDownLatch downLatch = new CountDownLatch(imageUrls.size());
		for (String imageUrl : imageUrls) {
			Thread itemThread = new Thread(() -> {
				DownloadUtil.donwloadImg(imageUrl, path);
//				downLatch.countDown();
			});
			itemThread.start();
//			executor.execute(itemThread);
//			Utils.print("getDetailList:" + imageUrl);
		}
//		try {
//			downLatch.await();
//			executor.shutdown();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		return imageUrls;
	}

	private static String[] parseImageNameFormat(String url) {
//        http://pic.5442.com/2012/1223/05/01.jpg!960.jpg
//        http://pic.5442.com/2012/1223/05/1.jpg!960.jpg
//        Utils.print(url);
		String name[] = new String[3];
		name[0] = url.substring(0, url.lastIndexOf("/") + 1);
		String tmp = url.substring(url.lastIndexOf("/"));
		name[1] = tmp.substring(tmp.indexOf("."));
		char zero = '0';
		name[2] = url.substring(url.lastIndexOf("/") + 1).charAt(0) == zero ? "true" : "false";
		return name;
	}
}
