package com.itxxz.demo.ch4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.itxxz.util.PublicUtilDict;

/**
 * Lucene�����£���������
 * 
 * @author ITѧϰ��-�з
 * @������http://www.itxxz.com
 * @date 2014-11-10
 * 
 */
public class SearchFiles {

	/**
	 * Lucene�����£���������
	 * 
	 * @author ITѧϰ��-�з
	 * @������http://www.itxxz.com
	 * @date 2014-11-10
	 * 
	 */
	public static void main(String[] args) {

		// ʵ����IKAnalyzer�ִ���
		Analyzer analyzer = new IKAnalyzer(true);

		Directory directory = null;
		IndexReader ireader = null;
		IndexWriter iwriter = null;
		IndexSearcher isearcher = null;
		final String filePath = "F://test//itxxz.txt";
		
		final String INDEX = "itxxz";
		
		try {
			// �����ڴ���������
			directory = FSDirectory.open(new File(INDEX));
			// ����IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(
					Version.LUCENE_4_10_2, analyzer);
			iwConfig.setOpenMode(OpenMode.CREATE);
			iwriter = new IndexWriter(directory, iwConfig);
			FileInputStream fis = new FileInputStream(new File(filePath));
			// д������
			Document doc = new Document();
			doc.add(new StringField("id", "10000", Field.Store.YES));
			doc.add(new TextField("title", "ITѧϰ��", Field.Store.YES));
			doc.add(new TextField("url", "http://www.itxxz.com", Field.Store.YES));
			doc.add(new TextField("contents", new BufferedReader(
					new InputStreamReader(fis,"GBK"))));
			iwriter.addDocument(doc);

			iwriter.close();

			// ��������**********************************
			// ʵ����������
			IndexReader reader = DirectoryReader.open(FSDirectory
					.open(new File("itxxz")));
			isearcher = new IndexSearcher(reader);
			String keyword = "ITѧϰ��-�з";
			// ʹ��QueryParser��ѯ����������Query����
			QueryParser qp = new QueryParser(Version.LUCENE_4_10_2,
					PublicUtilDict.CONTENT, analyzer);
			Query query = qp.parse(keyword);
			System.out.println("Query = " + query);

			// �������ƶ���ߵ�5����¼
			TopDocs topDocs = isearcher.search(query, 5);
			System.out.println("ƥ���ļ�������" + topDocs.totalHits);
			// ������
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (int i = 0; i < topDocs.totalHits; i++) {
				Document targetDoc = isearcher.doc(scoreDocs[i].doc);
				System.out.print("���ݣ�");
				System.out.print("[id:"+targetDoc.get("id"));
				System.out.print("],[title:"+targetDoc.get("title"));
				System.out.print("],[url:"+targetDoc.get("url")+"]");
			}

		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (ireader != null) {
				try {
					ireader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (directory != null) {
				try {
					directory.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
