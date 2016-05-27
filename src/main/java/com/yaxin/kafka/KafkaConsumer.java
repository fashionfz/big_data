package com.yaxin.kafka;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
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
import kafka.message.MessageAndMetadata;

public class KafkaConsumer{
	
	
	final ExecutorService pool = Executors.newFixedThreadPool(2);
	ConsumerConnector consumer = null;
	public KafkaConsumer(){
		try{
			Properties prop = new Properties();
			InputStream is = getClass().getClassLoader().getResourceAsStream("kafka.properties");
			prop.load(is);
			consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(prop));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void add(String topic) throws Exception{
		Map<String,Integer> topicMap = new HashMap<String,Integer>();
		topicMap.put(topic, new Integer(1));
		//topicMap.put("shell", new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap  = consumer.createMessageStreams(topicMap);
		File file = new File("f:\\a.log");
		final Writer os = new FileWriter(file);
		for(final KafkaStream<byte[],byte[]> stream : consumerMap.get(topic)){
			pool.submit(new Runnable(){
				@Override
				public void run() {
					ConsumerIterator<byte[], byte[]> it = stream.iterator();
					while(it.hasNext()){
						MessageAndMetadata<byte[], byte[]> data = it.next();
						System.out.println(data.topic()+"#####"+new String(data.message()));
						try {
							os.write(new String(data.message()));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				}
				
			});
		}
	}
	

	
	public static void main(String[] args){
		KafkaConsumer c = new KafkaConsumer();
		try {
			c.add("oc4");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
