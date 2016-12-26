package com.yaxin.zookeeper;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ListTest {

	public static void main(String[] args) {
		String xx ="^\\.\\d{3}";
		Pattern p  = Pattern.compile(xx);
		
		Matcher m = p.matcher(".789 ll");
		if(m.find()){
			System.out.println(m.group());
		}
	}
}
