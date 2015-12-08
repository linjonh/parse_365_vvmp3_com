package cn.linjonh.test;

import java.lang.annotation.Documented;

/**
 * @author jaysen.lin@foxmail.com
 * @since 2015/10/8
 * Project: JsoupLearning
 * package: cn.linjonh.test
 */
@Documented
public @interface MyAnnotation {
	String name();
	String value();
}
