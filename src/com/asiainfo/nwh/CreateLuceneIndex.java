package com.asiainfo.nwh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class CreateLuceneIndex {

	public static void main(String[] args) {
		if (args == null || args.length <= 0) {
			args = new String[] { 
					"-index", 
					"G:\\myProject\\Lucene\\index",
					"-docs", 
					"G:\\myProject\\Lucene\\13801.txt" };
		}
		String indexPath = ""; // 索引文件保存的路径
		String docsPath = null; // 资源文件所在目录
		boolean create = true;
		for (int i = 0; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				indexPath = args[i + 1];
				i++;
			} else if ("-docs".equals(args[i])) {
				docsPath = args[i + 1];
				i++;
			} else if ("-update".equals(args[i])) {
				create = false;
			}
		}

		if (docsPath == null) {
			System.err.println("资源文件所在目录为空，请指定资源文件所在目录！！！");
			System.exit(1);
		}

		final File docDir = new File(docsPath);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out.println("资源文件目录 '" + docDir.getAbsolutePath()
					+ "' 不存在或不可读，请检查！");
			System.exit(1);
		}

		Date start = new Date();
		try {
			System.out.println("建立索引文件到该目录 '" + indexPath + "'...");
			//MMapDirectory@G:\myProject\Lucene\index lockFactory=NativeFSLockFactory@G:\myProject\Lucene\index
			Directory dir = FSDirectory.open(new File(indexPath));
			// 创建解析器
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(
					Version.LUCENE_4_10_2, analyzer);
			if (create) {
				// 创建新的索引文件，删除所有其他的索引文件
				// （指的是该资源文件目录下的旧的索引文件，其他资源的索引文件不影响）
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// 如果有旧的索引文件，则更新索引文件
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);
			writer.close();
			Date end = new Date();
			System.out.println(end.getTime() - start.getTime()
					+ " total milliseconds");
		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}
	}

	/**
	 * 将资源文件索引到指定目录下，生成磁盘的索引文件
	 * 
	 * @param writer
	 *            索引文件
	 * @param file
	 *            资源文件
	 */
	static void indexDocs(IndexWriter writer, File file) throws IOException {
		if (!file.canRead()) {
			return;
		}
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

				// 每一个文档最终被封装成了一个 Document 对象
				// Document 是用来描述文档的，这里的文档可以指一个 HTML 页面，一封电子邮件，或者是一个文本文件。
				// 一个 Document 对象由多个 Field 对象组成的。
				// 可以把一个 Document 对象想象成数据库中的一个记录，而每个 Field 对象就是记录的一个字段。
				Document doc = new Document();
				// Field 对象是用来描述一个文档的某个属性的，比如一封电子邮件的标题和内容可以用两个 Field 对象分别描述。
				//stored,indexed,omitNorms,indexOptions=DOCS_ONLY<path:G:\myProject\Lucene\13801.txt>	
				Field pathField = new StringField("path", file.getPath(),
						Field.Store.YES);
				// pathField指的是资源文件的路径的field
				doc.add(pathField);
				// 这个field指的是最后的修改时间
				doc.add(new LongField("modified", file.lastModified(),
						Field.Store.NO));

				// 把资源文件中的内容分词后，索引到索引文件中，指定为UTF-8编码
				doc.add(new TextField("contents", new BufferedReader(
						new InputStreamReader(fis, StandardCharsets.UTF_8))));

				if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
					System.out.println("adding " + file);
					writer.addDocument(doc);
				} else {
					System.out.println("updating " + file);
					writer.updateDocument(new Term("path", file.getPath()), doc);
				}
			} finally {
				fis.close();
			}
		}
	}
}