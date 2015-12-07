package com.example.toshiba.ternakku.oauth;

import android.os.Parcel;
import android.os.Parcelable;


public class OauthAccessToken implements Parcelable {
	private String token;
	private String secret;
	
	public OauthAccessToken(String token, String secret) {
		this.token	= token;
		this.secret	= secret;
	}
	
	public OauthAccessToken(Parcel in) {
		token 	= in.readString();
		secret	= in.readString();
	}
	 
	public String getToken() {
		return token;
	}
	
	public String getSecret() {
		return secret;
	}
	
	public static final Creator<OauthAccessToken> CREATOR = new Creator<OauthAccessToken>() {
        public OauthAccessToken createFromParcel(Parcel in) {
            return new OauthAccessToken(in);
        }
 
        public OauthAccessToken[] newArray(int size) {
            return new OauthAccessToken[size];
        }
    };
    
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(token);
		out.writeString(secret);
	}
}