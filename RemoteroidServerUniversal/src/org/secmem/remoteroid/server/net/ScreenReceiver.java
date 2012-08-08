package org.secmem.remoteroid.server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ScreenReceiver {
	
	private static final int PORT = 55000;
	
	public void startListeningClientConnection(ClientAcceptListener listener){
		new AcceptClientThread(listener).start();
	}
	
	private class AcceptClientThread extends Thread{

		private ClientAcceptListener listener;
		
		public AcceptClientThread(ClientAcceptListener listener){
			if(listener==null)
				throw new IllegalArgumentException("Listener cannot be null");
			this.listener = listener;
		}
		
		@Override
		public void run() {
			try{
				// Start listening client connection
				Socket clientSocket = new ServerSocket(PORT).accept();
				
				// Get inputstream of connected socket
				ObjectInputStream inStream = new ObjectInputStream(clientSocket.getInputStream());
				
				// Connected!
				listener.onClientConnected(clientSocket.getInetAddress().getHostAddress());
				
				// Start listening data from client
				while(true){
					Object data = inStream.readObject();
					
				}
			}catch(IOException e){
				e.printStackTrace();
				listener.onInterrupt();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public interface ClientAcceptListener{
		public void onClientConnected(String clientIpAddress);
		public void onInterrupt();
	}
}
