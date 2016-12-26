package com.yaxin.zookeeper;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZooKeeperUtils {
	
	private static final String licencePath = "/agent/auth/data";
	private static final String agentNodePath = "/agent/node";
	

	public static void main(String[] srgs){
		try {
			ZooKeeper keeper = new ZooKeeper("192.168.1.166:2181", 500000, null);
			
			byte[] data = keeper.getData(licencePath, null, null);
			System.out.println(new String(data));
			String regPath = keeper.create(agentNodePath+"/", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			
			System.out.println(regPath);
			
			List<String> child = keeper.getChildren(agentNodePath, false);
			if(child!=null){
				System.out.println(child.size());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
