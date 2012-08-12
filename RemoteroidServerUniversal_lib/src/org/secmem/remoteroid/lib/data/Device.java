package org.secmem.remoteroid.lib.data;

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
	 * Get device's UUID in owner's account
	 * @return an UUID
	 */
	public String getUUID() {
		return deviceUUID;
	}

	/**
	 * Set device's UUID
	 * @param deviceUUID an UUID
	 */
	public void setDeviceUUID(String deviceUUID) {
		this.deviceUUID = deviceUUID;
	}
	
}
