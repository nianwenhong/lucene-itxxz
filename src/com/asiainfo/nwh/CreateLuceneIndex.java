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
		String indexPath = ""; // �����ļ������·��
		String docsPath = null; // ��Դ�ļ�����Ŀ¼
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
			System.err.println("��Դ�ļ�����Ŀ¼Ϊ�գ���ָ����Դ�ļ�����Ŀ¼������");
			System.exit(1);
		}

		final File docDir = new File(docsPath);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out.println("��Դ�ļ�Ŀ¼ '" + docDir.getAbsolutePath()
					+ "' �����ڻ򲻿ɶ������飡");
			System.exit(1);
		}

		Date start = new Date();
		try {
			System.out.println("���������ļ�����Ŀ¼ '" + indexPath + "'...");
			//MMapDirectory@G:\myProject\Lucene\index lockFactory=NativeFSLockFactory@G:\myProject\Lucene\index
			Directory dir = FSDirectory.open(new File(indexPath));
			// ����������
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(
					Version.LUCENE_4_10_2, analyzer);
			if (create) {
				// �����µ������ļ���ɾ�����������������ļ�
				// ��ָ���Ǹ���Դ�ļ�Ŀ¼�µľɵ������ļ���������Դ�������ļ���Ӱ�죩
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// ����оɵ������ļ�������������ļ�
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
	 * ����Դ�ļ�������ָ��Ŀ¼�£����ɴ��̵������ļ�
	 * 
	 * @param writer
	 *            �����ļ�
	 * @param file
	 *            ��Դ�ļ�
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

				// ÿһ���ĵ����ձ���װ����һ�� Document ����
				// Document �����������ĵ��ģ�������ĵ�����ָһ�� HTML ҳ�棬һ������ʼ���������һ���ı��ļ���
				// һ�� Document �����ɶ�� Field ������ɵġ�
				// ���԰�һ�� Document ������������ݿ��е�һ����¼����ÿ�� Field ������Ǽ�¼��һ���ֶΡ�
				Document doc = new Document();
				// Field ��������������һ���ĵ���ĳ�����Եģ�����һ������ʼ��ı�������ݿ��������� Field ����ֱ�������
				//stored,indexed,omitNorms,indexOptions=DOCS_ONLY<path:G:\myProject\Lucene\13801.txt>	
				Field pathField = new StringField("path", file.getPath(),
						Field.Store.YES);
				// pathFieldָ������Դ�ļ���·����field
				doc.add(pathField);
				// ���fieldָ���������޸�ʱ��
				doc.add(new LongField("modified", file.lastModified(),
						Field.Store.NO));

				// ����Դ�ļ��е����ݷִʺ������������ļ��У�ָ��ΪUTF-8����
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