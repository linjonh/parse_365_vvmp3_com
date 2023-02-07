package cn.linjonh.jsoup.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DownloadUtil {
    private static void printlog() {

    }

    /**
     * @param imgFileUrl
     * @param path
     * @return
     */
    public static boolean donwloadImg(String imgFileUrl, String path) {
        return donwloadImg(imgFileUrl, path, null);
    }

    /**
     * @param imgFileUrl
     * @param dirPath
     * @param fileName
     * @return
     */
    @SuppressWarnings("finally")
    public static boolean donwloadImg(String imgFileUrl, String dirPath, String fileName) {
        boolean flag = false;
        Exception error = null;
        do {
            File imageFile;
            File dir;
            dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            imageFile = new File(dirPath + "/" + Utils.getFileName(imgFileUrl));
            if (imageFile.exists()) {
                String log = "Exists file: " + imageFile.getAbsolutePath();
                Utils.print(log);
                Utils.writeLog(dirPath + "Log/", log);
                return true;
            }
            DataOutputStream out = null;
            DataInputStream in = null;
            HttpURLConnection connection = null;


            try {
                URL url = new URL(imgFileUrl);
                while (in == null) {
                    try {
                        connection = (HttpURLConnection) url.openConnection();
                        connection.connect();
                        connection.getResponseCode();
                        Map<String, List<String>> headerFields = connection.getHeaderFields();
                        Set<String> strings = headerFields.keySet();
                        for (String key : strings) {
                            List<String> list = headerFields.get(key);
                            for (String s : list) {
                                Utils.print("key:" + key + " values:" + s);
                            }
                        }
                        in = new DataInputStream(connection.getInputStream());
                        Utils.print("connected.........................imgFileUrl" + imgFileUrl);
                        Utils.writeLog(dirPath + "Log/", "connected.....................imgFileUrl" + imgFileUrl);
                        break;
                    } catch (Exception e) {
                        Utils.print("connection error:" + e.toString() + "\n^^^^^^^imgFileUrl:" + imgFileUrl);
                        Utils.writeLog(dirPath + "Log/",
                                "connection error:" + e.toString() + "\n^^^^^^^imgFileUrl:"
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
                    Utils.writeLog(dirPath + "Log/", "connect again.....................imgFileUrl" + imgFileUrl);
                }
                out = new DataOutputStream(new FileOutputStream(imageFile));

                byte[] buffer = new byte[4096];
                int count = 0;
//                while ((count = in.read(buffer)) > 0) {
//                    out.write(buffer, 0, count);
//                }
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
                Utils.writeLog(dirPath + "Log/", log);
                flag = true;

            } catch (Exception e) {
                error = e;
                String log = "donwload File: " + imageFile.getAbsolutePath() + " URL: " + imgFileUrl + " Error: " + e;
                Utils.print(log);
                Utils.writeLog(dirPath + "Log/", log);
                flag = false;
            } finally {
                try {
                    if (out != null)
                        out.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                    Utils.writeLog(dirPath + "Log/", e.toString());
                }
                try {
                    if (in != null)
                        in.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                    Utils.writeLog(dirPath + "Log/", e.toString());
                }
                try {
                    if (connection != null) {
                        connection.disconnect();
                    }
                } catch (Exception e2) {
                    Utils.writeLog(dir.getAbsolutePath(), e2.toString());
                    e2.printStackTrace();
                }
                if (!flag) {
                    if (imageFile.delete()) {
                        Utils.print("delete file:" + imageFile.getAbsolutePath());
                        Utils.writeLog(dirPath + "Log/", "delete file:" + imageFile.getAbsolutePath());
                    } else {
                        Utils.print("delete file failed:" + imageFile.getAbsolutePath());
                        Utils.writeLog(dirPath + "Log/", "delete file failed:" + imageFile.getAbsolutePath());
                    }
                }
            }
        } while (error != null && error instanceof SocketException);
        return flag;
    }
}
