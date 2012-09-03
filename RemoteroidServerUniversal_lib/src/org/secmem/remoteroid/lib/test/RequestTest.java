package org.secmem.remoteroid.lib.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;
import org.secmem.remoteroid.lib.api.API;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.request.Request;
import org.secmem.remoteroid.lib.request.Request.RequestBuilder;
import org.secmem.remoteroid.lib.request.Response;

public class RequestTest {

	@Test
	public void testRegister(){
		
		Account account = new Account();
		account.setEmail("test@test.com");
		account.setPassword("pass");
		Request request = RequestBuilder.getRequest(API.Account.ADD_ACCOUNT).setPayload(account).build();
		try {
			Response resp = request.sendRequest();
			if(resp.isSucceed()){
				Account acc = resp.getPayloadAsAccount();
				assertEquals("test@test.com", acc.getEmail());
			}else{
				fail();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testLogin(){
		
		Account account = new Account();
		account.setEmail("test@test.com");
		account.setPassword("pass");
		
		Request request = RequestBuilder.getRequest(API.Account.LOGIN).setPayload(account).build();
		try{
			Response resp = request.sendRequest();
			if(!resp.isSucceed()){
				fail();
			}
		}catch(MalformedURLException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
