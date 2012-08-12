package org.secmem.remoteroid.lib.data;

public class Device{

	private Account ownerAccount;
	private String nickname;
	private String registrationKey;
	private String deviceUUID;
	
	public Device(){
		
	}

	public Account getOwnerAccount() {
		return ownerAccount;
	}

	public void setOwnerAccount(Account ownerAccount) {
		this.ownerAccount = ownerAccount;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRegistrationKey() {
		return registrationKey;
	}

	public void setRegistrationKey(String registrationKey) {
		this.registrationKey = registrationKey;
	}

	public String getUUID() {
		return deviceUUID;
	}

	public void setDeviceUUID(String deviceUUID) {
		this.deviceUUID = deviceUUID;
	}
	
}
