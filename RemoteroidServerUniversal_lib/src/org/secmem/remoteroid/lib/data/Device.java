package org.secmem.remoteroid.lib.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.secmem.remoteroid.lib.util.DeviceUUIDGenerator;
import org.secmem.remoteroid.lib.util.Obfuscator;
import org.secmem.remoteroid.lib.util.SHAObfuscator;

/**
 * Contains data regarding each device.
 * @author Taeho Kim
 *
 */
public class Device{

	/**
	 * Device owner's account information
	 */
	private Account ownerAccount;
	
	/**
	 * Device's nickname to be displayed on list
	 */
	private String nickname;
	
	/**
	 * GCM registration key for device
	 */
	private String registrationKey;
	
	/**
	 * Device's UUID in each account
	 */
	private String deviceUUID;
	
	/**
	 * Default constructor for <code>Device</code>
	 */
	public Device(){
		
	}

	/**
	 * Returns owner's account.
	 * @return owner's account information
	 */
	public Account getOwnerAccount() {
		return ownerAccount;
	}

	/**
	 * Set owner's account information
	 * @param ownerAccount owner's account information
	 */
	public void setOwnerAccount(Account ownerAccount) {
		this.ownerAccount = ownerAccount;
	}

	/**
	 * Get device's nickname
	 * @return a nickname of this device
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Set device's nickname
	 * @param nickname a nickname
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * Get device's GCM registration key
	 * @return a GCM registration key
	 */
	public String getRegistrationKey() {
		return registrationKey;
	}

	/**
	 * Set device's GCM registration key
	 * @param registrationKey a GCM registration key
	 */
	public void setRegistrationKey(String registrationKey) {
		this.registrationKey = registrationKey;
	}

	/**
	 * Get device's Obfuscated UUID
	 * @return an UUID
	 */
	public String getUUID() {
		return deviceUUID;
	}
	
	private void setDeviceUUID(String uuid){
		this.deviceUUID = uuid;
	}

	/**
	 * Set device's UUID. Given raw UUID will be obfuscated.
	 * @param deviceUUID an UUID (device's Wi-Fi mac address by typical)
	 */
	public void setDeviceUUID(DeviceUUIDGenerator generator) {
		Obfuscator obfuscator = new SHAObfuscator();
		this.deviceUUID = obfuscator.generate(generator.generate());
	}
	
	public static Device fromJson(String jsonString) throws JSONException{
		JSONObject json = new JSONObject(jsonString);
		Account ownerAccount = null;
		try{
			ownerAccount = Account.fromJson(json.getJSONObject("ownerAccount"));
		}catch(JSONException e){
		}
		Device device = new Device();
		device.setOwnerAccount(ownerAccount);
		device.setNickname(json.getString("nickname"));
		device.setRegistrationKey(json.getString("registrationKey"));
		device.setDeviceUUID(json.getString("deviceUUID"));
		return device;
	}
	
	public static Device fromJson(JSONObject json) throws JSONException{
		Account ownerAccount = null;
		try{
			ownerAccount = Account.fromJson(json.getJSONObject("ownerAccount"));
		}catch(JSONException e){
		}
		Device device = new Device();
		device.setOwnerAccount(ownerAccount);
		device.setNickname(json.getString("nickname"));
		device.setRegistrationKey(json.getString("registrationKey"));
		device.setDeviceUUID(json.getString("deviceUUID"));
		return device;
	}

}
