package cn.linjonh.jsoup.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.linjonh.jsoup.util.Utils;

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
	public static boolean DonwloadImg(String imgFileUrl, String dirPath)/* fileUrl������Դ��ַ */
	{
		
		try {
			
			File dir=new File(dirPath);
			if(!dir.exists()){
				dir.mkdir();
			}
			File imageFile = new File(dirPath + "/" + Utils.getFileName(imgFileUrl));
			if(imageFile.exists()){
				Utils.print("Exists file: "+imageFile.getAbsolutePath());
				return true;
			}
			
			URL url = new URL(imgFileUrl);/* ��������Դ��ַ����,����ֵ��url */
			/* ��Ϊ��ϵ���������Դ�Ĺ̶���ʽ�÷����Ա�����in�������url��ȡ������Դ�������� */
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			DataInputStream in = new DataInputStream(connection.getInputStream());
			/* �˴�Ҳ����BufferedInputStream��BufferedOutputStream */
			
			DataOutputStream out = new DataOutputStream(new FileOutputStream(imageFile));
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
			System.out.println("\nsave " + imgFileUrl + " at:" + imageFile.getAbsolutePath()+" time at:"+Utils.getFormatedTime());
			return true;/* ������Դ��ȡ���洢���سɹ�����true */
			
		} catch (Exception e) {
			System.out.println("donwload " + imgFileUrl + " in " + dirPath +" error: "+e +" time at:"+Utils.getFormatedTime());
			return false;
		}
	}
}
