package cn.linjonh.test;


import java.util.Base64;

/**
 * @author jaysen.lin@foxmail.com
 * @since 2015/9/9
 * Project: JsoupLearning
 * package: cn.linjonh.test
 */
public class Base64Test {
	@MyAnnotation(name = "hi", value = "hi")
	public static void main(String[] args) {
		String url[] = {"UA-53846305-6", "http://bouncebreak.com/page/"};
		for (String s : url) {
			String enStr = Base64.getEncoder().encodeToString(s.getBytes());
			System.out.println(enStr);
//			String decodedStr = new String(Base64.getDecoder().decode(enStr));
//			System.out.println(decodedStr);
		}

	}
}
