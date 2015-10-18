package com.yaxin.kafka;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaConsumer extends Thread{
	
	ConsumerConnector consumer;
	String topic;
	Properties prop = new Properties();

	public KafkaConsumer(){
		try{
			InputStream is = getClass().getClassLoader().getResourceAsStream("kafka.properties");
			prop.load(is);
			consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(prop));
			topic = prop.getProperty("topic");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try{
			Map<String,Integer> topicMap = new HashMap<String,Integer>();
			topicMap.put(topic, new Integer(1));
			Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap  = consumer.createMessageStreams(topicMap);
			ExecutorService pool = Executors.newFixedThreadPool(1);
			for(final KafkaStream<byte[],byte[]> stream : consumerMap.get(topic)){
				pool.submit(new Runnable(){
					@Override
					public void run() {
						ConsumerIterator<byte[], byte[]> it = stream.iterator();
						while(it.hasNext()){
							System.out.println(new String(it.next().message()));
						}
					}
					
				});
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args){
		KafkaConsumer c = new KafkaConsumer();
		c.start();
	}
}
