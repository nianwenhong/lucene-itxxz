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
 * ʹ��IKAnalyzer���зִʵ���ʾ 2012-10-22
 * 
 */
public class IKTokenizer {

	/**
	 * Lucene�����£�IKAnalyzer���ķִ�
	 * 
	 * @author ITѧϰ��-�з
	 * @������http://www.itxxz.com
	 * @date 2014-11-11
	 * 
	 */
	public static void main(String[] args) {
		// ����IK�ִ�����ʹ��smart�ִ�ģʽ
		Analyzer analyzer = new IKAnalyzer(true);

		// ��ȡLucene��TokenStream����
		TokenStream ts = null;
		try {
			ts = analyzer
					.tokenStream(
							"demo",
							new StringReader(
									"Lucene��apache��������4 jakarta��Ŀ���һ������Ŀ��������һ��������ȫ�ļ������棬����һ��ȫ�ļ�������ļܹ���Lucene��Ŀ����Ϊ���������Ա�ṩһ�������õĹ��߰���Welcome to www.itxxz.com"));
			// ��ȡ��Ԫλ������
			OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
			// ��ȡ��Ԫ�ı�����
			CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
			// ��ȡ��Ԫ�ı�����
			TypeAttribute type = ts.addAttribute(TypeAttribute.class);

			// ����TokenStream������StringReader��
			ts.reset();
			// ������ȡ�ִʽ��
			while (ts.incrementToken()) {
				System.out.println(offset.startOffset() + " - "
						+ offset.endOffset() + " : " + term.toString() + " | "
						+ type.type());
			}
			// �ر�TokenStream���ر�StringReader��
			ts.end(); // Perform end-of-stream operations, e.g. set the final
						// offset.

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// �ͷ�TokenStream��������Դ
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
