package com.yaxin.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.sshtools.net.SocketTransport;
import com.sshtools.ssh.PasswordAuthentication;
import com.sshtools.ssh.SshAuthentication;
import com.sshtools.ssh.SshClient;
import com.sshtools.ssh.SshConnector;
import com.sshtools.ssh.SshSession;
import com.sshtools.ssh2.Ssh2Session;

public class SSHToolsChannel{
	SshClient client = null;
	Ssh2Session session = null;
	public SSHToolsChannel(){
		try{
			SshConnector connector = SshConnector.createInstance();
			SocketTransport transport = new SocketTransport("192.168.1.142", 22);
			client = connector.connect(transport, "root");  
			PasswordAuthentication password = new PasswordAuthentication();
			password.setPassword("root3306");
			if (client.authenticate(password) == SshAuthentication.COMPLETE && client.isConnected()
					&& client.isAuthenticated()) {
				session = (Ssh2Session) client.openSessionChannel();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String execute(String command) {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@222");    
		try{
			if (session != null && session.startShell()){
//				OutputStream writer = session.getOutputStream();
				session.executeCommand(command);
//				writer.write(command.getBytes());  
//				writer.flush();   
	            InputStream is = session.getInputStream();
	            byte[] b = new byte[1024];
	            int size;
	            while((size = is.read(b)) != -1)
	                System.out.println(new String(b,0,size,"UTF-8"));
//				writer.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}


	public void disconnect() {
		try {
			if(session != null)
				session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		client.disconnect();
	}
	
	public static void main(String[] args){
		SSHToolsChannel test = new SSHToolsChannel();
		test.execute("ls\n");
		test.disconnect();
	}
}
