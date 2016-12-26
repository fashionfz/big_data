package com.yaxin.hdfs;

import java.io.IOException;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.lib.CombineTextInputFormat;
import org.apache.hadoop.util.ReflectionUtils;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
    	JobConf conf = new JobConf();
	    InputFormat<?, ?> format = (InputFormat<?, ?>) ReflectionUtils.newInstance(CombineTextInputFormat.class, conf);
	    
	    if(Configurable.class.isAssignableFrom(format.getClass())){
	    	((Configurable)format).setConf(conf);
	    }
	    
	    format.getSplits(conf, 100);
    }
}
