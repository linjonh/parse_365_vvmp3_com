/**
 * 
 */
package cn.linjonh.jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author linjianyou
 * 
 */
public class MeiZiTu {

	private static final String htmlUrl = "http://www.meizitu.com/a/list_1_1.html";
	// private static String htmlUrl = "http://www.meizitu.com/tags.php?/%D4%A1%CA%D2/";

	/**
	 * 
	 */
	public MeiZiTu() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

//		String l=URLDecoder.decode("%D4%A1%CA%D2","GBK");
		// htmlUrl="http://www.meizitu.com/tags.php?"+URLEncoder.encode("浴室", "GBK")+"";
		/*URL oracle = new URL(htmlUrl);
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                                    yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) 
            System.out.println(inputLine);
        in.close();*/
//		System.out.println(l);
		// userAgent("Mozilla/5.0 (Linux; U; Android 4.3; zh-cn; HUAWEI B199 Build/HuaweiB199) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30").timeout(100000)
		Document doc = Jsoup.connect(htmlUrl).get();
//		 ConnUtil.getHtmlDocument(htmlUrl);
		 System.out.println(doc);
//		 Elements elements = doc.select("#wp_page_numbers li a");
//		 for (Element el : elements) {
//		 System.out.println(el.attr("href"));
//		 }
//		getTags(null);
	}

	static final String web_base = "http://www.meizitu.com/a/";
	static final String head_page = "list_1_1.html";

	public static ArrayList<HashMap<String, String>> getTags(String htmlContent) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		Document meizitu = null;
		if (htmlContent != null) {
			meizitu = Jsoup.parse(htmlContent);
		} else {
			// meizitu = ConnUtil.getHtmlDocument(web_base + head_page, ct);
			try {
				meizitu = Jsoup.connect(web_base + head_page).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (meizitu != null) {
			// System.out.println(meizitu);
			Elements els = meizitu.select("div.tags span a");
			for (Element el : els) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("TagText", el.attr("title"));
				map.put("TagURL", el.attr("href"));
				list.add(map);
			}
			System.out.println(els);
		} else {
			System.out.println("null");
		}
		return list;
	}
}
