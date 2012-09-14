package org.secmem.remoteroid.server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import org.secmem.remoteroid.lib.net.ScreenPacket;


public class ScreenReceiverThread extends Thread{

	private ScreenStateListener listener;
	private Socket socket;
	
	public ScreenReceiverThread(ScreenStateListener listener){
		if(listener==null)
			throw new IllegalArgumentException("Listener cannot be null");
		this.listener = listener;
	}
	
	public ScreenReceiverThread setSocket(Socket socket){
		this.socket = socket;
		return this;
	}
	
	@Override
	public void run() {
		if(socket==null){
			throw new IllegalStateException("You should assign a socket!");
		}
		try{
			// Start listening client connection
			System.out.println("Waiting for connection..");
			
			// Get inputstream of connected socket
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			
			// Start listening data from client
			while(true){
				ScreenPacket data = (ScreenPacket)inStream.readObject();
				listener.onReceiveImageData(data.getImageBytes());
			}
		}catch(IOException e){
			e.printStackTrace();
			listener.onScreenSocketLost();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public interface ScreenStateListener{
		public void onReceiveImageData(byte[] image);
		public void onScreenSocketLost();
	}
	
}


