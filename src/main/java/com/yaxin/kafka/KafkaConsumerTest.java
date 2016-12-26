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
import kafka.message.MessageAndMetadata;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;


public class KafkaConsumerTest{
	
	
	final ExecutorService pool = Executors.newFixedThreadPool(2);
	KafkaConsumer<String, String> consumer = null;
	public KafkaConsumerTest(){
		try{
			Properties prop = new Properties();
			InputStream is = getClass().getClassLoader().getResourceAsStream("kafka.properties");
			prop.load(is);	
			ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(prop));
			Map<String, Integer> topicMap = new HashMap<String, Integer>();
			topicMap.put("itba", new Integer(1));
			Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicMap);
			for (KafkaStream<byte[], byte[]> stream : consumerMap.get("itba")) {
				ConsumerIterator<byte[], byte[]> it = stream.iterator();
				// 循环获取数据
				MessageAndMetadata<byte[], byte[]> event;
				while (it.hasNext()) {
					event = it.next();
					System.out.println(new String(event.message()));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			consumer.close();
		}
	}
	
	public void add(String topic) throws Exception{
		
		
		while (true) {
	         ConsumerRecords<String, String> records = consumer.poll(5);
	         for (ConsumerRecord<String, String> record : records)
	             System.out.println(record.value());
	     }
		
		
//		Map<String,Integer> topicMap = new HashMap<String,Integer>();
//		topicMap.put(topic, new Integer(1));
//		//topicMap.put("shell", new Integer(1));
//		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap  = consumer.createMessageStreams(topicMap);
//		File file = new File("f:\\a.log");
//		final Writer os = new FileWriter(file);
//		for(final KafkaStream<byte[],byte[]> stream : consumerMap.get(topic)){
//			pool.submit(new Runnable(){
//				@Override
//				public void run() {
//					ConsumerIterator<byte[], byte[]> it = stream.iterator();
//					int count = 0;
//					while(it.hasNext()){
//						System.out.print(++count);
//						MessageAndMetadata<byte[], byte[]> data = it.next();
//						System.out.println("::"+new String(data.message()));
////						System.out.println(data.topic()+"#####"+new String(data.message()));
////						try {
////							os.write(new String(data.message()));
////						} catch (IOException e) {
////							e.printStackTrace();
////						}
//					}
//					
//				}
//				
//			});
//		}
		
		
		
	}
	

	
	public static void main(String[] args){
		new KafkaConsumerTest();
	}
}
