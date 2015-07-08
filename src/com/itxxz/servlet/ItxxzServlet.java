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
 * Lucene第七章：Lucene搜索WEB实例
 * 
 * @author IT学习者-螃蟹
 * @官网：http://www.itxxz.com
 * @date 2014-11-11
 * 
 */
public class ItxxzServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	//filePath：E:\开发软件\Eclipse\JavaDevp_Eclipse_final\j2eclipse_luna\dataSource\itxxz
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
		// 获取当前工程的绝对路径
		File directory = new File("");
		filePath = directory.getAbsolutePath()+"//itxxz";
		System.out.println("filePath="+filePath);
	}
	
	/**
	 * 数据初始化
	 */
	@Override
	public void init() throws ServletException {
		System.out.println("-----init-------");
		File directory = new File("");
		filePath = directory.getAbsolutePath()+"\\dataSource\\itxxz";
		System.out.println("文件创建的路径："+filePath);
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
	 * 创建索引文件
	 * @author IT学习者-螃蟹
	 */
	private void createIndex(){
		// 实例化IKAnalyzer分词器
		analyzer = new IKAnalyzer(true);

		Directory directory = null;
		IndexReader ireader = null;
		IndexWriter iwriter = null;
		
		try {
			// 建立内存索引对象
			directory = FSDirectory.open(new File(INDEX));
			// 配置IndexWriterConfig
			IndexWriterConfig iwConfig = new IndexWriterConfig(
					Version.LUCENE_4_10_2, analyzer);
			iwConfig.setOpenMode(OpenMode.CREATE);
			iwriter = new IndexWriter(directory, iwConfig);
			FileInputStream fis = null;
			for(int i=0;i<descs.length;i++){
				fis = new FileInputStream(new File(filePath+i+suffix));
				
				// 写入索引
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
		// 实例化搜索器
		IndexReader reader = DirectoryReader.open(FSDirectory
				.open(new File(INDEX)));
		isearcher = new IndexSearcher(reader);
		//keyword = "IT学习者-螃蟹";
		// 使用QueryParser查询分析器构造Query对象
		QueryParser qp = new QueryParser(Version.LUCENE_4_10_2,
				PublicUtilDict.CONTENT, analyzer);
		Query query = qp.parse(keyword);
		
		System.out.println("Query = " + query);

		// 搜索相似度最高的5条记录
		TopDocs topDocs = isearcher.search(query, page.getPageSize());
		System.out.println("匹配文件个数：" + topDocs.totalHits+",topDocs.totalHits:"+topDocs.totalHits);

		CommenUtils.doPagingSearchIK(pageKey, isearcher, query, page,dataList,analyzer);
	}
	
	/**
	 * 初始化描述
	 * @author IT学习者-螃蟹
	 */
	public void initData(){
		descs[0]="IT学习者，关注程序员的就业、工作和生活——『www.itxxz.com』";
		descs[1]="那么，我们不妨用java来Lucene一下，看看都有哪些爆料。。。";
		descs[2]="最近螃蟹很火，抢占各种微博、门户头条，长城内外甚是妖娆。";
		descs[3]="我是IT学习者-螃蟹，每天最喜欢的事情就是，喝一杯咖啡";
		descs[4]="有时候写一篇文章，敲一行代码，然后和bug愉快的玩耍。";
		descs[5]="或许是唐伯虎的那首诗，居然让我喜欢上了螃蟹。";
		descs[6]="我愿做一个自由的螃蟹，可以游行世间；我可以去高山聆听最清新的自然";
		descs[7]="可以在深海躲避阳光的刺眼，听鸟啼幽谷，看白云吹散";
		descs[8]="为什么，不自觉间竟然看不清眼前的世界。。。";
		descs[9]="有时候我很庆幸自己是一只螃蟹，我不能飞的想海鸥那么高，或者也没有像那只鸟一样眷恋一颗石子。";
		descs[10]="我可以懒懒散散的躺在沙滩上，只要回到大海，再次登岸的时候，或许就会是另一番景象。";
		descs[11]="我没有期待的明天，也没有眷恋的过往，每天都是看看阳光，听听海浪，然后好好的睡上一觉。";
		descs[12]="直到有一天，一只海鸥告诉我，我真羡慕你，你有可以回到的大海，而我们永远也无法拥抱天空。";
	}
	
	/**
	 * 将指定的内容写入到指定的文件中去
	 */
	public void writeFile() {
		for (int i = 0; i < descs.length; i++) {
			File file = new File(filePath+i+suffix);
			createFile(file.toString());
			
			//先将文件的内容清空
			try {
				FileWriter fw = new FileWriter(file);//FileWriter fw = new FileWriter(file,true),如果加了true则会一直往后添加
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("");
				bw.flush();
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//清空文件以后，再写入指定内容
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
			
			//将每次写入文件的内容打印
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
	 * 指定路径下创建指定文件
	 * @param destFileName
	 * @return
	 */
	public boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if(file.exists()) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在！");
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
            return false;
        }
        //判断目标文件所在的目录是否存在
        if(!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建它！");
            if(!file.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
                return false;
            }
        }
        //创建目标文件
        try {
            if (file.createNewFile()) {
                System.out.println("创建单个文件" + destFileName + "成功！");
                return true;
            } else {
                System.out.println("创建单个文件" + destFileName + "失败！");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("创建单个文件" + destFileName + "失败！" + e.getMessage());
            return false;
        }
    }
	
	/**
	 * filePath=F:\Study\AliBaba\lucene-itxxz//itxxz
	 * @param args
	 */
	public static void main(String[] args) {
		ItxxzServlet servlet = new ItxxzServlet();
		servlet.initData();//初始化
		servlet.writeFile();
	}
}
