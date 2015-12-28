package com.example.toshiba.ternakku.http;

import com.example.toshiba.ternakku.http.core.RestConnection;
import com.example.toshiba.ternakku.http.exeption.LisaException;
import com.example.toshiba.ternakku.model.Model;
import com.example.toshiba.ternakku.model.ProviderWrapper;
import com.example.toshiba.ternakku.model.User;
import com.example.toshiba.ternakku.oauth.OauthAccessToken;
import com.example.toshiba.ternakku.util.Cons;
import com.example.toshiba.ternakku.util.Debug;
import com.example.toshiba.ternakku.util.StringUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class UserConnection extends RestConnection {

	public User login(String token, String identifier, String password) throws Exception, LisaException {
		
		User user = null;
		
		try {
			identifier	= (identifier.startsWith("0")) ? "62"+identifier.substring(1, identifier.length()) : identifier;
			
			String url = Cons.ACCOUNTS_URL + "/authentication";
		
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			
			nameValuePairs.add(new BasicNameValuePair("identifier", 		identifier));
			nameValuePairs.add(new BasicNameValuePair("password", 			password));
			nameValuePairs.add(new BasicNameValuePair("requestToken", 		token));
			
			InputStream is	= connectPost(url, nameValuePairs);
			
			if (is != null) { 
				String response		= StringUtil.streamToString(is);

				
				JSONObject jsonObj 		= (JSONObject) new JSONTokener(response).nextValue(); 	
				
				if (!jsonObj.isNull("code")) {
					throw new LisaException(jsonObj.getString("code"));
				} else {
					JSONObject profileJson	= jsonObj.getJSONObject("profile");
					JSONObject tokenJson	= jsonObj.getJSONObject("accessToken");
					
					user 				= new User();
					
					user.fullName		= profileJson.getString("fullName");
					user.phone			= profileJson.getString("msisdn");
					user.accountId		= profileJson.getString("id");
					
					if (profileJson.isNull("gender")) {
						user.gender = "null";
					}else {
						user.gender			= profileJson.getString("gender");
					}
					
					if (profileJson.isNull("crop")) {
						user.crop = "-";
					}else {
						user.crop			= profileJson.getString("crop");
					}
					
					if (profileJson.isNull("state")) {
						user.state = "-";
					}else {
						user.state = profileJson.getString("state");
					}

					
					if (profileJson.isNull("birthDate")) {
						user.birthDate = "-";
					}else {
						user.birthDate = profileJson.getString("birthDate");
					}
					
					if (profileJson.isNull("location")) {
						user.location = "-";
					}else {
						user.location = profileJson.getString("location");
					}
					
					if (profileJson.isNull("hobby")) {
						user.hobby = "-";
					}else {
						user.hobby = profileJson.getString("hobby");
					}
					
					user.channelGroup	= (profileJson.isNull("channel-group")) ? "" : profileJson.getString("channel-group");
					user.subdistrict	= (profileJson.isNull("subdistrict")) ? "-" : profileJson.getString("subdistrict");
					user.hasStore		= (profileJson.isNull("hasStore")) ? "" : profileJson.getString("hasStore");
					user.storeId		= (profileJson.isNull("storeId")) ? "" : profileJson.getString("storeId");
					user.storeOwner		= (profileJson.isNull("store-owner")) ? "" : profileJson.getString("store-owner");
					user.type			= profileJson.getString("type");
					user.privacy		= profileJson.getString("privacy");
					user.communityId	= profileJson.getString("communityId");
					user.customerId		= profileJson.getString("customerId");
					user.points			= profileJson.getInt("points");
					user.avatar			= profileJson.getString("avatar");
					user.avatar			= (profileJson.isNull("avatar")) ? "" : profileJson.getString("avatar");
					
					user.forums			= profileJson.getJSONObject("totalConversations").getInt("forums");
					user.questions		= profileJson.getJSONObject("totalConversations").getInt("questions");
					user.articles		= profileJson.getJSONObject("totalConversations").getInt("articles");
					user.responses		= profileJson.getJSONObject("totalConversations").getInt("responses");
					
					
					user.accessToken	= new OauthAccessToken(tokenJson.getString("key"), tokenJson.getString("secret"));
				}
				is.close();
			} else {
				throw new LisaException("Response does not contain any data.");
			}
		            
		} catch (Exception e) { 
			throw e;
		}
		
		return user;
	}

	public ProviderWrapper getProvider() throws Exception, LisaException {
		ProviderWrapper wrapper = null;
		
		try {
			String url 		= Cons.ACCOUNTS_URL + "/communities/providers/"+ Cons.COMUNITY_ID + "?isKeyword=true";
		
			InputStream is	= connectGet(url);
			
			if (is != null) { 
				String response		= StringUtil.streamToString(is);
				
				Debug.i(response);
				
				JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();
				JSONArray jsonArr 	= jsonObj.getJSONArray("providers"); 		
				
				int length 			= jsonArr.length();
				
				if (length > 0) {
					wrapper					= new ProviderWrapper();
					wrapper.providerList 	= new ArrayList<Model>();
					wrapper.keywordReg		= "";
					wrapper.keywordReset	= "";
					
					for (int i = 0; i < length; i++) {
						JSONObject jsonItem = jsonArr.getJSONObject(i);
						
						Model model	= new Model();
						
						model.id 	= jsonItem.getString("provider");
						model.name	= jsonItem.getString("shortNumber");
						
						wrapper.providerList.add(model);
					}
					
					wrapper.keywordReg 	 = jsonObj.getJSONObject("keywords").getString("reg");
					wrapper.keywordReset = jsonObj.getJSONObject("keywords").getString("resetPassword");
				}
				
				is.close();
			} else {
				throw new LisaException("Response does not contain any data.");
			}
		            
		} catch (Exception e) { 
			throw e;
		}
		
		return wrapper;
	}



	public void checkPassword(String token, String identifier) throws Exception, LisaException {
		try {
			identifier	= (identifier.startsWith("0")) ? "62"+identifier.substring(1, identifier.length()) : identifier;
			
			String url = Cons.ACCOUNTS_URL + "/users/findByMsisdn/" + Cons.COMUNITY_ID + "/" + identifier;
			
			InputStream is = connectGet(url);
			
			if (is != null) {
				String response = StringUtil.streamToString(is);
				
				Debug.i(response);
				is.close();
			}else {
				throw new LisaException("Response does not contain any data");
			}
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
	}
	
	public User registerStep1(String token, String identifier, String password,
							  String verification, String msisdn, String privacy)
			throws Exception, LisaException {

		User user = null;

		try {

			identifier	= (identifier.startsWith("0")) ? "62"+identifier.substring(1, identifier.length()) : identifier;
			msisdn	= (msisdn.startsWith("0")) ? "62"+msisdn.substring(1, msisdn.length()) : msisdn;

			String url = Cons.ACCOUNTS_URL + "/register/users";

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);

			nameValuePairs.add(new BasicNameValuePair("requestToken", token));
			nameValuePairs.add(new BasicNameValuePair("identifier", identifier));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("profile[msisdn]", msisdn));
			nameValuePairs.add(new BasicNameValuePair("privacy", privacy));


			InputStream is = connectPost(url, nameValuePairs);

			if (is != null) {
				String response = StringUtil.streamToString(is);

//				Debug.i(response);

				JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();

				if (!jsonObj.isNull("code")) {
					throw new LisaException(jsonObj.getString("code"));
				} else {
					JSONObject profileJson = jsonObj.getJSONObject("profile");
					JSONObject tokenJson = jsonObj.getJSONObject("accessToken");

					user = new User();

					user.id = jsonObj.getString("id");
					user.identifier = jsonObj.getString("identifier");

					user.phone = jsonObj.getString("msisdn");
					user.channelGroup = (profileJson.isNull("channel-group")) ? "" : profileJson.getString("channel-group");
					user.accessToken = new OauthAccessToken(tokenJson.getString("key"), tokenJson.getString("secret"));
				}
				is.close();
			}else {
				throw new LisaException("Response does not contain any data.");
			}
		} catch (Exception e) {
			throw e;
		}

		return user;


	}
}
