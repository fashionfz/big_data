package com.yaxin.hbase;

public class HBaseConnectionProvider {
	//@Resource(name="hbaseConnectionPoolProvider")
	//StackObjectPool hbaseConnectionPoolProvider;
	
	int defaultMaxSize = 10;
	int defaultMinSize = 3;
	
	public void initConnections(int sizes){
		int initialSize = 0;
		initialSize = Math.max(Math.min(sizes, defaultMaxSize),defaultMinSize);
		int total = initialSize;
		while(initialSize-- > 0){
			//hbaseConnectionPoolProvider.addObject();
			//log.info "Add HBaseConnection TotalCount: ${total-initialSize} "
		}
	}
	
	public void acquireConnection(){
		//hbaseConnectionPoolProvider.borrowObject();
	}
	
//	public void closeConnection(HConnection conn){
//		if(conn){
//			//hbaseConnectionPoolProvider.returnObject(conn);
//		}
//	}
}
