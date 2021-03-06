package org.secmem.remoteroid.lib.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.secmem.remoteroid.lib.api.API;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.request.Request;
import org.secmem.remoteroid.lib.request.Request.Builder;
import org.secmem.remoteroid.lib.request.Response;

public class RequestTest {

	@Test
	public void testRegister(){
		
		Account account = new Account();
		account.setEmail("test@test.com");
		account.setPassword("pass");
		Request request = Builder.setRequest(API.Account.ADD_ACCOUNT).setPayload(account).build();
		
		Response resp = request.sendRequest();
		if(resp.isSucceed()){
			Account acc = resp.getPayloadAsAccount();
			assertEquals("test@test.com", acc.getEmail());
		}else{
			fail();
		}
		
		
	}
	
	@Test
	public void testLogin(){
		
		Account account = new Account();
		account.setEmail("test@test.com");
		account.setPassword("pass");
		
		Request request = Builder.setRequest(API.Account.LOGIN).setPayload(account).build();
		
		Response resp = request.sendRequest();
		if(!resp.isSucceed()){
			fail();
		}

	}

}
