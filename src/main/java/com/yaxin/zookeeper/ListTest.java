package com.yaxin.zookeeper;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ListTest {

	public static void main(String[] args) {
		String txt = "Wed Sep 10 17:27:13 2014 ppp";
		Pattern p = Pattern.compile("^\\w{3}\\s\\w{3}\\s\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\s\\d{4}");
		Matcher m = p.matcher(txt);
		if(m.find()){
			System.out.println(m.group());
		}
	}
}
