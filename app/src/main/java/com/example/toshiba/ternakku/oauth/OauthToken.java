package com.example.toshiba.ternakku.oauth;

 
public class OauthToken {
	private String token;
	private String secret;
	private String verifier;
	private boolean callbackConfirmed;
	
	public OauthToken(String token, String secret, String verifier, boolean callbackConfirmed) {
		this.token				= token;
		this.secret				= secret;
		this.verifier 			= verifier;
		this.callbackConfirmed	= callbackConfirmed; 
	}
	
	public String getToken() {
		return token;
	}
	
	public String getSecret() {
		return secret;
	}
	
	public String getVerifier() {
		return verifier;
	}
	
	public boolean callbackConfirmed() {
		return callbackConfirmed;
	}
}