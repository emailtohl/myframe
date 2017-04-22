package com.github.emailtohl.frame.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公用的工具类，对对象是否空值，数组合并等提供支持
 * 
 * @author helei
 *
 */
public final class CommonUtil {
	private CommonUtil() {}
	
	/**
	 * 判断对象是否为空值
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null)
			return true;
		boolean flag = false;// 既然不为null，则默认不为空值
		if (obj instanceof String) {
			flag = ((String) obj).isEmpty();
		} else if (obj instanceof Number) {
			flag = ((Number) obj).intValue() == 0 ? true : false;
		} else if (obj instanceof Boolean) {
			flag = !((Boolean) obj);
		} else if (obj instanceof Byte) {
			flag = ((Byte) obj).intValue() == 0 ? true : false;
		} else if (obj instanceof Character) {
			flag = Character.isWhitespace(((Character) obj));
		} else if (obj instanceof Collection) {
			flag = ((Collection<?>) obj).isEmpty();
		} else if (obj instanceof Map) {
			flag = ((Map<?, ?>) obj).isEmpty();
		} else {
			Class<?> clz = obj.getClass();
			if (clz.isArray() && Array.getLength(obj) == 0) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 将字符串数组连接在一起
	 * @param array 将要连接的字符串数组
	 * @param separator 连接符
	 * @return 字符串
	 */
	public static String join(String[] array, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			sb.append(separator).append(array[i]);
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}
	
	/**
	 * 将字符串集合连接在一起
	 * @param collection 将要连接的集合
	 * @param separator 连接符
	 * @return 字符串
	 */
	public static String join(Collection<String> collection, String separator) {
		StringBuilder sb = new StringBuilder();
		for (String s : collection) {
			sb.append(separator).append(s);
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}
	
	/**
	 * 合并两个数组，按参数顺序合并
	 * @param array1 第一个数组
	 * @param array2 第二个数组
	 * @return 合并的数组
	 */
	public static Object[] mergeArray(Object[] array1, Object[] array2) {
		if (array1 == null) {
			if (array2 != null) {
				return array2;
			} else {
				return new Object[0];
			}
		} else {
			if (array2 == null) {
				return array1;
			} else {
				Object[] newArray = new Object[array1.length + array2.length];
				System.arraycopy(array1, 0, newArray, 0, array1.length);
				System.arraycopy(array2, 0, newArray, array1.length, array2.length);
				return newArray;
			}
		}
	}
	
	/**
	 * 去掉字符串中的空格、制表符、回车、换行
	 */
	private static Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	
	/**
	 * 本方法用于解析前端javascript的unescape()函数对中文进行的编码
	 * 
	 * @param src 经unescape()编码后的字符串
	 * @return 解码后的字符串
	 */
	public static String unescape(String src) {
		if (src == null || src.length() == 0)
			return null;
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}
}
