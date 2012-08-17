package org.secmem.remoteroid.lib.api;

public class API {
	/**
	 * The address of your application URL, with trailing "/api" string which indicates 
	 * your base Remoteroid API's path.<br/>
	 * Replace "remoteroid-server" with your own application id.
	 */
	public static final String BASE_URL = "http://remoteroid-server.appspot.com/apis";
	
	public static class Account{
		public static final int ADD_ACCOUNT = 0x001;
		public static final int LOGIN = 0x002;
		public static final int DELETE_ACCOUNT = 0x003;
	}
	
	public static class Device{
		public static final int ADD_DEVICE = 0x101;
		public static final int UPDATE_DEVICE_INFO = 0x102;
		public static final int DELETE_DEVICE = 0x103;
		public static final int DELETE_ALL_USER_DEVICE = 0x104;
	}
	
}
