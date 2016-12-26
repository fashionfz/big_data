package com.yaxin.kafka;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;


public class KafkaProducerTest{

	public static void main(String[] args){
	    Properties props = new Properties();
	    props.put("bootstrap.servers", "172.16.9.90:9092");
	    props.put("client.id", "DemoProducer");
	    props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
	    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 
		 
		 try{
			 KafkaProducer<Integer, String> producer = new KafkaProducer<Integer, String>(props);
				// for(int i = 0; i < 10; i++){
					 String val = "2016-09-23 11:16:28,212 INFO [com.qwserv.itm.pfl.fm.alarm.AlarmShellThread]: shell map size:0 ;vt size:0";
					 Future<RecordMetadata> f = producer.send(new ProducerRecord<Integer, String>("topic_itba", 5, val));
					 RecordMetadata rm = f.get();
					 System.out.println(rm.toString());
				// }
				 producer.close();
				 
		 }catch(Exception e){
			 e.printStackTrace();
		 }


	}
}
