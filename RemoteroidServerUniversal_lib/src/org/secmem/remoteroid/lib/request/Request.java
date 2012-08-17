package org.secmem.remoteroid.lib.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.secmem.remoteroid.lib.api.API;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.data.Device;

import com.google.gson.Gson;

public class Request {
	private static final int TIMEOUT_IN_MILLIS = 5000;
	public enum RequestType{GET, POST};
	
	private RequestType requestType;
	private String requestPath;
	private String payload;
	
	private Request(){
	}
	
	public Request setRequest(String path, RequestType type){
		if(requestPath==null || requestPath.equals("")){
			throw new IllegalArgumentException("Request path cannot be null.");
		}
		if(requestType==null){
			throw new IllegalStateException("You must set request type. (GET/POST)");
		}
		
		this.requestPath = path;
		this.requestType = type;
		return this;
	}
	
	public Request attachPayload(Account account){
		this.payload = new Gson().toJson(account);
		return this;
	}
	
	public Request attachPayload(Device device){
		this.payload = new Gson().toJson(device);
		return this;
	}
	
	public String getResponse() throws MalformedURLException, IOException{
		if(requestType.equals(RequestType.GET)){
			return readRawData(openConnectionGET(requestPath));
		}else{
			return readRawData(openConnectionPOST(requestPath, payload));
		}
	}
	
	private HttpEntity openConnectionGET(String path) throws IOException, MalformedURLException{
		if(path==null){
			throw new IllegalArgumentException("Path cannot be null.");
		}
		URL url = new URL(API.BASE_URL+path);
		
		// Set connection timeout 
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT_IN_MILLIS);
		
		HttpClient client = new DefaultHttpClient(params);
		HttpGet httpGet = new HttpGet(url.toString());
		HttpResponse resp = client.execute(httpGet);
		
		HttpEntity entity = resp.getEntity();
		return entity;
	}
	
	private HttpEntity openConnectionPOST(String path, String payload) throws IOException{
		URL url = new URL(path!=null ? API.BASE_URL+path : API.BASE_URL);
		// Set connection timeout 
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT_IN_MILLIS);
		
		HttpClient client = new DefaultHttpClient(params);
		HttpPost httpPost = new HttpPost(url.toString());
		
		StringEntity entity = new StringEntity(payload);
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		HttpResponse resp = client.execute(httpPost);
		
		return resp.getEntity();
	}
	
	private String readRawData(HttpEntity entity) throws IOException{
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		
		while((line=reader.readLine())!=null){
			builder.append(line);
		}
		return builder.toString();
	}
	
	public static class RequestFactory {
		private RequestFactory(){
			
		}
		
		public static Request getRequest(int request){
			switch(request){
			case API.Account.ADD_ACCOUNT:
				return new Request().setRequest("/account/register", RequestType.POST);
			case API.Account.LOGIN:
				return new Request().setRequest("/account/login", RequestType.POST);
			case API.Account.DELETE_ACCOUNT:
				return new Request().setRequest("/account/unregister", RequestType.POST);
			case API.Device.ADD_DEVICE:
				return new Request().setRequest("/device/register", RequestType.POST);
			case API.Device.UPDATE_DEVICE_INFO:
				return new Request().setRequest("/device/update", RequestType.POST);
			case API.Device.DELETE_DEVICE:
				return new Request().setRequest("/device/delete", RequestType.POST);
			case API.Device.DELETE_ALL_USER_DEVICE:
				return new Request().setRequest("/device/deleteAll", RequestType.POST);
			default:
				throw new IllegalArgumentException();
			}
		}
	}
	
	
}
