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
 * Lucene��һ�£���������
 * 
 * @author ITѧϰ��-�з
 * @������http://www.itxxz.com
 * @date 2014-11-06
 *
 */
public class IndexFiles {

	/**
	 * @author ITѧϰ��-�з
	 * @param args
	 */
	public static void main1(String[] args) {
		boolean create = false;
		// ָ����Ҫ�������ļ�
		// String docsPath = "/searchKey/itxxz.txt";
		String docsPath = "F://test//itxxz5.txt";
		final File docDir = new File(docsPath);
		Date start = new Date();
		CommenUtils.printTime("��ʼʱ��");
		try {
			System.out.println("��������·��[highlight]...");
			Directory dir = FSDirectory.open(new File("itxxz"));
			// �������������������汾
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

			CommenUtils.printTime("����ʱ��");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		boolean create = false;
		// ָ����Ҫ�������ļ�
		try {
			System.out.println("����·����itxxz��...");
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
			System.out.println("��ʼɾ������ ..........");
			CommenUtils.deleteIndexDocs(writer, docDir);
			System.out.println("�鿴���� ..........");
			CommenUtils.readAllIndexDocs(writer, docDir);
			// System.out.println("�ָ� ..........");
			// CommenUtils.readDeleteIndexDocs(writer, docDir);
			// System.out.println("�鿴���� ..........");
			// CommenUtils.readAllIndexDocs(writer,docDir);
			// writer.close();

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}
	}

}
