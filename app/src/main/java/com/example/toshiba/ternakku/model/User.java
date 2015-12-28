package com.example.toshiba.ternakku.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.toshiba.ternakku.oauth.OauthAccessToken;


public class User implements Parcelable {

	public String id;
	public String identifier;
	public String fullName;
	public String phone;
	public String fieldSize;
	public String birthDate;
	public String gender;
	public String crop;
	public String hobby;
	public String location;
	public String state;
	public String subdistrict;
	public String type;
	public String privacy;
	public String accountId;
	public String communityId;
	public String customerId;
	public String avatar;
	public String channelGroup;
	public String hasBrowserVersion;
	public String hasStore;
	public String storeId;
	public String storeOwner;
	
	public int points = 0;
	public int forums = 0;
	public int questions = 0;
	public int articles = 0;
	public int responses = 0;

	public OauthAccessToken accessToken = new OauthAccessToken("", "");

	public User() {
	}

	public User(Parcel in) {
		id = in.readString();
		identifier = in.readString();
		fullName = in.readString();
		phone = in.readString();
		gender = in.readString();
		fieldSize = in.readString();
		birthDate = in.readString();
		crop = in.readString();
		hobby = in.readString();
		location = in.readString();
		state = in.readString();
		subdistrict = in.readString();
		type = in.readString();
		privacy = in.readString();
		accountId = in.readString();
		communityId = in.readString();
		customerId = in.readString();
		avatar = in.readString();
		responses = in.readInt();

		accessToken = OauthAccessToken.CREATOR.createFromParcel(in);
	}
	
	public static final Creator<User> CREATOR = new Creator<User>() {
		
		@Override
		public User[] newArray(int size) {
			// TODO Auto-generated method stub
			return new User[size];
		}
		
		@Override
		public User createFromParcel(Parcel in) {
			// TODO Auto-generated method stub
			return new User(in);
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeString(id);
		out.writeString(identifier);
		out.writeString(fullName);
		out.writeString(phone);
		out.writeString(gender);
		out.writeString(fieldSize);
		out.writeString(birthDate);
		out.writeString(hobby);
		out.writeString(crop);
		out.writeString(location);
		out.writeString(state);
		out.writeString(subdistrict);
		out.writeString(type);

		out.writeInt(responses);
		
		accessToken.writeToParcel(out, flags);
	}

}
