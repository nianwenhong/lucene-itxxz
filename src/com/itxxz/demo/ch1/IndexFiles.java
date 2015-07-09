package com.itxxz.demo.ch1;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.itxxz.util.CommenUtils;
/**
 * 
 * @author wenhong
 *
 */
public class IndexFiles {
	//源文件路径
	private String fileSource = "F://test";
	//源文件对象
	private final File fileSourceDir = new File(fileSource); 
	
	private IndexWriter indexWriter;
	
	/**
	 * 生成索引文件
	 */
	public void createFileIndex(){
		//开始创建索引的时间
		CommenUtils.printTime("开始时间");
		try {
			//生成索引文件的路径，这样写的话，该路径一般是工程目录下，F:\Study\AliBaba\lucene-itxxz\index
			Directory indexDir = FSDirectory.open(new File("index"));
			System.out.println("准备创建索引文将，索引文件的路径是："+indexDir);
			// 创建解析器进行分词等操作
			Analyzer analyzer = new StandardAnalyzer();
			// 配置IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
			// 用来创建索引并添加文档到索引中
			indexWriter = new IndexWriter(indexDir, iwConfig);
			CommenUtils.indexDocs(indexWriter, fileSourceDir);
			//indexWriter.close();
			//创建索引结束时间
			CommenUtils.printTime("结束时间");
		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}
		// 查看所有索引文件
		System.out.println("删除前的索引文件查看：----------------------------");
		this.viewAllIndexDocsFiles(indexWriter, fileSourceDir);
		// 删除指定的源文件所对应的索引文件
		System.out.println("准备删除指定源文件的索引文件：----------------------------");
		this.deleteIndexDocsFiles(indexWriter, fileSourceDir);
		System.out.println("成功删除指定源文件的索引文件：----------------------------");
		// 查看所有索引文件
		System.out.println("删除后的索引文件查看：----------------------------");
		this.viewAllIndexDocsFiles(indexWriter, fileSourceDir);
	}
	
	/**
	 * 查看所有索引文件
	 * @param args
	 */
	public void viewAllIndexDocsFiles(IndexWriter writer, File file){
		try {
			// 已经创建好的索引文件的路径
			Directory directory = FSDirectory.open(new File("index"));
			// 用来删除索引中的文档
			IndexReader reader = DirectoryReader.open(directory);
			reader.numDeletedDocs();
			IndexSearcher searcher = new IndexSearcher(reader);
			reader.maxDoc();
			Document doc = null;
			for (int i = 0; i < reader.maxDoc(); i++) {
				doc = searcher.doc(i);
				System.out.println("Doc [" + i + "] : title:"
						+ doc.get("title") + ", filename: "
						+ doc.get("filename") + ", Path: " + doc.get("path"));
			}
		} catch (CorruptIndexException e) {  
            e.printStackTrace();  
        } catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除对应源文件的索引文件
	 * @param writer
	 * @param file
	 */
	public void deleteIndexDocsFiles(IndexWriter writer , File file){
		try {
			Term term1 = new Term("F:\\test\\overview-frame.html");
			Term term2 = new Term("F:\\test\\overview-summary.html");
			writer.deleteDocuments(term1,term2);
			writer.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		IndexFiles indexFiles = new IndexFiles();
		indexFiles.createFileIndex();
	}

	public static void main2(String[] args) {
		boolean create = false;
		// 指定需要检索的文件
		try {
			System.out.println("索引路径【itxxz】...");
			// String docsPath = "F://test//itxxz7.txt";
			String docsPath = "F://test";
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
