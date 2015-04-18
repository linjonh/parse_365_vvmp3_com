package cn.linjonh.jsoup.HDYouMa;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.Utils;

public class GaoQingYouMa {

	public GaoQingYouMa() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		Document docs = ConnUtil.getHtmlDocument("http://66.bbs333.com/om/");
		Elements els = docs.select("td a");
		ThreadPoolExecutor executor=new ThreadPoolExecutor(4, 8, 1,TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
		final CountDownLatch countDownLatch=new CountDownLatch(els.size());
		for (Element el : els) {
			String url = el.attr("href");
//			final String path=el.baseUri() + url;
			final String path="http://66.bbs333.com" + url;
			Thread command=new Thread(new Runnable() {
				
				@Override
				public void run() {
					Document doc = ConnUtil.getHtmlDocument(path);
					Elements subEls=doc.select("td");
					Utils.writeLog("E:/workspace/", subEls.toString());
					System.out.println(subEls.toString());
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
