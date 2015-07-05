package com.itxxz.demo.ch6;

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
 * Lucene�ڰ��£�����ɾ������
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
	public static void main(String[] args) {
		boolean create = true;
		//ָ����Ҫ�������ļ�
		//String docsPath = "/searchKey/itxxz.txt";
		 String docsPath = "F://test//itxxz.txt";
		final File docDir = new File(docsPath);
		Date start = new Date();
		CommenUtils.printTime("��ʼʱ��");
		try {
			System.out.println("��������·��[highlight]...");
			Directory dir = FSDirectory.open(new File("itxxz"));
			//�������������������汾
			Analyzer analyzer = 
					new StandardAnalyzer(Version.LUCENE_4_10_2);
			IndexWriterConfig iwc = 
					new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
			if (create) {
		        iwc.setOpenMode(OpenMode.CREATE);
		      } else {
		        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		      }
			IndexWriter writer = new IndexWriter(dir, iwc);
			CommenUtils.indexDocs(writer, docDir);
			writer.close();

		    CommenUtils.printTime("����ʱ��");
			
		}catch (IOException e) {
		      System.out.println(" caught a " + e.getClass() 
		    		  + "\n with message: " + e.getMessage());
		}
	}
	

}
