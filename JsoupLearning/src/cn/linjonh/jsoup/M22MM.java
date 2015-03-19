package cn.linjonh.jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.print.Doc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
	private static Object roleArrayAddLock = new Object();
	private static JSONObject mRootJSONObj;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// String encodeStr = encodeWebsite();
		String encodeStr = ConnUtil.decode("D:/22mmdata", ConnUtil.KEYS);
		Document doc = Jsoup.parse(encodeStr);
		Elements modEls = doc.select("a");

		mRootJSONObj = Utils.readJsonDataFromDefaultFile();
		if (mRootJSONObj == null) {
			generateModuleListInfo(modEls);
		} else {
			print("mRootJSONObj is not null");
		}
		doWorkFlow();
	}

	/**
	 * 
	 * @param modEls
	 */
	private static void generateModuleListInfo(Elements modEls) {
		mRootJSONObj = new JSONObject();
		JSONArray section_url_array = new JSONArray();
		JSONObject section_datas = new JSONObject();
		for (int i = 0; i < modEls.size() - 2; i++) {
			// escape home page and last two page
			if (i == 0) {
				Website = modEls.get(i).attr("href");
				continue;
			}

			Element module = modEls.get(i);
			final String module_url = module.attr("href");
			final String module_name = module.html();
			String module_name_en = module_url.replace(Website + "mm/", "");
			module_name_en = module_name_en.replace("/", "");
			JSONObject module_array_item = new JSONObject();
			try {
				module_array_item.put("section_name_zh", module_name);
				module_array_item.put("section_name_en", module_name_en);
				module_array_item.put("section_url", module_url);
			} catch (JSONException e) {
				print(e.toString());
				e.printStackTrace();
			}
			section_url_array.put(module_array_item);

			// new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			ModuleInfoBean moduleItem = detectModulePageCount(module_url, module_name);
			syncAddModuleInfoToList(moduleItem);
			// }
			// }).start();

			JSONObject val = new JSONObject();
			try {
				val.put("section_name_zh", moduleItem.name_zh);
				val.put("section_name_en", module_name_en);
				val.put("section_page_size", moduleItem.pageSize);
				val.put("section_url", moduleItem.url);
				val.put("section_page_url_pattern", moduleItem.pagePattren);
				section_datas.put(module_name_en, val);
			} catch (JSONException e) {
				print(e.toString());
				e.printStackTrace();
			}

		}

		try {
			mRootJSONObj.put("section_url_list", section_url_array);
			mRootJSONObj.put("section_page_datas", section_datas);
		} catch (JSONException e) {
			print(e.toString());
			e.printStackTrace();
		}
		Utils.writeLoopDataToDefaultFile(mRootJSONObj);
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
		// synchronized (lock) {
		if (module_item == null) {
			// lock.notify();
			print("syncAddModuleInfoToList==>module_item == null");
			return;
		}
		mMdoduleList.add(module_item);
		int size = mMdoduleList.size();
		// if (size == 4) {
		// lock.notify();
		// }
		// }
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

	private static void doWorkFlow() {
		// TODO download image
		if (mRootJSONObj == null) {// first or othere unhandled situation
			doIteratorModuleWorking();
		} else {
			getModuleDataFromJson(mRootJSONObj);

			List<List<GridItemInfoBean>> moduleItems = parsingRoleItemArrayObjToGridItemInfoBeanList(mRootJSONObj);
			doVisiItemStep(moduleItems);
		}
		Utils.writeLoopDataToDefaultFile(mRootJSONObj);
	}

	private static void doVisiItemStep(List<List<GridItemInfoBean>> moduleItems) {
		ThreadPoolExecutor moduleItemsExecutor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.SECONDS,
				new LinkedBlockingDeque<Runnable>());
		final CountDownLatch doneSignal = new CountDownLatch(moduleItems.size());
		final JSONArray imageItemDatas = new JSONArray();

		for (int i = 0; i < moduleItems.size(); i++) {
			final List<GridItemInfoBean> gridItemInfoBeans = moduleItems.get(i);
			Thread command = new Thread(new Runnable() {
				@Override
				public void run() {
					if (gridItemInfoBeans.size() > 0) {
						String moduleName = "";
						JSONArray oneModuleItemsArray = visitImageItem(gridItemInfoBeans, moduleName);
						JSONObject sectionImageArrayObj = new JSONObject();
						try {
							sectionImageArrayObj.put("section_name", moduleName);
							sectionImageArrayObj.put("section_image_item_array", oneModuleItemsArray);
							imageItemDatas.put(sectionImageArrayObj);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					doneSignal.countDown();
				}
			});
			moduleItemsExecutor.execute(command);
		}

		try {
			doneSignal.await();
			moduleItemsExecutor.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			mRootJSONObj.put("all_section_image_item_array", imageItemDatas);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static void doIteratorModuleWorking() {
		final JSONObject section_grid_data = new JSONObject();
		final CountDownLatch doneSignal = new CountDownLatch(mMdoduleList.size());
		for (int j = 0; j < mMdoduleList.size(); j++) {
			final ModuleInfoBean someoneMod = mMdoduleList.get(j);
			Thread moduleThread = new Thread(new Runnable() {

				@Override
				public void run() {
					JSONArray array = new JSONArray();
					List<GridItemInfoBean> moduleGridItems = new ArrayList<M22MM.GridItemInfoBean>();
					for (int i = 1; i < someoneMod.pageSize; i++) {
						doVisiModulePage(someoneMod, i, array, moduleGridItems);
					}
					try {
						section_grid_data.put(someoneMod.name_en + "_role_item_array", array);
					} catch (JSONException e) {
						print(e.toString());
						e.printStackTrace();
					}
					doneSignal.countDown();
				}
			});
			executor.execute(moduleThread);
		}

		try {
			doneSignal.await();
			executor.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			mRootJSONObj.put("role_item_array_obj", section_grid_data);
		} catch (JSONException e) {
			print(e.toString());
			e.printStackTrace();
		}

	}

	private static void getModuleDataFromJson(JSONObject obj) {
		JSONObject sectionLists = obj.optJSONObject("section_page_datas");
		Iterator<String> ito = sectionLists.keys();
		mMdoduleList.clear();
		while (ito.hasNext()) {
			ModuleInfoBean bean = new ModuleInfoBean();
			try {
				JSONObject section = sectionLists.getJSONObject(ito.next());
				String section_name_en = section.getString("section_name_en");
				String section_name_zh = section.getString("section_name_zh");
				String section_url = section.getString("section_url");
				int page_size = section.getInt("section_page_size");
				String section_url_pattern = section.getString("section_page_url_pattern");
				bean.name_en = section_name_en;
				bean.name_zh = section_name_zh;
				bean.url = section_url;
				bean.pagePattren = section_url_pattern;
				bean.pageSize = page_size;

				mMdoduleList.add(bean);
				print("section_url=======>" + section_url);
				print(section_name_en + "====" + section_url_pattern);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.SECONDS,
			new LinkedBlockingDeque<Runnable>());

	/**
	 * 
	 * @return
	 */
	public static String encodeWebsite() {
		String encodeStr = ConnUtil.decode(
				"C:/Users/john.lin/git/parse_365_vvmp3_com/JsoupLearning/src/cn/linjonh/jsoup/22mmdata", (byte) 0);
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

	private static void doVisiModulePage(ModuleInfoBean someoneModulBeen, int pageIndex, JSONArray array,
			List<GridItemInfoBean> moduleGridItems) {
		// grid
		String htmlUrl = "";
		if (someoneModulBeen.pagePattren.equals("rec_")) {
			htmlUrl = someoneModulBeen.url;
		} else if (pageIndex == 1) {
			String tmppattern = someoneModulBeen.pagePattren.replace("_", "");
			htmlUrl = someoneModulBeen.url + tmppattern + ".html";
		} else {
			htmlUrl = someoneModulBeen.url + someoneModulBeen.pagePattren + pageIndex + ".html";
		}
		print("page index "+pageIndex+" visi Module Page==>>> " + htmlUrl);
		Document doc = ConnUtil.getHtmlDocument(htmlUrl);
		// contain seven items of top header
		Elements gridItemEls = doc.select(".c_inner .pic li a");
		if (gridItemEls.isEmpty()) {
			print("gridItems isEmpty");
			return;
		}
		// print(gridItems.toString());
		for (int i = 6; i < gridItemEls.size(); i++) {
			Element element = gridItemEls.get(i);
			String href = element.attr("href");
			GridItemInfoBean gridItemInfobean = new GridItemInfoBean();
			gridItemInfobean.name_zh = element.attr("title");
			gridItemInfobean.url = Website + href;
			gridItemInfobean.moduleBaseUrl = someoneModulBeen.url;
			gridItemInfobean.module_name_en = someoneModulBeen.name_en;

			moduleGridItems.add(gridItemInfobean);

			JSONObject value = new JSONObject();
			try {
				value.put("role_name_zh", gridItemInfobean.name_zh);
				value.put("role_url", gridItemInfobean.url);
				value.put("role_module_base_url", gridItemInfobean.moduleBaseUrl);
				array.put(value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// /**
	// *
	// */
	// private static JSONArray parseGridsElementsForPage(String moduleUrl,
	// Elements items) {
	// visitImageItem(gridItems);
	// }
	/**
	 * 
	 * @param beans
	 *            gird role items of one module
	 * @return
	 */
	private static JSONArray visitImageItem(final List<GridItemInfoBean> beans, String moduleName) {
		final JSONArray roleImageArray = new JSONArray();
		for (int i = 0; i < beans.size(); i++) {
			GridItemInfoBean imageItem = beans.get(i);
			print("visitImageItem==>>> " + imageItem.url);
			Document doc = ConnUtil.getHtmlDocument(imageItem.url);
			Elements picItems = doc.select("div.pagelist a");
			if (picItems.isEmpty()) {
				print("visitImageItem==>>>picItems is Empty:Elements picItems = doc.select(\"div.pagelist a\")");
				continue;
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
				continue;
			}
			if (relativeUrlPattern.isEmpty()) {
				print(" 通用相对地址模板: is Empty");
				continue;
			}
			try {
				relativeUrlPattern = relativeUrlPattern.substring(0, relativeUrlPattern.lastIndexOf("-"));
			} catch (Exception e) {
				print(e.toString());
				continue;
			}

			Elements showpages = doc.select("div.ShowPage strong");
			String pageSize = "";
			if (showpages.size() > 0) {
				Element showpageEl = showpages.get(0);
				pageSize = showpageEl.html();
				pageSize = pageSize.substring(pageSize.lastIndexOf("/") + 1);
				print(pageSize);
			}

			String imglastPageUrl = imageItem.moduleBaseUrl + relativeUrlPattern + "-" + pageSize + ".html";
			String log = "visit page: " + imglastPageUrl;
			print(log);
			// get image last page to obtain script which contain all
			// image URL.
			Document document = ConnUtil.getHtmlDocument(imglastPageUrl);

			Elements els = document.select("div#box-inner script");
			if (els.isEmpty()) {
				print("select image script is empty: div#box-inner script");
				print("Fialed =========>imageUrlSript is empty on page:" + log);
				continue;
			}

			String imageUrlSript = els.get(1).html();

			if (!imageUrlSript.isEmpty()) {
				String[] tmpUrls = imageUrlSript.split(";");
				String[] allImageItemUrls = parseImageUrl(tmpUrls);
				JSONObject roleItemObj = new JSONObject();
				JSONArray array = new JSONArray();
				for (int k = 0; k < allImageItemUrls.length; k++) {
					array.put(allImageItemUrls[k]);
				}
				try {
					roleItemObj.put("role_name_zh", imageItem.name_zh);
					roleItemObj.put("role_image_count", allImageItemUrls.length);
					roleItemObj.put("image_url_array", array);
					roleItemObj.put("section_name_en", imageItem.module_name_en);

					roleImageArray.put(roleItemObj);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
		moduleName = beans.get(0).module_name_en;
		return roleImageArray;
	}

	private static void doDownLoadImageWork(GridItemInfoBean imageItem, String[] allImageItemUrls, int k) {
		final String fileName = dirPath + imageItem.name_zh + "_" + (k + 1) + ".jpg";

		String tmp = imageItem.moduleBaseUrl;
		tmp = tmp.replace("mm/", "");
		// tmp = tmp.replace("http://", "");
		tmp = tmp.substring(tmp.indexOf("/")).replace("/", "_");

		final String fileName2 = dirPath + tmp + imageItem.name_zh + "_" + (k + 1) + ".jpg";
		// download
		final String url = allImageItemUrls[k];
		if (url.isEmpty()) {
			return;
		}

		Thread moduleThread = new Thread(new Runnable() {

			@Override
			public void run() {
				DonwloadUtil.donwloadImg(url, fileName, fileName2);
			}
		});
		moduleThread.start();
		return;
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
			print("parseImageUrl:" + imageUrl);
			int first = imageUrl.indexOf("\"");
			int last = imageUrl.lastIndexOf("\"");
			if (first != -1 && last != -1) {
				try {
					imageUrl = imageUrl.substring(first + 1, last);
					imageUrl = imageUrl.replace("big", "pic");
					items[j] = imageUrl;
				} catch (Exception e) {
					print("parseImageUrl Error:" + e);
				}
			} else {
				items[j] = "";
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
		Utils.writeLog(dirPath, str);
	}

	/**
	 * module item
	 * 
	 * @author linjianyou
	 * 
	 */
	static class ModuleInfoBean {
		public String name_zh;
		public String name_en;
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
	public static class GridItemInfoBean {
		public String name_zh;
		public String module_name_en;
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

	private static List<List<GridItemInfoBean>> parsingRoleItemArrayObjToGridItemInfoBeanList(JSONObject rootObj) {
		try {
			List<List<GridItemInfoBean>> result = new ArrayList<List<GridItemInfoBean>>();
			JSONObject moduleRole = rootObj.getJSONObject("role_item_array_obj");
			Iterator<String> keys = moduleRole.keys();

			while (keys.hasNext()) {
				String key = keys.next();
				JSONArray someoneModuleRoleArray = moduleRole.getJSONArray(key);

				List<GridItemInfoBean> moduleGrids = new ArrayList<GridItemInfoBean>();

				for (int i = 0; i < someoneModuleRoleArray.length(); i++) {
					JSONObject item = someoneModuleRoleArray.getJSONObject(i);
					String name_zh = item.getString("role_name_zh");
					String role_url = item.getString("role_url");
					String baseUrl = item.getString("role_module_base_url");

					GridItemInfoBean itemInfoBean = new GridItemInfoBean();
					itemInfoBean.name_zh = name_zh;
					itemInfoBean.url = role_url;
					itemInfoBean.moduleBaseUrl = baseUrl;
					moduleGrids.add(itemInfoBean);
				}

				result.add(moduleGrids);
			}

			return result;
		} catch (JSONException e) {
			print(e.toString());
			e.printStackTrace();
		}

		return null;
	}
}
