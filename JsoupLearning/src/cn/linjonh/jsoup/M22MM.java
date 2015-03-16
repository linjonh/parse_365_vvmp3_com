package cn.linjonh.jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.print.Doc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sun.org.apache.xpath.internal.operations.Div;

import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.DonwloadUtil;
import cn.linjonh.jsoup.util.Utils;

public class M22MM {

	public M22MM() {
	}

	private static String Website = "";
	private static List<ModuleInfoBean> mMdoduleList = new ArrayList<M22MM.ModuleInfoBean>();
	private static Object lock = new Object();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// String encodeStr = encodeWebsite();
		String encodeStr = ConnUtil.decode("D:/22mmdata", ConnUtil.KEYS);
		Document doc = Jsoup.parse(encodeStr);
		Elements modEls = doc.select("a");
		generateModuleListInfo(modEls);
		startDownloadExecutor();
	}

	/**
	 * 
	 * @param modEls
	 */
	private static void generateModuleListInfo(Elements modEls) {
		for (int i = 0; i < modEls.size() - 2; i++) {
			// escape home page and last two page
			if (i == 0) {
				Website = modEls.get(i).attr("href");
				continue;
			}

			Element module = modEls.get(i);
			final String module_url = module.attr("href");
			final String module_name = module.html();
//			new Thread(new Runnable() {
//
//				@Override
//				public void run() {
					ModuleInfoBean moduleItem = detectModulePageCount(module_url, module_name);
					syncAddModuleInfoToList(moduleItem);
//				}
//			}).start();
		}
	}

	/**
	 * 
	 * @param pageBaseUri
	 *            such as http://www.mm131.com/xinggan/list_6_
	 *            <p>
	 *            list.get(0)=list_6_
	 *            <p>
	 *            list.get(1)=http://www.mm131.com/xinggan/
	 * @param pageCount
	 *            total a module page count.
	 */
	private static void syncAddModuleInfoToList(ModuleInfoBean module_item) {
//		synchronized (lock) {
			if (module_item == null) {
//				lock.notify();
				print("syncAddModuleInfoToList==>module_item == null");
				return;
			}
			mMdoduleList.add(module_item);
			int size = mMdoduleList.size();
//			if (size == 4) {
//				lock.notify();
//			}
//		}
	}

	/**
	 * for download
	 * 
	 * @return List<ModuleInfoBean>
	 */
	private static List<ModuleInfoBean> readModuleInfo() {
		// synchronized (lock) {
		int size = mMdoduleList.size();
		print(">>>>mMdoduleList.size:" + size + " list content:" + mMdoduleList.toString());
		return mMdoduleList;
		// }
	}

	private static void startDownloadExecutor() {
//		synchronized (lock) {
//			try {
//				lock.wait();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}

			// TODO download image
			for (int j = 0; j < mMdoduleList.size(); j++) {
				final ModuleInfoBean someoneMod = mMdoduleList.get(j);
				Thread moduleThread = new Thread(new Runnable() {

					@Override
					public void run() {
						for (int i = 1; i < someoneMod.pageSize; i++) {
							visiModulePage(someoneMod, i);
						}
					}
				});
				executor.execute(moduleThread);
			}
//		}
	}

	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.SECONDS,
			new LinkedBlockingDeque<Runnable>());

	/**
	 * 
	 * @return
	 */
	public static String encodeWebsite() {
		String encodeStr = ConnUtil.decode("D:/22mmdata.txt", (byte) 0);
		ConnUtil.encodeString(encodeStr, ConnUtil.KEYS, "D:/22mmdata");
		return encodeStr;
	}

	/**
	 * 
	 * @param module_url
	 * @param module_name
	 */
	private static ModuleInfoBean detectModulePageCount(String module_url, String module_name) {
		print(">>>detect module url:" + module_url);
		Document doc = ConnUtil.getHtmlDocument(module_url);
		// ShowPage element
		Elements els = doc.select("div.ShowPage");
		if (els.isEmpty()) {
			print(">>>doc.select(\"div.ShowPage\") els.isEmpty() detect module url:" + module_url);
			return null;
		}
		Element divPageInfo = els.get(0);
		// page Pattern
		String preSuffix = divPageInfo.select("a").get(0).attr("href");
		preSuffix = preSuffix.substring(0, preSuffix.lastIndexOf("_") + 1);
		// count
		Elements imgGridCountEls = divPageInfo.select("span");
		String imgGridCount = imgGridCountEls.get(0).html();

		int gridItemSize = getImgGridCountNumber(imgGridCount);
		Double c = (double) (gridItemSize / 35f);

		ModuleInfoBean someone_module = new ModuleInfoBean();
		someone_module.name_zh = module_name;
		someone_module.url = module_url;
		someone_module.pageSize = (int) Math.ceil(c);
		someone_module.pagePattren = preSuffix;
		String log = "=====module url:=====" + module_url + "==============" + "\n[" + Utils.getFormatedTime()
				+ "]===module name_zh: " + module_name + "===============" + "\n[" + Utils.getFormatedTime()
				+ "]===module size:    " + someone_module.pageSize + "page==" + "\n[" + Utils.getFormatedTime()
				+ "]===module pagePattren:" + someone_module.pagePattren;
		print(log);
		return someone_module;
	}

	private static void visiModulePage(ModuleInfoBean bean, int pageIndex) {
		// grid
		String htmlUrl = "";
		if (bean.pagePattren.equals("rec_")) {
			htmlUrl = bean.url;
		} else if (pageIndex == 1) {
			String tmppattern = bean.pagePattren.replace("_", "");
			htmlUrl = bean.url + tmppattern + ".html";
		} else {
			htmlUrl = bean.url + bean.pagePattren + pageIndex + ".html";
		}
		print("visi Module Page==>>> " + htmlUrl);
		Document doc = ConnUtil.getHtmlDocument(htmlUrl);
		// contain seven items of top header
		Elements gridItems = doc.select(".c_inner .pic li a");
		if (gridItems.isEmpty()) {
			print("gridItems isEmpty");
			return;
		}
		// print(gridItems.toString());
		visitRoleGirdItem(bean.url, gridItems);
	}

	/**
	 * 
	 */
	private static void visitRoleGirdItem(String moduleUrl, Elements items) {
		List<GridItemInfoBean> gridItems = new ArrayList<M22MM.GridItemInfoBean>();
		for (int i = 6; i < items.size(); i++) {
			Element element = items.get(i);
			String href = element.attr("href");
			GridItemInfoBean bean = new GridItemInfoBean();
			bean.name_zh = element.attr("title");
			bean.url = Website + href;
			bean.moduleBaseUrl = moduleUrl;
			gridItems.add(bean);
		}
		visitImageItem(gridItems);
	}

	private static void visitImageItem(List<GridItemInfoBean> beans) {
		for (int i = 0; i < beans.size(); i++) {
			GridItemInfoBean imageItem = beans.get(i);
			// Thread visitItemThread=new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// TODO Auto-generated method stub
			print("visitImageItem==>>> " + imageItem.url);
			Document doc = ConnUtil.getHtmlDocument(imageItem.url);
			Elements picItems = doc.select("div.pagelist a");
			if (picItems.isEmpty()) {
				print("visitImageItem==>>>picItems is Empty:Elements picItems = doc.select(\"div.pagelist a\")");
				return;
			}
			/*
			 * escape previos two item : <a
			 * href="/mm/bagua/PmaHPdeaHeJaimbae.html">previous group</a>
			 */
			String relativeUrlPattern = "";
			try {
				relativeUrlPattern = picItems.get(1).attr("href");// 通用相对地址模板
				print(" 通用相对地址模板:" + relativeUrlPattern);
			} catch (Exception e) {
				String log = "EpicItems.get(1).attr(\"href\");// 通用相对地址模板" + e.toString();
				print(log);
				return;
			}
			if (relativeUrlPattern.isEmpty()) {
				print(" 通用相对地址模板: is Empty");
				return;
			}
			relativeUrlPattern = relativeUrlPattern.substring(0, relativeUrlPattern.lastIndexOf("-"));
			Elements showpages = doc.select("div.ShowPage strong");
			String pageSize = "";
			if (showpages.size() > 0) {
				Element showpageEl = showpages.get(0);
				pageSize = showpageEl.html();
				pageSize = pageSize.substring(pageSize.lastIndexOf("/") + 1);
				print(pageSize);
			}

			String imgFileUrl = imageItem.moduleBaseUrl + relativeUrlPattern + "-" + pageSize + ".html";
			String log = "visit page: " + imgFileUrl;
			print(log);
			// get image last page to obtain script which contain all image URL.
			Document document = ConnUtil.getHtmlDocument(imgFileUrl);

			Elements els = document.select("div#box-inner script");
			if(els.isEmpty()){
				print("select image script is empty: div#box-inner script");
				print("Fialed =========>imageUrlSript is empty on page:" + log);
				return;
			}
			String imageUrlSript = els.get(1).html();
			if (!imageUrlSript.isEmpty()) {
				String[] tmpUrls = imageUrlSript.split(";");
				String[] allImageItemUrls = parseImageUrl(tmpUrls);
				for (int k = 0; k < allImageItemUrls.length; k++) {
					final String fileName = dirPath + imageItem.name_zh + "_" + (k + 1) + ".jpg";

					String tmp = imageItem.moduleBaseUrl;
					tmp = tmp.replace("mm/", "");
					// tmp = tmp.replace("http://", "");
					tmp = tmp.substring(tmp.indexOf("/")).replace("/", "_");

					final String fileName2 = dirPath + tmp + imageItem.name_zh + "_" + (k + 1) + ".jpg";
					// download
					final String url = allImageItemUrls[k];
					if(url.isEmpty()){
						break;
					}
					Thread moduleThread = new Thread(new Runnable() {

						@Override
						public void run() {
							DonwloadUtil.donwloadImg(url, fileName, fileName2);
						}
					});
					moduleThread.start();
					// moduleThread.setPriority(Thread.MAX_PRIORITY);
					// ThreadPoolExecutor executor = new ThreadPoolExecutor(4,
					// 8, 1, TimeUnit.SECONDS,
					// new LinkedBlockingDeque<Runnable>());
					// executor.execute(moduleThread);
				}
			}
			// }
			// });
			// visitItemThread.setPriority(2);
			// executor.execute(visitItemThread);
		}

	}

	private static final String dirPath = "E:/MM22/";

	/**
	 * // <li>var arrayImg = new Array() // <li>arrayImg[0] =
	 * "http://srimg1.meimei22.com/big/suren/2014-8-22/1/407899112014050822400403_640.jpg"
	 * // <li>arrayImg[0] =
	 * "http://srimg1.meimei22.com/big/suren/2014-8-22/1/4078991120140508224023013_640.jpg"
	 * // <li>arrayImg[0] =
	 * "http://srimg1.meimei22.com/big/suren/2014-8-22/1/407899112014050822404702_640.jpg"
	 * // <li>arrayImg[0] =
	 * "http://srimg1.meimei22.com/big/suren/2014-8-22/1/4078991120140508224114010_640.jpg"
	 * // <li>getImgString()
	 * 
	 * @param urls
	 * @return
	 */
	private static String[] parseImageUrl(String[] urls) {
		String[] items = new String[urls.length - 2];

		for (int i = 1, j = 0; i < urls.length - 1; i++, j++) {
			
			String imageUrl = urls[i];
			print("parseImageUrl:"+imageUrl);
			int first = imageUrl.indexOf("\"");
			int last = imageUrl.lastIndexOf("\"");
			if (first != -1 && last != -1) {
				try {
					imageUrl = imageUrl.substring(first + 1, last);
					imageUrl = imageUrl.replace("big", "pic");
					items[j] = imageUrl;
				} catch (Exception e) {
					print("parseImageUrl Error:"+e);
				}
			}else{
				items[j]="";
			}
		}
		return items;
	}

	private static Elements loopToGetAllImageUrl(String url) {
		print("loopToGetAllImageUrl==>>> " + url);
		Document doc = ConnUtil.getHtmlDocument(url);
		Elements picItems = doc.select("div.pagelist a");
		if (picItems.size() > 0) {
			return picItems;
		} else {
			return null;
		}
	}

	private static void startDownloadImage(RoleImageInfoBeen imageBean) {
		// for (int i = 1; i < imageBean.imgeCount; i++) {
		// String imgFileUrl = "";
		// if (i == 1) {
		// /*
		// * because image page one(PiaeddHdCCCadHCHJ.html) has no number
		// * as page two :"PiaeddHdCCCadHCHJ-2.html";
		// */
		// imgFileUrl = imageBean.moduleBaseUrl + imageBean.pattern + ".html";
		// } else {
		// imgFileUrl = imageBean.moduleBaseUrl + imageBean.pattern + "-" + i +
		// ".html";
		// }

		// }
	}

	private static int getImgGridCountNumber(String pageCountStr) {
		String tmp = pageCountStr.substring(1, pageCountStr.lastIndexOf("套"));
		// print(tmp);
		return Integer.valueOf(tmp);
	}

	public static void print(String str) {
		String time = "[" + Utils.getFormatedTime() + "]: ";
		System.out.println(time + str);
		DonwloadUtil.writeLog(dirPath, str);
	}

	/**
	 * module item
	 * 
	 * @author linjianyou
	 * 
	 */
	static class ModuleInfoBean {
		public String name_zh;
		public String url;
		public String pagePattren;
		public int pageSize;
	}

	/**
	 * grid role item.
	 * 
	 * @author linjianyou
	 * 
	 */
	static class GridItemInfoBean {
		public String name_zh;
		public String url;
		public String moduleBaseUrl;
	}

	/**
	 * image item bean
	 * 
	 * @author linjianyou
	 * 
	 */
	static class RoleImageInfoBeen {
		public String pattern;
		public int imgeCount;
		public String moduleBaseUrl;
		public String name_zh;
	}

}
