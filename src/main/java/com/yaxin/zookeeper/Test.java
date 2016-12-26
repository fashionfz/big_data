package com.yaxin.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class Test {
	
	protected static CountDownLatch countDownLatch=new CountDownLatch(1); 

	public static void main(String[] args) throws Exception {
		//Watcher w = null;
		final ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 500000, null);
		Watcher w = new Watcher(){

			@Override
			public void process(WatchedEvent event) {
				
				System.out.println(event.getType()+"==="+event.getPath());
				
				try {
					zk.exists("/controller", this);
				} catch (Exception e) {
					e.printStackTrace();
				}
//				if(event.getState()==KeeperState.SyncConnected){  
//		            countDownLatch.countDown();  
//		        } 
			}
			
		};
		// TODO Auto-generated method stub
		//zk = new ZooKeeper("127.0.0.1:2181", 500000, w);
		
		
		
//		countDownLatch.await(); 
		zk.exists("/controller", w);
//		zk.create("/testRootPath", "testRootData".getBytes(), Ids.OPEN_ACL_UNSAFE,
//				   CreateMode.PERSISTENT); 
		Thread.sleep(1000000);
//		 zk.delete("/testRootPath",-1); 
		 
		 zk.close();
	}

}
