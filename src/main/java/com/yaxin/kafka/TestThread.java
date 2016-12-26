package com.yaxin.kafka;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class TestThread implements Runnable{
	
	KafkaConsumer<String, String> consumer = null;
	
	public TestThread(){
		try{
			Properties prop = new Properties();
			InputStream is = getClass().getClassLoader().getResourceAsStream("kafka.properties");
			prop.load(is);
			consumer = new KafkaConsumer<String, String>(prop);
			consumer.subscribe(Arrays.asList("channel166"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
	         ConsumerRecords<String, String> records = consumer.poll(5);
	         for (ConsumerRecord<String, String> record : records)
	             System.out.println(record.value());
	     }
	}

}
