package org.secmem.remoteroid.lib;

public class Packet {
	private byte[] image;
	
	public Packet(){
		
	}
	
	public void setImageBytes(byte[] imageData){
		this.image = imageData;
	}
	
	public byte[] getImageBytes(){
		return this.image;
	}
}
