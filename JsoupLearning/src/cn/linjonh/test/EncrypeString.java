package cn.linjonh.test;

public class EncrypeString {
	private static String[] section = { "http://www.mm131.com/xinggan/",
			"http://www.mm131.com/qingchun/", "http://www.mm131.com/xiaohua/",
			"http://www.mm131.com/chemo/", "http://www.mm131.com/qipao/",
			"http://www.mm131.com/mingxing/",

	};
	private static String[] sectionName = { "性感美女", "清纯美眉", "美女校花", "性感车模",
			"旗袍美女", "明星写真", };

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (String url : section) {
			byte[] bytes = url.getBytes();
			encodeString(bytes);
			String encryped = new String(bytes);
//			System.out.println("\""+encryped+"\",");
			System.out.println(decodeString(encryped));
		}
	}

	private static void encodeString(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] += 0x7;
		}
	}

	private static String decodeString(String src) {
		byte[] srcBytes = src.getBytes();
		for (int i = 0; i < srcBytes.length; i++) {
			srcBytes[i] -= 0x7;
		}
		String decodedString = new String(srcBytes);
		return decodedString;
	}

}
