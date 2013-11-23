package cn.linjonh.jsoup.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DonwloadUtil {

	public static boolean DonwloadImg(String fileUrl, String savePath, String pageIndicate)/* fileUrl������Դ��ַ */
	{

		try {
			URL url = new URL(fileUrl);/* ��������Դ��ַ����,����ֵ��url */
			/* ��Ϊ��ϵ���������Դ�Ĺ̶���ʽ�÷����Ա�����in�������url��ȡ������Դ�������� */
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			DataInputStream in = new DataInputStream(connection.getInputStream());
			/* �˴�Ҳ����BufferedInputStream��BufferedOutputStream */
			DataOutputStream out = new DataOutputStream(new FileOutputStream(savePath));
			/* ������savePath��������ȡ��ͼƬ�Ĵ洢�ڱ��ص�ַ��ֵ��out�������ָ���ĵ�ַ */
			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = in.read(buffer)) > 0)/* �����������ֽڵ���ʽ��ȡ��д��buffer�� */
			{
				out.write(buffer, 0, count);
			}
			out.close();/* ��������Ϊ�ر�����������Լ�������Դ�Ĺ̶���ʽ */
			in.close();
			connection.disconnect();
			System.out.println("��" + pageIndicate + "��ҳ" + fileUrl + "\n" + savePath);
			return true;/* ������Դ��ȡ���洢���سɹ�����true */

		} catch (Exception e) {
			System.out.println(e + "\n" + fileUrl + "\n" + savePath);
			return false;
		}
	}
}
