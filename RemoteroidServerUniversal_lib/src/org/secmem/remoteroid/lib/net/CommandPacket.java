package org.secmem.remoteroid.lib.net;

import java.util.HashMap;

import com.google.gson.Gson;

public class CommandPacket {
	/**
	 * Socket port number for command.
	 */
	public static final int SOCKET_PORT = 55001;

	private static final String KEY_COMMAND = "command";

	
	public static class Command{

		/**
		 * Command for 'Request device information'.<br/>
		 * Sent from <b>Server</b>.
		 */
		public static final int REQUEST_DEVICE_INFO = 0x001;
		
		/**
		 * Command for 'Send key up message'.<br/>
		 * Sent from <b>Server</b>.
		 * @see CommandPacket.Extra#KEYCODE
		 */
		public static final int KEY_UP = 0x002;
		
		/**
		 * Command for 'Send key down message'.<br/>
		 * Sent from <b>Server</b>.
		 * @see CommandPacket.Extra#KEYCODE
		 */
		public static final int KEY_DOWN = 0x003;
		
		/**
		 * Command for 'Send touch down message'.<br/>
		 * Sent from <b>Server</b>.
		 * @see CommandPacket.Extra#KEY_TOUCH_X
		 * @see CommandPacket.Extra#KEY_TOUCH_Y
		 */
		public static final int TOUCH_DOWN = 0x003;
		
		/**
		 * Command for 'Send touch up message'.<br/>
		 * Sent from <b>Server</b>.
		 * @see CommandPacket.Extra#KEY_TOUCH_X
		 * @see CommandPacket.Extra#KEY_TOUCH_Y
		 */
		public static final int TOUCH_UP = 0x004;
		
		/**
		 * Command for 'Device information'.
		 * Sent from <b>Client</b>.
		 * @see CommandPacket.Extra#KEY_SCREEN_WIDTH
		 * @see CommandPacket.Extra#KEY_SCREEN_HEIGHT
		 */
		public static final int DEVICE_INFO = 0x100;
		
		/**
		 * Command for 'Show/Handle notification'.<br/>
		 * Sent from <b>Client</b>.
		 * @see CommandPacket.Extra#KEY_NOTIFICATION_DATA1
		 * @see CommandPacket.Extra#KEY_NOTIFICATION_DATA2
		 * @see CommandPacket.Extra#KEY_NOTIFICATION_DATA3
		 */
		public static final int NOTIFICATION = 0x101;
		
		/**
		 * Command for 'Disconnect connection'.<br/>
		 * Sent from both <b>Server or Client</b>.
		 */
		public static final int DISCONNECT = 0x200;
	}
	
	public static class Extra{
		/**
		 * Type : int
		 */
		public static final String KEY_SCREEN_WIDTH = "width";
		/**
		 * Type : int
		 */
		public static final String KEY_SCREEN_HEIGHT = "height";

		/**
		 * Type ; int
		 */
		public static final String KEY_KEYCODE = "keycode";
		/**
		 * Type : int
		 */
		public static final String KEY_TOUCH_X = "touch_x";
		/**
		 * Type : int
		 */
		public static final String KEY_TOUCH_Y = "touch_y";
		
		/**
		 * Type : int
		 * @see #NOTIFICATION_TYPE_GENERAL
		 * @see #NOTIFICATION_TYPE_KAKAOTALK
		 * @see #NOTIFICATION_TYPE_PHONE
		 * @see #NOTIFICATION_TYPE_SMS
		 */
		public static final String KEY_NOTIFICATION_TYPE = "noti_type";
		public static final String KEY_NOTIFICATION_DATA1 = "noti_d1";
		public static final String KEY_NOTIFICATION_DATA2 = "noti_d2";
		public static final String KEY_NOTIFICATION_DATA3 = "noti_d3";

		public static final String NOTIFICATION_TYPE_PHONE = "phone";
		public static final String NOTIFICATION_TYPE_SMS = "sms";
		public static final String NOTIFICATION_TYPE_KAKAOTALK = "kakao";
		public static final String NOTIFICATION_TYPE_GENERAL = "gen";
	}
	
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
	
	public CommandPacket addExtra(String key, String data){
		d.put(key, data);
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
			throw new IllegalStateException("Data has not been set for key "+key);
		int commandAsInt;
		try{
			commandAsInt = Integer.parseInt(value);
		}catch(NumberFormatException e){
			e.printStackTrace();
			throw new IllegalStateException("Can't recognize data as integer.");
		}
		return commandAsInt;
	}
	
	public String getStringExtra(String key){
		String value = d.get(key);
		if(value==null){
			throw new IllegalStateException("Data has not been set for key "+key);
		}
		return value;
	}
	
	public static class CommandFactory{
		
		public static CommandPacket requestDeviceInfo(){
			return new CommandPacket().setCommand(Command.REQUEST_DEVICE_INFO);
		}
		
		public static CommandPacket sendDeviceInfo(int width, int height){
			return new CommandPacket().setCommand(Command.DEVICE_INFO)
					.addExtra(Extra.KEY_SCREEN_WIDTH, width)
					.addExtra(Extra.KEY_SCREEN_HEIGHT, height);
		}
		
		public static CommandPacket keyDown(int keyCode){
			return new CommandPacket().setCommand(Command.KEY_DOWN)
					.addExtra(Extra.KEY_KEYCODE, keyCode);
		}
		
		public static CommandPacket keyUp(int keyCode){
			return new CommandPacket().setCommand(Command.KEY_UP)
					.addExtra(Extra.KEY_KEYCODE, keyCode);
		}
		
		public static CommandPacket touchDown(int x, int y){
			return new CommandPacket().setCommand(Command.TOUCH_DOWN)
					.addExtra(Extra.KEY_TOUCH_X, x).addExtra(Extra.KEY_TOUCH_Y, y);
		}
		
		public static CommandPacket touchUp(int x, int y){
			return new CommandPacket().setCommand(Command.TOUCH_UP)
					.addExtra(Extra.KEY_TOUCH_X, x).addExtra(Extra.KEY_TOUCH_Y, y);
		}
		
		public static CommandPacket notification(int notificationType, String... data){
			CommandPacket packet = new CommandPacket();
			packet.setCommand(Command.NOTIFICATION);
			packet.addExtra(Extra.KEY_NOTIFICATION_TYPE, notificationType);
			
			if(data==null)
				throw new IllegalArgumentException();
			
			int dataLen = data.length;
			
			switch(dataLen){
			case 1:
				packet.addExtra(Extra.KEY_NOTIFICATION_DATA1, data[0]);
				break;
			
			case 2:
				packet.addExtra(Extra.KEY_NOTIFICATION_DATA1, data[0]);
				packet.addExtra(Extra.KEY_NOTIFICATION_DATA2, data[1]);
				break;
			
			case 3:
				packet.addExtra(Extra.KEY_NOTIFICATION_DATA1, data[0]);
				packet.addExtra(Extra.KEY_NOTIFICATION_DATA2, data[1]);
				packet.addExtra(Extra.KEY_NOTIFICATION_DATA3, data[2]);
				break;
			}
			
			return packet;
		}
		
		public static CommandPacket disconnect(){
			return new CommandPacket().setCommand(Command.DISCONNECT);
		}
	}
	
	@Override
	public String toString(){
		return new Gson().toJson(this);
	}
	
	public static CommandPacket fromString(String jsonString){
		return new Gson().fromJson(jsonString, CommandPacket.class);
	}

}
