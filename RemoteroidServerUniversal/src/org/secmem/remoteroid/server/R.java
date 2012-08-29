package org.secmem.remoteroid.server;

import java.util.ResourceBundle;

public class R {
	private static ResourceBundle res = ResourceBundle.getBundle("strings");
	
	public static String getString(String key){
		return res.getString(key);
	}

}
