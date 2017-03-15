package com.yaxin.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class AutoSetDNS {

	public static void main(String[] args) {
		try{
			CloseableHttpClient httpClient = HttpClients.createDefault();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("email", "????");
			params.put("password", "????");
			Meta meta = httpPostRequest(httpClient, "https://www.dnspod.cn/Auth/Login", params);
			System.out.println(meta.getCookie());
			String surl = "https://www.dnspod.cn/Api/Record.Modify?"+meta.getCookie();
			Meta res = httpPostRequest(httpClient, surl, createParam());
			System.out.println(res.getResult());
			httpClient.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static Meta httpPostRequest(CloseableHttpClient httpClient, String url, Map<String, Object> params)
			throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost(url);
		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
		return getResult(httpClient, httpPost);
	}

	private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for (Map.Entry<String, Object> param : params.entrySet()) {
			pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
		}
		return pairs;
	}

	private static Meta getResult(CloseableHttpClient httpClient, HttpRequestBase request) {
		Meta meta = new Meta();
		try {
			CloseableHttpResponse response = httpClient.execute(request);
			Header[] h = response.getHeaders("Set-Cookie");
			header : for(Header x : h){
				String value = x.getValue();
				String[] items = value.split(";");
				for(String item : items){
					if(item.indexOf("_xsrf") >=0){
						meta.setCookie(item);
						break header;
					}
				}
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String result = EntityUtils.toString(entity);
				response.close();
				meta.setResult(result);
				return meta;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
		return meta;
	}
	
	public static Map<String, Object> createParam(){
		Map<String, Object> param = new HashMap<String, Object>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		param.put("id", 275239303);
		param.put("ttl", 600);
		param.put("value", "118.113.4.100");
		param.put("enabled", 1);
		param.put("status", "enabled");
		param.put("updated_on", df.format(new Date()));
		param.put("line", "默认");
		param.put("line_id", 0);
		param.put("type", "A");
		
		param.put("server_order", 3);
		param.put("domain", "itdb.tk");
		param.put("sub_domain", "www");
		param.put("record_type", "A");
		param.put("record_line", "默认");
		param.put("domain_id", 53503732);
		param.put("domain_grade", "DP_Free");
		param.put("record_id", 275239303);
		param.put("isWeight", false);
		param.put("format", "json");
		param.put("lang", "cn");
		param.put("error_on_empty", "no");
		param.put("use_session", "yes");
		return param;
	}

}

class Meta{
	
	private String cookie;
	private String result;
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
