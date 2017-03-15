package com.yaxin.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
public class SSHChannel2{
	

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
	
	public static void main(String[] args){
		SSHChannel2 ssh = new SSHChannel2("192.168.1.142", 22, "root", "root3306");
		ssh.execute2("cd /tmp/test", 0);
		ssh.execute2("pwd", 0);
		ssh.disconnect();
	}

	public SSHChannel2(String ip, int port, String user, String password) {
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.password = password;
		connect();
	}

	public void disconnect() {
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
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void execute2(String command, int timeout){
		ChannelShell channel = null;
		OutputStream out=null;
		BufferedReader in = null;
		try {
	        channel  = (ChannelShell) session.openChannel("shell");
	        if(timeout > 0)
	        	channel.connect(timeout);
	        else
	        	channel.connect();
	        out = channel.getOutputStream();
	        in = new BufferedReader(new InputStreamReader(channel.getInputStream())); 
	        
			if(command != null && !"".equals(command)){
	            out.write((command+"\r").getBytes());
	            out.flush();
			}
            out.write("\r".getBytes());
            out.flush();
			String line;    
			while ((line = in.readLine()) != null){    
			    System.out.println("#############" + line);   
			    if(checkOver(line)){
			    	break;
			    }
			}  
//            byte[] b = new byte[1024];
//            int size;
//            while((size = in.read(b)) != -1){
//            	System.out.println(size);
//                System.out.println("$$" + new String(b,0,size,"UTF-8"));
//            }
            
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (channel != null) {
				channel.disconnect();
			}
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
