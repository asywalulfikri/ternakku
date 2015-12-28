package com.example.toshiba.ternakku.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.toshiba.ternakku.R;
import com.example.toshiba.ternakku.helper.AlertDialogManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpParameters;


public class Startup extends Activity {

//	this for send message text using intent
	public final static String EXTRA_MESSAGE = "lisa";
	
	EditText etRequestToken;
	String getToken, rawToken, tokenMessage;
	ProgressBar pbStartup;
	OAuthConsumer consumer;
	AlertDialogManager alert = new AlertDialogManager();

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup_layout);

		etRequestToken = (EditText) findViewById(R.id.etRequestToken);
		pbStartup = (ProgressBar) findViewById(R.id.pbStartup);

//		Check Connection
		if (isOnline()) {
			new getToken().execute();
		} else {
			alert.showAlertDialog(Startup.this, "Error", "No Network Connection", false);	
		}

//		This for move to Login Form
		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(2000);
				} catch (InterruptedException e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally {
					Intent intent = new Intent(Startup.this, LoginActivity.class);
//					String message = etRequestToken.getText().toString();
					String message = tokenMessage;
					intent.putExtra(EXTRA_MESSAGE, message);
					startActivity(intent);
				}
			}
		};
		timer.start();
	}

	@SuppressWarnings("rawtypes")
	private class getToken extends AsyncTask {

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
//			HttpManager manage = new HttpManager();
			try {
				getRequestToken();
			} catch (OAuthMessageSignerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

//	This for check Connection
	protected boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		} else {
			return false;
		}
	}

//	This for get Request Token from REST API
	private void getRequestToken() throws IOException,
			OAuthMessageSignerException, OAuthExpectationFailedException,
			OAuthCommunicationException {

		final String uri = "https://accounts.8villages.com/oauth/request-token";

		URL url = new URL(uri);
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		urlConnection.setRequestProperty("Accept", "application/json");
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoOutput(true);

		consumer = new DefaultOAuthConsumer("Ha4MbCabE3XBlALPUMIz5P0EUE0.",
				"Qr5cwJPq4pN0gil3SbmXLIsrYy_EpULHNa2bmpM9l0w.");

		HttpParameters doubleEncodedParams = new HttpParameters();
		doubleEncodedParams.put("realm", uri);
		consumer.setAdditionalParameters(doubleEncodedParams);
		consumer.sign(urlConnection);

		try {

			int rawCode = urlConnection.getResponseCode();
			String statusCode = String.valueOf(rawCode);
			Log.d("Status Code", statusCode);

			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());

			@SuppressWarnings("resource")
			String inputStreamString = new Scanner(in, "UTF-8").useDelimiter(
					" ").next();

			String[] temp = inputStreamString.split("&");

			String tempToken = temp[0].toString();
			rawToken = tempToken.substring(12, 40);

			Log.d("request token", rawToken);

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		} finally {
			urlConnection.disconnect();
		}
//		etRequestToken.setText(rawToken);
		tokenMessage = rawToken;
	}
	
//	This for destroy app
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

}
