package com.itxxz.demo.ch5;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 使用IKAnalyzer进行分词的演示 2012-10-22
 * 
 */
public class IKTokenizer {

	/**
	 * Lucene第六章：IKAnalyzer中文分词
	 * 
	 * @author IT学习者-螃蟹
	 * @官网：http://www.itxxz.com
	 * @date 2014-11-11
	 * 
	 */
	public static void main(String[] args) {
		// 构建IK分词器，使用smart分词模式
		Analyzer analyzer = new IKAnalyzer(true);

		// 获取Lucene的TokenStream对象
		TokenStream ts = null;
		try {
			ts = analyzer
					.tokenStream(
							"demo",
							new StringReader(
									"Lucene是apache软件基金会4 jakarta项目组的一个子项目，它不是一个完整的全文检索引擎，而是一个全文检索引擎的架构，Lucene的目的是为软件开发人员提供一个简单易用的工具包。Welcome to www.itxxz.com"));
			// 获取词元位置属性
			OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
			// 获取词元文本属性
			CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
			// 获取词元文本属性
			TypeAttribute type = ts.addAttribute(TypeAttribute.class);

			// 重置TokenStream（重置StringReader）
			ts.reset();
			// 迭代获取分词结果
			while (ts.incrementToken()) {
				System.out.println(offset.startOffset() + " - "
						+ offset.endOffset() + " : " + term.toString() + " | "
						+ type.type());
			}
			// 关闭TokenStream（关闭StringReader）
			ts.end(); // Perform end-of-stream operations, e.g. set the final
						// offset.

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 释放TokenStream的所有资源
			if (ts != null) {
				try {
					ts.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
