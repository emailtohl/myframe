package com.github.emailtohl.frame.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.Timestamp;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

/**
 * 本类为对象序列化工具类，可将对象序列化为json或xml
 * 同时，本类支持将json解析为树形数据结构的Object，使用此Object需要根据json实际结构进行强制类型转换
 * 若要将xml解析为java中可使用的对象，可使用JDK自带的org.w3c.dom.Document工具
 * 
 * 此外，在JDK中，对序列化对象提供了强大支持：
 * （1）java识别的对象序列化 使用ObjectOutputStream的void
 * wrriteObject(Object obj)将对象输出到流中 使用ObjectInputStream的Object
 * readObject()会将流中的对象读取出来
 * 
 * （2）XML序列化对象 java.beans.XMLEncoder的void writeObject(Object obj)方法会将对象序列化到流中
 * java.beans.XMLDecoder的Object readObject()方法会将流中的对象读取出来
 * 
 * @author helei
 * 2015.10.17 完成对象转json，对象转xml功能
 * 2015.10.23 完成json转Object功能
 */
public class Serializing {
	/**
	 * 分析序列号的Object，并序列化成json
	 * 
	 * @param obj
	 *            将要序列化的对象
	 * @return json
	 */
	public String toJson(Object obj) {
		class JsonBuilder {
			StringBuilder json = new StringBuilder();
			Set<Object> set = new HashSet<Object>();

			void toJson(Object obj) {
				if (!(obj instanceof Serializable)) {
					json.append('"').append("").append('"');
					return;
				}
				Class<?> clz = obj.getClass();
				if (obj instanceof Number || obj instanceof Boolean) {
					json.append(obj);
				} else if (obj instanceof Calendar) {
					json.append(((Calendar) obj).getTime().getTime());
				} else if (otherAvailableObj(obj)) {// 将已知的可以直接toString使用的类列进来
					json.append('"').append(obj).append('"');
				} else if (clz.isArray()) {
					json.append('[');
					int length = Array.getLength(obj);
					for (int i = 0; i < length; i++) {
						if (i > 0) {
							json.append(',');
							toJson(Array.get(obj, i));
						} else {
							toJson(Array.get(obj, i));
						}
					}
					json.append(']');
				} else if (Collection.class.isAssignableFrom(clz)) {
					json.append('[');
					Iterator<?> i = ((Collection<?>) obj).iterator();
					boolean first = true;
					while (i.hasNext()) {
						if (first) {
							first = false;
						} else {
							json.append(',');
						}
						toJson(i.next());
					}
					json.append(']');
				} else if (Map.class.isAssignableFrom(clz)) {
					json.append('{');
					Map<?, ?> map = (Map<?, ?>) obj;
					boolean first = true;
					for (Entry<?, ?> entry : map.entrySet()) {
						String name = entry.getKey().toString();
						if (first) {
							first = false;
						} else {
							json.append(',');
						}
						json.append('"').append(name).append('"').append(':');
						toJson(entry.getValue());
					}
					json.append('}');
				} else {// 当做普通的bean处理
					if (set.contains(obj)) {// 若遇到相互关联的情况，则终止递归
						json.append('"').append("").append('"');
						return;
					}
					set.add(obj);
					json.append('{');
					Map<String, Field> map = BeanUtils.fieldMap(obj);
					boolean first = true;
					for (Map.Entry<String, Field> entry : map.entrySet()) {
						Field field = entry.getValue();
						int mod = field.getModifiers();
						if (Modifier.isStatic(mod) || Modifier.isTransient(mod)) {
							continue;
						}
						if (first) {
							first = false;
						} else {
							json.append(',');
						}
						String name = entry.getKey();
						json.append('"').append(name).append('"').append(':');
						try {
							toJson(field.get(obj));
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							throw new RuntimeException("解析失败");
						}
					}
					json.append('}');
				}
			}
		}// end inner class
		JsonBuilder jb = new JsonBuilder();
		jb.toJson(obj);
		return jb.json.toString();
	}

	/**
	 * 分析序列号的Object，并序列化成xml
	 * 
	 * @param obj 将要序列化的对象
	 * @param cdata 内容是否用<![CDATA[]]>包装（即让xml解析器忽略内容中的“<，>”等敏感符号
	 * @return xml
	 */
	public String toXml(Object obj, boolean cdata) {
		class XmlBuilder {
			StringBuilder xml = new StringBuilder();
			Set<Object> set = new HashSet<Object>();

			void write(String content, boolean cdata) {
				if (cdata) {
					xml.append("<![CDATA[").append(content).append("]]>");
				} else {
					xml.append(content);
				}
			}
			void toXml(Object obj, boolean cdata) {
				if (!(obj instanceof Serializable)) {
					write("", cdata);
					return;
				}
				Class<?> clz = obj.getClass();
				if (obj instanceof Number || obj instanceof Boolean) {
					write(obj.toString(), false);
				} else if (obj instanceof Calendar) {
					write(String.valueOf(((Calendar) obj).getTime().getTime()), false);
				} else if (otherAvailableObj(obj)) {// 将已知的可以直接toString使用的类列进来
					write(obj.toString(), cdata);
				} else if (clz.isArray()) {
					String tagName = clz.getComponentType().getSimpleName();
					int length = Array.getLength(obj);
					for (int i = 0; i < length; i++) {
						xml.append('<').append(tagName).append('>');
						toXml(Array.get(obj, i), cdata);
						xml.append('<').append('/').append(tagName).append('>');
					}
				} else if (Collection.class.isAssignableFrom(clz)) {
					Iterator<?> i = ((Collection<?>) obj).iterator();
					while (i.hasNext()) {
						toXml(i.next(), cdata);
					}
				} else if (Map.class.isAssignableFrom(clz)) {
					Map<?, ?> map = (Map<?, ?>) obj;
					for (Entry<?, ?> entry : map.entrySet()) {
						String tagName = entry.getKey().toString();
						xml.append('<').append(tagName).append('>');
						toXml(entry.getValue(), cdata);
						xml.append('<').append('/').append(tagName).append('>');
					}
				} else {// 当做普通的bean处理
					if (set.contains(obj)) {// 若遇到相互关联的情况，则终止递归
						write("", cdata);
						return;
					}
					set.add(obj);
					String tagName = clz.getSimpleName();
					xml.append('<').append(tagName).append('>');
					Map<String, Field> map = BeanUtils.fieldMap(obj);
					for (Map.Entry<String, Field> entry : map.entrySet()) {
						Field field = entry.getValue();
						int mod = field.getModifiers();
						if (Modifier.isStatic(mod) || Modifier.isTransient(mod)) {
							continue;
						}
						String propName = entry.getKey();
						xml.append('<').append(propName).append('>');
						try {
							toXml(field.get(obj), cdata);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							throw new RuntimeException("解析失败");
						}
						xml.append('<').append('/').append(propName).append('>');
					}
					xml.append('<').append('/').append(tagName).append('>');
				}
			}
		}// end inner class
		XmlBuilder xb = new XmlBuilder();
		xb.xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		if (obj.getClass().isArray() || obj instanceof Collection) {
			xb.xml.append("<list>");
			xb.toXml(obj, cdata);
			xb.xml.append("</list>");
		} else {
			xb.toXml(obj, cdata);
		}
		return xb.xml.toString();
	}

	/**
	 * 在对象中筛选出可以直接用字符串表示的对象
	 * 
	 * @param o
	 * @return
	 */
	private boolean otherAvailableObj(Object o) {
		return o instanceof String || o instanceof Date || o instanceof Enum || o instanceof Character
				|| o instanceof Timestamp || o instanceof TimeZone || o instanceof TemporalAmount || o instanceof Temporal;
	}
	
	/**
	 * 用逗号来切分此json字符串，切分的逗号位于此json逻辑结构的顶层，例如：
	 * 输入：{"a":"str1","b":{"c":"str2","d":"str3"}}
	 * 输出：分别将"a":"str1"以及"b":{"c":"str2","d":"str3"}存放于List<String>中
	 * 注意，json内容中不能有"，{，}，[，]符合
	 * 
	 * @param json 字符串
	 * @return List<String>
	 */
	private List<String> splitDot(String json) {
		json = json.trim();
		json = json.substring(1, json.length() - 1);
		List<Integer> pl = new ArrayList<Integer>();// 逗号的切分点list
		char[] cs = json.toCharArray();
		// 下面切分逗号
		int i, j = 0;// j是栈指针j跟踪“{、}、[、]”符号里面的结构
		boolean b = true;// b跟踪双引号中的结构，只有在双引号之外的逗号和冒号才被当做结构来切分
		for (i = 0; i < cs.length; i++) {
			char c = cs[i];
			if (c == '{' || c == '[') {
				j++;
			} else if (c == '}' || c == ']') {
				j--;
			} else if (c == '"') {
				b = !b;
			} else if (c == ',' && j == 0 && b) {// 当此逗号位于json结构的顶层，且不在双引号之内，那么这个位置就是切分点
				pl.add(i);
			}
		}
		List<String> sl = new ArrayList<String>();
		String s;
		i = 0;
		for (Integer p : pl) {
			s = new String(cs, i, p - i).trim();
			if (s.length() > 0) {// 不能把空字符串添加进来
				sl.add(s);
				i = p + 1;
			}
		}
		s = new String(cs, i, cs.length - i).trim();
		if (s.length() > 0) {// 不能把空字符串添加进来
			sl.add(s);
		}
		return sl;
	}

	/**
	 * 此json是一个{}包裹的对象，则将此json的顶层结构解析成Map<String, String>结构
	 * 注意，json内容中不能有"，{，}，[，]符合
	 * 
	 * @param json 具有{}包裹的json数据
	 * @return Map<String, String>
	 */
	private Map<String, String> jsonToMap(String json) {
		Map<String, String> map = new HashMap<String, String>();
		List<String> list = splitDot(json);
		int i, j = 0;
		boolean b = true;
		for (String s : list) {// 下面对于每个冒号进行切分
			char[] cs = s.toCharArray();
			for (i = 0; i < cs.length; i++) {
				char c = cs[i];
				if (c == '"') {
					b = !b;
				} else if (c == ':' && j == 0 && b) {// 当此逗号位于json结构的顶层，且不在双引号之内，那么这个位置就是切分点
					break;// 只可能有一个冒号分隔符，当前i就是对应的index
				} else if (c == '{' || c == '[') {
					j++;
				} else if (c == '}' || c == ']') {
					j--;
				}
			}
			String key = new String(cs, 0, i);
			String val = new String(cs, i + 1, cs.length - i - 1);
			map.put(key.trim(), val.trim());
		}
		return map;
	}

	/**
	 * 本方法将json解析为一个Object，使用时，需根据具体结构进行强转，{}被解析为树形的Map，而[]则解析为List，例如：
	 * {"a":"str1","b":{"c":"str2","d":"str3"},"e":[{"f":"str4"}]}
	 * 解析后为一个Object，先强转为Map<String, Object> (String) get("a") 将获取"str1"
	 * (Map<String, Object>) get("b") 将获取其下一级的Map (List<String>) get("e")
	 * 将获取下一级的List 以此类推
	 * 
	 * 注意，本方法不支持内容含有【" : { } [ ]】字符的json
	 * 
	 * @param json
	 * @return 用Object类型引用的树形数据结构
	 */
	public Object fromJson(String json) {
		if (json == null || json.isEmpty()) {
			return "";
		}
		char c = json.trim().charAt(0);
		if (c == '{') {
			Map<String, Object> map = new HashMap<String, Object>();
			for (Entry<String, String> en : jsonToMap(json).entrySet()) {
				String key = en.getKey();
				if (key.startsWith("\"") && key.endsWith("\"")) {
					key = key.substring(1, key.length() - 1);
				}
				map.put(key, fromJson(en.getValue()));
			}
			return map;
		} else if (c == '[') {
			List<Object> list = new ArrayList<Object>();
			for (String s : splitDot(json)) {
				list.add(fromJson(s));
			}
			return list;
		} else {// 到了底层，可以进行还原
			if (json.startsWith("\"") && json.endsWith("\"")) {
				json = json.substring(1, json.length() - 1);
			}
			return json;
		}
	}
}
