package org.secmem.remoteroid.lib.data;

public class WakeupMessage {

	private Device device;
	private String serverIpAddress;
	
	public WakeupMessage(){
		
	}
	
	public WakeupMessage(String ipAddress){
		this();
		this.serverIpAddress = ipAddress;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public String getServerIpAddress() {
		return serverIpAddress;
	}

	public void setServerIpAddress(String serverIpAddress) {
		this.serverIpAddress = serverIpAddress;
	}
	
}
