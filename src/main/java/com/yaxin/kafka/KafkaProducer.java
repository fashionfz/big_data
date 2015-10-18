package com.yaxin.kafka;

import java.io.InputStream;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaProducer extends Thread{
	
	Producer<Integer,String> producer;
	String topic;
	Properties prop = new Properties();
	
	public KafkaProducer(){
		try{
			InputStream is = getClass().getClassLoader().getResourceAsStream("kafka.properties");
			prop.load(is);
			producer = new Producer<Integer,String>(new ProducerConfig(prop));
			topic = prop.getProperty("topic");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try{
			for(int i=0;i<20 ; i++){
				String msg = "this is test msg "+System.currentTimeMillis();
				producer.send(new KeyedMessage<Integer, String>(topic, msg));
				sleep(3000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		KafkaProducer p = new KafkaProducer();
		p.start();
	}
}
