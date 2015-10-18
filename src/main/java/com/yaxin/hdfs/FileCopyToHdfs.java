package com.yaxin.hdfs;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
//import org.apache.hadoop.util.Progressable

//import javax.security.auth.login.Configuration;

public class FileCopyToHdfs {

	public static void main(String[] args) throws Exception {
		try {
			//uploadToHdfs();
			// deleteFromHdfs();
			// getDirectoryFromHdfs();
			//appendToHdfs();
			readFromHdfs();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println("SUCCESS");
		}
	}

	/** 上传文件到HDFS上去 */
	// 加入common下的所有包和hdfs下的主包

	 private static void uploadToHdfs() throws
		 FileNotFoundException,IOException {
		 String localSrc = "d://qq.txt";
		 String dst = "hdfs://192.168.0.104:9000/hdfs/test/qq.txt";
		 InputStream in = new BufferedInputStream(new FileInputStream(localSrc));
		 Configuration conf = new Configuration();
		
		 FileSystem fs = FileSystem.get(URI.create(dst), conf);
		 OutputStream out = fs.create(new Path(dst));
		 IOUtils.copyBytes(in, out, 4096, true);
	 }

	/** 从HDFS上读取文件 */
	private static void readFromHdfs() throws FileNotFoundException, IOException {
		String dst = "hdfs://192.168.0.104:9000/hdfs/test/qq.txt";
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(dst), conf);
		FSDataInputStream hdfsInStream = fs.open(new Path(dst));

		OutputStream out = new FileOutputStream("d:/qq-hdfs.txt");
		byte[] ioBuffer = new byte[1024];
		int readLen = hdfsInStream.read(ioBuffer);

		while (-1 != readLen) {
			out.write(ioBuffer, 0, readLen);
			readLen = hdfsInStream.read(ioBuffer);
		}
		out.close();
		hdfsInStream.close();
		fs.close();
	}

	/**
	 * 以append方式将内容添加到HDFS上文件的末尾;注意：文件更新，需要在hdfs-site.xml中添
	 * <property><name>dfs.append.support</name><value>true</value></property>
	 */
	private static void appendToHdfs() throws FileNotFoundException, IOException {
		String dst = "hdfs://192.168.0.104:9000/hdfs/test/qq.txt";
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(dst), conf);
		FSDataOutputStream out = fs.append(new Path(dst));

		int readLen = "zhangzk add by hdfs java api".getBytes().length;

		while (-1 != readLen) {
			out.write("zhangzk add by hdfs java api".getBytes(), 0, readLen);
		}
		out.close();
		fs.close();
	}

	/** 从HDFS上删除文件 */
	private static void deleteFromHdfs() throws FileNotFoundException, IOException {
		String dst = "hdfs://192.168.0.104:9000/hdfs/test/qq-bak.txt";
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(dst), conf);
		fs.deleteOnExit(new Path(dst));
		fs.close();
	}

	/** 遍历HDFS上的文件和目录 */
	private static void getDirectoryFromHdfs() throws FileNotFoundException, IOException {
		String dst = "hdfs://192.168.0.104:9000/hdfs/test";
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(dst), conf);
		FileStatus fileList[] = fs.listStatus(new Path(dst));
		int size = fileList.length;
		for (int i = 0; i < size; i++) {
			System.out.println("name:" + fileList[i].getPath().getName() + "/t/tsize:" + fileList[i].getLen());
		}
		fs.close();
	}

}
