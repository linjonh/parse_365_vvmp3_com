package cn.linjonh.jsoup.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.linjonh.jsoup.M22MM;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;

public class ConnUtil {
	/**
	 * 
	 * @param htmlUrl
	 *            dir LogPath.logPath
	 * @return
	 */
	public static Document getHtmlDocument(String htmlUrl) {
		return getHtmlDocument(htmlUrl, LogPath.logPath);
	}

	/**
	 * 
	 * @param htmlUrl
	 * @param logDir
	 *            log dir
	 * @return
	 */
	public static Document getHtmlDocument(String htmlUrl, String logDir) {
		Document document = null;
		do {
			try {
				// Thread.sleep(0);
				document = Jsoup
						.connect(htmlUrl)
						.userAgent(
								"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36")
						.get();
				String log = "document connected";
				Utils.print(log);
				Utils.writeLog(logDir, log);
			} catch (IOException e) {
				String log = "htmlUrl: " + htmlUrl
						+ "^^^\ndocument connect exception: " + e
						+ "\ntrying connect again...";
				Utils.print(log);
				Utils.writeLog(logDir, log);
			}
			// catch (InterruptedException e) {
			// e.printStackTrace();
			// String
			// log="htmlUrl: "+htmlUrl+"^^^\ngetHtmlDocument InterruptedException: "+e.toString();
			// Utils.print(log);
			// Utils.writeLog(dir, log);
			// }
		} while (document == null);
		return document;
	}

	/**
	 * 
	 * @param fileName
	 * @param parsekey
	 * @return decoded string
	 */
	public static String decode(String fileName, byte parsekey) {

		ByteArrayBuffer byteBuffer = new ByteArrayBuffer();
		// read.
		try {
			File file = new File(fileName);
			FileInputStream in = new FileInputStream(file);
			byte[] by = new byte[1024];

			int count = 0;
			while ((count = in.read(by)) > 0) {
				for (int k = 0; k < count; k++) {
					by[k] -= parsekey;
				}
				byteBuffer.write(by);
			}
			in.close();
			byteBuffer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("read=====\n" + byteBuffer.toString());
		return byteBuffer.toString();
	}

	/**
	 * 
	 * @param encodeStr
	 * @param encodekey
	 * @param saveFileName
	 */
	public static void encodeString(String encodeStr, byte encodekey,
			String saveFileName) {
		createDirectoysIfNeed(saveFileName);
		int i = encodeStr.getBytes().length;
		FileOutputStream os;
		File file = new File(saveFileName);
		try {
			os = new FileOutputStream(file, false);
			DataOutputStream dataOutputStream = new DataOutputStream(os);
			for (int j = 0; j < i; j++) {
				dataOutputStream.write(encodeStr.getBytes()[j] + encodekey);
			}
			dataOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * create directory if not exists on Linux or windows platform
	 * 
	 * @param path
	 */
	public static void createDirectoysIfNeed(String path) {
		int hitBackSlashIndex = path.lastIndexOf("/");
		int hitSlashIndex = path.lastIndexOf("\\");
		// disk path on Linux at 0, windows at 2
		String tmppath = "";
		if (hitSlashIndex != -1 && hitBackSlashIndex != -1) {
			// has both back slash and slash
			if (hitBackSlashIndex < hitSlashIndex) {
				if (hitBackSlashIndex != 0 && hitBackSlashIndex != 2) {
					tmppath = path.substring(0, hitSlashIndex);
				}
			} else {
				if (hitSlashIndex != 0 && hitSlashIndex != 2) {
					tmppath = path.substring(0, hitBackSlashIndex);
				}
			}
		} else if (hitBackSlashIndex != -1 && hitBackSlashIndex != 0) {
			// only Linux back slash char
			tmppath = path.substring(0, hitBackSlashIndex);

		} else if (hitSlashIndex != -1 && hitSlashIndex != 2) {
			// only Windows slash char
			tmppath = path.substring(0, hitSlashIndex);
		}
		File dir = new File(tmppath);
		if (!dir.exists()) {
			if (dir.mkdirs()) {
				System.out.println("mkdirs success!");
			} else {
				System.out.println("mkdirs failed!");
			}
		}
	}

	public static final byte KEYS = 6;
	public static final String FILE_DIR = "D:/data";
}
