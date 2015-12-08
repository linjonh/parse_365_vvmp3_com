package cn.linjonh.jsoup.ZhuaMeiNet;

import cn.linjonh.data.BasePreviewImageData;
import cn.linjonh.jsoup.util.ConnUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jaysen.lin@foxmail.com
 * @since 2015/7/3.
 */
public class ZhuaMei {
    public static final String base_url = "http://zhuamei.net/";
    public static final String pageUrlSeg = "home.php?mod=space&do=album&view=all&page=";

    public static void main(String[] args) {
        List<BasePreviewImageData> items = getRoleItems();
        List<ZhuaMei.ImageItemInfo> infos=getAlbumImgUrls(items.get(0));
        getHightQulityImageUrl(infos.get(0));
    }

    public static List<BasePreviewImageData> getRoleItems() {
        List<BasePreviewImageData> datas = new ArrayList<>();
        Document doc = ConnUtil.getHtmlDocument(base_url);
        if (doc == null) {
            return datas;
        }
        Elements els = doc.select("div.pgs a.last");
//        println(els.toString());
        int count = getPageCount(els.get(0).attr("href"));
        //items
        Elements items = doc.select("div#main li");
        println("item.size :" + items.size());
        for (Element item : items) {
            Element albumAndPreview = item.select("a").get(0);//href and img
            Element imgEl = albumAndPreview.child(0);
            String albumUrl = albumAndPreview.attr("href");
            String imgPreview = imgEl.attr("src");
            String title = imgEl.attr("alt");
            String description = item.select("p").get(0).text();//<p>
            BasePreviewImageData data = new BasePreviewImageData();
            data.albumSetUrl = base_url + albumUrl;
            if (imgPreview.contains("http")) {
                data.previewImgUrl = imgPreview;
            } else {
                data.previewImgUrl = base_url + imgPreview;
            }

            data.title = title;
            data.next = count;
            data.description = description;
//            println(data.toString());
            datas.add(data);
        }
        return datas;
    }


    public static List<ImageItemInfo> getAlbumImgUrls(BasePreviewImageData item) {
        List<ImageItemInfo> imglist = new ArrayList<>();
        Document doc = ConnUtil.getHtmlDocument(item.albumSetUrl);
//        println(doc.toString());
        Elements els = doc.select("div#main ul#tiles li a");
//        println(els.toString());
        for (Element el : els) {
            String href = el.attr("href");
            String imgSrc = el.child(0).attr("src");
            ImageItemInfo info = new ImageItemInfo();
            info.imgOriginUrl = base_url + href;
            info.imgThumSrc = base_url + imgSrc;
//            println(info.toString());
            imglist.add(info);
        }
        return imglist;
    }

    public static String getHightQulityImageUrl(ImageItemInfo info) {
        Document document = ConnUtil.getHtmlDocument(info.imgOriginUrl);
        Elements els = document.select("div#photo_pic a img");
        if (els != null && els.size() > 0) {
            String imgHightQulityUrl = base_url + els.get(0).attr("src");
            println("imgHightQulityUrl: " + imgHightQulityUrl);
            return imgHightQulityUrl;
        }
        return null;
    }

    /**
     * @param pageString page segment
     * @return page size
     */
    public static int getPageCount(String pageString) {
        pageString = pageString.replace(pageUrlSeg, "");
        int count = Integer.valueOf(pageString);
        println("parsed page count is " + count);
        return count;
    }

    public static void println(String string) {
        System.out.println(string);
    }

    public static class ImageItemInfo {
        public String imgThumSrc;
        public String imgOriginUrl;

        @Override
        public String toString() {
            return "imgThumSrc: " + imgThumSrc + "\nimgOriginUrl: " + imgOriginUrl;
        }
    }
}
