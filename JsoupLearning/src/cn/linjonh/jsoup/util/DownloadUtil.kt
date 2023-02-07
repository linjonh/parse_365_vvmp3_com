package cn.linjonh.jsoup.util

import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.SocketException
import java.net.URL
import java.net.http.HttpHeaders

object DownloadUtil {
    private fun printlog() {}
    fun getSeparatedPath(dir: String): String {
        return if (dir.endsWith("\\")) {
            dir
        } else dir + File.separator
    }

    /**
     * @param imgFileUrl
     * @param path
     * @return
     */
    @JvmStatic
    fun downloadImg(imgFileUrl: String?, path: String?): Boolean {
        return downloadImg(imgFileUrl!!, path!!, null)
    }

    /**
     * @param imgFileUrl
     * @param path
     * @return
     */
    @JvmStatic
    fun downloadImg(imgFileUrl: String?, path: String?, fileName: String? = null): Boolean {
        return downloadImg(imgFileUrl!!, path!!, fileName)
    }

    /**
     * @param imgFileUrl
     * @param dirPath
     * @param fileName
     * @return
     */
    @JvmStatic
    fun downloadImg(
        imgFileUrl: String,
        dirPath: String,
        fileName: String? = null,
        method: String? = null,
        referer: String? = null, isLogHeaders: Boolean = false
    ): Boolean {
        var flag = false
        var error: Exception? = null
        do {
            val dir: File = File(dirPath)
            if (!dir.exists()) {
                dir.mkdir()
            }
            val imageFile: File = File(getSeparatedPath(dirPath) + Utils.getFileName(imgFileUrl))
            if (imageFile.exists()) {
                val log = "Exists file: " + imageFile.absolutePath
                Utils.print("$log $imgFileUrl")
                Utils.writeLog(getSeparatedPath(dirPath) + "Log/", log)
                return true
            }
            var out: DataOutputStream? = null
            var inputStream: DataInputStream? = null
            var connection: HttpURLConnection? = null
            try {
                val url = URL(imgFileUrl)
                while (inputStream == null) {
                    try {
                        connection = url.openConnection() as HttpURLConnection
                        if (method != null) {
                            connection.addRequestProperty("method", method)
                        }
                        if (referer != null)
                            connection.addRequestProperty("referer", referer)
                        if (isLogHeaders) {
                            Utils.print("------requestProperties------")
                            val requestProperties = connection.requestProperties
                            for (key in requestProperties.keys) {
                                val list = requestProperties[key]!!
                                for (s in list) {
                                    Utils.print("$key: $s")
                                }
                            }
                        }

                        connection.connect()

                        Utils.print("responseCode:${connection.responseCode}")
                        if (isLogHeaders) {
                            val headerFields = connection.headerFields
                            Utils.print("------------")
                            val strings: Set<String> = headerFields.keys
                            for (key in strings) {
                                val list = headerFields[key]!!
                                for (s in list) {
                                    Utils.print("$key: $s")
                                }
                            }
                        }
                        inputStream = DataInputStream(connection.inputStream)
                        Utils.print("connected.........................imgFileUrl$imgFileUrl")
                        Utils.writeLog(
                            getSeparatedPath(dirPath) + "Log/",
                            "connected.....................imgFileUrl$imgFileUrl"
                        )
                        break
                    } catch (e: Exception) {
                        Utils.printError("connection error:$e\n^^^^^^^imgFileUrl:$imgFileUrl")
                        Utils.writeLog(
                            getSeparatedPath(dirPath) + "Log/",
                            """
                                connection error:$e
                                ^^^^^^^imgFileUrl:$imgFileUrl
                                """.trimIndent()
                        )

                        try {
                            connection?.disconnect()
                        } catch (e2: Exception) {
                            Utils.writeLog(dir.absolutePath, e2.toString())
                            e2.printStackTrace()
                        }
                        if (!flag) {
                            if (imageFile.delete()) {
                                Utils.print("delete file:" + imageFile.absolutePath)
                                Utils.writeLog(
                                    getSeparatedPath(dirPath) + "Log/",
                                    "delete file:" + imageFile.absolutePath
                                )
                            } else {
                                Utils.print("delete file failed:" + imageFile.absolutePath)
                                Utils.writeLog(
                                    getSeparatedPath(dirPath) + "Log/",
                                    "delete file failed:" + imageFile.absolutePath
                                )
                            }
                        }

                        if (e is FileNotFoundException) {
                            break
                        }
                        if (e is MalformedURLException) {
                            break
                        }
                        if (e is NullPointerException) {
                            break
                        }
                        if (e.message.equals("Already connected")) {
                            break
                        }
                    }
                    Utils.printError("connect again.........................imgFileUrl$imgFileUrl")
                    Utils.writeLog(
                        getSeparatedPath(dirPath) + "Log/", "connect again.....................imgFileUrl$imgFileUrl"
                    )
                }
                inputStream?.let {
                    out = DataOutputStream(FileOutputStream(imageFile))
                    val buffer = ByteArray(4096)
                    var count = 0
                    while (true) {
                        count = inputStream.read(buffer)
                        if (count > 0) {
                            out!!.write(buffer, 0, count);
                        } else {
                            break
                        }
                    }

                    if (out != null) {
                        out!!.close()
                    }
                    inputStream?.close()
                    connection?.disconnect()
                    val log = "save File: " + imageFile.absolutePath + " URL: " + imgFileUrl
                    Utils.print(log)
                    Utils.writeLog(getSeparatedPath(dirPath) + "Log/", log)
                    flag = true
                }
                if (inputStream == null) flag = false
            } catch (e: Exception) {
                error = e
                val log = "donwload File: " + imageFile.absolutePath + " URL: " + imgFileUrl + " Error: " + e
                Utils.printError(log)
                Utils.writeLog(getSeparatedPath(dirPath) + "Log/", log)
                flag = false
            } finally {
                try {
                    out?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Utils.writeLog(getSeparatedPath(dirPath) + "Log/", e.toString())
                }
                try {
                    inputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Utils.writeLog(getSeparatedPath(dirPath) + "Log/", e.toString())
                }
                if (!flag) {
                    if (imageFile.delete()) {
                        Utils.print("delete file:" + imageFile.absolutePath)
                        Utils.writeLog(getSeparatedPath(dirPath) + "Log/", "delete file:" + imageFile.absolutePath)
                    } else {
                        Utils.print("delete file failed:" + imageFile.absolutePath)
                        Utils.writeLog(
                            getSeparatedPath(dirPath) + "Log/",
                            "delete file failed:" + imageFile.absolutePath
                        )
                    }
                }
            }
            try {
                connection?.disconnect()
            } catch (e2: Exception) {
                Utils.writeLog(dir.absolutePath, e2.toString())
                e2.printStackTrace()
            }
        } while (error != null && error is SocketException)
        return flag
    }
}