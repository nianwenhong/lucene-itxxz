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
 * Lucene第八章：更新删除索引
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
	public static void main(String[] args) {
		boolean create = true;
		//指定需要检索的文件
		//String docsPath = "/searchKey/itxxz.txt";
		 String docsPath = "F://test//itxxz.txt";
		final File docDir = new File(docsPath);
		Date start = new Date();
		CommenUtils.printTime("开始时间");
		try {
			System.out.println("创建索引路径[highlight]...");
			Directory dir = FSDirectory.open(new File("itxxz"));
			//创建解析器，并声明版本
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

		    CommenUtils.printTime("结束时间");
			
		}catch (IOException e) {
		      System.out.println(" caught a " + e.getClass() 
		    		  + "\n with message: " + e.getMessage());
		}
	}
	

}
