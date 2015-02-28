package cn.linjonh.jsoup.util;


public class PrintDocumentStructure {
	static final String web_1="http://tu.meinvdd.com/meinv/5977.html";
	static final String web_2="http://www.meizitu.com";
	public PrintDocumentStructure() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(ConnUtil.getHtmlDocument(web_2));
	}

}
