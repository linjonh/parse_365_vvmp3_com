package cn.linjonh.data;

public class BasePreviewImageData {
	public String title;
	public String imgPreviewUri;
	public String albumSetUrl;
	public String timeline;
	public String description;
	public int    next;
	public int    pageSize;

	@Override
	public String toString() {
		return title
				+ "\nimgPreviewUri: " + imgPreviewUri
				+ "\nalbumSetUrl: " + albumSetUrl
				+ "\ntimeline: " + timeline
				+ "\ndescription: " + description;
	}
}
