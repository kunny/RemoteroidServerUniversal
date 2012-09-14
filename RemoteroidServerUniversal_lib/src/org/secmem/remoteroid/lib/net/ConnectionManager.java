package org.secmem.remoteroid.lib.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionManager {
	private static final int DEFAULT_TIMEOUT = 5000;
	
	private Socket commandSocket;
	private Socket screenSocket;
	
	private ObjectInputStream commandInStream;
	private ObjectOutputStream commandOutStream;
	private OutputStream screenOutStream;
	
	private ServerConnectionListener connListener;
	private ServerCommandListener commListener;
	
	private String targetIpAddress;
	
	public ConnectionManager(){
		
	}
	
	public void setServerConnectionListener(ServerConnectionListener listener){
		this.connListener = listener;
	}
	
	public void setServerCommandListener(ServerCommandListener listener){
		this.commListener = listener;
	}
	
	public synchronized void connectCommand(String ipAddress){
		this.targetIpAddress = ipAddress;
		new CommandConnectionThread(connListener).setTargetAddress(targetIpAddress).start();
	}
	
	public synchronized void connectScreen(String ipAddress){
		new ScreenConnectionThread(connListener).setTargetAddress(ipAddress).start();
	}
	
	public void disconnectScreen(){
		try{
			if(screenSocket!=null){
				screenSocket.close();
			}
		}catch(IOException e){
			
		}
	}
	
	public synchronized void disconnect(){
		try{
			commandSocket.close();
			screenSocket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public boolean isCommandConnected(){
		return (commandSocket!=null && commandSocket.isConnected());
	}
	
	public boolean isScreenConnected(){
		return (screenSocket!=null && screenSocket.isConnected());
	}
	
	public void listenCommandFromServer(){
		if(commandSocket==null || screenSocket==null || !commandSocket.isConnected() || !screenSocket.isConnected()){
			throw new IllegalStateException("Socket does not connected to server.");
		}
		new CommandReader(commListener).setInputStream(commandInStream).start();
	}
	
	public synchronized void sendScreen(final byte[] image){
		if(connListener==null){
			throw new IllegalStateException("Connection listener cannot be null.");
		}
		
		try{
			screenOutStream.write(image);
		}catch(IOException e){
			e.printStackTrace();
			disconnect();
		}
	}
	
	public void sendCommand(CommandPacket command){
		if(commListener==null){
			throw new IllegalStateException("Command listener cannot be null.");
		}
		
		try{
			commandOutStream.writeObject(command.toString());
		}catch(IOException e){
			e.printStackTrace();
			commListener.onDisconnected();
		}
	}
	
	class CommandConnectionThread extends Thread{
		
		private ServerConnectionListener listener;
		private String targetIpAddress;
		
		public CommandConnectionThread(ServerConnectionListener listener){
			if(listener==null){
				throw new IllegalStateException("Listener cannot be null");
			}
			this.listener = listener;
		}
		
		public CommandConnectionThread setTargetAddress(String ipAddress){
			this.targetIpAddress = ipAddress;
			return this;
		}
		
		@Override
		public void run(){
			if(targetIpAddress==null){
				throw new IllegalStateException("Target's IP address cannot be null");
			}
			
			if(commandSocket!=null){
				try {
					commandSocket.close();
				} catch (IOException e) {}
			}
			
			try{
				commandSocket = new Socket();
				commandSocket.connect(new InetSocketAddress(targetIpAddress, CommandPacket.SOCKET_PORT), DEFAULT_TIMEOUT);
				
				// Get input/outputstream from command socket
				commandInStream = new ObjectInputStream(commandSocket.getInputStream());
				commandOutStream = new ObjectOutputStream(commandSocket.getOutputStream());
				
				listener.onCommandConnected(targetIpAddress);
			}catch(IOException e){
				e.printStackTrace();
				listener.onFailed();
			}
		}
	}
	
	class ScreenConnectionThread extends Thread{
		
		private ServerConnectionListener listener;
		private String targetIpAddress;
		
		public ScreenConnectionThread(ServerConnectionListener listener){
			if(listener==null){
				throw new IllegalStateException("Listener cannot be null");
			}
			this.listener = listener;
		}
		
		public ScreenConnectionThread setTargetAddress(String ipAddress){
			this.targetIpAddress = ipAddress;
			return this;
		}
		
		@Override
		public void run(){
			if(targetIpAddress==null){
				throw new IllegalStateException("Target's IP address cannot be null");
			}
			
			if(screenSocket!=null){
				try{
					screenSocket.close();
				}catch(IOException e){}
			}
			try{
				screenSocket = new Socket();
				screenSocket.connect(new InetSocketAddress(targetIpAddress, ScreenPacket.SOCKET_PORT), DEFAULT_TIMEOUT);
				
				// Get outputstream from screen socket
				screenOutStream = screenSocket.getOutputStream();
				
				listener.onScreenConnected(targetIpAddress);
			}catch(IOException e){
				e.printStackTrace();
				listener.onFailed();
			}
		}
	}
	
	class CommandReader extends Thread{
		private ServerCommandListener listener;
		private ObjectInputStream commandInputStream;
		
		public CommandReader(ServerCommandListener listener){
			if(listener==null){
				throw new IllegalArgumentException("Listener cannot be null.");
			}
			this.listener = listener;
		}
		
		public CommandReader setInputStream(ObjectInputStream is){
			this.commandInputStream = is;
			return this;
		}
		
		@Override
		public void run(){
			if(commandInputStream==null){
				throw new IllegalStateException("CommandInputStream should not be null.");
			}
			try{
				CommandPacket packet = CommandPacket.fromString((String)commandInputStream.readObject());
				listener.onCommand(packet);
			}catch(IOException e){
				listener.onDisconnected();
			} catch (ClassNotFoundException e) {
				listener.onDisconnected();
			}
		}
	}
	
	public interface ServerConnectionListener{
		public void onCommandConnected(String ipAddress);
		public void onScreenConnected(String ipAddress);
		public void onFailed();
	}
	
	public interface ServerCommandListener{
		public void onCommand(CommandPacket command);
		public void onDisconnected();
	}
	
}
