package org.secmem.remoteroid.lib.net;

import java.io.Serializable;

public class ScreenPacket implements Serializable{
	
	private static final long serialVersionUID = 8768936635689630777L;

	public static final int SOCKET_PORT = 55000;
	
	private byte[] image;
	
	public ScreenPacket(){
		
	}
	
	public void setImageBytes(byte[] imageData){
		this.image = imageData;
	}
	
	public byte[] getImageBytes(){
		return this.image;
	}
}
