package com.m520it.mobilesafe.utils;

import java.io.File;
import java.net.URL;

public class FileTest {
	public static void main(String[] args) throws Exception {

		testFile1();
		//		testFile2();
		File dir = new File("MyTestFile/FileTest");
		//		testListAllFile(dir);
		//		testModifyAllFileName(dir);
	}

	// 获取分隔符 操作File路径和名称
	public static void testFile1() throws Exception {

		System.out.println("---------testFile1()----------");
		// 获取属性分隔符
		//		System.out.println(File.pathSeparator);
		//		System.out.println(File.pathSeparatorChar);
		// 获取路径分隔符
		//		System.out.println(File.separator);
		//		System.out.println(File.separatorChar);
		//		System.out.println("--------------------------------");

		URL url = FileTest.class.getResource("/");//file:/D:/Java/workspace/bingo_10-22/bin/
		System.out.println("url: " + url);
		System.out.println("url.getPath(): " + url.getPath());
		System.out.println("url.toURI(): " + url.toURI());
		System.out.println("-------------------------------------");
		File dir = new File(url.getPath());
		//		File dir = new File(url.toURI());
		//		File dir = new File("file:/D:/Java/workspace/bingo_10-22/bin/");
		//		File dir = new File("/cdf/123.t");// 表示 D:\cdf\123.t\1.2\365.txt
		//		File dir = new File("cdf/123.t");
		File f1 = new File(dir, "/1.2/365.txt");//
		// ------------------------------------
		System.out.println(f1.getAbsoluteFile());// 获取绝对路径(包括文件名称)
		System.out.println(f1.getAbsolutePath());// 获取绝对路径(包括文件名称)
		System.out.println("---------------------------------------------------");
		System.out.println(f1.getPath());// 获取文件路径(包括文件名称)
		System.out.println("---------------------------------------------------");
		System.out.println(f1.getParentFile());// 获取父目录路径
		System.out.println(f1.getParent());// 获取父目录路径
		System.out.println("---------------------------------------------------");
		System.out.println(f1.getName());// 获取文件名称
		System.out.println("---------------------------------------------------");
	}

	// 检测File状态的方法:
	public static void testFile2() {

		System.out.println("---------testFile2()----------");
		File f1 = new File("c:/abc/cdf/123.txt");

		System.out.println(f1.canExecute());// 判断是否是可执行文件
		System.out.println(f1.canRead());// 判断该文件是否可读
		System.out.println(f1.canWrite());// 判断该文件是否可写
		System.out.println(f1.isHidden());// 判断该文件是否是隐藏文件
		System.out.println(f1.lastModified());// 判断该文件的最后修改时间
		System.out.println(f1.length());// 获取该文件的长度大小(单位字节)

	}

	// 需求1:列出指定目录中所有的文件,包括子文件夹中的所有文件(使用递归算法(recursion)).
	// 需求2:并记录某个文件目录下面有多少个文件
	public static void testListAllFile(File dir) {

		// 判断f是否为文件,是----直接返回
		if (dir.isFile()) return;
		// 列出此路径名目录中的文件
		File[] fs = dir.listFiles();
		//		System.out.println(f + "====里面文件数为：" + fs.length);
		for (File file : fs) {
			System.out.println(file);
			if (file.isDirectory()) {
				testListAllFile(file);
			}
		}
	}

	// 批量修改文件名称
	public static void testModifyAllFileName(File dir) {

		// dir = "MyTestFile/FileTest";
		if (dir.isFile()) return;
		File[] fs = dir.listFiles();
		String deleteText = "123456";
		for (File file : fs) {
			String oldFileName = file.getName();
			if (oldFileName.contains(deleteText)) {

				String newFileName = oldFileName.replace(deleteText, "my");
				file.renameTo(new File(file.getParent() + "/" + newFileName));// 注意返回值
				//				file.renameTo(new File(file.getParent(), newFileName));
			}
		}
		// 请慎用java的File中renameTo(File)方法:博客地址：http://xiaoych.iteye.com/blog/149328
		/* renameTo可以用来给File改名字,改路径
		 * 1.source不管是代表一个目录，还是一个文件的路径都必须是在磁盘上存在的，如：E:\access\A代表，在E盘的access文件夹
		 * 下有一个名字为A的文件夹；或E:\access\cookie2.java表示在E盘的access文件夹下有一个名字为cookie2.java的文件。
		 * 2.dest则恰恰相反，代表一个不存在的目录或文件路径（仅限最后一个'\'后面文件夹或文件的不存在，其余的目录必须已存在）。
		 * 其中1，2两点必须同时具备，否则出错（返回false）
		 */
	}
}
