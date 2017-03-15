package com.yaxin.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Hashtable;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
/**
 * 
 * <li>文件名称: itamJobHandler</li> <li>文件描述: SSHChannel.java</li> <li>版权所有:
 * 版权所有(C)2016-2018</li> <li>公 司: 勤智数码科技股份有限公司</li> <li>内容摘要:SSH连接通道</li> <li>
 * 其他说明:无</li> <li>完成日期：2016年12月26日</li> <li>修改记录: 无</li>
 * 
 * @version 产品版本
 * @author Administrator
 */
public class SSHChannel3{
	

	private Session session;
	private StringBuffer buffer = new StringBuffer();

	public static final int COMMAND_EXECUTION_SUCCESS_OPCODE = -2;
	public static final String BACKSLASH_R = "\r";
	public static final String BACKSLASH_N = "\n";
	public static final String COLON_CHAR = ":";
	public static String ENTER_CHARACTER = BACKSLASH_R;
	public static final int SSH_PORT = 22;
	public static String[] linuxPromptRegEx = new String[] { "~]#", "~#", "#", ":~#", "/$", ">" };
	public static String[] errorMsg = new String[] { "could not acquire the config lock " };

	private String ip;
	private int port;
	private String user;
	private String password;
	private ChannelShell channel;
	
	public static void main(String[] args){
		SSHChannel3 ssh = new SSHChannel3("172.16.7.234", 22, "root", "123456");
		ssh.execute2("", 10000);
		ssh.execute2("pwd", 10000);
		ssh.execute2("pwd", 10000);
		ssh.disconnect();
	}

	public SSHChannel3(String ip, int port, String user, String password) {
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.password = password;
		connect();
	}

	public void disconnect() {
		
		if (channel != null) {
			channel.disconnect();
		}
		
		if (session != null) {
			session.disconnect();
		}
	}

	public String getResponse() {
		return buffer.toString();
	}

	private void connect() {
		try{
			JSch jsch = new JSch();
	        session = jsch.getSession(user, ip, SSH_PORT);
	        if (password != null) {
	            session.setPassword(password);
	        }
	        Hashtable<String,String> config = new Hashtable<String,String>();
	        config.put("StrictHostKeyChecking", "no");
	        session.setConfig(config);
	        session.connect();
	        channel  = (ChannelShell) session.openChannel("shell");
	        channel.connect();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void execute2(String command, int timeout){
		try {
	        Reader is =  new InputStreamReader(channel.getInputStream());
	        Writer os = new OutputStreamWriter(channel.getOutputStream());
			if(command != null && !"".equals(command)){
				os.write(command+"\r");
	            os.flush();
			}
			os.write("\r");
            os.flush();
			char cs[] = new char[256];
			int length;   
			while (true){    
				length = is.read(cs);
				String print = new String(cs, 0, length);
				System.out.println(print);
			    if(checkOver(print)){
			    	break;
			    }
			}  
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private boolean checkOver(String line){
	    if(line.indexOf("]# ") > 0 && line.lastIndexOf("]# ") == (line.length() - 3)){
	    	return true;
	    }
	    if(line.indexOf("]$ ") > 0 && line.lastIndexOf("]$ ") == (line.length() - 3)){
	    	return true;
	    }
	    return false;
	}
}
