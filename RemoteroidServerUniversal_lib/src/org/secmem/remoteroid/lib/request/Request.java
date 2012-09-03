package org.secmem.remoteroid.lib.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
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
import org.secmem.remoteroid.lib.data.WakeupMessage;

import com.google.gson.Gson;

/**
 * Contains data needed to send request to the Remoteroid server.
 * @author Taeho Kim
 *
 */
public class Request {
	private static final int TIMEOUT_IN_MILLIS = 5000;
	public enum RequestType{GET, POST};
	
	private RequestType requestType;
	private String requestPath;
	private String payload;
	
	private Request(){
	}
	
	private Request setRequest(String path, RequestType type){
		if(path==null || path.equals("")){
			throw new IllegalArgumentException("Request path cannot be null.");
		}
		if(type==null){
			throw new IllegalStateException("You must set request type. (GET/POST)");
		}
		
		this.requestPath = path;
		this.requestType = type;
		return this;
	}
	
	/**
	 * Attach an {@link Account} type payload to this request.
	 * @param account an Account object
	 * @return this request
	 */
	private Request setPayload(Account account){
		this.payload = new Gson().toJson(account);
		return this;
	}
	
	/**
	 * Attach a {@link Device} type payload to this request.
	 * @param device a Device object
	 * @return this request
	 */
	private Request setPayload(Device device){
		this.payload = new Gson().toJson(device);
		return this;
	}
	
	/**
	 * Attach a {@link WakeupMessage} type payload to this request.
	 * @param msg a WakeupMessage object
	 * @return this request
	 */
	private Request setPayload(WakeupMessage msg){
		this.payload = new Gson().toJson(msg);
		return this;
	}
	
	/**
	 * Send a request to server
	 * @return a Response
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public Response sendRequest() throws MalformedURLException, IOException{
		Response response = new Response();
		try{
			String respInString;
			if(requestType.equals(RequestType.GET)){
				respInString = readRawData(openConnectionGET(requestPath));
			}else{
				respInString = readRawData(openConnectionPOST(requestPath, payload));
			}
			response.parse(respInString);
		}catch(ParseException e){
			e.printStackTrace();
		}
		// If cannot parse the response, just return default Response.
		// (Default result for Response is 'FAILED')
		return response;
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
	
	/**
	 * Provides construction of various {@link Request}.
	 * @author Taeho Kim
	 *
	 */
	public static class RequestBuilder {
		private Request req;
		
		private RequestBuilder(){
			req = new Request();
		}
		
		private void setRequest(String path, RequestType type){
			req.setRequest(path, type);
		}
		
		/**
		 * Generate a new request.
		 * @param request API code for request
		 * @return a Request object
		 * @see org.secmem.remoteroid.lib.api.API.Account Client APIs for Account
		 * @see org.secmem.remoteroid.lib.api.API.Device Client APIs for Device
		 */
		public static RequestBuilder getRequest(int request){
			RequestBuilder builder = new RequestBuilder();
			switch(request){
			case API.Account.ADD_ACCOUNT:
				builder.setRequest("/account/register", RequestType.POST);
				break;
			case API.Account.LOGIN:
				builder.setRequest("/account/login", RequestType.POST);
				break;
			case API.Account.DELETE_ACCOUNT:
				builder.setRequest("/account/unregister", RequestType.POST);
				break;
			case API.Device.ADD_DEVICE:
				builder.setRequest("/device/register", RequestType.POST);
				break;
			case API.Device.LIST_DEVICE:
				builder.setRequest("/device/list", RequestType.POST);
				break;
			case API.Device.UPDATE_DEVICE_INFO:
				builder.setRequest("/device/update", RequestType.POST);
				break;
			case API.Device.DELETE_DEVICE:
				builder.setRequest("/device/delete", RequestType.POST);
				break;
			case API.Device.DELETE_ALL_USER_DEVICE:
				builder.setRequest("/device/deleteAll", RequestType.POST);
				break;
			case API.Wakeup.WAKE_UP:
				builder.setRequest("/wakeup", RequestType.POST);
				break;
			default:
				throw new IllegalArgumentException();
			}
			return builder;
		}
		
		public RequestBuilder setPayload(Account account){
			if(req!=null){
				req.setPayload(account);
			}
			return this;
		}
		
		public RequestBuilder setPayload(Device device){
			if(req!=null){
				req.setPayload(device);
			}
			return this;
		}
		
		public RequestBuilder setPayload(WakeupMessage msg){
			if(req!=null){
				req.setPayload(msg);
			}
			return this;
		}
		
		public Request build(){
			return req;
		}
	}
	
	
}
