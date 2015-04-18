package cn.linjonh.jsoup.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.linjonh.jsoup.util.Utils;

public class DonwloadUtil {
	private static void printlog() {

	}

	/**
	 * 
	 * @param imgFileUrl
	 * @param path
	 * @return
	 */
	public static boolean donwloadImg(String imgFileUrl, String path) {
		return donwloadImg(imgFileUrl, path, null);
	}

	/**
	 * 
	 * @param imgFileUrl
	 * @param dirPath1
	 * @param fileName
	 * @return
	 */
	@SuppressWarnings("finally")
	public static boolean donwloadImg(String imgFileUrl, String dirPath1, String fileName) {
		boolean flag = false;
		File imageFile = null;
		File dir = null;

		if (dirPath1.lastIndexOf("/") == dirPath1.length() - 1) {
			// no name, just directory, then auto generate file name
			dir = new File(dirPath1);
			if (!dir.exists()) {
				dir.mkdir();
			}
			imageFile = new File(dirPath1 + "/" + Utils.getFileName(imgFileUrl));
		} else {
			// contain file name.
			dir = new File(dirPath1.substring(0, dirPath1.lastIndexOf("/")));
			if (!dir.exists()) {
				dir.mkdir();
			}
			imageFile = new File(dirPath1);
		}

		if (imageFile.exists()) {
			String log = "Exists file: " + imageFile.getAbsolutePath();
			Utils.print(log);
			Utils.writeLog(dir.getAbsolutePath(), log);
			return true;
		} else if (fileName != null) {
			imageFile = new File(fileName);// add module name
			if (imageFile.exists()) {
				String log = "Exists file: " + imageFile.getAbsolutePath();
				Utils.print(log);
				Utils.writeLog(dir.getAbsolutePath(), log);
				return true;
			}
		}

		DataOutputStream out = null;
		DataInputStream in = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(imgFileUrl);
			while (in == null) {
				try {
					connection = (HttpURLConnection) url.openConnection();
					in = new DataInputStream(connection.getInputStream());
				} catch (Exception e) {
					Utils.print("connection error:" + e.toString() + "\n^^^^^^^imgFileUrl:" + imgFileUrl);
					Utils.writeLog(dir.getAbsolutePath(), "connection error:" + e.toString() + "\n^^^^^^^imgFileUrl:"
							+ imgFileUrl);
					if (e instanceof FileNotFoundException) {
						break;
					}
					if (e instanceof MalformedURLException) {
						break;
					}
					if (e instanceof NullPointerException) {
						break;
					}
				}
				Utils.print("connect again.........................imgFileUrl" + imgFileUrl);
				Utils.writeLog(dir.getAbsolutePath(), "connect again.....................imgFileUrl" + imgFileUrl);
			}
			out = new DataOutputStream(new FileOutputStream(imageFile));

			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = in.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
			if (connection != null) {
				connection.disconnect();
			}
			String log = "save File: " + imageFile.getAbsolutePath() + " URL: " + imgFileUrl;
			Utils.print(log);
			Utils.writeLog(dir.getAbsolutePath(), log);
			flag = true;

		} catch (Exception e) {
			String log = "donwload File: " + imageFile.getAbsolutePath() + " URL: " + imgFileUrl + " Error: " + e;
			Utils.print(log);
			Utils.writeLog(dir.getAbsolutePath(), log);
			if (imageFile.delete()) {
				Utils.print("delete file:" + imageFile.getAbsolutePath());
			}
			flag = false;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (final IOException e) {
				e.printStackTrace();
				Utils.writeLog(dir.getAbsolutePath(), e.toString());
			}
			try {
				if (in != null)
					in.close();
			} catch (final IOException e) {
				e.printStackTrace();
				Utils.writeLog(dir.getAbsolutePath(), e.toString());
			}
			try {
				if (connection != null) {
					connection.disconnect();
				}
			} catch (Exception e2) {
				Utils.writeLog(dir.getAbsolutePath(), e2.toString());
				e2.printStackTrace();
			}
			return flag;
		}
	}
//	/**
//	 * 
//	 * @param fileUrl
//	 * @param savePath
//	 * @param pageIndicate
//	 * @return
//	 */
//	public static boolean startDonwloadImage(String fileUrl, String savePath, String pageIndicate) {
//		Utils.createDirectoysIfNeed(savePath);
//		try {
//			URL url = new URL(fileUrl);
//			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//			DataInputStream in = new DataInputStream(connection.getInputStream());
//			DataOutputStream out = new DataOutputStream(new FileOutputStream(savePath));
//			byte[] buffer = new byte[4096];
//			int count = 0;
//			while ((count = in.read(buffer)) > 0) {
//				out.write(buffer, 0, count);
//			}
//			out.close();
//			in.close();
//			connection.disconnect();
//			System.out.println("��" + pageIndicate + "��ҳ" + fileUrl + "\n" + savePath);
//			String log = "save File: " + imageFile.getAbsolutePath() + " URL: " + imgFileUrl;
//			Utils.print(log);
//			Utils.writeLog(dir.getAbsolutePath(), log);
//			return true;
//		} catch (Exception e) {
//			System.out.println(e + "\n" + fileUrl + "\n" + savePath);
//			return false;
//		}
//	}
}
