package org.secmem.remoteroid.lib.net;

import java.util.HashMap;

import com.google.gson.Gson;

public class CommandPacket {
	public static final int SOCKET_PORT = 55001;
	
	public static final int CMD_INIT_CONNECTION = 0;
	
	private static final String KEY_COMMAND = "command";
	private static final String KEY_SCREEN_WIDTH = "width";
	private static final String KEY_SCREEN_HEIGHT = "height";
	
	private HashMap<String, String> d;
	
	private CommandPacket(){
		
	}
	
	public CommandPacket setCommand(int command){
		d.put(KEY_COMMAND, new Integer(command).toString());
		return this;
	}
	
	public CommandPacket addExtra(String key, int data){
		d.put(key, new Integer(data).toString());
		return this;
	}
	
	public int getCommand(){
		String command = d.get(KEY_COMMAND);
		if(command==null)
			throw new IllegalStateException("Command has not been set.");
		int commandAsInt;
		try{
			commandAsInt = Integer.parseInt(command);
		}catch(NumberFormatException e){
			e.printStackTrace();
			throw new IllegalStateException("Can't recognize command.");
		}
		return commandAsInt;
	}
	
	/**
	 * Get an integer type extra value.
	 * @param key a key for data
	 * @return a data
	 */
	public int getIntExtra(String key){
		String value = d.get(key);
		if(value==null)
			throw new IllegalStateException("Data has not been set.");
		int commandAsInt;
		try{
			commandAsInt = Integer.parseInt(value);
		}catch(NumberFormatException e){
			e.printStackTrace();
			throw new IllegalStateException("Can't recognize data as integer.");
		}
		return commandAsInt;
	}
	
	public static class Builder{
		CommandPacket sendInitialData(int width, int height){
			return new CommandPacket().setCommand(CMD_INIT_CONNECTION)
					.addExtra(KEY_SCREEN_WIDTH, width)
					.addExtra(KEY_SCREEN_HEIGHT, height);
		}
	}
	
	@Override
	public String toString(){
		return new Gson().toJson(this);
	}

}
