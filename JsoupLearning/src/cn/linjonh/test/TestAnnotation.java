package cn.linjonh.test;

import java.lang.annotation.Annotation;

/**
 * @author jaysen.lin@foxmail.com
 * @since 2015/10/8
 * Project: JsoupLearning
 * package: cn.linjonh.test
 */
public class TestAnnotation implements MyAnnotation {
	@Override
	public String name() {
		return null;
	}

	@Override
	public String value() {
		return null;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return null;
	}
}
