package com.itxxz.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.itxxz.bean.Information;
import com.itxxz.util.CommenUtils;
import com.itxxz.util.Pageinate;
import com.itxxz.util.PublicUtilDict;

/**
 * Lucene�����£�Lucene����WEBʵ��
 * 
 * @author ITѧϰ��-�з
 * @������http://www.itxxz.com
 * @date 2014-11-11
 * 
 */
public class ItxxzServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	//filePath��E:\�������\Eclipse\JavaDevp_Eclipse_final\j2eclipse_luna\dataSource\itxxz
	private static String filePath;
	
	private final String suffix = ".txt";
	
	private final String INDEX = "dataIndex";
	
	private String[] descs = new String[13];
	
	private List<Information> dataList = new ArrayList<Information>();
	
	private String changepage;
	
	private IndexSearcher isearcher = null;
	
	private Analyzer analyzer = null;
	
	public String getChangepage() {
		return changepage;
	}

	public void setChangepage(String changepage) {
		this.changepage = changepage;
	}

	public ItxxzServlet() { }
	
	public void doExec(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("-----doExec-------");
		doPost(request, response);
		
	}
	
	static {
		// ��ȡ��ǰ���̵ľ���·��
		File directory = new File("");
		filePath = directory.getAbsolutePath()+"//itxxz";
		System.out.println("filePath="+filePath);
	}
	
	/**
	 * ���ݳ�ʼ��
	 */
	@Override
	public void init() throws ServletException {
		System.out.println("-----init-------");
		File directory = new File("");
		filePath = directory.getAbsolutePath()+"\\dataSource\\itxxz";
		System.out.println("�ļ�������·����"+filePath);
		initData();
		writeFile();
		createIndex();
		
	}

	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("--------doPost---------");
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		Pageinate page = new Pageinate();
		System.out.println("init_page:"+page);
		String keyword = request.getParameter("searchkey");
		String pageKey = request.getParameter("changepage");
		String startparam = request.getParameter("startparam");
		
		try {
			if(startparam != null){
				page.setStart(Integer.parseInt(startparam));
			}
			if(keyword != null){
				searchFile(keyword,pageKey,page);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}
		request.setAttribute("dataList", dataList);
		request.setAttribute("searchkey", keyword);
		request.setAttribute("startparam", page.getStart());
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/dataList.jsp");
	    dispatcher.forward(request, response);
	}

	/**
	 * ���������ļ�
	 * @author ITѧϰ��-�з
	 */
	private void createIndex(){
		// ʵ����IKAnalyzer�ִ���
		analyzer = new IKAnalyzer(true);

		Directory directory = null;
		IndexReader ireader = null;
		IndexWriter iwriter = null;
		
		try {
			// �����ڴ���������
			directory = FSDirectory.open(new File(INDEX));
			// ����IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(
					Version.LUCENE_4_10_2, analyzer);
			iwConfig.setOpenMode(OpenMode.CREATE);
			iwriter = new IndexWriter(directory, iwConfig);
			FileInputStream fis = null;
			for(int i=0;i<descs.length;i++){
				fis = new FileInputStream(new File(filePath+i+suffix));
				
				// д������
				Document doc = new Document();
				doc.add(new StringField("id", i+"", Field.Store.YES));
				doc.add(new TextField("title", "itxxz_"+i, Field.Store.YES));
				doc.add(new TextField("path", filePath+i+suffix, Field.Store.YES));
				doc.add(new TextField("description", descs[i], Field.Store.YES));
				doc.add(new TextField("contents", new BufferedReader(
						new InputStreamReader(fis,"GBK"))));
				iwriter.addDocument(doc);
				
			}
			fis.close();
			iwriter.close();

		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
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
	
	public void searchFile(String keyword,String pageKey,Pageinate page) throws IOException, ParseException, InvalidTokenOffsetsException{
		System.out.println("pageKey:"+pageKey);
		// ʵ����������
		IndexReader reader = DirectoryReader.open(FSDirectory
				.open(new File(INDEX)));
		isearcher = new IndexSearcher(reader);
		//keyword = "ITѧϰ��-�з";
		// ʹ��QueryParser��ѯ����������Query����
		QueryParser qp = new QueryParser(Version.LUCENE_4_10_2,
				PublicUtilDict.CONTENT, analyzer);
		Query query = qp.parse(keyword);
		
		System.out.println("Query = " + query);

		// �������ƶ���ߵ�5����¼
		TopDocs topDocs = isearcher.search(query, page.getPageSize());
		System.out.println("ƥ���ļ�������" + topDocs.totalHits+",topDocs.totalHits:"+topDocs.totalHits);

		CommenUtils.doPagingSearchIK(pageKey, isearcher, query, page,dataList,analyzer);
	}
	
	/**
	 * ��ʼ������
	 * @author ITѧϰ��-�з
	 */
	public void initData(){
		descs[0]="ITѧϰ�ߣ���ע����Ա�ľ�ҵ���������������www.itxxz.com��";
		descs[1]="��ô�����ǲ�����java��Luceneһ�£�����������Щ���ϡ�����";
		descs[2]="����з�ܻ���ռ����΢�����Ż�ͷ������������������次�";
		descs[3]="����ITѧϰ��-�з��ÿ����ϲ����������ǣ���һ������";
		descs[4]="��ʱ��дһƪ���£���һ�д��룬Ȼ���bug������ˣ��";
		descs[5]="�������Ʋ���������ʫ����Ȼ����ϲ�������з��";
		descs[6]="��Ը��һ�����ɵ��з�������������䣻�ҿ���ȥ��ɽ���������µ���Ȼ";
		descs[7]="��������������Ĵ��ۣ��������Ĺȣ������ƴ�ɢ";
		descs[8]="Ϊʲô�����Ծ��侹Ȼ��������ǰ�����硣����";
		descs[9]="��ʱ���Һ������Լ���һֻ�з���Ҳ��ܷɵ��뺣Ÿ��ô�ߣ�����Ҳû������ֻ��һ������һ��ʯ�ӡ�";
		descs[10]="�ҿ�������ɢɢ������ɳ̲�ϣ�ֻҪ�ص��󺣣��ٴεǰ���ʱ�򣬻���ͻ�����һ������";
		descs[11]="��û���ڴ������죬Ҳû�о����Ĺ�����ÿ�춼�ǿ������⣬�������ˣ�Ȼ��úõ�˯��һ����";
		descs[12]="ֱ����һ�죬һֻ��Ÿ�����ң�������Ľ�㣬���п��Իص��Ĵ󺣣���������ԶҲ�޷�ӵ����ա�";
	}
	
	/**
	 * ��ָ��������д�뵽ָ�����ļ���ȥ
	 */
	public void writeFile() {
		for (int i = 0; i < descs.length; i++) {
			File file = new File(filePath+i+suffix);
			createFile(file.toString());
			
			//�Ƚ��ļ����������
			try {
				FileWriter fw = new FileWriter(file);//FileWriter fw = new FileWriter(file,true),�������true���һֱ�������
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("");
				bw.flush();
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//����ļ��Ժ���д��ָ������
			try {
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(descs[i]);
				bw.flush();
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//��ÿ��д���ļ������ݴ�ӡ
			try {
				FileReader fr = new FileReader(file);
				BufferedReader bReader = new BufferedReader(fr);
				String string = bReader.readLine();
				System.out.println(i+"."+string);
				bReader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ָ��·���´���ָ���ļ�
	 * @param destFileName
	 * @return
	 */
	public boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if(file.exists()) {
            System.out.println("���������ļ�" + destFileName + "ʧ�ܣ�Ŀ���ļ��Ѵ��ڣ�");
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            System.out.println("���������ļ�" + destFileName + "ʧ�ܣ�Ŀ���ļ�����ΪĿ¼��");
            return false;
        }
        //�ж�Ŀ���ļ����ڵ�Ŀ¼�Ƿ����
        if(!file.getParentFile().exists()) {
            //���Ŀ���ļ����ڵ�Ŀ¼�����ڣ��򴴽���Ŀ¼
            System.out.println("Ŀ���ļ�����Ŀ¼�����ڣ�׼����������");
            if(!file.getParentFile().mkdirs()) {
                System.out.println("����Ŀ���ļ�����Ŀ¼ʧ�ܣ�");
                return false;
            }
        }
        //����Ŀ���ļ�
        try {
            if (file.createNewFile()) {
                System.out.println("���������ļ�" + destFileName + "�ɹ���");
                return true;
            } else {
                System.out.println("���������ļ�" + destFileName + "ʧ�ܣ�");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("���������ļ�" + destFileName + "ʧ�ܣ�" + e.getMessage());
            return false;
        }
    }
	
	/**
	 * filePath=F:\Study\AliBaba\lucene-itxxz//itxxz
	 * @param args
	 */
	public static void main(String[] args) {
		ItxxzServlet servlet = new ItxxzServlet();
		servlet.initData();//��ʼ��
		servlet.writeFile();
	}
}
