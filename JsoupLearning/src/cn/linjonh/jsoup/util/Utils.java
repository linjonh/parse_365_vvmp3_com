package cn.linjonh.jsoup.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

public class Utils {

	public Utils() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		print(getFileName("http://xz1.mm667.com/s/009.jpg"));
	}

	/**
	 * @param imgFileUrl
	 * @return
	 */
	public static String getFileName(String imgFileUrl) {
		String imgFileName = imgFileUrl.replace("http://", "");
		imgFileName = imgFileName.substring(imgFileName.indexOf("/") + 1);
		imgFileName = imgFileName.replace("/", "_");
		return imgFileName;

	}

	public static void print(String str) {
		String time = "[" + Utils.getFormatedTime() + "]: ";
		System.out.println(time + str);
	}
	public static void printError(String str) {
		String time = "[" + Utils.getFormatedTime() + "]: ";
//		System.Logger.Level.ERROR;
		System.getLogger("cn.linjonh.jsoup.util.LOG").log(System.Logger.Level.ERROR,time + str);
	}

	public static String getFormatedTime() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		return dateFormat.format(date);
	}

	public static String getFormatedTime(String patternFormat) {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(patternFormat,
				Locale.CHINA);
		return dateFormat.format(date);
	}

	/**
	 * create directory if not exists on Linux or windows platform
	 *
	 * @param path
	 */
	public static boolean createDirectoysIfNeed(String path) {
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
				System.out.println("createDirectoysIfNeed() mkdirs success! filePath:" + dir.getAbsolutePath());
				return true;
			} else {
				System.out.println("createDirectoysIfNeed() mkdirs failed! filePath:" + dir.getAbsolutePath());
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * @param json
	 */
	public static void writeLoopDataToDefaultFile(JSONObject json) {
		writeJsonDataToFile(null, json);
	}

	/**
	 * @param filePath
	 * @param json
	 */
	public static void writeJsonDataToFile(String filePath, JSONObject json) {
		File stateFile = null;
		if (filePath == null) {
			filePath = "D:/LoopPositionState/status.json";
			if (!createDirectoysIfNeed(filePath)) {
				filePath = "/home/status.json";
				createDirectoysIfNeed(filePath);
			}
		}
		stateFile = new File(filePath);
		try {
			OutputStreamWriter out = new OutputStreamWriter(
					new FileOutputStream(stateFile, false));
			BufferedWriter writer = new BufferedWriter(out);
			writer.write(json.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	public static JSONObject readJsonDataFromDefaultFile() {
		String filePath = "D:/LoopPositionState/status.json";
		return readJsonDataFromFile(filePath);
	}

	/**
	 * @param filePath
	 * @return
	 */
	public static JSONObject readJsonDataFromFile(String filePath) {
		File stateFile = new File(filePath);
		JSONObject jsonObject = null;
		if (stateFile.exists()) {
			try {
				InputStreamReader inputStreamReader = new InputStreamReader(
						new FileInputStream(stateFile));
				BufferedReader reader = new BufferedReader(inputStreamReader);
				StringBuilder builder = new StringBuilder();
				String tmp = "";
				while ((tmp = reader.readLine()) != null) {
					builder.append(tmp);
				}
				reader.close();
				try {
					jsonObject = new JSONObject(builder.toString());

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}

	/**
	 * write log to default logPath {@link LogPath}
	 *
	 * @param log
	 */
	public static synchronized void writeLog(String log) {
		writeLog(LogPath.logPath, log);
	}

	public static final int SIZE_10M   = 10 * 1024 * 1024;
	public static       int file_index = 0;

	/**
	 * @param logPath
	 * @param log
	 */
	public static synchronized void writeLog(String logPath, String log) {
		File file;
//		do {
			file = new File(logPath + "donwloadImgPageIndex" + file_index + ".txt");
			if (!file.exists()) {
				createDirectoysIfNeed(logPath);
			}
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
//			file_index++;
//		} while (file.length() > SIZE_10M);
		String time = "[" + Utils.getFormatedTime() + "]: ";
		log = time + log + "\n";
//		log = log + "\n";
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file, true);
			fileOutputStream.write(log.getBytes());
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
