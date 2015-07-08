package com.itxxz.demo.ch3;

import java.io.File;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * LuceneµÚÈýÕÂ£º¸ßÁÁÏÔÊ¾
 * 
 * @author ITÑ§Ï°Õß-ó¦Ð·
 * @¹ÙÍø£ºhttp://www.itxxz.com
 * @date 2014-11-10
 * 
 */
public class SearchFiles {

	/*  *//**
	 * ËÑË÷¹Ø¼ü×Ö¸ßÁÁ²âÊÔ
	 * 
	 * @param args
	 * @throws Exception
	 */

	public static void main2(String[] args) throws Exception {
		String text = "welcome come to www.itxxz.com,I'm itxxz";
		TermQuery query = new TermQuery(new Term("field", "itxxz"));
		Scorer scorer = new QueryScorer(query);
		SimpleHTMLFormatter formatter = new SimpleHTMLFormatter(
				"<font color='red'>", "</font>");
		Highlighter hig = new Highlighter(formatter, scorer);

		TokenStream tokens = new IKAnalyzer().tokenStream("field",
				new StringReader(text));

		System.out.println(hig.getBestFragment(tokens, text));
	}

	public static void main(String[] args) throws Exception {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(  
	            "highlight")));  
	    IndexSearcher searcher = new IndexSearcher(reader);  
	    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_2);  
	    QueryParser parser = new QueryParser("notv", analyzer);  
	    Query query = parser.parse("million");  
	    TopDocs hits = searcher.search(query, 10);  
	  
	    SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter(  
	            "<font color='red'>", "</font>");  
	    Highlighter highlighter = new Highlighter(htmlFormatter,  
	            new QueryScorer(query));  
	    for (int i = 0; i < hits.scoreDocs.length; i++) {  
	        int id = hits.scoreDocs[i].doc;  
	        Document doc = searcher.doc(id);  
	        String text = doc.get("notv");  
	        TokenStream tokenStream = TokenSources.getAnyTokenStream(  
	                searcher.getIndexReader(), id, "notv", analyzer);  
	        TextFragment[] frag = highlighter.getBestTextFragments(tokenStream,  
	                text, false, 10);  
	        for (int j = 0; j < frag.length; j++) {  
	            if ((frag[j] != null) && (frag[j].getScore() > 0)) {  
	                System.out.println((frag[j].toString()));  
	                System.out.println("notv-----end");  
	            }  
	        }  
	        text = doc.get("tv");  
	        tokenStream = TokenSources.getAnyTokenStream(  
	                searcher.getIndexReader(), hits.scoreDocs[i].doc, "tv",  
	                analyzer);  
	        frag = highlighter.getBestTextFragments(tokenStream, text, false,  
	                10);  
	        for (int j = 0; j < frag.length; j++) {  
	            if ((frag[j] != null) && (frag[j].getScore() > 0)) {  
	                System.out.println((frag[j].toString()));  
	                System.out.println("tv-----end");  
	            }  
	        }  
	    }  
	}
}
