package com.yaxin.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.Coprocessor;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.protobuf.ResponseConverter;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.shaded.com.google.protobuf.RpcController;
import org.apache.hadoop.hbase.util.Bytes;

public class Hbase {

	public static void main(String[] args){
		Configuration conf=HBaseConfiguration.create();
		User user=null;
		Connection con = null;
		Admin admin = null;
		Table tab = null;
		try {
			conf.addResource(Hbase.class.getClassLoader().getResourceAsStream("core-site.xml"));
			conf.addResource(Hbase.class.getClassLoader().getResourceAsStream("hbase-site.xml"));
			conf.addResource(Hbase.class.getClassLoader().getResourceAsStream("hdfs-site.xml"));
			user = UserUtil.createUserForTesting(conf, "hbase", new String[]{"supergroup"});
			con = ConnectionFactory.createConnection(conf, user);
			
			admin = con.getAdmin();
			//delete table
			//delTable(admin);
			//create table
			//createTable(con,admin,tab);
			// write table ============================
			write(con,tab);
			//query
			//int count = query(con,tab);
			//System.out.println(count);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				admin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				con.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static void createTable(Connection con,Admin admin,Table tab) throws IOException{
		if(admin.tableExists(TableName.valueOf("dengbin_test_table"))){
			System.out.println("table is exists");
		}else{
			HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf("dengbin_test_table"));
			//tableDescriptor.addFamily(new HColumnDescriptor("name"));// "indicator"
			HColumnDescriptor hcd1 = new HColumnDescriptor("base_info");
			tableDescriptor.addFamily(hcd1);
			HColumnDescriptor hcd2 = new HColumnDescriptor("study");
			tableDescriptor.addFamily(hcd2);
			HColumnDescriptor hcd3 = new HColumnDescriptor("work");
			tableDescriptor.addFamily(hcd3);
			admin.createTable(tableDescriptor);
		}
	}
	
	public static void delTable(Admin admin) throws IOException{
		if(admin.tableExists(TableName.valueOf("dengbin_test_table"))) {
			admin.disableTable(TableName.valueOf("dengbin_test_table"));
			admin.deleteTable(TableName.valueOf("dengbin_test_table"));
			System.out.println("table delete ...");
		}
	}
	
	public static void write(Connection con,Table tab){
		try{
			tab = con.getTable(TableName.valueOf("dengbin_test_table"));
			List<Put> puts = new ArrayList<Put>();
			for(int i=0;i<=1000000;i++){
				Put p = new Put(String.format("row%03d", System.currentTimeMillis()).getBytes());
				p.addColumn("base_info".getBytes(), "name".getBytes(), Constant.NAMES[RandomUtil.getRandom(0, Constant.NAMES.length)].getBytes());
				p.addColumn("base_info".getBytes(), "sex".getBytes(), Constant.SEXS[RandomUtil.getRandom(0, Constant.SEXS.length)].getBytes());
				p.addColumn("base_info".getBytes(), "bathday".getBytes(), Constant.getBathday().getBytes());
				p.addColumn("study".getBytes(), "school".getBytes(), Constant.SCHOOL[RandomUtil.getRandom(0, Constant.SCHOOL.length)].getBytes());
				p.addColumn("study".getBytes(), "xueli".getBytes(), Constant.Educations[RandomUtil.getRandom(0, Constant.Educations.length)].getBytes());
				p.addColumn("work".getBytes(), "compliy".getBytes(), Constant.COMPLIY[RandomUtil.getRandom(0, Constant.COMPLIY.length)].getBytes());
				puts.add(p);
				Thread.sleep(10);
				
				if(i%1000==0){
					tab.put(puts);
					puts.clear();
					System.out.println(puts.size());
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				tab.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static int query(Connection con,Table tab){
		int count = 0;
		try{
			tab = con.getTable(TableName.valueOf("dengbin_test_table"));
			Scan scan = new Scan();
			

			ResultScanner results = tab.getScanner(scan);
			for (Result rowResult : results) {
				
				for(Cell cell :rowResult.rawCells()){
					System.out.print(new String(CellUtil.cloneFamily(cell))+"---");
					System.out.print(new String(CellUtil.cloneQualifier(cell))+"---");
					System.out.println(new String(CellUtil.cloneValue(cell)));
				}
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				tab.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	
	
	
	
//	  public void getRowCount(RpcController controller,Connection con,Table tab) {
//			Scan scan = new Scan();
//			scan.setFilter(new FirstKeyOnlyFilter());
////			ExampleProtos.CountResponse response = null;
//			InternalScanner scanner = null;
//			try {
//				tab.coprocessorService(ExampleProtos.RowCountService.class, startKey, endKey, callable)
//			scanner = env.getRegion().getScanner(scan);
//			List<Cell> results = new ArrayList<Cell>();
//			boolean hasMore = false;
//			byte[] lastRow = null;
//			long count = 0;
//			do {
//			hasMore = scanner.next(results);
//			for (Cell kv : results) {
//			byte[] currentRow = CellUtil.cloneRow(kv);
//			if (lastRow == null || !Bytes.equals(lastRow, currentRow)) {
//			lastRow = currentRow;
//			count++;
//			}
//			}
//			results.clear();
//			} while (hasMore);
//			
//			response = ExampleProtos.CountResponse.newBuilder()
//			.setCount(count).build();
//			} catch (IOException ioe) {
//			ResponseConverter.setControllerException(controller, ioe);
//			} finally {
//			if (scanner != null) {
//			try {
//			scanner.close();
//			} catch (IOException ignored) {}
//			}
//			}
//			done.run(response);
//			}
}
