package com.yaxin.hbase;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.util.Methods;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;

public class UserUtil extends User{
	
	private String shortName;
	
	private UserUtil() throws IOException {
		try {
			ugi = UserGroupInformation.getCurrentUser();
		} catch (IOException ioe) {
			throw ioe;
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new UndeclaredThrowableException(e,
					"Unexpected exception getting current secure user");
		}
	}
	
	private UserUtil(UserGroupInformation ugi) {
		this.ugi = ugi;
	}
	

	@Override
	public String getShortName() {
		if (shortName != null)
			return shortName;

		try {
			shortName = ugi.getShortUserName();
//			shortName = (String) call(ugi, "getShortUserName", null, null);
			return shortName;
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new UndeclaredThrowableException(e,
					"Unexpected error getting user short name");
		}
	}

	@Override
	public void obtainAuthTokenForJob(JobConf job) throws IOException, InterruptedException {
	      try {
	          Class<?> c = Class.forName(
	              "org.apache.hadoop.hbase.security.token.TokenUtil");
	          Methods.call(c, null, "obtainTokenForJob",
	              new Class[]{JobConf.class, UserGroupInformation.class},
	              new Object[]{job, ugi});
	        } catch (ClassNotFoundException cnfe) {
	          throw new RuntimeException("Failure loading TokenUtil class, "
	              +"is secure RPC available?", cnfe);
	        } catch (IOException ioe) {
	          throw ioe;
	        } catch (InterruptedException ie) {
	          throw ie;
	        } catch (RuntimeException re) {
	          throw re;
	        } catch (Exception e) {
	          throw new UndeclaredThrowableException(e,
	              "Unexpected error calling TokenUtil.obtainAndCacheToken()");
	        }
		
	}

	@Override
	public void obtainAuthTokenForJob(Configuration conf, Job job) throws IOException, InterruptedException {
	      try {
	          Class<?> c = Class.forName(
	              "org.apache.hadoop.hbase.security.token.TokenUtil");
	          Methods.call(c, null, "obtainTokenForJob",
	              new Class[]{Configuration.class, UserGroupInformation.class,
	                  Job.class},
	              new Object[]{conf, ugi, job});
	        } catch (ClassNotFoundException cnfe) {
	          throw new RuntimeException("Failure loading TokenUtil class, "
	              +"is secure RPC available?", cnfe);
	        } catch (IOException ioe) {
	          throw ioe;
	        } catch (InterruptedException ie) {
	          throw ie;
	        } catch (RuntimeException re) {
	          throw re;
	        } catch (Exception e) {
	          throw new UndeclaredThrowableException(e,
	              "Unexpected error calling TokenUtil.obtainAndCacheToken()");
	        }
		
	}

	@Override
	public <T> T runAs(PrivilegedAction<T> arg0) {
		try {
			return (T) ugi.doAs(arg0);
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new UndeclaredThrowableException(e,
					"Unexpected exception in runAs()");
		}
	}

	@Override
	public <T> T runAs(PrivilegedExceptionAction<T> arg0) throws IOException, InterruptedException {
		try {
			return (T) ugi.doAs(arg0);
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new UndeclaredThrowableException(e,
					"Unexpected exception in runAs()");
		}
	}

	//==========================
    public static User createUserForTesting(Configuration conf,
            String name, String[] groups) {
    	return new UserUtil(UserGroupInformation.createUserForTesting(name, groups));
    }
    
    
    public static void login(Configuration conf, String fileConfKey,
            String principalConfKey, String localhost) throws IOException {
          if (isSecurityEnabled()) {
            SecurityUtil.login(conf, fileConfKey, principalConfKey, localhost);
          }
        
    }

    /**
     * Returns the result of {@code UserGroupInformation.isSecurityEnabled()}.
     */
    public static boolean isSecurityEnabled() {
      return UserGroupInformation.isSecurityEnabled();
    
    }
}
