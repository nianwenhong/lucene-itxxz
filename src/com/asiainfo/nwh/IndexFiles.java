package com.asiainfo.nwh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.itxxz.util.CommenUtils;
import com.itxxz.util.PublicUtilDict;
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
			//创建索引
			this.indexDocs(indexWriter, fileSourceDir);
			//indexWriter.close();
			//创建索引结束时间
			CommenUtils.printTime("结束时间");
		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}
		/*
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
		*/
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
	
	/**
	 * 创建索引
	 * @author wenhong
	 * @param writer
	 * @param file
	 * @throws IOException
	 */
		public void indexDocs(IndexWriter writer, File file)
			throws IOException {
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {
				FileInputStream fis;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException fnfe) {
					return;
				}
				try {
					Document doc = new Document();
					Field pathField = new StringField("path", file.getPath(),Field.Store.YES);
					doc.add(pathField);
					doc.add(new TextField("filename", file.getName(),Field.Store.YES));
					doc.add(new LongField("modified", file.lastModified(),
							Field.Store.NO));
					doc.add(new TextField("contents", new BufferedReader(
							new InputStreamReader(fis, "UTF-8"))));

					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						System.out.println("新增 " + file);
						writer.addDocument(doc);
					} else {
						System.out.println("更新 " + file);
						writer.updateDocument(new Term("path", file.getPath()),doc);
					}
					writer.commit();
				} finally {
					fis.close();
				}
			}
		}
	}
		
	@SuppressWarnings("deprecation")
	public void searchFiles(){
		String queries = null;
		int repeat = 5;
		String queryString = null;
		int hitsPerPage = 5;
		boolean raw = false;
		
		System.out.println("准备开始索引文件：----");
		//生成索引文件的路径，这样写的话，该路径一般是工程目录下，F:\Study\AliBaba\lucene-itxxz\index
		try {
			Directory indexDir = FSDirectory.open(new File("index"));
			IndexReader reader = DirectoryReader.open(indexDir);
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer();
			BufferedReader in = null;
			in = new BufferedReader(new InputStreamReader(System.in,
					StandardCharsets.UTF_8));
			QueryParser parser = new QueryParser(Version.LUCENE_4_10_2,
					PublicUtilDict.CONTENT, analyzer);
			while (true) {
				if (queries == null && queryString == null) { 
					System.out.println("请输入关键词: ");
				}
				String line = queryString != null ? queryString : in.readLine();
				if (line == null || line.length() == -1) {
					break;
				}
				line = line.trim();
				if (line.length() == 0) {
					break;
				}
				Query query = parser.parse(line);
				if (repeat > 0) {
					for (int i = 0; i < repeat; i++) {
						searcher.search(query, null, 100);
					}
				}
				CommenUtils.doPagingSearch(in, searcher, query, hitsPerPage, raw,
						queries == null && queryString == null);
				if (queryString != null) {
					break;
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		IndexFiles indexFiles = new IndexFiles();
		//生成索引文件（包含创建索引，生成索引文件）
		indexFiles.createFileIndex();
		
		//索引文件
		indexFiles.searchFiles();
	}
}
