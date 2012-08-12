package org.secmem.remoteroid.lib.data;

public class Account{

	private String email;
	private String password;
	
	public Account(){
		
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString(){
		return "Account [email="+email+", password="+password+"]";
	}
	
}
