package cn.linjonh.jsoup.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

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

	public static String getFormatedTime() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		String appedDateInfo = dateFormat.format(date);
		return appedDateInfo;
	}

	public static String getFormatedTime(String patternFormat) {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(patternFormat, Locale.CHINA);
		String appedDateInfo = dateFormat.format(date);
		return appedDateInfo;
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
				System.out.println("mkdirs success!");
				return true;
			} else {
				System.out.println("mkdirs failed!");
				return false;
			}
		} else {
			return true;
		}
	}

	public static void writeLoopDataToDefaultFile(JSONObject json) {
		writeJsonDataToFile(null, json);
	}

	/**
	 * 
	 * @param filename
	 * @param postion
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
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(stateFile, false));
			BufferedWriter writer = new BufferedWriter(out);
			writer.write(json.toString());
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @return
	 */
	public static JSONObject readJsonDataFromDefaultFile(){
		String filePath = "D:/LoopPositionState/status.json"; 
		return readJsonDataFromFile(filePath);
	}
	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static JSONObject readJsonDataFromFile(String filePath) {
		File stateFile = new File(filePath);
		JSONObject jsonObject = null;
		if (stateFile.exists()) {
			try {
				InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(stateFile));
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
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}
}
