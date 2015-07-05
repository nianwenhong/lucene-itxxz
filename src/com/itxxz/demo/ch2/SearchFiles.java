package com.itxxz.demo.ch2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.itxxz.util.CommenUtils;
import com.itxxz.util.PublicUtilDict;

/**
 * Lucene第二章：文件搜索
 * 		
 * @author IT学习者-螃蟹
 * @官网：http://www.itxxz.com
 * @date 2014-11-06
 *
 */
public class SearchFiles {

	/**
	 * @author IT学习者-螃蟹
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {

		String queries = null;
		int repeat = 5;
		String queryString = null;
		int hitsPerPage = 5;
		boolean raw = false;

		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
				"index")));
		IndexSearcher searcher = new IndexSearcher(reader);

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_2);

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
	}

}
