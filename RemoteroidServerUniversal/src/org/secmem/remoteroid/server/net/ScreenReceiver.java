package org.secmem.remoteroid.server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.secmem.remoteroid.lib.net.ImagePacket;

public class ScreenReceiver {
	
	private Socket socket;
	
	public void startReceivingImage(ImageReceiveListener listener){
		new ReceiveImageThread(listener).start();
	}
	
	public void stopReceivingImage(){
		if(socket!=null){
			try{
				socket.close();
			}catch(IOException e){
			
			}
		}
	}
	
	private class ReceiveImageThread extends Thread{

		private ImageReceiveListener listener;
		
		public ReceiveImageThread(ImageReceiveListener listener){
			if(listener==null)
				throw new IllegalArgumentException("Listener cannot be null");
			this.listener = listener;
		}
		
		@Override
		public void run() {
			try{
				// Start listening client connection
				System.out.println("Waiting for connection..");
				
				socket = new ServerSocket(ImagePacket.SOCKET_PORT).accept();
				
				// Get inputstream of connected socket
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
				
				// Connected!
				listener.onClientConnected(socket.getInetAddress().getHostAddress());
				
				// Start listening data from client
				while(true){
					ImagePacket data = (ImagePacket)inStream.readObject();
					listener.onReceiveImageData(data.getImageBytes());
				}
			}catch(IOException e){
				e.printStackTrace();
				listener.onInterrupt();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public interface ImageReceiveListener{
		public void onClientConnected(String clientIpAddress);
		public void onReceiveImageData(byte[] image);
		public void onInterrupt();
	}
}
