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
	//Դ�ļ�·��
	private String fileSource = "F://test";
	//Դ�ļ�����
	private final File fileSourceDir = new File(fileSource); 
	
	private IndexWriter indexWriter;
	
	/**
	 * ���������ļ�
	 */
	public void createFileIndex(){
		//��ʼ����������ʱ��
		CommenUtils.printTime("��ʼʱ��");
		try {
			//���������ļ���·��������д�Ļ�����·��һ���ǹ���Ŀ¼�£�F:\Study\AliBaba\lucene-itxxz\index
			Directory indexDir = FSDirectory.open(new File("index"));
			System.out.println("׼�����������Ľ��������ļ���·���ǣ�"+indexDir);
			// �������������зִʵȲ���
			Analyzer analyzer = new StandardAnalyzer();
			// ����IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
			// ������������������ĵ���������
			indexWriter = new IndexWriter(indexDir, iwConfig);
			CommenUtils.indexDocs(indexWriter, fileSourceDir);
			//indexWriter.close();
			//������������ʱ��
			CommenUtils.printTime("����ʱ��");
		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}
		// �鿴���������ļ�
		System.out.println("ɾ��ǰ�������ļ��鿴��----------------------------");
		this.viewAllIndexDocsFiles(indexWriter, fileSourceDir);
		// ɾ��ָ����Դ�ļ�����Ӧ�������ļ�
		System.out.println("׼��ɾ��ָ��Դ�ļ��������ļ���----------------------------");
		this.deleteIndexDocsFiles(indexWriter, fileSourceDir);
		System.out.println("�ɹ�ɾ��ָ��Դ�ļ��������ļ���----------------------------");
		// �鿴���������ļ�
		System.out.println("ɾ����������ļ��鿴��----------------------------");
		this.viewAllIndexDocsFiles(indexWriter, fileSourceDir);
	}
	
	/**
	 * �鿴���������ļ�
	 * @param args
	 */
	public void viewAllIndexDocsFiles(IndexWriter writer, File file){
		try {
			// �Ѿ������õ������ļ���·��
			Directory directory = FSDirectory.open(new File("index"));
			// ����ɾ�������е��ĵ�
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
	 * ɾ����ӦԴ�ļ��������ļ�
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
		// ָ����Ҫ�������ļ�
		try {
			System.out.println("����·����itxxz��...");
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
