package org.secmem.remoteroid.server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.secmem.remoteroid.lib.net.CommandPacket;

import com.google.gson.Gson;

public class CommandReceiverThread extends Thread {
	private CommandStateListener listener;
	private Socket socket;
	
	public CommandReceiverThread(CommandStateListener listener){
		if(listener==null)
			throw new IllegalArgumentException("Listener cannot be null");
		this.listener = listener;
	}
	
	public CommandReceiverThread setSocket(Socket socket){
		this.socket = socket;
		return this;
	}
	
	public void run(){
		try{
			// Start listening client connection
			System.out.println("Waiting for connection..");
			
			Socket clientSocket = new ServerSocket(CommandPacket.SOCKET_PORT).accept();
			
			// Get inputstream of connected socket
			ObjectInputStream inStream = new ObjectInputStream(clientSocket.getInputStream());
			
			// Start listening data from client
			while(true){
				String rawdata = (String)inStream.readObject();
				CommandPacket packet = new Gson().fromJson(rawdata, CommandPacket.class);
				listener.onReceiveCommand(packet);
			}
		}catch(IOException e){
			e.printStackTrace();
			listener.onCommandSocketLost();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public interface CommandStateListener{
		public void onReceiveCommand(CommandPacket packet);
		public void onCommandSocketLost();
	}
}
