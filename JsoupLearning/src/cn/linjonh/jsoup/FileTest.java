package cn.linjonh.jsoup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileTest {
	private static int len = 0;

	public FileTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) {

		try {
			FileInputStream in = new FileInputStream(
					"D:\\H5\\H5_Meizitu\\meiztu.html");
			byte[] inb = new byte[1024];
			
			
			FileOutputStream os2 = new FileOutputStream("F:\\filetest.txt");
			
			// byte[] b = "hello".getBytes();
			while (in.read(inb) != -1) {
				System.out.println(inb.length);
				os2.write(inb);
			}
			in.close();
			
			len = inb.length;
			os2.close();
			System.out.println("写完了");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			FileOutputStream os = new FileOutputStream("F:\\filetest.txt",true);
			byte[] b = "nnnnnnnnnnnnnnnnnnnnnnaaaaaa".getBytes();
			os.write(b);
			os.close();
			System.out.println("写完了");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
