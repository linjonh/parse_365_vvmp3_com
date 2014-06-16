/**
 * http://ccrt.cc/html/yazhou/
 */
package cn.linjonh.jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
		 * TODO 第一级菜单网页
		 */
		Document document = ConnUtil.getHtmlDocument(yaZhouUrl);
		// System.out.println(document.toString());// Doc
		Elements menuLists = document.select("div.fitCont_2 ul li");// 第一页网格菜单
		Elements pageLists = document.select("div.gengduo ul a[href]");// 网格图片菜单分页
		// System.out.println(menuLists + "\n");
		// System.out.println(pageLists + "\n");

		ArrayList<HashMap<String, String>> pageMaps = createNameUrl(pageLists, 1);// 相对地址的maps
//		ArrayList<HashMap<String, String>> cellMenuMaps = createNameUrl(menuLists, 2);// 相对地址的maps
//		visitCellMenuPictures(cellMenuMaps);
		/**
		 * 第一页的菜单创建
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
		 * 遍历菜单
		 */
		Iterator<String> mainIte = mainPageMaps.keySet().iterator();
		while (mainIte.hasNext()) {
			String mainPageKey = mainIte.next();
			String cellMenuAbsHtmlURL = mainPageMaps.get(mainPageKey);

			Document mainPageDoc = ConnUtil.getHtmlDocument(cellMenuAbsHtmlURL);
			menuLists = mainPageDoc.select("div.fitCont_2 ul li");// 第一页网格菜单

			ArrayList<HashMap<String, String>> cellMenuMaps = createNameUrl(menuLists,
					TYPE_CELLMENULIST);// 相对地址的maps
			visitCellMenuPictures(cellMenuMaps, mainPageKey);
		}
		


	}

	/**
	 * @param cellMenuMaps
	 */
	public static void visitCellMenuPictures(ArrayList<HashMap<String, String>> cellMenuMaps,
			String pageIndicate) {
		/**
		 * {previewImgURL=http://s.ccrt.cc/05408.jpg, title=房间里的美女小曼妩媚人体,
		 * HtmlRelativeUrl=/html/yazhou/iaa5408.htm}
		 */
		for (HashMap<String, String> map : cellMenuMaps) {
			String title = map.get("title");
			// String previewImgURL = map.get("previewImgURL");
			String HtmlRelativeUrl = map.get("HtmlRelativeUrl");
			String AbsHtmlURI = baseUrl + HtmlRelativeUrl;
			System.out.println(title);
			/**
			 * TODO 第二级图片网页
			 */
			Document personHtmldoc = ConnUtil.getHtmlDocument(AbsHtmlURI);//
			Elements NextppEl = personHtmldoc.select("div.pp");
			/**
			 * <img src="http://ccrt.kanshuzu.com/pic105/10503-1.jpg" alt="房间里的美女小曼妩媚人体"
			 * onload="pic_width(this)" onclick="obp(this)" /> <img
			 * src="http://ccrt.kanshuzu.com/pic105/10503-2.jpg" alt="房间里的美女小曼妩媚人体"
			 * onload="pic_width(this)" onclick="obp(this)" />
			 */
			Elements NextimgSrcEls = NextppEl.select("a[href] img[src]");
			/**
			 * <a href="iaa5408_2.htm">[2]</a> <a href="iaa5408_3.htm">[3]</a> <a
			 * href="iaa5408_4.htm">[4]</a>
			 */
			Elements NextimgSrcPages = NextppEl.select("a[href]");

			lookAndSaveImg(map, NextimgSrcEls, pageIndicate);// 第一页图片下载

			/**
			 * 创建遍历下载图片地址Maps,相对地址,上一级地址是yaZhouUrl = baseUrl + "/html/yazhou/";
			 */
			ArrayList<HashMap<String, String>> imgHtmlPageURLs = createNameUrl(NextimgSrcPages,
					TYPE_IMG_PAGE_LIST);
			Iterator<HashMap<String, String>> RelativeIte = imgHtmlPageURLs.iterator();
			while (RelativeIte.hasNext()) {
				HashMap<String, String> relHtmlURlMap = RelativeIte.next();
				String pKey = relHtmlURlMap.keySet().iterator().next();

				/**
				 * TODO 第三级下一张图片网页
				 */
				String AbsNextHtmlURI = yaZhouUrl + relHtmlURlMap.get(pKey);
				Document personNextHtmldoc = ConnUtil.getHtmlDocument(AbsNextHtmlURI);//

				NextppEl = personNextHtmldoc.select("div.pp");
				NextimgSrcEls = NextppEl.select("a[href] img[src]");
				NextimgSrcPages = NextppEl.select("a[href]");

				lookAndSaveImg(map, NextimgSrcEls, pageIndicate);
				// System.out.println("ppEl==>\n" + ppEl);
				// System.out.println("imgSrcEl==>\n" + imgSrcEls);
				// System.out.println("imgSrcPages==>\n" + imgSrcPages);
			}

		}
	}

	/**
	 * @param map
	 * @param imgSrcEls
	 */
	public static void lookAndSaveImg(HashMap<String, String> map, Elements imgSrcEls,
			String pageIndicate) {
		/**
		 * src="http://ccrt.kanshuzu.com/pic103/10351-1.jpg"
		 */
		ArrayList<HashMap<String, String>> imgSrcURLs = createNameUrl(imgSrcEls,
				TYPE_IMG_PAGE_CONTENT);
		Iterator<HashMap<String, String>> ite = imgSrcURLs.iterator();
		while (ite.hasNext()) {
			HashMap<String, String> ImgMap = ite.next();
			String ImgName = ImgMap.keySet().iterator().next();
			String fileUrl = ImgMap.get(ImgName);
			/**
			 * {previewImgURL=http://s.ccrt.cc/05408.jpg, title=房间里的美女小曼妩媚人体,
			 * HtmlRelativeUrl=/html/yazhou/iaa5408.htm}
			 */


			String fileName = ImgName + fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
			DonwloadUtil.DonwloadImg(fileUrl, "C:/ccrt/" + fileName, pageIndicate);
		}
	}
	/**
	 * 
	 * @param elLists
	 * @param type
	 *            被解析的元素所代表的意思
	 *            <p>
	 *            TYPE_PAGELIST=1表示该页的其他html目录页码
	 *            <p>
	 *            TYPE_CELLMENULIST=2表示该页的图角色菜单
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> createNameUrl(Elements elLists, int type) {
		ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
		switch (type){
			case TYPE_PAGELIST :
				for (int i = 0; i < elLists.size() - 1; i++) {// 过滤掉最末页
					HashMap<String, String> map = new HashMap<String, String>();
					Element el=elLists.get(i);
					String PageRelativeUrl = el.attr("href");
					String PageName = el.text();
					map.put(PageName, PageRelativeUrl);
					hashMaps.add(map);
				}
				break;
			case TYPE_CELLMENULIST :
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
			case TYPE_IMG_PAGE_LIST :
				/**
				 * <a href="iaa5408_2.htm"><img src="http://ccrt.kanshuzu.com/pic105/10503-1.jpg"
				 * alt="房间里的美女小曼妩媚人体" onload="pic_width(this)" onclick="obp(this)" /></a>
				 * <p>
				 * <a href="iaa5408_2.htm"><img src="http://ccrt.kanshuzu.com/pic105/10503-2.jpg"
				 * alt="房间里的美女小曼妩媚人体" onload="pic_width(this)" onclick="obp(this)" /></a>
				 * <p>
				 * <a href="iaa5408_2.htm">[2]</a>
				 * <p>
				 * <a href="iaa5408_3.htm">[3]</a>
				 * <p>
				 * <a href="iaa5408_4.htm">[4]</a>
				 */
				for (int i = 2; i < elLists.size(); i++) {// 过滤掉前两条已显示图片的html元素
					HashMap<String, String> map = new HashMap<String, String>();
					Element element = elLists.get(i);
					String htmlRelativeURL = element.attr("href");
					String pageName = element.text();
					map.put(pageName, htmlRelativeURL);
					hashMaps.add(map);
				}

				break;
			case TYPE_IMG_PAGE_CONTENT :
				/**
				 * <img src="http://ccrt.kanshuzu.com/pic105/10503-1.jpg" alt="房间里的美女小曼妩媚人体"
				 * onload="pic_width(this)" onclick="obp(this)" />
				 * <p>
				 * <img src="http://ccrt.kanshuzu.com/pic105/10503-2.jpg" alt="房间里的美女小曼妩媚人体"
				 * onload="pic_width(this)" onclick="obp(this)" />
				 */
				for (int i = 0; i < elLists.size(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					Element element = elLists.get(i);
					String downloadImgURL = element.attr("src");
					String ImgName=element.attr("alt");
					map.put(ImgName, downloadImgURL);
					hashMaps.add(map);
				}
				break;
		}
		return hashMaps;
	}
}
