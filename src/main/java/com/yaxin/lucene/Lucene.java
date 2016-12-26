package com.yaxin.lucene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.analysis.morfologik.MorfologikAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.QueryBuilder;

import com.google.common.collect.ImmutableClassToInstanceMap.Builder;

public class Lucene {

	public static void main(String[] args) throws IOException, ParseException{
		//RAMDirectory directory = new RAMDirectory();  
		java.nio.file.Path path = java.nio.file.Paths.get("D:\\temp");
		SimpleFSDirectory directory = new SimpleFSDirectory(path);
		//LocalFile(directory);
		
		
		
        //IndexReader reader = new IndexReader();  
        IndexReader reader = DirectoryReader.open(directory);    
        IndexSearcher searcher = new IndexSearcher(reader);   
        //searcher.setSimilarity(new IKSimilarity());  
        String keyWords = "\"2015-11-27 14:39:31\"";  
        MorfologikAnalyzer analyzer = new MorfologikAnalyzer(); 
        QueryBuilder qp = new QueryBuilder(analyzer);
        
        org.apache.lucene.search.BooleanQuery.Builder bd = new BooleanQuery.Builder();
       // Query query = qp.createPhraseQuery("_time", keyWords);
        
      //  Query query = qp.createBooleanQuery("_time", keyWords);
        
        QueryParser parser =new QueryParser("_time", analyzer);
        Query query = parser.parse("\"2015-11-27 14:39:31\"");
        
        
        QueryParser parser2 =new QueryParser("level", analyzer);
        Query query2 = parser2.parse("WARN | ERROR");
        
        System.out.println("xxx:"+query2);
        
        bd.add(query, Occur.MUST);
 //       bd.add(query2, Occur.MUST);
        
       
        Query b = Test.create("level=INFO and _content=NORMAL");
        
        System.out.println("query == > " + b);
        
        Query A = qp.createPhraseQuery("level", "INFO");
        
        TopDocs topDocs = searcher.search(b, Integer.MAX_VALUE);  
        ScoreDoc[] ss = topDocs.scoreDocs;
        for(ScoreDoc sd : ss){
        	Document doc = searcher.doc(sd.doc);
        	System.out.println("_time="+doc.get("_time")+"{"+doc.get("_content")+"}");
        }
        System.out.println(topDocs.totalHits);  
	}
	
	
	public static void HDFS(RAMDirectory directory) throws IOException{
		String dst = "hdfs://192.168.10.153:9000/hdfs/test/qq.txt";
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(dst), conf);
		FSDataInputStream hdfsInStream = fs.open(new Path(dst));
		//DataInputStream d = new DataInputStream(hdfsInStream);
		BufferedReader reader =new BufferedReader(new InputStreamReader(hdfsInStream));
		//创建IKAnalyzer中文分词对象  
		MorfologikAnalyzer analyzer = new MorfologikAnalyzer(); 
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		// 使用智能分词  
        //analyzer.setUseSmart(true); 
		IndexWriter writer = new IndexWriter(directory,iwc);
		//FileReader read = new FileReader("D:\\index"+"part-00000");
		//BufferedReader br = new BufferedReader(read);
		String row;
		Document doc =new Document();
	    while((row = reader.readLine())!=null){
	    	doc.add(new TextField("contents",row,Field.Store.YES));  
			writer.addDocument(doc);
	    }
		writer.close();
		reader.close();
		hdfsInStream.close();
		fs.close();
	}
	
	
	public static void LocalFile(Directory directory) throws IOException{
		//创建IKAnalyzer中文分词对象  
		MorfologikAnalyzer analyzer = new MorfologikAnalyzer(); 
		//analyzer.setUseSmart(true); 
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(directory,iwc);
		writer.deleteAll();
		FileReader read = new FileReader("D:\\test.log");
		BufferedReader br = new BufferedReader(read);
		StringBuilder row = new StringBuilder();
		String line;
//		Document doc =new Document();
//		row = br.readLine();
//		doc.add(new TextField("_content",row,Field.Store.YES));  
//		writer.addDocument(doc);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String prevLine = "";
	    while((line = br.readLine())!=null){
	    	if(line.length()>19){
	    		String ts = line.substring(0, 19);
	    		Date dt = null;
	    		try{//下一行
	    			dt = df.parse(ts);
	    		}catch(Exception e){
	    			
	    		}
	    		
	    		if(dt == null){
	    			row.append(line);
	    		}else{
	    			prevLine = line;
	    			
	    	    	Document doc =new Document();
	    	    	doc.add(new TextField("_content",prevLine+row.toString(),Field.Store.YES));  
	    	    	doc.add(new TextField("_time",prevLine.substring(0, 19),Field.Store.YES));
	    	    	doc.add(new TextField("level",prevLine.substring(22, 27),Field.Store.YES));
	    			writer.updateDocument(new Term("id",""+(prevLine+row.toString()).hashCode()),doc);
	    	    	row = new StringBuilder();
	    		}
	    	}

	    }
	    br.close();
		writer.close();
		
	}
}
