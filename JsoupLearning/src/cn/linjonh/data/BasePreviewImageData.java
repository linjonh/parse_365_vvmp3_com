package cn.linjonh.data;

public class BasePreviewImageData {
	public String title;
	public String previewImgUrl;
	public String albumSetUrl;
	public String timeline;
	public String description;
	public int next;
	@Override
	public String toString() {
		return title+"\npreviewImgUrl: "+previewImgUrl+"\nalbumSetUrl: "+albumSetUrl+"\ntimeline: "+timeline+"\ndescription: "+description;
	}
}
