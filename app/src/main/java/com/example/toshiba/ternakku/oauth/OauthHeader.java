package com.example.toshiba.ternakku.oauth;


import com.example.toshiba.ternakku.util.EncodeUtil;

public class OauthHeader {
	
	public static String buildRequestTokenHeader(String callbackUrl, String consumerKey, String nonce, String signature,
			String signatureMethod, String timestamp, String version) {
		
		StringBuilder sb = new StringBuilder();
		
		//sb.append("Oauth ");
		//sb.append("oauth_callback=");
	//	sb.append("\"");
	//	sb.append(URIUtil.encode(callbackUrl));
	//	sb.append("\", ");
		
		sb.append("oauth_consumer_key=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(consumerKey));
		sb.append("\", ");
		
		sb.append("oauth_nonce=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(nonce));
		sb.append("\", ");
		
		sb.append("oauth_signature=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(signature));
		sb.append("\", ");
		
		sb.append("oauth_signature_method=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(signatureMethod));
		sb.append("\", ");
		
		sb.append("oauth_timestamp=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(timestamp));
		sb.append("\", ");
		
		sb.append("oauth_version=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(version));
		sb.append("\"");
		
		return sb.toString();
	}
	
	public static String buildRequestHeader(String consumerKey, String nonce, String signature, String signatureMethod,
			String timestamp, String token, String verifier, String version) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("OAuth ");
		
		sb.append("oauth_consumer_key=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(consumerKey));
		sb.append("\", ");
		
		sb.append("oauth_nonce=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(nonce));
		sb.append("\", ");
		
		sb.append("oauth_signature=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(signature));
		sb.append("\", ");
		
		sb.append("oauth_signature_method=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(signatureMethod));
		sb.append("\", ");
		
		sb.append("oauth_timestamp=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(timestamp));
		sb.append("\", ");
		
		sb.append("oauth_token=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(token));
		sb.append("\", ");
		
		sb.append("oauth_verifier=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(verifier));
		sb.append("\", ");
		
		sb.append("oauth_version=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(version));
		sb.append("\"");
		
		return sb.toString();
	}
	
	public static String buildRequestHeader(String consumerKey, String nonce, String signature, String signatureMethod,
			String timestamp, String token,  String version) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("OAuth ");
		
		sb.append("oauth_consumer_key=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(consumerKey));
		sb.append("\", ");
		
		sb.append("oauth_nonce=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(nonce));
		sb.append("\", ");
		
		sb.append("oauth_signature=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(signature));
		sb.append("\", ");
		
		sb.append("oauth_signature_method=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(signatureMethod));
		sb.append("\", ");
		
		sb.append("oauth_timestamp=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(timestamp));
		sb.append("\", ");
		
		sb.append("oauth_token=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(token));
		sb.append("\", ");
		
		sb.append("oauth_version=");
		sb.append("\"");
		sb.append(EncodeUtil.encode(version));
		sb.append("\"");
		
		return sb.toString();
	}
}