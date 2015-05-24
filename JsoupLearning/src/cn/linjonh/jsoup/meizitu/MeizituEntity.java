package cn.linjonh.jsoup.meizitu;

public class MeizituEntity {

	public MeizituEntity() {
		// TODO Auto-generated constructor stub
	}

	private String titles;
	private String picThumbURL;
	private String picDetailURL;

	/**
	 * @return the titles
	 */
	public String getTitles() {
		return titles;
	}

	/**
	 * @param titles
	 *            the titles to set
	 */
	public void setTitles(String titles) {
		this.titles = titles;
	}

	/**
	 * @return the picThumbURL
	 */
	public String getPicThumbURL() {
		return picThumbURL;
	}

	/**
	 * @param picThumbURL
	 *            the picThumbURL to set
	 */
	public void setPicThumbURL(String picThumbURL) {
		this.picThumbURL = picThumbURL;
	}

	/**
	 * @return the picDetailURL
	 */
	public String getPicDetailURL() {
		return picDetailURL;
	}

	/**
	 * @param picDetailURL
	 *            the picDetailURL to set
	 */
	public void setPicDetailURL(String picDetailURL) {
		this.picDetailURL = picDetailURL;
	}

	@Override
	public String toString() {
		String str = "titles :" + titles + " picThumbURL:" + picThumbURL
				+ " picDetailURL:" + picDetailURL;
		return str;
	}
}
