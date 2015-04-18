/**
 * http://ccrt.cc/html/yazhou/
 */
package cn.linjonh.jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.DonwloadUtil;

/**
 * @author linjonh
 * 
 */
public class CcrtYaZhou {

	public static final int TYPE_PAGELIST = 1;
	public static final int TYPE_CELLMENULIST = 2;
	public static final int TYPE_IMG_PAGE_LIST = 3;
	private static final int TYPE_IMG_PAGE_CONTENT = 4;
	public static String baseUrl = "http://ccrt.cc";
	public static String yaZhouUrl = baseUrl + "/html/yazhou/";

	/**
	 * 
	 */
	public CcrtYaZhou() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/**
		 * TODO ��һ���˵���ҳ
		 */
		Document document = ConnUtil.getHtmlDocument(yaZhouUrl);
		// System.out.println(document.toString());// Doc
		Elements menuLists = document.select("div.fitCont_2 ul li");// ��һҳ����˵�
		Elements pageLists = document.select("div.gengduo ul a[href]");// ����ͼƬ�˵���ҳ
		// System.out.println(menuLists + "\n");
		// System.out.println(pageLists + "\n");

		ArrayList<HashMap<String, String>> pageMaps = createNameUrl(pageLists, 1);// ��Ե�ַ��maps
		// ArrayList<HashMap<String, String>> cellMenuMaps =
		// createNameUrl(menuLists, 2);// ��Ե�ַ��maps
		// visitCellMenuPictures(cellMenuMaps);
		/**
		 * ��һҳ�Ĳ˵�����
		 */
		HashMap<String, String> mainPageMaps = new HashMap<String, String>();
		for (HashMap<String, String> map : pageMaps) {
			Iterator<String> iterator = map.keySet().iterator();
			if (iterator.hasNext()) {
				String pagekey = iterator.next();
				String relativeRul = map.get(pagekey);
				String pageURI = yaZhouUrl + relativeRul;// http://ccrt.cc/html/yazhou/index2.html
				System.out.println("pageURI==>\n" + pageURI);
				mainPageMaps.put(pagekey, pageURI);
			}
		}

		/**
		 * �����˵�
		 */
		Set<String> keySet = mainPageMaps.keySet();
		Iterator<String> mainIte = keySet.iterator();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
		final CountDownLatch countDownLatch = new CountDownLatch(keySet.size());
		while (mainIte.hasNext()) {
			final String mainPageKey = mainIte.next();
			final String cellMenuAbsHtmlURL = mainPageMaps.get(mainPageKey);
			Thread command = new Thread(new Runnable() {

				@Override
				public void run() {
					Document mainPageDoc = ConnUtil.getHtmlDocument(cellMenuAbsHtmlURL);
					Elements menuLists = mainPageDoc.select("div.fitCont_2 ul li");

					ArrayList<HashMap<String, String>> cellMenuMaps = createNameUrl(menuLists, TYPE_CELLMENULIST);// ��Ե�ַ��maps
					visitCellMenuPictures(cellMenuMaps, mainPageKey);
					countDownLatch.countDown();
				}

			});
			executor.execute(command);
		}
		try {
			countDownLatch.await();
			executor.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param cellMenuMaps
	 */
	public static void visitCellMenuPictures(ArrayList<HashMap<String, String>> cellMenuMaps, final String pageIndicate) {
		/**
		 * {previewImgURL=http://s.ccrt.cc/05408.jpg,
		 * title=���������ŮС����������,
		 * HtmlRelativeUrl=/html/yazhou/iaa5408.htm}
		 */
		for (final HashMap<String, String> map : cellMenuMaps) {
			String title = map.get("title");
			// String previewImgURL = map.get("previewImgURL");
			String HtmlRelativeUrl = map.get("HtmlRelativeUrl");
			String AbsHtmlURI = baseUrl + HtmlRelativeUrl;
			System.out.println(title);
			/**
			 * TODO �ڶ���ͼƬ��ҳ
			 */
			Document personHtmldoc = ConnUtil.getHtmlDocument(AbsHtmlURI);//
			Elements NextppEl = personHtmldoc.select("div.pp");
			/**
			 * <img src="http://ccrt.kanshuzu.com/pic105/10503-1.jpg"
			 * alt="���������ŮС����������" onload="pic_width(this)"
			 * onclick="obp(this)" /> <img
			 * src="http://ccrt.kanshuzu.com/pic105/10503-2.jpg"
			 * alt="���������ŮС����������" onload="pic_width(this)"
			 * onclick="obp(this)" />
			 */
			Elements NextimgSrcEls = NextppEl.select("a[href] img[src]");
			/**
			 * <a href="iaa5408_2.htm">[2]</a> <a href="iaa5408_3.htm">[3]</a>
			 * <a href="iaa5408_4.htm">[4]</a>
			 */
			Elements NextimgSrcPages = NextppEl.select("a[href]");

			lookAndSaveImg(map, NextimgSrcEls, pageIndicate);// ��һҳͼƬ����

			/**
			 * ������������ͼƬ��ַMaps,��Ե�ַ,��һ����ַ��yaZhouUrl = baseUrl +
			 * "/html/yazhou/";
			 */
			ArrayList<HashMap<String, String>> imgHtmlPageURLs = createNameUrl(NextimgSrcPages, TYPE_IMG_PAGE_LIST);
			
			ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>());
			final CountDownLatch countDownLatch = new CountDownLatch(imgHtmlPageURLs.size());
			
			Iterator<HashMap<String, String>> RelativeIte = imgHtmlPageURLs.iterator();
			while (RelativeIte.hasNext()) {
				
				HashMap<String, String> relHtmlURlMap = RelativeIte.next();
				String pKey = relHtmlURlMap.keySet().iterator().next();

				/**
				 * TODO ��������һ��ͼƬ��ҳ
				 */
				String AbsNextHtmlURI = yaZhouUrl + relHtmlURlMap.get(pKey);
				Document personNextHtmldoc = ConnUtil.getHtmlDocument(AbsNextHtmlURI);//

				NextppEl = personNextHtmldoc.select("div.pp");
				final Elements nextimgSrcEls = NextppEl.select("a[href] img[src]");
				NextimgSrcPages = NextppEl.select("a[href]");

				Thread command=new Thread(new Runnable(
						){
					@Override
					public void run() {
						lookAndSaveImg(map, nextimgSrcEls, pageIndicate);
						// System.out.println("ppEl==>\n" + ppEl);
						// System.out.println("imgSrcEl==>\n" + imgSrcEls);
						// System.out.println("imgSrcPages==>\n" + imgSrcPages);
						countDownLatch.countDown();
					}
				});
				executor.execute(command);
			}
			try {
				countDownLatch.await();
				executor.shutdown();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param map
	 * @param imgSrcEls
	 */
	public static void lookAndSaveImg(HashMap<String, String> map, Elements imgSrcEls, String pageIndicate) {
		/**
		 * src="http://ccrt.kanshuzu.com/pic103/10351-1.jpg"
		 */
		ArrayList<HashMap<String, String>> imgSrcURLs = createNameUrl(imgSrcEls, TYPE_IMG_PAGE_CONTENT);
		Iterator<HashMap<String, String>> ite = imgSrcURLs.iterator();
		while (ite.hasNext()) {
			HashMap<String, String> ImgMap = ite.next();
			String ImgName = ImgMap.keySet().iterator().next();
			String fileUrl = ImgMap.get(ImgName);
			/**
			 * {previewImgURL=http://s.ccrt.cc/05408.jpg,
			 * title=���������ŮС����������,
			 * HtmlRelativeUrl=/html/yazhou/iaa5408.htm}
			 */

			String fileName = ImgName + fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
			cn.linjonh.jsoup.util.Utils.writeLog("D:/ccrt/", "pageIndex:" + pageIndicate);
			DonwloadUtil.donwloadImg(fileUrl, "D:/ccrt/" + fileName);
		}
	}

	/**
	 * 
	 * @param elLists
	 * @param type
	 *            ��������Ԫ�����������˼
	 *            <p>
	 *            TYPE_PAGELIST=1��ʾ��ҳ������htmlĿ¼ҳ��
	 *            <p>
	 *            TYPE_CELLMENULIST=2��ʾ��ҳ��ͼ��ɫ�˵�
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> createNameUrl(Elements elLists, int type) {
		ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
		switch (type) {
		case TYPE_PAGELIST:
			for (int i = 0; i < elLists.size() - 1; i++) {// ���˵���ĩҳ
				HashMap<String, String> map = new HashMap<String, String>();
				Element el = elLists.get(i);
				String PageRelativeUrl = el.attr("href");
				String PageName = el.text();
				map.put(PageName, PageRelativeUrl);
				hashMaps.add(map);
			}
			break;
		case TYPE_CELLMENULIST:
			for (int i = 0; i < elLists.size(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				Element el = elLists.get(i);
				String CellMenuRelativeUrl = el.child(0).attr("href");
				String CellName = el.child(0).attr("title");
				String PreviewImgUrl = el.child(0).child(0).attr("src");

				map.put("title", CellName);
				map.put("previewImgURL", PreviewImgUrl);
				map.put("HtmlRelativeUrl", CellMenuRelativeUrl);
				hashMaps.add(map);
			}
			break;
		case TYPE_IMG_PAGE_LIST:
			/**
			 * <a href="iaa5408_2.htm"><img
			 * src="http://ccrt.kanshuzu.com/pic105/10503-1.jpg"
			 * alt="���������ŮС����������" onload="pic_width(this)"
			 * onclick="obp(this)" /></a>
			 * <p>
			 * <a href="iaa5408_2.htm"><img
			 * src="http://ccrt.kanshuzu.com/pic105/10503-2.jpg"
			 * alt="���������ŮС����������" onload="pic_width(this)"
			 * onclick="obp(this)" /></a>
			 * <p>
			 * <a href="iaa5408_2.htm">[2]</a>
			 * <p>
			 * <a href="iaa5408_3.htm">[3]</a>
			 * <p>
			 * <a href="iaa5408_4.htm">[4]</a>
			 */
			for (int i = 2; i < elLists.size(); i++) {// ���˵�ǰ��������ʾͼƬ��htmlԪ��
				HashMap<String, String> map = new HashMap<String, String>();
				Element element = elLists.get(i);
				String htmlRelativeURL = element.attr("href");
				String pageName = element.text();
				map.put(pageName, htmlRelativeURL);
				hashMaps.add(map);
			}

			break;
		case TYPE_IMG_PAGE_CONTENT:
			/**
			 * <img src="http://ccrt.kanshuzu.com/pic105/10503-1.jpg"
			 * alt="���������ŮС����������" onload="pic_width(this)"
			 * onclick="obp(this)" />
			 * <p>
			 * <img src="http://ccrt.kanshuzu.com/pic105/10503-2.jpg"
			 * alt="���������ŮС����������" onload="pic_width(this)"
			 * onclick="obp(this)" />
			 */
			for (int i = 0; i < elLists.size(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				Element element = elLists.get(i);
				String downloadImgURL = element.attr("src");
				String ImgName = element.attr("alt");
				map.put(ImgName, downloadImgURL);
				hashMaps.add(map);
			}
			break;
		}
		return hashMaps;
	}
}
