package org.secmem.remoteroid.server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.secmem.remoteroid.lib.net.CommandPacket;

import com.google.gson.Gson;

public class CommandReceiverThread extends Thread {
	private CommandReceiveListener listener;
	
	public CommandReceiverThread(CommandReceiveListener listener){
		if(listener==null)
			throw new IllegalArgumentException("Listener cannot be null");
		this.listener = listener;
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
			listener.onDisconnected();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public interface CommandReceiveListener{
		public void onReceiveCommand(CommandPacket packet);
		public void onDisconnected();
	}
}
