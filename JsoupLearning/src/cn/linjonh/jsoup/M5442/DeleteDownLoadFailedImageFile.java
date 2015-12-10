package cn.linjonh.jsoup.M5442;

import cn.linjonh.jsoup.util.Utils;

import java.io.File;

/**
 * Created by john on 2015/12/9.
 */
public class DeleteDownLoadFailedImageFile {
	private static       String path = "D:\\M5442_IMG\\";
	private static final int    KB   = 1024;

	public static void main(String[] args) {
		if (args != null && args.length > 0) {
			path = args[0];
		}
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
//            int count = 1000;
			for (int i = 0; i < files.length; i++) {
				File image = files[i];
				String name = image.getName();
				if (!image.isDirectory() && !name.contains(".txt")) {
					long size = image.length() / KB;
					if (size < 50) {
						Utils.print("index:" + i + " size: " + size + "KB, " + name);
						if (image.delete()) {
							Utils.print("deleted file index:" + i + " size: " + size + "KB, " + name);
							Utils.writeLog(path + "DeletedFile/",
									"deleted file index:" + i + " size: " + size + "KB, " + name);
						} else {
							Utils.print("Failed to delete file index:" + i + " size: " + size + "KB, " + name);
							Utils.writeLog(path + "DeletedFile/",
									"Failed to delete file index:" + i + " size: " + size + "KB, " + name);
						}
//                        count--;
//                        if (count < 0) {
//                            break;
//                        }
					}
				}

			}
		}

	}
}
