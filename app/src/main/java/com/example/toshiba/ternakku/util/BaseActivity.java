package com.example.toshiba.ternakku.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toshiba.ternakku.R;
import com.example.toshiba.ternakku.model.User;
import com.example.toshiba.ternakku.oauth.OauthAccessToken;
import com.example.toshiba.ternakku.oauth.OauthConsumer;
import com.example.toshiba.ternakku.oauth.OauthToken;


public class BaseActivity extends AppCompatActivity {
	protected SQLiteDatabase mSqLite;
	protected SharedPreferences mSharedPref;

	private boolean mIsDbOpen = false;
	private boolean mEnableDb = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSharedPref = getSharedPreferences(Cons.PRIVATE_PREF,
				Context.MODE_PRIVATE);
	}

	@Override
	protected void onPause() {
		if (mEnableDb && mIsDbOpen) {
			closeDatabase();
		}

		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mSharedPref = getSharedPreferences(Cons.PRIVATE_PREF,
				Context.MODE_PRIVATE);

		if (mEnableDb && !mIsDbOpen) {
			openDatabase();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public void back() {
		setResult(RESULT_OK);
		finish();
	}

	/*************************************************************
	 * This function for setup Language default by locale
	 * 
	 * ***********************************************************
	 */



	/******************************************************
	 * This function for Toast
	 * 
	 * ****************************************************
	 */
	@SuppressLint("InflateParams")
	public void showToast(String text, boolean islong) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.layout_toast, null);

		TextView textView = (TextView) layout.findViewById(R.id.tv_title);

		textView.setText(text);

		Toast toast = new Toast(getApplicationContext());

		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration((islong) ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	public void showToast(String text) {
		showToast(text, true);
	}
	
	public void ShowToastNew(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

	/************************************************************
	 * Till here for Toast
	 * 
	 * **********************************************************
	 */

	/***********************************************************
	 * This for setting and show dialog
	 * 
	 * *********************************************************
	 */
	@SuppressLint("InflateParams")
	public void showInfoAndNewIntent(String message, final Intent intent,
			final int reqCode) {
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_info, null);

		TextView messageTv = (TextView) view.findViewById(R.id.tv_message);

//		if (getDefaultLanguage().equals(Cons.LANG_ID)) {
//			messageTv.setTypeface(mFont);
//		}

		messageTv.setText(message);

		Builder builder = new Builder(this);
		builder.setView(view).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();

						startActivityForResult(intent, reqCode);
					}
				});
		builder.create().show();
	}

	@SuppressLint("InflateParams")
	public void showDialog(String title, String message, final boolean back) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_info, null);

		TextView messageTv = (TextView) view.findViewById(R.id.tv_message);

//		if (getDefaultLanguage().equals(Cons.LANG_ID)) {
//			messageTv.setTypeface(mFont);
//		}

		messageTv.setText(message);

		Builder builder = new Builder(this);
		
		if (!title.equals(""))
			builder.setTitle(title);

		builder.setCancelable(false);
		builder.setView(view).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						if (back) {
							back();
						}
					}
				});

		builder.create().show();
	}
	
	@SuppressLint("InflateParams")
	public void showDialogEditProfile(String title, String message,
			final boolean back) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_info, null);

		TextView messageTv = (TextView) view.findViewById(R.id.tv_message);

		messageTv.setText(message);

		Builder builder = new Builder(this);

		if (!title.equals(""))
			builder.setTitle(title);
		
		builder.setCancelable(false);
		builder.setInverseBackgroundForced(back);
		builder.setView(view).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (back) {
							launchRingDialog();
						}
					}
				});

		builder.create().show();
	}
	
	private void launchRingDialog() {
		final ProgressDialog progressDialog= ProgressDialog.show(getActivity(), getString(R.string.text_wait), "Loading...", true);
		progressDialog.setCancelable(true);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(7000);
				} catch (Exception e) {

				}
				progressDialog.dismiss();
//				goEditProfile();
			}
		}).start();
	}
	
	@SuppressLint("InflateParams")
	public void showDialogExit(String message) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.dialog_info, null);

		TextView messageTv = (TextView) view.findViewById(R.id.tv_message);

		messageTv.setText(message);

		Builder builder = new Builder(this);

		builder.setView(view).setPositiveButton("Ya",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
						clearUser();

						getActivity().finish();
						System.exit(0);
					}
				});
		
		builder.setView(view).setNegativeButton("Tidak",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

//	private void goEditProfile() {
//		startActivity(new Intent(getActivity(), EditProfileActivity.class));
//		finish();
//	}
	
	public void showInfoGoEditProfile(String message, boolean back) {
		showDialogEditProfile("", message, back);
	}

	public void showInfo(String message) {
		showDialog("", message, false);
	}

	public void showInfo(String message, boolean back) {
		showDialog("", message, back);
	}

	public void showError(String message) {
		showDialog("Error", message, false);
	}

	public void showWarning(String message) {
		showDialog("Warning", message, false);
	}

	/***********************************************************
	 * Till here for setting dialog
	 * 
	 * *********************************************************
	 */

	public void confirmExit() {
		Builder builder = new Builder(this);

		builder.setMessage("Development")
				.setNegativeButton("Logout",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								showToast("You have been logged out.");

								clearUser();

								finish();
							}
						})
				.setPositiveButton("Close App",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						});

		builder.create().show();
	}

	/*******************************************************************
	 * This all about Oauth and SharedPreferences User
	 * 
	 * *****************************************************************
	 */

	public OauthConsumer getOauthConsumer() {
		return new OauthConsumer(Cons.CONSUMER_KEY, Cons.CONSUMER_SECRET);
	}

	public void saveRequestToken(OauthToken token) {
		Debug.i("Saving request token...");

		Editor editor = mSharedPref.edit();

		editor.putString(Cons.OAUTH_REQ_TOKEN, token.getToken());
		editor.putString(Cons.OAUTH_REQ_TOKEN_SECRET, token.getSecret());
		editor.putString(Cons.OAUTH_REQ_VERIFIER, token.getVerifier());

		editor.commit();
	}

	public OauthToken getRequestToken() {
		String token = mSharedPref.getString(Cons.OAUTH_REQ_TOKEN, "");
		String secret = mSharedPref.getString(Cons.OAUTH_REQ_TOKEN_SECRET, "");
		String verifier = mSharedPref.getString(Cons.OAUTH_REQ_VERIFIER, "");

		return (token.equals("") || secret.equals("")) ? null : new OauthToken(
				token, secret, verifier, true);
	}

	public void saveAccessToken(OauthAccessToken token) {
		Editor editor = mSharedPref.edit();

		editor.putString(Cons.OAUTH_TOKEN, token.getToken());
		editor.putString(Cons.OAUTH_TOKEN_SECRET, token.getSecret());

		editor.commit();
	}

	public void clearAccessToken() {
		Editor editor = mSharedPref.edit();

		editor.putString(Cons.OAUTH_TOKEN, "");
		editor.putString(Cons.OAUTH_TOKEN_SECRET, "");

		editor.commit();
	}

	public OauthAccessToken getAccessToken() {
		String token = mSharedPref.getString(Cons.OAUTH_TOKEN, "");
		String secret = mSharedPref.getString(Cons.OAUTH_TOKEN_SECRET, "");

		return (token.equals("") || secret.equals("")) ? null
				: new OauthAccessToken(token, secret);
	}

	public void saveUser(User user) {
		saveUser(user, true);
	}

	public void saveUser(User user, boolean token) {
		Editor editor = mSharedPref.edit();

		editor.putString(Cons.USER_ID, user.id);
		editor.putString(Cons.USER_IDENTIFIER, user.identifier);
		editor.putString(Cons.USER_NAME, user.fullName);
		editor.putString(Cons.USER_PHONE, user.phone);
		editor.putString(Cons.USER_GENDER, user.gender);
		editor.putString(Cons.USER_FIELD_SIZE, user.fieldSize);
		editor.putString(Cons.USER_BIRTH_DATE, user.birthDate);
		editor.putString(Cons.USER_CROP, user.crop);
		editor.putString(Cons.USER_AVATAR, user.avatar);
		editor.putString(Cons.USER_LOCATION, user.location);
		editor.putString(Cons.USER_SUBDISTRICT, user.subdistrict);
		editor.putString(Cons.USER_STATE, user.state);
		editor.putString(Cons.USER_HOBBY, user.hobby);
		editor.putString(Cons.USER_ACCOUNTID, user.accountId);
		editor.putString(Cons.USER_COMMUNITYID, user.communityId);
		editor.putString(Cons.USER_CUSTOMERID, user.customerId);
		editor.putString(Cons.USER_PRIVACY, user.privacy);
		editor.putString(Cons.USER_TYPE, user.type);
		editor.putString(Cons.USER_CHANNEL_GROUP, user.channelGroup);
		editor.putString(Cons.USER_HASBROWSER, user.hasBrowserVersion);
		editor.putString(Cons.USER_HAS_STORE, user.hasStore);
		editor.putString(Cons.USER_STORE_ID, user.storeId);

		editor.putInt(Cons.USER_POINTS, user.points);
		editor.putInt(Cons.USER_FORUMS, user.forums);
		editor.putInt(Cons.USER_ARTICLES, user.articles);
		editor.putInt(Cons.USER_QUESTIONS, user.questions);
		editor.putInt(Cons.USER_RESPONSES, user.responses);
		editor.commit();

		if (token)
			saveAccessToken(user.accessToken);
	}

	public User getUser() {
		OauthAccessToken accessToken = getAccessToken();

		if (accessToken == null) {
			return null;
		}

		User user = new User();

		user.accessToken = accessToken;

		user.id				 = mSharedPref.getString(Cons.USER_ID, "");
		user.identifier		 = mSharedPref.getString(Cons.USER_IDENTIFIER, "");
		user.fullName		 = mSharedPref.getString(Cons.USER_NAME, "");
		user.phone			 = mSharedPref.getString(Cons.USER_PHONE, "");
		user.gender 		 = mSharedPref.getString(Cons.USER_GENDER, "");
		user.fieldSize 		 = mSharedPref.getString(Cons.USER_FIELD_SIZE, "");
		user.birthDate 		 = mSharedPref.getString(Cons.USER_BIRTH_DATE, "");
		user.crop 			 = mSharedPref.getString(Cons.USER_CROP, "");
		user.hobby 			 = mSharedPref.getString(Cons.USER_HOBBY, "");
		user.location		 = mSharedPref.getString(Cons.USER_LOCATION, "");
		user.state			 = mSharedPref.getString(Cons.USER_STATE, "");
		user.subdistrict	 = mSharedPref.getString(Cons.USER_SUBDISTRICT, "");
		user.accountId	 	 = mSharedPref.getString(Cons.USER_ACCOUNTID, "");
		user.communityId 	 = mSharedPref.getString(Cons.USER_COMMUNITYID, "");
		user.customerId		 = mSharedPref.getString(Cons.USER_CUSTOMERID, "");
		user.privacy		 = mSharedPref.getString(Cons.USER_PRIVACY, "");
		user.type			 = mSharedPref.getString(Cons.USER_TYPE, "");
		user.avatar 		 = mSharedPref.getString(Cons.USER_AVATAR, "");
		user.channelGroup	 = mSharedPref.getString(Cons.USER_CHANNEL_GROUP, "");
		user.hasBrowserVersion = mSharedPref.getString(Cons.USER_HASBROWSER, "");
		user.hasStore		= mSharedPref.getString(Cons.USER_HAS_STORE, "");
		user.storeId		= mSharedPref.getString(Cons.USER_STORE_ID, "");

		user.points 		 = mSharedPref.getInt(Cons.USER_POINTS, 0);

		user.forums 		 = mSharedPref.getInt(Cons.USER_FORUMS, 0);
		user.questions 		 = mSharedPref.getInt(Cons.USER_QUESTIONS, 0);
		user.articles 		 = mSharedPref.getInt(Cons.USER_ARTICLES, 0);
		user.responses		 = mSharedPref.getInt(Cons.USER_RESPONSES, 0);

		return user;
	}

	public void clearUser() {
		Editor editor = mSharedPref.edit();

		editor.putString(Cons.USER_ID, "");
		editor.putString(Cons.USER_IDENTIFIER, "");
		editor.putString(Cons.USER_NAME, "");
		editor.putString(Cons.USER_PHONE, "");
		editor.putString(Cons.USER_AVATAR, "");
		editor.putString(Cons.USER_GENDER, "");
		editor.putString(Cons.USER_FIELD_SIZE, "");
		editor.putString(Cons.USER_BIRTH_DATE, "");
		editor.putString(Cons.USER_CROP, "");
		editor.putString(Cons.USER_HOBBY, "");
		editor.putString(Cons.USER_LOCATION, "");
		editor.putString(Cons.USER_STATE, "");
		editor.putString(Cons.USER_SUBDISTRICT, "");
		editor.putString(Cons.USER_ACCOUNTID, "");
		editor.putString(Cons.USER_COMMUNITYID, "");
		editor.putString(Cons.USER_CUSTOMERID, "");
		editor.putString(Cons.USER_PRIVACY, "");
		editor.putString(Cons.USER_TYPE, "");
		editor.putString(Cons.USER_CHANNEL_GROUP, "");
		editor.putString(Cons.USER_HASBROWSER, "");
		editor.putString(Cons.USER_HAS_STORE, "");
		editor.putString(Cons.USER_STORE_ID, "");

		editor.commit();

		clearAccessToken();
	}

	/*******************************************************************
	 * Till here for about Oauth and SharedPreferences User
	 * 
	 * *****************************************************************
	 */
	
	public SQLiteDatabase getDatabase() {
		return mSqLite;
	}
	
	public SharedPreferences getSharedPreferences() {
		return mSharedPref;
	}
	
	public int getDeviceType() {
		return (Util.isHoneycombTablet(this)) ? 3 : 2;
	}
	
	public String getOS() {
		return Build.VERSION.RELEASE;
	}
	
	public String getLatestUpdate() {
		
		return mSharedPref.getString(Cons.LASTUPD_KEY, "2014-01-29 00:00:00");
	}
	
	public BaseActivity getActivity() {
		return this;
	}

	protected void enableDatabase() {
		mEnableDb = true;

		openDatabase();
	}
	
	public void logout() {
		Editor editor = mSharedPref.edit();
    	
		//editor.putBoolean(Cons.PM_KEEP_LOGIN, 	false);
    
    	editor.commit();    	
	}

	// This for open Koneksi to Database
	private void openDatabase() {
		if (mIsDbOpen) {
			Debug.i(Cons.TAG, "Database already open");
			return;
		}

		String db = Cons.DBPATH + Cons.DBNAME;

		try {
			mSqLite = SQLiteDatabase.openDatabase(db, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			mIsDbOpen = mSqLite.isOpen();

			Debug.i(Cons.TAG, "Database open");
		} catch (SQLiteException e) {
			Debug.e(Cons.TAG, "Can not open database " + db, e);
		}
	}

	// This for Close Koneksi to Database
	private void closeDatabase() {
		if (!mIsDbOpen)
			return;

		mSqLite.close();

		mIsDbOpen = false;

		Debug.i(Cons.TAG, "Database closed");
	}

}
