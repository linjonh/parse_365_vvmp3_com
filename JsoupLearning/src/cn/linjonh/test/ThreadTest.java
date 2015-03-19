package cn.linjonh.test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.linjonh.jsoup.util.Utils;

public class ThreadTest {

	public ThreadTest() {
	}

	static ThreadPoolExecutor executor;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();
		executor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.SECONDS, workQueue);
		final CountDownLatch latch = new CountDownLatch(6);
		for (int i = 0; i < 6; i++) {
			final int index = i;
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					Utils.print("thread:" + index);
					latch.countDown();
				}
			});
			executor.execute(thread);
		}
		try {
			latch.await();
			executor.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Utils.print("done work");
	}

}
