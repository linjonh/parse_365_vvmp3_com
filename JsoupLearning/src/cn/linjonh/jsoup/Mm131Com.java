package cn.linjonh.jsoup;

import cn.linjonh.jsoup.util.ConnUtil;
import cn.linjonh.jsoup.util.DownloadUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Mm131Com {

    private static final boolean D = true;
    private static final boolean isDebug = false;

    public Mm131Com() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
//		Document doc = Jsoup.parse(ConnUtil.decode("D:/data", ConnUtil.KEYS));
        Document doc = ConnUtil.getHtmlDocument("http://www.mm131.com/xinggan/", "D:/log");
        Elements els = doc.select("a");
        boolean escapeHomePage = false;
        for (Element el : els) {
            if (!escapeHomePage) {
                escapeHomePage = true;
                continue;
            }
            final String moduleUrl = el.attr("href");
            final String moduleName = el.html();
            printStringForDebug("moduleName:" + moduleName, true);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    detectPerModuleUrlAndCount(moduleUrl);
                }
            }).start();
        }
        startDownload();
    }

    /**
     * @param moduleUrl
     */
    private static void detectPerModuleUrlAndCount(String moduleUrl) {
        printStringForDebug("=======================" + moduleUrl + "========================", isDebug);
        Document doc = ConnUtil.getHtmlDocument(moduleUrl);
        printStringForDebug("=======================Module Page count========================", isDebug);
        Elements modulePages = doc.select("dl.list-left dd.page a");
        // last element tell us page count
        String pageCountStringRelativeUrl = modulePages.last().toString();
        printStringForDebug(pageCountStringRelativeUrl, isDebug);

        int page_count = parseModuleCount(pageCountStringRelativeUrl);
        List<String> moduleUnionBaseUrl = parseModuleBaseUrl(modulePages.last());
        addModuleInfo(moduleUnionBaseUrl, page_count);
        readModuleInfo();
    }

    static Object lock = new Object();
    static ArrayList<HashMap<String, String>> mInfos = new ArrayList<HashMap<String, String>>();

    /**
     * @param pageBaseUri such as http://www.mm131.com/xinggan/list_6_
     *                    <p>
     *                    list.get(0)=list_6_
     *                    <p>
     *                    list.get(1)=http://www.mm131.com/xinggan/
     * @param pageCount   total a module page count.
     */
    private static void addModuleInfo(List<String> pageBaseUriList, int pageCount) {
        synchronized (lock) {
            HashMap<String, String> map = new HashMap<String, String>();

            map.put(pageBaseUriList.get(0), pageBaseUriList.get(1));
            map.put(MODULE_PAGE_COUNT, String.valueOf(pageCount));

            printString("add map:" + map.toString());
            mInfos.add(map);
            int size = mInfos.size();
            if (size == 6) {
                lock.notify();
            }
        }
    }

    /**
     * @return
     */
    private static List<HashMap<String, String>> readModuleInfo() {
        // synchronized (lock) {
        int size = mInfos.size();
        printStringForDebug(String.valueOf(size) + " list content:" + mInfos.toString(), true);
        return mInfos;
        // }
    }

    /**
     * @param el moye element
     * @return
     */
    private static List<String> parseModuleBaseUrl(Element el) {
        String base = el.baseUri();
        String moye = el.attr("href").toString();

        printStringForDebug("moye:" + moye, isDebug);

        int last_dash = moye.lastIndexOf("_");
        String itemUnionStr = moye.substring(0, last_dash + 1);

        printStringForDebug("itemUnionStr: " + itemUnionStr, isDebug);
        ArrayList<String> list = new ArrayList<String>();
        list.add(itemUnionStr);
        list.add(base);

        return list;
    }

    /**
     * @param moye
     * @return
     */
    private static int parseModuleCount(String moye) {
        int last_dash = moye.lastIndexOf("_");
        int last_dot = moye.lastIndexOf(".");

        String str_count = moye.substring(last_dash + 1, last_dot);
        printStringForDebug(str_count, isDebug);
        return Integer.valueOf(str_count);
    }

    /**
     * @param str
     * @param isDebug
     */
    public static void printStringForDebug(String str, boolean isDebug) {
        if (D && isDebug)
            System.out.println(str);
    }

    public static void printString(String str) {
        System.out.println(str);
    }

    public static void printString(int val) {
        System.out.println(String.valueOf(val));
    }

    private static final String MODULE_PAGE_COUNT = "m_page_count";

    public static void startDownload() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            printString("====start download================>");
            List<HashMap<String, String>> info = readModuleInfo();
            for (final HashMap<String, String> map : info) {// iterator module
                Thread downloadThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        printString("thread id:" + Thread.currentThread().getId() + "\nthread name:"
                                + Thread.currentThread().getName() + "\npriority:"
                                + Thread.currentThread().getPriority() + "\nstate" + Thread.currentThread().getState());

                        Set<String> set = map.keySet();
                        int size = set.size();// two item key.
                        int count = 0;
                        String moduleBaseURI = "";
                        String modulePathKeywords = "";
                        // while (set.iterator().hasNext()) {
                        Iterator<String> itor = set.iterator();
                        for (int i = 0; i < size; i++) {
                            String key = itor.next();
                            if (key.equals(MODULE_PAGE_COUNT)) {
                                count = Integer.valueOf(map.get(key));
                            } else {
                                moduleBaseURI = map.get(key);// such as
                                // http://www.mm131.com/xinggan/
                                modulePathKeywords = key;// list_6_
                            }
                            printString("Key" + key + " " + "map.get(key)" + map.get(key));
                        }
                        printString("iterate someone Module page count:" + count);
                        for (int i = 1; i <= count; i++) {
                            // iterate someone Module page.
                            String gridItemUrl;
                            if (i == 1) {
                                gridItemUrl = moduleBaseURI;
                            } else {
                                gridItemUrl = moduleBaseURI + modulePathKeywords + String.valueOf(i) + ".html";
                            }
                            String moduleName = moduleBaseURI;
                            moduleName = moduleName.replace("http://www.mm131.com/", "");
                            moduleName = moduleName.replace("/", "");
                            goIntoMoudleGridItem(gridItemUrl, i, count, moduleName);
                        }
                    }
                });
                executor.execute(downloadThread);
            }
        }
    }

    private static void goIntoMoudleGridItem(String gridItemUrl, int curModulePageIndex, int curMouduleCount,
                                             String moduleName) {
        printString("go into grid item page:" + gridItemUrl);
        Document gridDoc = ConnUtil.getHtmlDocument(gridItemUrl);
        Elements picGridItems = gridDoc.select("dl.list-left dd a[target]");
        // List<String> itemUrls = new ArrayList<String>();
        for (Element picGridItem : picGridItems) {
            String griditemUrl = picGridItem.attr("href");
            // itemUrls.add(griditemUrl);
            printString(curModulePageIndex + "(<==current module page index)" + " curModulePageCount:"
                    + curMouduleCount);

            List<String> detatilInfo = goToDetail(griditemUrl);

            String imgStrcount = detatilInfo.get(0);
            String imgName = detatilInfo.get(1);
            imgName = imgName.substring(0, imgName.lastIndexOf("("));
            String imgBaseUrl = detatilInfo.get(2);
            boolean hasPreZero = Boolean.valueOf(detatilInfo.get(3));

            int imgcount = Integer.valueOf(imgStrcount);

            for (int j = 1; j < imgcount; j++) {
                String imgIndexName = getImageIndexName(hasPreZero, j);
                String dirPath1 = "E:/MM131/" + imgName + "_" + imgIndexName;
                String dirPath2 = "E:/MM131/" + moduleName + "_" + imgName + "_" + imgIndexName;
                String url = imgBaseUrl + imgIndexName;
                DownloadUtil.donwloadImg(url, dirPath1, dirPath2);
            }
        }
        // printStringForDebug(picGridItems.toString(), isDebug);
        // gridDoc.select("")
    }

    /**
     * @param hasPreZero
     * @param j
     * @return
     */
    private static String getImageIndexName(boolean hasPreZero, int j) {
        String imgIndexName;
        if (hasPreZero && j < 10) {
            imgIndexName = "0" + j + ".jpg";
        } else {
            imgIndexName = j + ".jpg";
        }
        return imgIndexName;
    }

    /**
     * <li>
     * imgName <li>
     * imgBaseUri:http://img1.mm131.com/pic/1987/ <li>
     * imgCount:i
     * <p>
     * imgSrc:http://img1.mm131.com/pic/1987/i.jpg
     *
     * @param pageUrl
     * @return List<String>:imgCount,imgName,imgCount
     */
    private static List<String> goToDetail(String pageUrl) {
        printString("==>go to detatil page:" + pageUrl);
        Document detatilDoc = ConnUtil.getHtmlDocument(pageUrl);
        Elements els = detatilDoc.select("div.content-pic img");
        Element el = els.get(0); // only one item.
        String imgName = el.attr("alt");
        String src = el.attr("src");// src="http://img1.mm131.com/pic/1987/9.jpg"
        boolean hasPreZero = checkUpPresuffx(src);
        String imgBaseUrl = src.substring(0, src.lastIndexOf("/") + 1);

        Elements imgCoutNodes = detatilDoc.select("div.content-page span");
        Element imgCountEl = imgCoutNodes.get(0);
        String imgCountStr = imgCountEl.html();
        imgCountStr = imgCountStr.substring(1, imgCountStr.length() - 1);
        List<String> list = new ArrayList<String>();
        list.add(imgCountStr);// 0
        list.add(imgName);// 1
        list.add(imgBaseUrl);// 2
        list.add(String.valueOf(hasPreZero));// 3
        printString(list.toString());
        return list;
    }

    /**
     * check up previous suffix if with zero.
     *
     * @return
     */
    private static boolean checkUpPresuffx(String imgCountStr) {
        imgCountStr = imgCountStr.substring(imgCountStr.lastIndexOf("/"));
        if (imgCountStr.indexOf("0") == 0) {
            return true;
        }
        return false;
    }

    static ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 1, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>());

}
