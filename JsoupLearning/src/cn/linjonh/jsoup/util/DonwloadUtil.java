package cn.linjonh.jsoup.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.linjonh.jsoup.util.Utils;

public class DonwloadUtil {

	// public static boolean DonwloadImg(String fileUrl, String savePath, String
	// pageIndicate)/* fileUrl������Դ��ַ */
	// {
	//
	// try {
	// URL url = new URL(fileUrl);/* ��������Դ��ַ����,����ֵ��url */
	// /* ��Ϊ��ϵ���������Դ�Ĺ̶���ʽ�÷����Ա�����in�������url��ȡ������Դ�������� */
	// HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	// DataInputStream in = new DataInputStream(connection.getInputStream());
	// /* �˴�Ҳ����BufferedInputStream��BufferedOutputStream */
	// DataOutputStream out = new DataOutputStream(new
	// FileOutputStream(savePath));
	// /* ������savePath��������ȡ��ͼƬ�Ĵ洢�ڱ��ص�ַ��ֵ��out�������ָ���ĵ�ַ */
	// byte[] buffer = new byte[4096];
	// int count = 0;
	// while ((count = in.read(buffer)) > 0)/* �����������ֽڵ���ʽ��ȡ��д��buffer��
	// */
	// {
	// out.write(buffer, 0, count);
	// }
	// out.close();/* ��������Ϊ�ر�����������Լ�������Դ�Ĺ̶���ʽ */
	// in.close();
	// connection.disconnect();
	// System.out.println("��" + pageIndicate + "��ҳ" + fileUrl + "\n" +
	// savePath);
	// return true;/* ������Դ��ȡ���洢���سɹ�����true */
	//
	// } catch (Exception e) {
	// System.out.println(e + "\n" + fileUrl + "\n" + savePath);
	// return false;
	// }
	// }
	/**
	 * 
	 * @param imgFileUrl
	 * @param dirPath1
	 * @param fileName
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
			writeLog(dir.getAbsolutePath(), log);
			return true;
		} else if (fileName != null) {
			imageFile = new File(fileName);// add module name
			if (imageFile.exists()) {
				String log = "Exists file: " + imageFile.getAbsolutePath();
				Utils.print(log);
				writeLog(dir.getAbsolutePath(), log);
				return true;
			}
		}

		DataOutputStream out = null;
		DataInputStream in = null;
		HttpURLConnection connection = null;
		try {

			URL url = new URL(imgFileUrl);
			connection = (HttpURLConnection) url.openConnection();
			in = new DataInputStream(connection.getInputStream());

			out = new DataOutputStream(new FileOutputStream(imageFile));

			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = in.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}
			out.close();
			in.close();
			connection.disconnect();
			String log = "save File: " + imageFile.getAbsolutePath() + " URL: " + imgFileUrl;
			System.out.println(log);
			writeLog(dir.getAbsolutePath(), log);
			flag = true;

		} catch (Exception e) {
			String log = "donwload File: " + imageFile.getAbsolutePath() + " URL: " + imgFileUrl + " Error: " + e;
			System.out.println(log);
			writeLog(dir.getAbsolutePath(), log);
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
			}
			try {
				if (in != null)
					in.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			connection.disconnect();
			return flag;
		}
	}

	public static synchronized void writeLog(String dir, String log) {
		File file = new File(dir + "/log.txt");
		String time = "[" + Utils.getFormatedTime() + "]: ";
		log = time + log + "\n";
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file, true);
			fileOutputStream.write(log.getBytes());
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
