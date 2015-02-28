package cn.linjonh.jsoup.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
<<<<<<< HEAD
=======
import java.io.File;
>>>>>>> aedf8a00ed4e3726b810ef3c48f62f91f9f8de69
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DonwloadUtil {

	public static boolean DonwloadImg(String fileUrl, String savePath, String pageIndicate)/* fileUrl网络资源地址 */
	{

		try {
			URL url = new URL(fileUrl);/* 将网络资源地址传给,即赋值给url */
			/* 此为联系获得网络资源的固定格式用法，以便后面的in变量获得url截取网络资源的输入流 */
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			DataInputStream in = new DataInputStream(connection.getInputStream());
			/* 此处也可用BufferedInputStream与BufferedOutputStream */
			DataOutputStream out = new DataOutputStream(new FileOutputStream(savePath));
			/* 将参数savePath，即将截取的图片的存储在本地地址赋值给out输出流所指定的地址 */
			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = in.read(buffer)) > 0)/* 将输入流以字节的形式读取并写入buffer中 */
			{
				out.write(buffer, 0, count);
			}
			out.close();/* 后面三行为关闭输入输出流以及网络资源的固定格式 */
			in.close();
			connection.disconnect();
			System.out.println("第" + pageIndicate + "网页" + fileUrl + "\n" + savePath);
			return true;/* 网络资源截取并存储本地成功返回true */

		} catch (Exception e) {
			System.out.println(e + "\n" + fileUrl + "\n" + savePath);
			return false;
		}
	}
	public static boolean DonwloadImg(String imgFileUrl, String dirPath)/* fileUrl网络资源地址 */
	{
		
		try {
			
			File dir=new File(dirPath);
			if(!dir.exists()){
				dir.mkdir();
			}
			File imageFile = new File(dirPath + "/" + Utils.getFileName(imgFileUrl));
			if(imageFile.exists()){
				return true;
			}
			
			URL url = new URL(imgFileUrl);/* 将网络资源地址传给,即赋值给url */
			/* 此为联系获得网络资源的固定格式用法，以便后面的in变量获得url截取网络资源的输入流 */
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			DataInputStream in = new DataInputStream(connection.getInputStream());
			/* 此处也可用BufferedInputStream与BufferedOutputStream */
			
			DataOutputStream out = new DataOutputStream(new FileOutputStream(imageFile));
			/* 将参数savePath，即将截取的图片的存储在本地地址赋值给out输出流所指定的地址 */
			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = in.read(buffer)) > 0)/* 将输入流以字节的形式读取并写入buffer中 */
			{
				out.write(buffer, 0, count);
			}
			out.close();/* 后面三行为关闭输入输出流以及网络资源的固定格式 */
			in.close();
			connection.disconnect();
			System.out.println("\nsave " + imgFileUrl + " at:" + imageFile.getAbsolutePath());
			return true;/* 网络资源截取并存储本地成功返回true */
			
		} catch (Exception e) {
			System.out.println("donwload " + imgFileUrl + " in " + dirPath +" error: "+e );
			return false;
		}
	}
}
