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
	
	public void setTargetIpAddress(String address){
		this.targetIpAddress = address;
	}
	
	public void setServerConnectionListener(ServerConnectionListener listener){
		this.connListener = listener;
	}
	
	public void setServerCommandListener(ServerCommandListener listener){
		this.commListener = listener;
	}
	
	public synchronized void connect(String ipAddress){
		this.targetIpAddress = ipAddress;
		new ServerConnectionThread(connListener).setTargetAddress(targetIpAddress).start();
	}
	
	public synchronized void teardown(){
		try{
			commandSocket.close();
			screenSocket.close();
		}catch(IOException e){
			
		}
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
		
		new Thread(new Runnable(){
			public void run(){
				try{
					screenOutStream.write(image);
				}catch(IOException e){
					e.printStackTrace();
					connListener.onScreenDisconnected();
				}
			}
		}).start();
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
	
	class ServerConnectionThread extends Thread{
		
		private ServerConnectionListener listener;
		private String targetIpAddress;
		
		public ServerConnectionThread(ServerConnectionListener listener){
			if(listener==null){
				throw new IllegalStateException("Listener cannot be null");
			}
			this.listener = listener;
		}
		
		public ServerConnectionThread setTargetAddress(String ipAddress){
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
			
			if(screenSocket!=null){
				try{
					screenSocket.close();
				}catch(IOException e){}
			}
			try{
				commandSocket = new Socket();
				commandSocket.connect(new InetSocketAddress(targetIpAddress, CommandPacket.SOCKET_PORT), DEFAULT_TIMEOUT);
				
				screenSocket = new Socket();
				screenSocket.connect(new InetSocketAddress(targetIpAddress, ScreenPacket.SOCKET_PORT), DEFAULT_TIMEOUT);
				
				// Get input/outputstream from command socket
				commandInStream = new ObjectInputStream(commandSocket.getInputStream());
				commandOutStream = new ObjectOutputStream(commandSocket.getOutputStream());
				
				// Get outputstream from screen socket
				screenOutStream = screenSocket.getOutputStream();
				
			}catch(IOException e){
				listener.onConnected();
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
		public void onConnected();
		public void onFailed();
		public void onScreenDisconnected();
	}
	
	public interface ServerCommandListener{
		public void onCommand(CommandPacket command);
		public void onDisconnected();
	}
	
}
