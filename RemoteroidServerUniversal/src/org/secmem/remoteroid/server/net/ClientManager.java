package org.secmem.remoteroid.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import org.secmem.remoteroid.lib.net.CommandPacket;
import org.secmem.remoteroid.lib.net.ImagePacket;
import org.secmem.remoteroid.server.net.CommandReceiverThread.CommandStateListener;
import org.secmem.remoteroid.server.net.ScreenReceiverThread.ScreenStateListener;

public class ClientManager implements CommandStateListener, ScreenStateListener{
	private static final Logger log = Logger.getLogger("ClientManager");
	
	public enum ConnectionState{NONE, WAITING_CONNECTION, CONNECTED};
	
	private Socket commandSocket;
	private Socket screenSocket;
	
	private ConnectionState connState;
	
	private ClientStateListener listener;
	
	public ClientManager(ClientStateListener listener){
		this.listener = listener;
	}
	
	public void setConnectionState(ConnectionState state){
		this.connState = state;
	}
	
	public ConnectionState getConnectionState(){
		return this.connState;
	}
	
	public void waitClientConnection(){
		if(listener==null){
			throw new IllegalStateException("Client state listener cannot be null!");
		}
		
		new Thread(new Runnable(){
			@Override
			public void run(){
				try{
				log.info("Waiting client...(command)");
				commandSocket = new ServerSocket(CommandPacket.SOCKET_PORT).accept();
				
				log.info("Waiting client...(screen)");
				screenSocket = new ServerSocket(ImagePacket.SOCKET_PORT).accept();
				
				new CommandReceiverThread(ClientManager.this).setSocket(commandSocket).run();
				new ScreenReceiverThread(ClientManager.this).setSocket(screenSocket).run();
				}catch(IOException e){
					e.printStackTrace();
					listener.onDisconnected();
				}
			}
		}).start();
			
	}
	
	public void disconnect(){
		log.info("Disconnect requested. cleaning up connections..");
		// Close all connections
		cleanup();
	}
	
	private synchronized void cleanup(){
		try{
			if(screenSocket!=null && screenSocket.isConnected()){
				screenSocket.close();
				screenSocket = null;
			}
			
			if(commandSocket!=null && commandSocket.isConnected()){
				commandSocket.close();
				commandSocket = null;
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onReceiveImageData(byte[] image) {
		listener.onReceiveScreen(image);
	}

	@Override
	public void onScreenSocketLost() {
		cleanup();
	}

	@Override
	public void onReceiveCommand(CommandPacket packet) {
		listener.onReceiveCommand(packet);
	}

	@Override
	public void onCommandSocketLost() {
		cleanup();
	}
	
	public interface ClientStateListener{
		public void onConnected(String ipAddress);
		public void onReceiveCommand(CommandPacket packet);
		public void onReceiveScreen(byte[] image);
		public void onDisconnected();
	}

}
