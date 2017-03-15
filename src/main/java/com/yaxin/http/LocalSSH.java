package com.yaxin.http;

import java.io.InputStream;

public class LocalSSH {

	public static void main(String[] args){
		try{
			Runtime rt = Runtime.getRuntime();
			Process p2 = rt.exec("cd /");
			Process p = rt.exec("pwd");
			InputStream is = p.getInputStream();
            byte[] b = new byte[1024];
            int size;
            while((size = is.read(b)) != -1)
                System.out.println(new String(b,0,size,"GBK"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
