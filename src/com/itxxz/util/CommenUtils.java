package com.itxxz.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.itxxz.Chinese;
import com.itxxz.bean.Information;

/**
 * 工具类
 * 
 * @author IT学习者-螃蟹
 * @官网：http://www.itxxz.com
 * @date 2014-11-06
 * 
 */
public class CommenUtils {

	private static long starttime = 0;

	private static long pretime = System.currentTimeMillis();

	public static void printTime(String tag) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		if (starttime == 0) {
			starttime = System.currentTimeMillis();
			//获取系统当前时间
			System.out.println("[" + tag + "] start timer:" + df.format(new Date()));
		} else {
			long curtime = System.currentTimeMillis();
			System.out.println("[" + tag + "]当前时间:" + df.format(new Date()) + " 总时间:"
					+ (curtime - starttime) + " 上次间隔:" + (curtime - pretime));
			pretime = System.currentTimeMillis();
		}
	}
/**
 * 创建索引
 * @author IT学习者-螃蟹
 * @param writer
 * @param file
 * @throws IOException
 */
	public static void indexDocs(IndexWriter writer, File file)
			throws IOException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
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
					// at least on windows, some temporary files raise this
					// exception with an "access denied" message
					// checking if the file can be read doesn't help
					return;
				}

				try {

					// make a new, empty document
					Document doc = new Document();

					// Add the path of the file as a field named "path". Use a
					// field that is indexed (i.e. searchable), but don't
					// tokenize
					// the field into separate words and don't index term
					// frequency
					// or positional information:
					Field pathField = new StringField("path", file.getPath(),
							Field.Store.YES);
					doc.add(pathField);
					doc.add(new TextField("filename", file.getName(), Field.Store.YES));
					//doc.add(new TextField("notv", "The website www.itxxz.com dream that draws over 17 million monthly visitors", Field.Store.YES));
					//doc.add(new TextField("tv", "The ipad also can run all of the million or so apps available for the iphone", Field.Store.YES));
					doc.add(new TextField("title", "我是青鸟", Field.Store.YES));

					// Add the last modified date of the file a field named
					// "modified".
					// Use a LongField that is indexed (i.e. efficiently
					// filterable with
					// NumericRangeFilter). This indexes to milli-second
					// resolution, which
					// is often too fine. You could instead create a number
					// based on
					// year/month/day/hour/minutes/seconds, down the resolution
					// you require.
					// For example the long value 27-- would mean
					// February --, 2, 2-3 PM.
					doc.add(new LongField("modified", file.lastModified(),
							Field.Store.NO));

					// Add the contents of the file to a field named "contents".
					// Specify a Reader,
					// so that the text of the file is tokenized and indexed,
					// but not stored.
					// Note that FileReader expects the file to be in UTF-8
					// encoding.
					// If that's not the case searching for special characters
					// will fail.
					doc.add(new TextField("contents", new BufferedReader(
							new InputStreamReader(fis, "UTF-8"))));

					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						// New index, so we just add the document (no old
						// document can be there):
						System.out.println("新增 " + file);
						writer.addDocument(doc);
					} else {
						// Existing index (an old copy of this document may have
						// been indexed) so
						// we use updateDocument instead to replace the old one
						// matching the exact
						// path, if present:
						System.out.println("更新 " + file);
						writer.updateDocument(new Term("path", file.getPath()),
								doc);
					}
					//deleteIndexDocs(writer, file);
					writer.commit();

				} finally {
					fis.close();
				}
			}
		}
	}
	
	/**
	 * 删除索引
	 * @author IT学习者-螃蟹
	 * @param writer
	 * @param file
	 * @throws IOException
	 */
	public static void deleteIndexDocs(IndexWriter writer, File file)
			throws IOException {

		try {
		
			//System.out.println("删除文档路径："+file.getPath());
			Term t1 = new Term("path", "F:\\test\\itxxz1.txt");
			Term t2 = new Term("path", "F:\\test\\itxxz2.txt");
			writer.deleteDocuments(t1,t2);
			writer.commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新索引
	 * @author IT学习者-螃蟹
	 * @param writer
	 * @param file
	 * @throws IOException
	 */
	public static void updateIndexDocs(IndexWriter writer, File file)
			throws IOException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
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
					// at least on windows, some temporary files raise this
					// exception with an "access denied" message
					// checking if the file can be read doesn't help
					return;
				}

				try {

					// make a new, empty document
					Document doc = new Document();

					// Add the path of the file as a field named "path". Use a
					// field that is indexed (i.e. searchable), but don't
					// tokenize
					// the field into separate words and don't index term
					// frequency
					// or positional information:
					Field pathField = new StringField("path", file.getPath(),
							Field.Store.YES);
					doc.add(pathField);
					doc.add(new TextField("filename", file.getName(), Field.Store.YES));
					doc.add(new TextField("notv", "The website www.itxxz.com dream that draws over 17 million monthly visitors", Field.Store.YES));
					doc.add(new TextField("tv", "The ipad also can run all of the million or so apps available for the iphone", Field.Store.YES));

					doc.add(new LongField("modified", file.lastModified(),
							Field.Store.NO));

					// Add the contents of the file to a field named "contents".
					// Specify a Reader,
					// so that the text of the file is tokenized and indexed,
					// but not stored.
					// Note that FileReader expects the file to be in UTF-8
					// encoding.
					// If that's not the case searching for special characters
					// will fail.
					doc.add(new TextField("contents", new BufferedReader(
							new InputStreamReader(fis, "UTF-8"))));

					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						// New index, so we just add the document (no old
						// document can be there):
						System.out.println("adding " + file);
						writer.addDocument(doc);
					} else {
						// Existing index (an old copy of this document may have
						// been indexed) so
						// we use updateDocument instead to replace the old one
						// matching the exact
						// path, if present:
						System.out.println("updating " + file);
						writer.updateDocument(new Term("path", file.getPath()),
								doc);
					}
					writer.commit();
				} finally {
					fis.close();
				}
			}
		}
	}
	
	/**
	 * 恢复索引
	 * @author IT学习者-螃蟹
	 * @param writer
	 * @param file
	 * @throws IOException
	 */
	public static void readDeleteIndexDocs(IndexWriter writer,File file)
			throws IOException {

		try {
			System.out.println("是否删除："+writer.hasDeletions());
			writer.commit();
		} catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
	}
	
	/**
	 * 查看所有索引文件
	 * @author IT学习者-螃蟹
	 * @官网：http://www.itxxz.com
	 * @date 2014-11-17
	 * @param writer
	 * @param file
	 * @throws IOException
	 */
	public static void readAllIndexDocs(IndexWriter writer,File file)
			throws IOException {
		try {
			Directory dir = FSDirectory.open(new File("itxxz"));
			IndexReader reader=DirectoryReader.open(dir);
			reader.numDeletedDocs();
			IndexSearcher searcher = new IndexSearcher(reader);
			reader.maxDoc();
			Document doc = null;
			for (int i = 0; i < reader.maxDoc(); i++) {
				doc = searcher.doc(i);
				System.out.println("Doc [" + i + "] : title:" + doc.get("title")
						+ ", filename: " + doc.get("filename")
						+ ", Path: " + doc.get("path"));
			}
		} catch (CorruptIndexException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
	}

	/**
	 * Lucene第四章：分页搜索
	 * 		
	 * @author IT学习者-螃蟹
	 * @官网：http://www.itxxz.com
	 * @date 2014-11-10
	 *
	 */
	public static void doPagingSearch(BufferedReader in,
			IndexSearcher searcher, Query query, int hitsPerPage, boolean raw,
			boolean interactive) throws IOException {

		TopDocs results = searcher.search(query, hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = results.totalHits;
		System.out.println(" 共有" + numTotalHits + "个相关文档");

		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);

		while (true) {
			System.out.println("init:end-"+end);
			if (end > hits.length) {
				System.out
						.println("当前显示 "+ numTotalHits +"个相关文档中的 1 - " + hits.length + "  "
								+ " 个相关文档.");
				System.out.println("是否查看更多 (y/n) ?");
				String line = in.readLine();
				if (line.length() == 0 || line.charAt(0) == 'n') {
					break;
				}

				hits = searcher.search(query, numTotalHits).scoreDocs;
			}

			end = Math.min(hits.length, start + hitsPerPage);
			System.out.println("hits.length:"+hits.length);
			System.out.println("start + hitsPerPage:"+start +"+"+ hitsPerPage+"="+(start + hitsPerPage));
			System.out.println("end:"+end);

			for (int i = start; i < end; i++) {
				if (raw) { 
					System.out.println("doc=" + hits[i].doc + " score="
							+ hits[i].score);
					continue;
				}

				Document doc = searcher.doc(hits[i].doc);
				String path = doc.get("path");
				if (path != null) {
					System.out.println((i + 1) + ". " + path);
					String title = doc.get("title");
					if (title != null) {
						System.out.println("   Title: " + doc.get("title"));
					}
				} else {
					System.out.println((i + 1) + ". "
							+ "没有匹配的文档");
				}

			}

/*			if (!interactive || end == 0) {
				break;
			}*/

			if (numTotalHits >= end) {
				boolean quit = false;
				while (true) {
					System.out.print("输入页码或者选择： ");
					if (start - hitsPerPage >= 0) {
						System.out.print("(p)上一页, ");
					}
					if (start + hitsPerPage < numTotalHits) {
						System.out.print("(n)下一页, ");
					}
					System.out
							.println("(q)退出当前搜索.");

					String line = in.readLine();
					if (line.length() == 0 || line.charAt(0) == 'q') {
						quit = true;
						break;
					}
					if (line.charAt(0) == 'p') {
						start = Math.max(0, start - hitsPerPage);
						break;
					} else if (line.charAt(0) == 'n') {
						if (start + hitsPerPage < numTotalHits) {
							start += hitsPerPage;
						}
						break;
					} else {
						int page = Integer.parseInt(line);
						if ((page - 1) * hitsPerPage < numTotalHits) {
							start = (page - 1) * hitsPerPage;
							break;
						} else {
							System.out.println("该页不存在");
						}
					}
				}
				if (quit)
					break;
				end = Math.min(numTotalHits, start + hitsPerPage);
			}
		}
	}
	
	public static void doPagingSearchIK(String line,
			IndexSearcher searcher, Query query, Pageinate page,
			List<Information> dataList,Analyzer analyzer) throws IOException, InvalidTokenOffsetsException {
		dataList.clear();
		SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter(
				"<font color='red'>", "</font>");
		Highlighter highlighter = new Highlighter(htmlFormatter,
				new QueryScorer(query));
		TopDocs results = searcher.search(query, page.getPageSize());
		
		int numTotalHits = results.totalHits;
		
		ScoreDoc[] hits = searcher.search(query, results.totalHits).scoreDocs;
		
		if(line != null &&!line.isEmpty()){
			if (line.charAt(0) == 'p') {
				int start = Math.max(0, page.getStart() - page.getPageSize());
				page.setStart(start);
				page.setEnd(Math.min(numTotalHits, page.getStart() + page.getPageSize()));
			} else if (line.charAt(0) == 'n') {
				if (page.getStart() + page.getPageSize() < numTotalHits) {
					page.setStart(page.getStart() + page.getPageSize());
					page.setEnd(Math.min(numTotalHits, page.getStart() + page.getPageSize()));
				}
			} 
		}
		
		page.setEnd(Math.min(hits.length, page.getStart() + page.getPageSize()));
		for (int i = page.getStart(); i < page.getEnd(); i++) {

			Document doc = searcher.doc(hits[i].doc);
			TokenStream tokenStream = TokenSources.getAnyTokenStream(
					searcher.getIndexReader(), hits[i].doc, "description", analyzer);
			TextFragment[] frag = highlighter.getBestTextFragments(tokenStream,
					doc.get("description"), false, 10);
			Information info = new Information();
			info.setId(doc.get("id"));
			info.setTitle(doc.get("title"));
			info.setPath(doc.get("path"));
			info.setDescription(frag[0].toString());
			dataList.add(info);

		}
	}
	
	public static void doPagingSearchIK1(String line,
			IndexSearcher searcher, Query query, Pageinate page,
			List<Information> dataList) throws IOException {
		dataList.clear();
		TopDocs results = searcher.search(query, page.getPageSize());
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = results.totalHits;
		System.out.println(" 共有" + numTotalHits + "个相关文档");

		if(page.getEnd() != -1){
			//page.setEnd(Math.min(numTotalHits, page.getPageSize()));
			hits = searcher.search(query, numTotalHits).scoreDocs;
		}else{
			//hits = searcher.search(query, numTotalHits).scoreDocs;
		}

		if (page.getEnd() > hits.length) {
			System.out
					.println("当前显示 "+ numTotalHits +"个相关文档中的 1 - " + hits.length + "  "
							+ " 个相关文档.");

			hits = searcher.search(query, numTotalHits).scoreDocs;
		}

		page.setEnd(Math.min(hits.length, page.getStart() + page.getPageSize()));

		System.out.println("hits.length:"+hits.length);
		System.out.println(0+"----"+page);
		for (int i = page.getStart(); i < page.getEnd(); i++) {

			Document doc = searcher.doc(hits[i].doc);
			Information info = new Information();
			info.setId(doc.get("id"));
			info.setTitle(doc.get("title"));
			info.setPath(doc.get("path"));
			info.setDescription(doc.get("description"));
			dataList.add(info);
			String path = doc.get("path");
			if (path != null) {
				System.out.println((i + 1) + ". " + path);
			} else {
				System.out.println((i + 1) + ". "
						+ "没有匹配的文档");
			}

		}

		if (numTotalHits >= page.getEnd()) {
			System.out.print("输入页码或者选择： ");
			if (page.getStart() - page.getPageSize() >= 0) {
				System.out.print("(p)上一页, ");
			}
			if (page.getStart() + page.getPageSize() < numTotalHits) {
				System.out.print("(n)下一页, ");
			}
			System.out.println("line:"+line);
			if(line == null){
				return;
			}

			System.out.println(line.charAt(0));
			
			if (line.charAt(0) == 'p') {
				int start = Math.max(0, page.getStart() - page.getPageSize());
				page.setStart(start);
				page.setEnd(Math.min(numTotalHits, page.getStart() + page.getPageSize()));
				System.out.println(1+"----"+page);
				return;
			} else if (line.charAt(0) == 'n') {
				if (page.getStart() + page.getPageSize() < numTotalHits) {
					page.setStart(page.getStart() + page.getPageSize());
					page.setEnd(Math.min(numTotalHits, page.getStart() + page.getPageSize()));
				}
				System.out.println(2+"----"+page);
				return;
			} else {
				int pageNum = Integer.parseInt(line);
				if ((pageNum - 1) * page.getPageSize() < numTotalHits) {
					page.setStart((pageNum - 1) * page.getPageSize());
					System.out.println(3+"----"+page);
					return;
				} else {
					System.out.println("该页不存在");
				}
			}
			page.setEnd(Math.min(numTotalHits, page.getStart() + page.getPageSize()));
			System.out.println(4+"----"+page);
		}
	}
	
	/**
	 * 生成和汉字
	 * 
	 * @author IT学习者-螃蟹
	 * @return
	 * @throws Exception
	 */
	public static String getChinese() throws Exception {
		String str = null;
		int highPos, lowPos;
		Random random = new Random();
		highPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = 161 + Math.abs(random.nextInt(93));
		byte[] b = new byte[2];
		b[0] = (new Integer(highPos)).byteValue();
		b[1] = (new Integer(lowPos)).byteValue();
		str = new String(b, "GB2312");
		return str;
	}

	public static String get300Chinese() throws Exception {
		Chinese ch = new Chinese();
		String str = "";
		for (int i = 300; i > 0; i--) {
			str = str + ch.getChinese(i);
		}
		return str;
	}

	public static void main(String[] args) throws Exception {

		get300Chinese();
	}

}
