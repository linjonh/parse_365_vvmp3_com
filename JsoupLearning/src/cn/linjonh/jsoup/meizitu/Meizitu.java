package cn.linjonh.jsoup.meizitu;


import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.linjonh.jsoup.util.ConnUtil;

//import android.content.Context;

public class Meizitu {
	static final String web_base = "http://www.meizitu.com/a/";
	public static final String HURL = "http://www.meizitu.com";
	static final String head_page = "list_1_1.html";

	public Meizitu() {
	}
	public static void main(String[] args){
		doGetDataLists();
	}
//	/**
//	 * s
//	 * 
//	 * @param ct
//	 * @param html
//	 * @param l
//	 * @return
//	 */
//	public static ArrayList<MeizituEntity> getDataLists(Boolean isTagRUL, Context ct, String html, OnLoadDocumentComplete l) {
//		return doGetDataLists(isTagRUL, html, l, null, null, ct);
//	}
//
//	/**
//	 * 默认主页url
//	 * 
//	 * @param ct
//	 * @param l
//	 * @return
//	 */
//	public static ArrayList<MeizituEntity> getDataLists(Context ct, OnLoadDocumentComplete l) {
//		return doGetDataLists(false, null, l, null, null, ct);
//	}
//
//	/**
//	 * 
//	 * @param ct
//	 * @param html
//	 * @return
//	 */
//	public static ArrayList<MeizituEntity> getDataLists(Context ct, String html) {
//		return doGetDataLists(false, html, null, null, null, ct);
//	}
//
//	/**
//	 * 
//	 * @param ct
//	 * @param nextPageStr
//	 * @return
//	 */
//	public static ArrayList<MeizituEntity> getNextDataLists(Context ct, String nextPageStr,OnLoadDocumentComplete l) {
//		return doGetDataLists(false, null, l, nextPageStr, null, ct);
//	}

	// public static ArrayList<MeizituEntity> getDataListsByTag(Context ct,
	// String tagURL, OnLoadDocumentComplete l) {
	// return doGetDataLists(null, l, null, tagURL, ct);
	// }

	/**
	 * 
	 * @param docs
	 * @return
	 */
	public static ArrayList<String> getNextPagesStr(Boolean isTagURL, Document docs) {
		ArrayList<String> strs = new ArrayList<String>();
		Elements els = docs.select("#wp_page_numbers li a");
		for (int i = 0; i < els.size(); i++) {
			String next = els.get(i).attr("href");
			if (isTagURL) {
				if (!next.contains("http")) {
					next = HURL + next;
				}
			} else {
				if (!next.contains("http")) {
					next = web_base + next;
				}
			}
			strs.add(next);
		}
		return strs;
	}

	/**
	 * 主页的图片更新信息
	 * 
	 * @param isTagURL
	 * @param html
	 *            本地缓存的html文档内容
	 * @param l
	 *            OnLoadDocumentComplete监听器
	 * @param nextPageUrl
	 *            下一页地址
	 * @return
	 */
//	private static ArrayList<MeizituEntity> doGetDataLists(boolean isTagURL, String html, OnLoadDocumentComplete l, String nextPageUrl,
//			String TagURL, Context ct) {
	private static ArrayList<MeizituEntity> doGetDataLists() {
		// TODO Auto-generated method stub
		ArrayList<MeizituEntity> lists = new ArrayList<MeizituEntity>();
		Document meizitu = null;
//		if (html != null) {
//			// 本地缓存的html文档内容
//			meizitu = Jsoup.parse(html);
//		} else if (nextPageUrl != null) {
//			// 加载下一页页面
//			if(!nextPageUrl.contains("http")){
//				nextPageUrl= web_base + nextPageUrl;
//			}
//			meizitu = ConnUtil.getHtmlDocument(nextPageUrl, ct);
//		} else if (TagURL != null) {
//			meizitu = ConnUtil.getHtmlDocument(TagURL, ct);
//		} else {
			// 默认加载首页
//			meizitu = ConnUtil.getHtmlDocument(web_base + head_page, ct);
//		String url=web_base + head_page;
//		System.out.println(url);
			meizitu = ConnUtil.getHtmlDocument("http://www.meizitu.com/tags.php?/%D4%A1%CA%D2/");
//		}

		if (meizitu != null) {
//			if (l != null) {
//				l.onComplete(meizitu, getNextPagesStr(isTagURL, meizitu));
//			}
			getNextPagesStr(false, meizitu);
			// System.out.println(meizitu.toString());
			Elements titles = meizitu.select(".metaRight a");
			Elements pics = meizitu.select("#picture a");
//			Elements titles = meizitu.select("div.con .tit a");
//			Elements pics = meizitu.select("div.con div.pic a");

			// if (titles != null)
			// System.out.println(titles.size());
			// System.out.println(pics.size());
			if (pics.size() != 0) {
//				 System.out.println(pics.toString());
				for (int i = 0; i < pics.size(); i++) {
					Element pic = pics.get(i);
					Element title = titles.get(i);

					MeizituEntity entity = new MeizituEntity();

					String detailHtmlURL = pic.attr("href");
					String picThumb = pic.select("img").attr("data-original");
					
					entity.setTitles(title.text());
					entity.setPicDetailURL(detailHtmlURL);
					entity.setPicThumbURL(picThumb);
					
					System.out.println(entity.toString());
					lists.add(entity);
				}
			} else {
				System.out.println("empty");
			}

		} else {
			System.out.println("document null");
		}
		// test print out
		// for (MeizituEntity en : mLists) {
		// System.out.println(en.getTitles());
		// System.out.println(en.getPicDetailURL());
		// System.out.println(en.getPicThumbURL() + "\n");
		// }
		return lists;
	}

//	/**
//	 * 主页某一item的详细图片信息
//	 * 
//	 * @param ct
//	 * @param picHtmlURL
//	 *            某item的详细html地址
//	 * @return
//	 */
//	public static ArrayList<String> getDetaitlePictureURLs(Boolean isFromTag, Context ct, String picHtmlURL) {
//		Document doc = ConnUtil.getHtmlDocument(picHtmlURL, ct);
//		ArrayList<String> strs = new ArrayList<String>();
//		if (doc != null) {
//			// if (!isFromTag) {
//			// Elements els = doc.select("div#picture img");
//			// }else{
//			Elements els = doc.select("div.postContent img");
//			// }
//			for (Element el : els) {
//				String url = el.attr("src");
////				L.i("Meizitu", "url" + url);
//				strs.add(url);
//			}
//		}
//		return strs;
//
//	}
//
//	/**
//	 * 
//	 * @param ct
//	 *            检查网络用到的上下文
//	 * @param htmlContent
//	 *            缓存的网页内容,为null则默认从首页地址加载
//	 * @return
//	 */
//	public static ArrayList<HashMap<String, String>> getTags(Context ct, String htmlContent) {
//		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
//		Document meizitu = null;
//		if (htmlContent != null) {
//			meizitu = Jsoup.parse(htmlContent);
//		} else {
//			meizitu = ConnUtil.getHtmlDocument(web_base + head_page, ct);
//		}
//		if (meizitu != null) {
//			Elements els = meizitu.select("div.tags span a");
//			for (Element el : els) {
//				HashMap<String, String> map = new HashMap<String, String>();
//				map.put(KEY_TAG_TEXT, el.attr("title"));
//				map.put(KEY_TAG_URL, el.attr("href"));
//				list.add(map);
//			}
//		} else {
//			System.out.println("getTags==>Meizitu is null");
//		}
//		return list;
//	}
//
//	/**
//	 * 
//	 * @param ct
//	 * @return tag maps
//	 * 
//	 */
//	public static ArrayList<HashMap<String, String>> loadStaticMenuTagsAndURLs(Context ct) {
//		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
//		String[] titles = ct.getResources().getStringArray(R.array.tag_name_array);
//		for (int i = 0; i < titles.length; i++) {
//			HashMap<String, String> map = new HashMap<String, String>();
//			map.put(KEY_TAG_TEXT, titles[i]);
//			map.put(KEY_TAG_URL, Constant.URL_TAGS[i]);
//			list.add(map);
//		}
//		return list;
//	}

	public static String KEY_TAG_TEXT = "tag_text";
	public static String KEY_TAG_URL = "tag_url";
}
