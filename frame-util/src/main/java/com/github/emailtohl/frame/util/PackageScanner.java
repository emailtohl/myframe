package com.github.emailtohl.frame.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 扫描包，获取包下的所有类
 * 
 * @author HeLei
 */
public final class PackageScanner {
	private static final Logger logger = Logger.getLogger(PackageScanner.class.getName());

	private PackageScanner() {
	}

	/**
	 * 查找包名下的所有类实例
	 * 
	 * @param packageName 包名
	 * @return 类实例集合
	 */
	public static Set<Class<?>> getClasses(String packageName) {
		// 第一个class类的集合
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		// 获取包的名字并进行替换
		String packageDirName = packageName.replace('.', '/');
		// logger.debug(new ClassPathResource(packageDirName).getURI());
		// 定义一个枚举的集合 并进行循环来处理这个目录下的内容
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
		} catch (IOException e) {
			logger.log(Level.WARNING, packageName + " read failed!", e);
			throw new IllegalArgumentException(e);
		}
		// 循环迭代下去
		while (dirs.hasMoreElements()) {
			// 获取下一个元素
			URL url = dirs.nextElement();
			// 得到协议的名称
			String protocol = url.getProtocol();
			// 如果是以文件的形式保存在服务器上
			if ("file".equals(protocol)) {
				logger.fine("scan file system");
				// 获取包的物理路径
				String filePath;
				try {
					filePath = URLDecoder.decode(url.getFile(), Charset.defaultCharset().name());
				} catch (UnsupportedEncodingException e) {
					logger.log(Level.WARNING, e.getMessage(), e);
					continue;
				}
				// 以文件的方式扫描整个包下的文件 并添加到集合中
				findClassesByFileSystem(packageName, filePath, true, classes);
			} else if ("jar".equals(protocol)) {
				// 如果是jar包文件
				// 定义一个JarFile
				logger.fine("scan jar package");
				JarFile jar = null;
				// 获取jar
				try {
					jar = ((JarURLConnection) url.openConnection()).getJarFile();
				} catch (IOException e) {
					logger.log(Level.WARNING, "jar scan error", e);
					continue;
				}
				findClassesByJar(jar, packageName, true, classes);
			}
		}
		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有类实例
	 * 
	 * @param packageName 包名，用于classLoader加载
	 * @param filePath    查找类的目录
	 * @param recursive   递归查找
	 * @param classes     存放类实例的集合
	 */
	public static void findClassesByFileSystem(String packageName, String filePath, final boolean recursive,
			Set<Class<?>> classes) {
		File dir = new File(filePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			logger.fine(String.format("用户定义包 {} 下没有任何文件", packageName));
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		if (dirfiles instanceof File[]) {
			// 循环所有文件
			for (File file : dirfiles) {
				// 如果是目录 则继续扫描
				if (file.isDirectory()) {
					findClassesByFileSystem(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
							classes);
				} else {
					// 如果是java类文件 去掉后面的.class 只留下类名
					String className = file.getName().substring(0, file.getName().length() - 6);
					try {
						// 添加到集合中去
						// classes.add(Class.forName(packageName + '.' + className));
						// 这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
						classes.add(Thread.currentThread().getContextClassLoader()
								.loadClass(packageName + '.' + className));
					} catch (ClassNotFoundException e) {
						logger.log(Level.INFO, "添加用户自定义视图类错误 找不到此类的.class文件", e);
					}
				}
			}
		}
	}

	/**
	 * 从jar包中获取所有类的实例
	 * 
	 * @param jar         jar文件
	 * @param packageName 扫描的包路径
	 * @param recursive   递归查找
	 * @param classes     存放类实例的集合
	 */
	public static void findClassesByJar(JarFile jar, String packageName, boolean recursive, Set<Class<?>> classes) {
		// 获取包的名字并进行替换
		String packageDirName = packageName.replace('.', '/');
		// 从此jar包 得到一个枚举类
		Enumeration<JarEntry> entries = jar.entries();
		// 同样的进行循环迭代
		while (entries.hasMoreElements()) {
			// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			// 如果是以/开头的
			if (name.charAt(0) == '/') {
				// 获取后面的字符串
				name = name.substring(1);
			}
			// 如果前半部分和定义的包名相同
			if (name.startsWith(packageDirName)) {
				int idx = name.lastIndexOf('/');
				// 如果以"/"结尾 是一个包
				if (idx != -1) {
					// 获取包名 把"/"替换成"."
					packageName = name.substring(0, idx).replace('/', '.');
				}
				// 如果可以迭代下去 并且是一个包
				if ((idx != -1) || recursive) {
					// 如果是一个.class文件 而且不是目录
					if (name.endsWith(".class") && !entry.isDirectory()) {
						// 去掉后面的".class" 获取真正的类名
						String className = name.substring(packageName.length() + 1, name.length() - 6);
						try {
							// 添加到classes
							classes.add(Class.forName(packageName + '.' + className));
						} catch (ClassNotFoundException e) {
							logger.log(Level.INFO, packageName + '.' + className + " not found", e);
						}
					}
				}
			}
		}
	}
}
