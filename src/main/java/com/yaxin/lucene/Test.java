package com.yaxin.lucene;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.morfologik.MorfologikAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;






public class Test {


	public static void main(String[] args) {
		//String txt = "((_domain=OC_3.5 or _domain=Test_Data) and _host=66) or _ip = 77 or time =dd";
		
		//String txt = "aaa=111 or _bbb=222 or ccc=333 or _ddd = 444 or eee =555";
		
		String txt = "26-Jul-2016 14:11:05.783";
		
		String pattern = "^\\d{2}-\\w{3}-\\d{4}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3}";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(txt);
		if(m.find()){
			System.out.println(m.group());
		}
		
	}
	
	
	public static Query create(String txt){
		String pattern = "\\([^\\(\\)]*\\)";
		Pattern p = Pattern.compile(pattern);
		Map<String,String> res = new HashMap<>();
		//提取括号
		String val = find(txt, p , 0, res);
		System.out.println(val);
		MorfologikAnalyzer analyzer = new MorfologikAnalyzer(); 
		QueryBuilder qp = new QueryBuilder(analyzer);
		return perse(qp, val, res);
	}
	
	
	//递归解析创建query
	public static Query perse(QueryBuilder qp, String val, Map<String, String> res){
		List<Meta> list = new ArrayList<>();
		String pre = "";
		int preIndex = 0;
		String preOperator=null;
		for(int i =0;i<val.length();i++){
			if(val.charAt(i) == ' '){
				//System.out.println("i : "+i +" & preIndex : " + preIndex);
				if("".equals(pre)){
					pre = val.substring(preIndex, i);
					preIndex = i+1;
					continue;
				}
				String temp = val.substring(preIndex, i);
				
				System.out.println(temp+"---"+preIndex);
				if(temp.toLowerCase().equals("or") || temp.toLowerCase().equals("and")){
					//check
					if(list.size() > 0 && !preOperator.equals(temp)){
						throw new RuntimeException("操作符异常");
					}
					list.add(new Meta(pre, preOperator));
					pre = "";
					preOperator = temp.toLowerCase();
					preIndex = i+1;
					continue;
				}
				pre = pre+" " +temp;
				preIndex = i+1;
			}
			if(i == (val.length() - 1)){
				pre =pre +" " + val.substring(preIndex);
				list.add(new Meta(pre, preOperator));
			}
		}
		
		String pattern2 = "\\$\\d+\\$";
		Pattern p2 = Pattern.compile(pattern2);
		
        org.apache.lucene.search.BooleanQuery.Builder bd = new BooleanQuery.Builder();
        
		Occur op = Occur.MUST;
		if(list.size() >1){
			if("or".equals(list.get(1).getOperator()))
				op = Occur.SHOULD;
			else if("and".equals(list.get(1).getOperator()))
				op = Occur.MUST;
		}
		
		for(Meta item : list){
			//System.out.println(item.getContent()+"---"+item.getOperator());
			Matcher mx = p2.matcher(item.getContent());
			if(mx.find()){
				String key = mx.group();
				String code = res.get(key);
				String stentc = item.getContent().replace(key, code);
				//递归
				Query q = perse(qp, stentc, res);
				bd.add(q, op);
				
			}else{
				//create Query
				String[] field = item.getContent().split("=");
				Query q = null;
				if(field.length == 1)
					//q = new TermQuery(new Term("_content",field[0].trim()));
					q = qp.createPhraseQuery("_content",field[0].trim());
				else if(field.length == 2)
					//q = new TermQuery(new Term(field[0].trim(),field[1].trim()));
					q = qp.createPhraseQuery(field[0].trim(),field[1].trim());
				bd.add(q, op);
			}
		}
		Query query = bd.build();
		return query;
	}
	
	
	
	//递归提取括号
	public static String find(String src,Pattern p,int index,Map<String,String> map){
		Matcher m = p.matcher(src);
		if(m.find()){
			String val = m.group();
			map.put("$"+index+"$", val.substring(1, val.length()-1));
			String txt = src.replace(val, "$"+index+"$");
			return find(txt,p,++index,map);
		}else
			return src;
	}
	
	
	
	public static int count(String src,char target){
		int count = 0;
		for(int i =0;i<src.length();i++){
			if(src.charAt(i) == target)
				count++;
		}
		return count;
	}
	
	
	public static int count(String src,String target){
		int count = 0;
		int index = 0;
		while(true){
			if((index = src.indexOf(target)) > 0){
				count++;
				src = src.substring(index);
			}else
				break;
		}
		return count;
	}
	

}

class Meta{
	
	public Meta(String content, String operator){
		this.content = content;
		this.operator = operator;
	}
	
	private String content;
	private String operator;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
}

