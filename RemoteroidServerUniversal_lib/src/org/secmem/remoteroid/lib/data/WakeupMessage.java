package org.secmem.remoteroid.lib.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contains data regarding Wake-up message(Remote connection) from server.
 * @author Taeho Kim
 *
 */
public class WakeupMessage {

	/**
	 * A device will be waked up for
	 */
	private Device device;
	
	/**
	 * Server's IP Address, where device should connect to
	 */
	private String serverIpAddress;
	
	/**
	 * Default constructor for <code>WakeupMessage</code>
	 */
	public WakeupMessage(){
		
	}
	
	/**
	 * Constructs <code>WakeupMessage</code> with given server's IP Address.
	 * @param ipAddress Server's IP Address where remote device should connect to
	 */
	public WakeupMessage(String ipAddress){
		this();
		this.serverIpAddress = ipAddress;
	}

	/**
	 * Get device information.
	 * @return a device information
	 */
	public Device getDevice() {
		return device;
	}

	/**
	 * Set message device to be waked up.
	 * @param device a device
	 */
	public void setDevice(Device device) {
		this.device = device;
	}

	/**
	 * Get server's IP Address.
	 * @return a server's IP address
	 */
	public String getServerIpAddress() {
		return serverIpAddress;
	}

	/**
	 * Set server's IP Address.
	 * @param serverIpAddress a server's IP Address
	 */
	public void setServerIpAddress(String serverIpAddress) {
		this.serverIpAddress = serverIpAddress;
	}
	
	public static WakeupMessage fromJson(String jsonString) throws JSONException{
		JSONObject json = new JSONObject(jsonString);
		Device device = Device.fromJson(json.getJSONObject("device"));
		WakeupMessage msg = new WakeupMessage();
		msg.setDevice(device);
		msg.setServerIpAddress(json.getString("serverIpAddress"));
		return msg;
	}
	
}
