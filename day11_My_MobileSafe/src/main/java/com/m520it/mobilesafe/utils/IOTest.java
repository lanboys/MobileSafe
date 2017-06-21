package com.m520it.mobilesafe.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Scanner;

public class IOTest {
	public static void main(String[] args) throws Exception {

		//		testFileInputOutputStream();
		//		testFileReaderWriter();
		testStringReaderWriter();

		//		File src = new File("MyTestFile/IOTest/stream.txt");
		//		File des = new File("MyTestFile/IOTest/stream1.txt");
		//		testCharFileStreamCopyFile(src, des);
		//		File src1 = new File("MyTestFile/IOTest");
		//		File dest1 = new File("MyTestFile/IOTest/java");
		//		testByteFileStreamCopyFile(src1, dest1);
		//		testByteBufferedCopyFile(src1, dest1);
		//		testCharBufferedCopyFile(src1, dest1);
		//		testScanner();

		//		File f = new File("MyTestFile/IOTest/obj.txt");
		//		SerializableTest.testSerializableWrite(f);
		//		SerializableTest.testSerializableRead(f);
		//		testSystemInOut();// 注意!!!!此方法中有重定向标准输出流
		//		testBufferedReader();

	}

	//测试字符串的内存流
	public static void testStringReaderWriter() throws IOException {

		StringWriter sWriter = new StringWriter();

		sWriter.write("我是蓝兵");//查Unicode存入内存
		sWriter.write("年年17岁");
		System.out.println(sWriter);
		//----------------------------------------------------------
		StringReader sReader = new StringReader(sWriter.toString());

		System.out.println((char) sReader.read());
		System.out.println((char) sReader.read());

		char[] buffer = new char[8];
		sReader.read(buffer);
		for (char c : buffer) {
			System.out.println(c + "--");
		}
		System.out.println(new String(buffer));

		sWriter.close();
		sReader.close();

		sReader = new StringReader("我是大帅哥");
		sReader.read(buffer);
		System.out.println(new String(buffer));
		sReader.close();
	}

	// 重定向标准流
	public static void testSystemInOut() throws Exception {

		System.out.println("------------testSystemInOut()--------------");
		File f = new File("MyTestFile/IOTest/A.txt");

		// 重定向标准输出流------->打印流
		PrintStream ps = System.out;//java.io.PrintStream
		System.out.println(ps.getClass().getName());
		System.setOut(new PrintStream(f, "utf-8"));
		System.out.print("我");//"我":0xCED2, 5:0x35,默认以gbk形式存储起来
		System.setOut(ps);
		// -----------------------------------------------------

		//字节流读取
		//		InputStream in = new FileInputStream(f);
		//		System.out.println(in.read());//0xCE
		//		System.out.println(in.read());//0xD2
		//		System.out.println(in.read());//-1
		//字符流读取
		//		Reader in1 = new FileReader(f);
		//		System.out.println(in1.read());//25105
		////		System.out.println((char) in1.read());
		//		System.out.println(in1.read());
		//		in.close();
		//		in1.close();
		// -----------------------------------------------------

		//重定向标准输入流------>文件字节流
		//		InputStream isStream = System.in;
		//		System.out.println(isStream.getClass().getName());//java.io.BufferedInputStream
		//		System.setIn(new FileInputStream(f));
		////		System.setIn(new BufferedInputStream(new FileInputStream(f)));
		//		int a = System.in.read();// 此处不再阻塞,直接从文件字节流中读取
		//		System.out.println(a);
		//		a = System.in.read();
		//		System.out.println(a);
		//		System.setIn(isStream);

	}

	// Java通过serialVersionUID(序列化版本号)来判断字节码是否发生改变
	static class SerializableTest implements Serializable {
		private static final long serialVersionUID = 1L;
		transient private String name;// 某些数据不需要做序列化,在前面添加 transient
		private int age;
		//		private int id;

		public SerializableTest(String name, int age) {
			this.name = name;
			this.age = age;
		}

		@Override
		public String toString() {

			return "User [name=" + name + ", age=" + age + "]";
		}

		// 序列化操作
		private static void testSerializableWrite(File f) throws Exception {

			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
			out.writeObject(new SerializableTest("lan", 17));
			out.close();

		}

		// 反序列化操作
		private static void testSerializableRead(File f) throws Exception {

			ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
			SerializableTest user = (SerializableTest) in.readObject();
			System.out.println(user);
		}
	}

	public static void testFileReaderWriter() throws Exception {

		File target = new File("MyTestFile/IOTest/2eam.txt");

		//读和写的流同时对准一个file 为什么读不了?????????
		//文件写入对象被创建时会执行一个native的open(在FileOutputStream中)操作，这个操作会清空文件。你也可以选择创建追加模式的文件写入对象。
		//FileWriter  fw = new FileWriter(f,true);

		OutputStreamWriter writer = new FileWriter(target, false);
		//		InputStreamReader reader = new FileReader(target);

		System.out.println(writer.getEncoding());//GBK
		//		System.out.println(reader.getEncoding());//GBK

		//		writer.write("我");//等价于   writer.write(25105);
		//		writer.flush();//没flush的话,读不出来
		//		char[] a = new char[10];//为什么读不出来
		//		System.out.println(reader.read(a));
		//		System.out.println(Arrays.toString(a));

		writer.close();
		//		reader.close();

	}

	public static void testFileInputOutputStream() throws Exception {

		// 1.创建目标对象------表示把数据保存到哪一个文件
		File target = new File("MyTestFile/IOTest/stream.txt");
		// 2.创建文件字节输出流对象------水管
		OutputStream out = new FileOutputStream(target, false);
		// 3.具体的输出操作------往外写操作
		out.write(5);
		//		out.write("slfjowjeow\n".getBytes());
		// 4.关闭资源对象
		out.close();
		//		System.out.println("-------------------");

		//		File des = new File("MyTestFile/IOTest/stream.txt");
		//		InputStream in = new FileInputStream(des);
		//		System.out.println(in.read());
		//		System.out.println(in.read());
		//		System.out.println(in.read());
		//		System.out.println(in.read());
		//		in.close();
	}

	public static void testScanner() throws Exception {

		File des = new File("MyTestFile/IOTest/stream.txt");
		//		Scanner sc = new Scanner(new BufferedInputStream(new FileInputStream(des)));
		Scanner sc = new Scanner(System.in);
		while (sc.hasNextLine()) {// 为什么会一直循环阻塞下去???
			System.out.println(sc.nextLine());
			// System.out.println(sc.nextInt());
		}
		sc.close();
	}

	/**
	 *
	 * @param src
	 * @param dest
	 * @param isAdd   是否在目标文件追加内容
	 */
	// 拷贝一个文件到到另一个文件
	public static void testCharFileStreamCopyFile(File src, File dest) {
		// 1.创建源和目标:形参传进来

		// 2.创建流对象
		// 文件字节流
		InputStream in = null;
		OutputStream out = null;
		// 文件字符流
		//		Reader in = null;
		//		Writer out = null;

		//		char[] buffer = new char[5];
		byte[] buffer = new byte[8];
		int len = -1;

		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);
			//			in = new FileReader(src);
			//			out = new FileWriter(dest);
			// 3.读和写操作
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
				//				System.out.println(len);
				//				System.out.println(new String(b));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/*
	 * 案例1:文件拷贝案例-拷贝指定目录的指定类型文件到指定目录.
	 分析:  比如把C:/java目录中所有的java文件拷贝到D:/text/把拷贝的所有文件的拓展名改为.txt.

	 */

	// 文件字节流实现(并且修改后缀名)
	public static void testByteFileStreamCopyFile(File src, File dest) throws Exception {
		// 1.创建源和目标File

		// 2.列出源目录下所有符合条件的文件
		File[] files = src.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {

				return new File(dir, name).toString().endsWith(".bat");
			}
		});
		// 3.迭代操作每个文件
		for (File file : files) {
			InputStream in = new FileInputStream(file);
			// 替换后缀名,并创建目标文件
			OutputStream out = new FileOutputStream(new File(dest, file.getName().replace("bat", "java")));
			// 拷贝操作
			byte[] buffer = new byte[15];
			int len = -1;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			// 关闭资源(注意要在 for 循环内关闭)
			in.close();
			out.close();
		}
	}

	// 字节缓冲流实现
	public static void testByteBufferedCopyFile(File sFile, File dFile) {

		InputStream is = null;
		OutputStream os = null;

		byte[] buffer = new byte[1024];
		int len = -1;
		File[] sFiles = sFile.listFiles();

		for (File sFile2 : sFiles) {
			if (sFile2.getName().endsWith(".java")) {
				try {
					is = new BufferedInputStream(new FileInputStream(sFile2));
					os = new BufferedOutputStream(new FileOutputStream(new File(dFile, sFile2.getName())));
					while ((len = is.read(buffer)) != -1) {
						os.write(buffer, 0, len);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("FileNotFoundException");
				} finally {
					try {
						if (os != null) {
							os.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						if (is != null) {
							is.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	// 字符缓冲流实现
	public static void testCharBufferedCopyFile(File sFile, File dFile) {

		Reader reader = null;
		Writer writer = null;

		char[] buffer = new char[1024];
		int len = -1;

		File[] files = sFile.listFiles();

		for (File file : files) {
			if (file.getName().endsWith(".java")) {
				try {
					reader = new BufferedReader(new FileReader(file));
					writer = new BufferedWriter(new FileWriter(dFile));
					while (-1 != (len = reader.read(buffer))) {
						writer.write(buffer, 0, len);// 注意用此方法
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (writer != null) {
							writer.close();// 注意判断后再关闭,并且注意先后顺序
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					try {
						if (reader != null) {
							reader.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}

	}
}
