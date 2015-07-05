package com.itxxz.demo.ch1;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.itxxz.util.CommenUtils;
import com.itxxz.util.PublicUtilDict;

/**
 * Lucene第一章：创建索引
 * 
 * @author IT学习者-螃蟹
 * @官网：http://www.itxxz.com
 * @date 2014-11-06
 *
 */
public class IndexFiles {

	/**
	 * @author IT学习者-螃蟹
	 * @param args
	 */
	public static void main1(String[] args) {
		boolean create = false;
		// 指定需要检索的文件
		// String docsPath = "/searchKey/itxxz.txt";
		String docsPath = "F://test//itxxz5.txt";
		final File docDir = new File(docsPath);
		Date start = new Date();
		CommenUtils.printTime("开始时间");
		try {
			System.out.println("创建索引路径[highlight]...");
			Directory dir = FSDirectory.open(new File("itxxz"));
			// 创建解析器，并声明版本
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_2);
			IndexWriterConfig iwc = new IndexWriterConfig(
					Version.LUCENE_4_10_2, analyzer);
			if (create) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			IndexWriter writer = new IndexWriter(dir, iwc);
			CommenUtils.indexDocs(writer, docDir);
			writer.close();

			CommenUtils.printTime("结束时间");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		boolean create = false;
		// 指定需要检索的文件
		try {
			System.out.println("索引路径【itxxz】...");
			String docsPath = "F://test//itxxz7.txt";
			final File docDir = new File(docsPath);
			Directory dir = FSDirectory.open(new File("itxxz"));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_2);
			IndexWriterConfig iwc = new IndexWriterConfig(
					Version.LUCENE_4_10_2, analyzer);
			if (create) {
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			IndexWriter writer = new IndexWriter(dir, iwc);
			// CommenUtils.indexDocs(writer, docDir);
			CommenUtils.readAllIndexDocs(writer, docDir);
			System.out.println("开始删除索引 ..........");
			CommenUtils.deleteIndexDocs(writer, docDir);
			System.out.println("查看索引 ..........");
			CommenUtils.readAllIndexDocs(writer, docDir);
			// System.out.println("恢复 ..........");
			// CommenUtils.readDeleteIndexDocs(writer, docDir);
			// System.out.println("查看索引 ..........");
			// CommenUtils.readAllIndexDocs(writer,docDir);
			// writer.close();

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}
	}

}
