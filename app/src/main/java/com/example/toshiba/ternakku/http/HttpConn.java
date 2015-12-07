package com.example.toshiba.ternakku.http;


import com.example.toshiba.ternakku.oauth.OauthAccessToken;
import com.example.toshiba.ternakku.oauth.OauthConsumer;
import com.example.toshiba.ternakku.oauth.OauthHeader;
import com.example.toshiba.ternakku.oauth.OauthSignature;
import com.example.toshiba.ternakku.oauth.OauthUtil;
import com.example.toshiba.ternakku.util.Debug;
import com.example.toshiba.ternakku.util.StringUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.List;

public class HttpConn {
	private OauthConsumer mOauthConsumer;
	private OauthAccessToken mAcessToken;
	
	public static final String REQUEST_POST = "POST";
	public static final String REQUEST_PUT = "PUT";
	public static final String REQUEST_GET = "GET";
	
	public HttpConn(OauthConsumer consumer, OauthAccessToken acessToken) {
		mOauthConsumer	= consumer;
		mAcessToken		= acessToken;
	}
	
	public String connectPostMultiPart(String requestUri, List<NameValuePair> params, HttpEntity entity) throws Exception {
		String response 		= "";
		
		InputStream stream = null;
		
		try {
			HttpParams httpParams 	= new HttpParams();
			
			String nonce			= OauthUtil.createNonce();
			String timestamp		= OauthUtil.getTimeStamp();
			
			httpParams.put("oauth_consumer_key", 		new HttpValues(mOauthConsumer.getConsumerKey()));
			httpParams.put("oauth_nonce", 				new HttpValues(nonce));
			httpParams.put("oauth_signature_method", 	new HttpValues(OauthUtil.SIGNATURE_METHOD));
			httpParams.put("oauth_timestamp", 			new HttpValues(timestamp));
			httpParams.put("oauth_token", 				new HttpValues(mAcessToken.getToken()));
			httpParams.put("oauth_version", 			new HttpValues(OauthUtil.OAUTH_VERSION));
			
			StringBuilder sb = new StringBuilder();
			
			if (params != null) {
				int size = params.size();
				
				for (int i = 0; i < size; i++) {
					BasicNameValuePair param = (BasicNameValuePair) params.get(i);
					
					httpParams.put(param.getName(), new HttpValues(param.getValue()));
					
					sb.append(param.getName());
					sb.append("=");
					sb.append(param.getValue());
					
					if (i != size-1) {
						sb.append("&");
					}
				}
			}
			
			OauthSignature reqSignature	= new OauthSignature();
			
			String sigBase		= reqSignature.createSignatureBase(REQUEST_POST, requestUri, httpParams.getQueryString());
			String signature	= reqSignature.createRequestSignature(sigBase, mOauthConsumer.getConsumerSecret(), mAcessToken.getSecret());
			
			String authHeader	= OauthHeader.buildRequestHeader(
					mOauthConsumer.getConsumerKey(),
					nonce,
					signature,
					OauthUtil.SIGNATURE_METHOD,
					timestamp,
					mAcessToken.getToken(),
					OauthUtil.OAUTH_VERSION);
			
			Debug.i("Method " + REQUEST_POST);
			Debug.i("URI " + requestUri);
			Debug.i("Parameters " + sb.toString());
			Debug.i("Query string " + httpParams.getQueryString());
			Debug.i("Token " + mAcessToken.getToken());
			Debug.i("Secret " + mAcessToken.getSecret());
			Debug.i("Timestamp " + timestamp);
			Debug.i("Nonce " + nonce);
			Debug.i("Signature base " + sigBase);
			Debug.i("Signature " + signature);
			
			Debug.i("POST " + requestUri);
			Debug.i("Authorization " + authHeader);
			
			java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
			java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);

			System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
			System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
			System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");
			
			HttpClient httpClient 	= new DefaultHttpClient();
			HttpPost httpPost 		= new HttpPost(requestUri);
			
			httpPost.addHeader("Authorization", authHeader);			
			httpPost.setEntity(entity);
		       
			HttpResponse httpResponse 	= httpClient.execute(httpPost);
			
			//if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity 		= httpResponse.getEntity();
					
				if (httpEntity == null) {
					throw new Exception("Return value is empty");
				}
					
				stream  	= httpEntity.getContent();							
				response	= StringUtil.streamToString(stream);
				
				Debug.i("Response " + response);
			//} else {
				//throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
			//}
		} catch (Exception e) {
			throw e;
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		
		return response;
	}
	
	public String connectPost(String requestUri) throws Exception {
		return connectPost(requestUri, null);
	}
	
	public String connectPost(String requestUri, List<NameValuePair> params) throws Exception {
		String response 		= "";
		
		InputStream stream = null;
		
		try {
			HttpParams httpParams 	= new HttpParams();
			
			String nonce			= OauthUtil.createNonce();
			String timestamp		= OauthUtil.getTimeStamp();
			
			httpParams.put("oauth_consumer_key", 		new HttpValues(mOauthConsumer.getConsumerKey()));
			httpParams.put("oauth_nonce", 				new HttpValues(nonce));
			httpParams.put("oauth_signature_method", 	new HttpValues(OauthUtil.SIGNATURE_METHOD));
			httpParams.put("oauth_timestamp", 			new HttpValues(timestamp));
			httpParams.put("oauth_token", 				new HttpValues(mAcessToken.getToken()));
			httpParams.put("oauth_version", 			new HttpValues(OauthUtil.OAUTH_VERSION));
			
			StringBuilder sb = new StringBuilder();
			
			if (params != null) {
				int size = params.size();
				
				for (int i = 0; i < size; i++) {
					BasicNameValuePair param = (BasicNameValuePair) params.get(i);
					
					httpParams.put(param.getName(), new HttpValues(param.getValue()));
					
					sb.append(param.getName());
					sb.append("=");
					sb.append(param.getValue());
					
					if (i != size-1) {
						sb.append("&");
					}
				}
			}
			
			OauthSignature reqSignature	= new OauthSignature();
			
			String sigBase		= reqSignature.createSignatureBase(REQUEST_POST, requestUri, httpParams.getQueryString());
			String signature	= reqSignature.createRequestSignature(sigBase, mOauthConsumer.getConsumerSecret(), mAcessToken.getSecret());
			
			String authHeader	= OauthHeader.buildRequestHeader(
					mOauthConsumer.getConsumerKey(),
					nonce,
					signature,
					OauthUtil.SIGNATURE_METHOD,
					timestamp,
					mAcessToken.getToken(),
					OauthUtil.OAUTH_VERSION);
			
			Debug.i("Method " + REQUEST_POST);
			Debug.i("URI " + requestUri);
			Debug.i("Parameters " + sb.toString());
			Debug.i("Query string " + httpParams.getQueryString());
			Debug.i("Token " + mAcessToken.getToken());
			Debug.i("Secret " + mAcessToken.getSecret());
			Debug.i("Timestamp " + timestamp);
			Debug.i("Nonce " + nonce);
			Debug.i("Signature base " + sigBase);
			Debug.i("Signature " + signature);
			
			Debug.i("POST " + requestUri);
			Debug.i("Authorization " + authHeader);
			
			java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
			java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);

			System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
			System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
			System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");
			
			HttpClient httpClient 	= new DefaultHttpClient();
			HttpPost httpPost 		= new HttpPost(requestUri);
			
			httpPost.addHeader("Authorization", authHeader);			
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		       
			HttpResponse httpResponse 	= httpClient.execute(httpPost);
			
			//if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity 		= httpResponse.getEntity();
					
				if (httpEntity == null) {
					throw new Exception("Return value is empty");
				}
					
				stream  	= httpEntity.getContent();							
				response	= StringUtil.streamToString(stream);
				
				Debug.i("Response " + response);
			//} else {
				//throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
			//}
		} catch (Exception e) {
			throw e;
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		
		return response;
	}
	
	public String connectGet(String requestUri) throws Exception {
		return connectGet(requestUri, null);
	}
	
	public String connectGet(String requestUri, List<NameValuePair> params) throws Exception {
		String response 		= "";
		
		InputStream stream = null;
		
		try {
			HttpParams httpParams 	= new HttpParams();
			
			String nonce			= OauthUtil.createNonce();
			String timestamp		= OauthUtil.getTimeStamp();
			
			httpParams.put("oauth_consumer_key", 		new HttpValues(mOauthConsumer.getConsumerKey()));
			httpParams.put("oauth_nonce", 				new HttpValues(nonce));
			httpParams.put("oauth_signature_method", 	new HttpValues(OauthUtil.SIGNATURE_METHOD));
			httpParams.put("oauth_timestamp", 			new HttpValues(timestamp));
			httpParams.put("oauth_token", 				new HttpValues(mAcessToken.getToken()));
			httpParams.put("oauth_version", 			new HttpValues(OauthUtil.OAUTH_VERSION));
			
			String requestUrl 	= requestUri;
			String requestParam	= "";
			
			if (params != null) {
				StringBuilder requestParamSb = new StringBuilder();
				int size = params.size();
				
				for (int i = 0; i < size; i++) {
					BasicNameValuePair param = (BasicNameValuePair) params.get(i);
					
					httpParams.put(param.getName(), new HttpValues(param.getValue()));
					
					requestParamSb.append(param.getName() + "=" + param.getValue() + ((i != size-1) ? "&" : ""));
				}
				
				requestParam = requestParamSb.toString();
				
				requestUrl = requestUri + ((requestUri.contains("?")) ? "&" + requestParam : "?" + requestParam); 
 			}
			
			OauthSignature reqSignature	= new OauthSignature();
			
			String sigBase		= reqSignature.createSignatureBase(REQUEST_GET, requestUri, httpParams.getQueryString());
			String signature	= reqSignature.createRequestSignature(sigBase, mOauthConsumer.getConsumerSecret(), mAcessToken.getSecret());
			
			String authHeader	= OauthHeader.buildRequestHeader(
									mOauthConsumer.getConsumerKey(), 
									nonce,
									signature, 
									OauthUtil.SIGNATURE_METHOD, 
									timestamp,
									mAcessToken.getToken(),
									OauthUtil.OAUTH_VERSION);
					
			Debug.i("Method " + REQUEST_GET);
			Debug.i("URI " + requestUri);
			Debug.i("Parameters " + requestParam);
			Debug.i("Token " + mAcessToken.getToken());
			Debug.i("Secret " + mAcessToken.getSecret());
			Debug.i("Timestamp " + timestamp);
			Debug.i("Nonce " + nonce);
			Debug.i("Signature base " + sigBase);
			Debug.i("Signature " + signature);
			
			Debug.i("POST " + requestUri);
			Debug.i("Authorization " + authHeader);


			java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
			java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);

			System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
			System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
			System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");
			
			HttpClient httpClient 	= new DefaultHttpClient();
			HttpGet httpGet 		= new HttpGet(requestUrl);

			httpGet.addHeader("Authorization", authHeader);			
			
			HttpResponse httpResponse 	= httpClient.execute(httpGet);
			HttpEntity httpEntity 		= httpResponse.getEntity();
				
			if (httpEntity == null) {
				throw new Exception("Return value is empty");
			}
					
			stream  	= httpEntity.getContent();							
			response	= StringUtil.streamToString(stream);
				
			Debug.i("Response " + response);
			
			//if (httpResponse.getStatusLine().getStatusCode() != 200) {
				//throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
			//}
			
		} catch (Exception e) {
			throw e;
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		
		return response;
	}
	
	public String connectPut(String requestUri, List<NameValuePair> params) throws Exception {
		String response 		= "";
		
		InputStream stream = null;
		
		try {
			HttpParams httpParams 	= new HttpParams();
			
			String nonce			= OauthUtil.createNonce();
			String timestamp		= OauthUtil.getTimeStamp();
			
			httpParams.put("oauth_consumer_key", 		new HttpValues(mOauthConsumer.getConsumerKey()));
			httpParams.put("oauth_nonce", 				new HttpValues(nonce));
			httpParams.put("oauth_signature_method", 	new HttpValues(OauthUtil.SIGNATURE_METHOD));
			httpParams.put("oauth_timestamp", 			new HttpValues(timestamp));
			httpParams.put("oauth_token", 				new HttpValues(mAcessToken.getToken()));
			httpParams.put("oauth_version", 			new HttpValues(OauthUtil.OAUTH_VERSION));
			
			StringBuilder sb = new StringBuilder();
			
			if (params != null) {
				int size = params.size();
				
				for (int i = 0; i < size; i++) {
					BasicNameValuePair param = (BasicNameValuePair) params.get(i);
					
					httpParams.put(param.getName(), new HttpValues(param.getValue()));
					
					sb.append(param.getName());
					sb.append("=");
					sb.append(param.getValue());
					
					if (i != size-1) {
						sb.append("&");
					}
				}
			}
			
			OauthSignature reqSignature	= new OauthSignature();
			
			String sigBase		= reqSignature.createSignatureBase(REQUEST_PUT, requestUri, httpParams.getQueryString());
			String signature	= reqSignature.createRequestSignature(sigBase, mOauthConsumer.getConsumerSecret(), mAcessToken.getSecret());
			
			String authHeader	= OauthHeader.buildRequestHeader(
									mOauthConsumer.getConsumerKey(), 
									nonce,
									signature, 
									OauthUtil.SIGNATURE_METHOD, 
									timestamp,
									mAcessToken.getToken(),
									OauthUtil.OAUTH_VERSION);
					
			Debug.i("Method " + REQUEST_PUT);
			Debug.i("URI " + requestUri);
			Debug.i("Parameters " + sb.toString());
			Debug.i("Token " + mAcessToken.getToken());
			Debug.i("Secret " + mAcessToken.getSecret());
			Debug.i("Timestamp " + timestamp);
			Debug.i("Nonce " + nonce);
			Debug.i("Signature base " + sigBase);
			Debug.i("Signature " + signature);
			
			Debug.i("PUT " + requestUri);
			Debug.i("Authorization " + authHeader);
			
			java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
			java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);

			System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
			System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
			System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");
			
			HttpClient httpClient 	= new DefaultHttpClient();
			HttpPut httpPost 		= new HttpPut(requestUri);
			
			httpPost.addHeader("Authorization", authHeader);			
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			
			HttpResponse httpResponse 	= httpClient.execute(httpPost);
			
			//if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity 		= httpResponse.getEntity();
					
				if (httpEntity == null) {
					throw new Exception("Return value is empty");
				}
					
				stream  	= httpEntity.getContent();							
				response	= StringUtil.streamToString(stream);
				
				Debug.i("Response " + response);
			//} else {
				//throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
			//}
		} catch (Exception e) {
			throw e;
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		
		return response;
	}
	
	public String connectPutMutiPart(String requestUri, List<NameValuePair> params, HttpEntity entity) throws Exception {
		String response 		= "";
		
		InputStream stream = null;
		
		try {
			HttpParams httpParams 	= new HttpParams();
			
			String nonce			= OauthUtil.createNonce();
			String timestamp		= OauthUtil.getTimeStamp();
			
			httpParams.put("oauth_consumer_key", 		new HttpValues(mOauthConsumer.getConsumerKey()));
			httpParams.put("oauth_nonce", 				new HttpValues(nonce));
			httpParams.put("oauth_signature_method", 	new HttpValues(OauthUtil.SIGNATURE_METHOD));
			httpParams.put("oauth_timestamp", 			new HttpValues(timestamp));
			httpParams.put("oauth_token", 				new HttpValues(mAcessToken.getToken()));
			httpParams.put("oauth_version", 			new HttpValues(OauthUtil.OAUTH_VERSION));
			
			StringBuilder sb = new StringBuilder();
			
			if (params != null) {
				int size = params.size();
				
				for (int i = 0; i < size; i++) {
					BasicNameValuePair param = (BasicNameValuePair) params.get(i);
					
					httpParams.put(param.getName(), new HttpValues(param.getValue()));
					
					sb.append(param.getName());
					sb.append("=");
					sb.append(param.getValue());
					
					if (i != size-1) {
						sb.append("&");
					}
				}
			}
			
			OauthSignature reqSignature	= new OauthSignature();
			
			String sigBase		= reqSignature.createSignatureBase(REQUEST_PUT, requestUri, httpParams.getQueryString());
			String signature	= reqSignature.createRequestSignature(sigBase, mOauthConsumer.getConsumerSecret(), mAcessToken.getSecret());
			
			String authHeader	= OauthHeader.buildRequestHeader(
									mOauthConsumer.getConsumerKey(), 
									nonce,
									signature, 
									OauthUtil.SIGNATURE_METHOD, 
									timestamp,
									mAcessToken.getToken(),
									OauthUtil.OAUTH_VERSION);
					
			Debug.i("Method " + REQUEST_PUT);
			Debug.i("URI " + requestUri);
			Debug.i("Parameters " + sb.toString());
			Debug.i("Token " + mAcessToken.getToken());
			Debug.i("Secret " + mAcessToken.getSecret());
			Debug.i("Timestamp " + timestamp);
			Debug.i("Nonce " + nonce);
			Debug.i("Signature base " + sigBase);
			Debug.i("Signature " + signature);
			
			Debug.i("PUT " + requestUri);
			Debug.i("Authorization " + authHeader);
			
			java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
			java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);

			System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
			System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
			System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");
			
			HttpClient httpClient 	= new DefaultHttpClient();
			HttpPut httpPost 		= new HttpPut(requestUri);
			
			httpPost.addHeader("Authorization", authHeader);			
			httpPost.setEntity(entity);
			
			HttpResponse httpResponse 	= httpClient.execute(httpPost);
			
			//if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity 		= httpResponse.getEntity();
					
				if (httpEntity == null) {
					throw new Exception("Return value is empty");
				}
					
				stream  	= httpEntity.getContent();							
				response	= StringUtil.streamToString(stream);
				
				Debug.i("Response " + response);
			//} else {
				//throw new Exception(httpResponse.getStatusLine().getReasonPhrase());
			//}
		} catch (Exception e) {
			throw e;
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		
		return response;
	}
}