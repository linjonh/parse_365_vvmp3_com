package cn.linjonh.jsoup.meirentu

import cn.linjonh.jsoup.util.ConnUtil
import cn.linjonh.jsoup.util.DownloadUtil
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class MeiRenTu {
    companion object {
        const val web_base = "https://meirentu.cc/"

        //允爾
        const val yuner_url = "https://meirentu.cc/model/%E5%85%81%E7%88%BE-3.html"

        //龙女宝宝
        const val longnv_url = "https://meirentu.cc/model/%E9%BE%99%E5%A5%B3%E5%AE%9D%E5%AE%9D.html"

        //尤妮丝
        const val younis_url = "https://meirentu.cc/model/%E5%B0%A4%E5%A6%AE%E4%B8%9D.html"

        //大美媚京
        const val dameimei = "https://meirentu.cc/model/%E5%A4%A7%E7%BE%8E%E5%AA%9A%E4%BA%AC.html"
        //心妍小公主
        const val xinyan = "https://meirentu.cc/model/%E5%BF%83%E5%A6%8D%E5%B0%8F%E5%85%AC%E4%B8%BB.html"
        //妲己_Toxic
        const val danji = "https://meirentu.cc/s/%E5%A6%B2%E5%B7%B1_Toxic.html"
        //田冰冰
        const val tianbingbing = "https://meirentu.cc/s/%E7%94%B0%E5%86%B0%E5%86%B0.html"
        //小海臀
        const val xiaohaitun = "https://meirentu.cc/model/%E5%B0%8F%E6%B5%B7%E8%87%80.html"

        const val FOLDER_NAME = "小海臀"
        const val RUN_URL_PATH = xiaohaitun

        @JvmStatic
        fun main(args: Array<String>) {
            val htmlDocument = ConnUtil.getHtmlDocument(RUN_URL_PATH)
            //搜索结果分页数
            val pagesElements = htmlDocument.select("div.page a")
            if (pagesElements.size > 0)
                for (page in pagesElements) {
                    val pageUrlSegment = page.attr("href")
                    val pageUrl = web_base + pageUrlSegment
                    val pageDoc = ConnUtil.getHtmlDocument(pageUrl)
                    visitPage(pageDoc)
                }
            else{
                visitPage(htmlDocument)
            }
        }

        private fun visitPage(htmlDocument: Document) {
            //album set item
            val albumItemElements = htmlDocument.select("li.list_n2 a")
            val executorService = Executors.newCachedThreadPool()
            val countDownLatch = CountDownLatch(albumItemElements.size)
            for (item in albumItemElements) {
                executorService.execute {
                    fetchAlbumPage(item)
                    countDownLatch.countDown()
                    println("=========countDownLatch leave size: ${countDownLatch.count}============")

                }
            }
            println("=========countDownLatch.await()============")
            countDownLatch.await()
            executorService.shutdown();
            println("=========executorService.shutdown()============")
        }


        private fun fetchAlbumPage(first: Element? = null, albumPageUrl: String? = null) {
            val pageUrl = if (first != null) {
                println(first.toString())
                val attr = first.attr("href")
                println(attr)
                web_base + attr
            } else albumPageUrl ?: return
            val htmlDocument = ConnUtil.getHtmlDocument(pageUrl)
            val pagesElements = htmlDocument.select("div.content_left div.page a")//get pages info
            pagesElements.removeLast()//删除【下一页】多余的item
            for (pageEl in pagesElements) {// loop page content,and select images tag to download
                val restUrl = pageEl.attr("href")
                val nextPageUrl = web_base + restUrl
//                executorService.execute {
                fetchCurrentPageImages(nextPageUrl)
//                }
            }
        }

        private fun fetchCurrentPageImages(pageUrl: String) {
            val htmlDocument = ConnUtil.getHtmlDocument(pageUrl)
            //select current page  image tag elements
            val elements = htmlDocument.select("div.content_left div img")
            for (el in elements) {//
                val imageUrl = el.attr("src")
                val downloadImg =
                    DownloadUtil.downloadImg(imageUrl, "E:\\相册\\$FOLDER_NAME", method = "GET", referer = pageUrl)
                println("imageUrl: $downloadImg")
            }
        }
    }
}