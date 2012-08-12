package org.secmem.remoteroid.server.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.swt.graphics.ImageData;
import org.secmem.remoteroid.lib.Packet;

public class ScreenReceiver {
	
	private static final int PORT = 20000;
	
	public void startReceivingImage(ImageReceiveListener listener){
		new ReceiveImageThread(listener).start();
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
				
				Socket clientSocket = new ServerSocket(4010).accept();
				
				// Get inputstream of connected socket
				ObjectInputStream inStream = new ObjectInputStream(clientSocket.getInputStream());
				
				// Connected!
				listener.onClientConnected(clientSocket.getInetAddress().getHostAddress());
				
				// Start listening data from client
				while(true){
					Packet data = (Packet)inStream.readObject();
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
