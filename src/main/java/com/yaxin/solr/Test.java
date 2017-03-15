package com.yaxin.solr;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.yarn.util.timeline.TimelineUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest.AddField;
import org.apache.solr.client.solrj.request.schema.SchemaRequest.Fields;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.DefaultSolrParams;
import org.apache.solr.common.params.GroupParams;
import org.apache.solr.common.params.SolrParams;


public class Test {

	public static void main(String[] args) {
		try{
			//String urlString = "http://192.168.1.142:8080/solr/solr";
			//SolrClient client = new HttpSolrClient.Builder(urlString).build();	
			CloudSolrClient client = new CloudSolrClient.Builder().withZkHost("192.168.1.166:2181").build();
			client.setDefaultCollection("solr");
			//group(client);
			//getFieldFacet(client, "_dataType");
			//delete(client);
			//delete(client);
			
			write(client);
			//query(client);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void query(SolrClient client){
		try{
			SolrQuery query = new SolrQuery();
			query.setRequestHandler("/select");
			query.set("q", "*:*");
			QueryResponse res = client.query(query);
			SolrDocumentList docs = res.getResults();
			for(SolrDocument doc : docs){
				System.out.println(doc.get("id").getClass());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public static void group(SolrClient client){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date begin = cal.getTime();
		
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		Date end = cal.getTime();
		SolrQuery query = new SolrQuery();
		query.setRequestHandler("/select");
		query.set("q", "_domain:OC_4.1 AND instanceId:1001 AND metricId:icmpDelayTime");
		query.set("fq", "_dateTime:["+sdf.format(begin)+" TO "+sdf.format(end)+"]");
		
		query.setParam(GroupParams.GROUP, true);
		query.setParam(GroupParams.GROUP_FIELD, "metricValue");  
		query.setParam(GroupParams.GROUP_LIMIT, "0"); 
		query.setRows(1000);
		
		try{
			QueryResponse response = client.query(query);
			GroupResponse groupResponse = response.getGroupResponse(); 
			if(groupResponse != null) {  
			    List<GroupCommand> groupList = groupResponse.getValues();  
			    for(GroupCommand groupCommand : groupList) {  
			        List<Group> groups = groupCommand.getValues();  
			        for(Group group : groups) {  
			            System.out.println(group.getGroupValue() +"---"+ (int)group.getResult().getNumFound());  
			        }  
			    }  
			} 
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void AddField(SolrClient client){
		Map<String, Object> fieldAttributes = new HashMap<>();;
		
		
		fieldAttributes.put("name", "alarmContent");
		fieldAttributes.put("type", "text_general");
		fieldAttributes.put("indexed", false);
		fieldAttributes.put("stored", true);
		AddField add = new SchemaRequest.AddField(fieldAttributes);
		try {
			org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse res = add.process(client);
			client.commit();
			int code = res.getStatus();
			System.out.println(code);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	
	public static void queryField(SolrClient client){
		SolrParams q = new SolrQuery();
		Fields  fields = new SchemaRequest.Fields(null);
		try {
			org.apache.solr.client.solrj.response.schema.SchemaResponse.FieldsResponse res = fields.process(client);
			List<Map<String, Object>>  list = res.getFields();
			for(Map<String, Object> map : list){
				System.out.println(map.get("name"));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	
	public static void delete(SolrClient solr){
		try{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); 
			df.setTimeZone(TimeZone.getTimeZone("UTC"));  
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2016);
			cal.set(Calendar.MONTH, 11);
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date end = cal.getTime();
			cal.add(Calendar.YEAR, -10);
			Date begin = cal.getTime();
			System.out.println(df.format(begin)+" TO "+df.format(end));
			//solr.deleteByQuery("_dateTime:["+df.format(begin)+" TO "+df.format(end)+"]");
			solr.deleteByQuery("*:*");
			solr.commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void write(SolrClient solr){
		int i = 0;
		List<SolrInputDocument> list = new ArrayList<>(1000);
		while(i < 1000000000){
			Calendar cal = Calendar.getInstance();
			SolrInputDocument document = new SolrInputDocument();
			document.addField("dateTime", cal.getTime().getTime());
			document.addField("_domain", "OC_4.1");
			document.addField("resourceId", "CiscoSwitchNIC");
			document.addField("instanceName", "qzcore");
			document.addField("subInstanceName", "Gi0/2");
			document.addField("metricValue", "0.0");
			document.addField("_dataType", "DevicePerformanceKPI");
			document.addField("_sourceType", "oc4");
			document.addField("_host", "oc4_demo");
			document.addField("content", "{\"dateTime\":"+cal.getTime().getTime()+",\"resourceId\":\"CiscoSwitchNIC\",\"instanceId\":75501,\"metricId\":\"ifBandWidthUtil\",\"instanceName\":\"qzcore\",\"subInstanceName\":\"Gi0/2\",\"metricValue\":0,\"_dataType\":\"DevicePerformanceKPI\",\"subInstanceId\":75522}");
			document.addField("instanceId", "75501");
			document.addField("metricId", "ifBandWidthUtil");
			document.addField("_dateTime", cal.getTime());
			document.addField("_source", "UDP40000");
			document.addField("id", UUID.randomUUID().toString());
			document.addField("subInstanceId", "75522");
			
			
			
			SolrInputDocument doc2 = new SolrInputDocument();
			doc2.addField("_domain", "Oracle_11g");
			doc2.addField("ip", "192.168.1.210");
			doc2.addField("_source", "Oracle210_alert.log");
			doc2.addField("_dataType", "LogFile");
			doc2.addField("_sourceType", "base");
			doc2.addField("_host", "oracle_210");
			doc2.addField("content", "\n host_addr='192.168.1.210'>\n <txt>    nt OS err code: 0\n </txt>\n</msg>");
			doc2.addField("channelID", "Oracle210_alert.log");
			doc2.addField("_dateTime", cal.getTime());
			doc2.addField("id", UUID.randomUUID().toString());
			doc2.addField("subInstanceId", "75522");
			
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			list.add(document);
			list.add(doc2);
			i++;
			if(i%1000 == 0){
				System.out.println("commit");
				try{
					long b = System.currentTimeMillis();
					solr.add(list);
					solr.commit();
					System.out.println("commit 1000 to solr.. for time : " + (System.currentTimeMillis() - b));
				}catch(Exception e){
					e.printStackTrace();
				}
				list.clear();
			}
		}
	}
	
	
	public static void getFieldFacet(SolrClient solr, String field){
		String query = "_domain:OC_4.1";
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  
		df.setTimeZone(TimeZone.getTimeZone("UTC"));   
		try{
			SolrQuery sq = new SolrQuery();
			if(query != null && !"".equals(query))
				sq.set("q", query);
//			if(query.getDetailEndTime() != null && query.getDetailStartTime() != null)
//				sq.set("fq", "_dateTime:["+df.format(query.getDetailStartTime())+" TO "+df.format(covertLt(query.getDetailEndTime()))+"]");
			sq.setFacet(true);
			sq.addFacetField(field);
			sq.setFacetLimit(10);
			sq.setFacetMissing(false);
			sq.setFacetMinCount(1);
			QueryResponse res = solr.query(sq);
			if(res != null){
				List<FacetField> facets = res.getFacetFields();
				for(FacetField ff : facets){
					for(Count c : ff.getValues()){
						System.out.println(c.getName() + "--" + c.getCount());
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
