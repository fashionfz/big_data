package com.yaxin.http;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.oro.text.regex.MalformedPatternException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import expect4j.Closure;
import expect4j.Expect4j;
import expect4j.ExpectState;
import expect4j.matches.EofMatch;
import expect4j.matches.Match;
import expect4j.matches.RegExpMatch;
import expect4j.matches.TimeoutMatch;
/**
 * 
 * <li>文件名称: itamJobHandler</li> <li>文件描述: SSHChannel.java</li> <li>版权所有:
 * 版权所有(C)2016-2018</li> <li>公 司: 勤智数码科技股份有限公司</li> <li>内容摘要:SSH连接通道</li> <li>
 * 其他说明:无</li> <li>完成日期：2016年12月26日</li> <li>修改记录: 无</li>
 * 
 * @version 产品版本
 * @author Administrator
 */
public class SSHChannel{
	private static final Logger logger = LoggerFactory.getLogger(SSHChannel.class);

	private Session session;
	private ChannelShell channel;
	private static Expect4j expect = null;
	private static final long defaultTimeOut = 1000;
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
		SSHChannel ss = new SSHChannel("192.168.1.142", 22, "root", "root3306");
		String result = ss.execute(new String[]{"cp /tmp/test.rar /tmp/tt/test.rar"});
		System.out.println(result);
		ss.execute(new String[]{"\r"});
		ss.disconnect();
	}

	public SSHChannel(String ip, int port, String user, String password) {
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.password = password;
		expect = getExpect();
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

	private Expect4j getExpect() {
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(user, ip, port);
			session.setPassword(password);
			Hashtable<String, String> config = new Hashtable<String, String>();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			localUserInfo ui = new localUserInfo();
			session.setUserInfo(ui);
			channel = (ChannelShell) session.openChannel("shell");
			Expect4j expect = new Expect4j(channel.getInputStream(), channel.getOutputStream());
			channel.connect();
			return expect;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	public String execute(String[] commands) {
		if (expect == null) {
			return null;
		}

		Closure closure = new Closure() {
			public void run(ExpectState expectState) throws Exception {
				buffer.append(expectState.getBuffer());// buffer is string
														// buffer for appending
														// output of executed
														// command
				System.out.println("####" + expectState.getBuffer());
				expectState.exp_continue();

			}
		};
		List<Match> lstPattern = new ArrayList<Match>();
		String[] regEx = linuxPromptRegEx;
		if (regEx != null && regEx.length > 0) {
			synchronized (regEx) {
				for (String regexElement : regEx) {// list of regx like, :>, />
													// etc. it is possible
													// command prompts of your
													// remote machine
					try {
						RegExpMatch mat = new RegExpMatch(regexElement, closure);
						lstPattern.add(mat);
					} catch (MalformedPatternException e) {
						return null;
					} catch (Exception e) {
						return null;
					}
				}
				lstPattern.add(new EofMatch(new Closure() { // should cause
															// entire page to be
															// collected
							public void run(ExpectState state) {
							}
						}));
				lstPattern.add(new TimeoutMatch(defaultTimeOut, new Closure() {
					public void run(ExpectState state) {
					}
				}));
			}
		}
		try {
			boolean isSuccess = true;
			for (String strCmd : commands) {
				isSuccess = isSuccess(lstPattern, strCmd);
			}
			isSuccess = !checkResult(expect.expect(lstPattern));

			String response = buffer.toString().toLowerCase();
			for (String msg : errorMsg) {
				if (response.indexOf(msg) > -1) {
					return null;
				}
			}

			return buffer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}finally{
			buffer.setLength(0);
		}
	}
	
	
	private boolean isSuccess(List<Match> objPattern, String strCommandPattern) {
		try {
			boolean isFailed = checkResult(expect.expect(objPattern));
			if (!isFailed) {
				expect.send(strCommandPattern);
				expect.send("\r");
				return true;
			}
			return false;
		} catch (MalformedPatternException ex) {
			return false;
		} catch (Exception ex) {
			return false;
		}
	}

	private boolean checkResult(int intRetVal) {
		if (intRetVal == COMMAND_EXECUTION_SUCCESS_OPCODE) {
			return true;
		}
		return false;
	}

	public static class localUserInfo implements UserInfo {
		String passwd;

		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			return true;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			return true;
		}

		public void showMessage(String message) {

		}
	}
}
