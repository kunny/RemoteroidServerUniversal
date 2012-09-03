package org.secmem.remoteroid.lib.request;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.secmem.remoteroid.lib.api.Codes;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.data.Device;
import org.secmem.remoteroid.lib.data.WakeupMessage;

/**
 * Represents 'Response' for client's request to Remoteroid server.
 * @author Taeho Kim
 *
 */
public class Response {
	private static final Logger log = Logger.getLogger("ResponseParser");
	
	private int result = Codes.Result.FAILED;
	private int errorCode = Codes.NONE;
	private String payload;
	
	/**
	 * Default constructor for Response.
	 */
	public Response(){
		
	}
	
	/**
	 * Parses a response into a {@link Response} format.
	 * @param responseInJson a response
	 * @throws ParseException if the response cannot be parsed into a valid format
	 */
	public void parse(String responseInJson) throws ParseException{
		JSONObject obj;
		try{
			obj = new JSONObject(responseInJson);
			result = obj.getInt("result");
		}catch(JSONException e){
			throw new ParseException("Cannot find result code. response="+responseInJson);
		}
		
		try{
			if(obj!=null){
				// An ErrorCode may not exists.
				errorCode = obj.getInt("errorCode");
			}
		}catch(JSONException e){
		}
		
		try{
			if(obj!=null){
				// A Payload may not exists.
				payload = obj.getString("data");
			}
		}catch(JSONException e){
		}
	}
	
	/**
	 * Returns the request has succeed or not.
	 * @return true if request succeed, false otherwise
	 */
	public boolean isSucceed(){
		return result==Codes.Result.OK ? true : false;
	}
	
	/**
	 * Get an error code if previous request has failed.
	 * @return an Error code if previous request has failed, {@link org.secmem.remoteroid.lib.api.Codes#NONE Codes.NONE} otherwise.
	 */
	public int getErrorCode(){
		return errorCode;
	}
	
	/**
	 * Returns a payload attached to response.
	 * @return a payload in String if exists
	 * @throws IllegalStateException If there are no payload exists in the response
	 */
	private String getPayload(){
		if(payload!=null){
			return payload;
		}else{
			throw new IllegalStateException("There are no payload exists in this response.");
		}
	}
	
	/**
	 * Parse payload as an {@link Account} type.
	 * @return the payload in {@link Account} type if possible, null if the payload is not an Account type.
	 */
	public Account getPayloadAsAccount(){
		String payload = getPayload();
		try{
			Account account = Account.fromJson(payload);
			return account;
		}catch(JSONException e){
			e.printStackTrace();
			log.severe("Cannot parse payload as Account type. (payload="+payload+")");
			return null;
		}
	}
	
	/**
	 * Parse payload as a {@link Device} type.
	 * @return the payload in {@link Device} type if possible, null if the payload is not a Device type.
	 */
	public Device getPayloadAsDevice(){
		String payload = getPayload();
		try{
			Device device = Device.fromJson(payload);
			return device;
		}catch(JSONException e){
			e.printStackTrace();
			log.severe("Cannot parse payload as Device type. (payload="+payload+")");
			return null;
		}
	}
	
	/**
	 * Parse payload as a List of {@link Device} type.
	 * @return the payload in {@link Device} type if possible, null if the payload is not a Device type.
	 */
	public ArrayList<Device> getPayloadAsDeviceList(){
		String payload = getPayload();
		try{
			ArrayList<Device> list = new ArrayList<Device>();
			
			JSONArray arr = new JSONArray(payload);
			int arrSize = arr.length();
			
			for(int i=0; i<arrSize; ++i){
				Device device = Device.fromJson(arr.getJSONObject(i));
				list.add(device);
			}
			
			return list;
		}catch(JSONException e){
			e.printStackTrace();
			log.severe("Cannot parse payload as Device list type. (payload="+payload+")");
			return null;
		}
	}
	
	/**
	 * Parse payload as an {@link WakeupMessage} type.
	 * @return the payload in {@link WakeupMessage} type if possible, null if the payload is not a WakeupMessage type.
	 */
	public WakeupMessage getPayloadAsWakeupMessage(){
		String payload = getPayload();
		try{
			WakeupMessage msg = WakeupMessage.fromJson(payload);
			return msg;
		}catch(JSONException e){
			e.printStackTrace();
			log.severe("Cannot parse payload as WakeupMessage type. (payload="+payload+")");
			return null;
		}
	}
}
