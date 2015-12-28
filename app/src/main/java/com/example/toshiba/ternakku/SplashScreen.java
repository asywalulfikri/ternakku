package com.example.toshiba.ternakku;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.WindowManager;

import com.example.toshiba.ternakku.database.ProviderDb;
import com.example.toshiba.ternakku.database.SyncDb;
import com.example.toshiba.ternakku.http.UserConnection;
import com.example.toshiba.ternakku.model.ProviderWrapper;
import com.example.toshiba.ternakku.oauth.OauthConsumer;
import com.example.toshiba.ternakku.oauth.OauthProvider;
import com.example.toshiba.ternakku.oauth.OauthToken;
import com.example.toshiba.ternakku.ui.LoginActivity;
import com.example.toshiba.ternakku.util.BaseActivity;
import com.example.toshiba.ternakku.util.Cons;
import com.example.toshiba.ternakku.util.Debug;
import com.example.toshiba.ternakku.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
public class SplashScreen extends BaseActivity {

    private TokenTask mTokenTask;
    private ProviderTask mProviderTask;

    private SyncDb mSyncDb;
    private ProviderDb mProviderDb;

    private boolean mIsLoading = false;

    public final static String REGID_SHARED = "regid";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String Sender_id = Cons.GCM_SENDER_ID;

    static final String TAG = "GCMDemo";

    AsyncTask<Void, Void, Void> mRegisterTask;

    Context context;

    SharedPreferences prefs;

    String regId = "";

    private GoogleCloudMessaging gcm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Util.createAppDir();

        context = getApplicationContext();

//		if (checkPlayServices()) {
//			gcm 		= GoogleCloudMessaging.getInstance(this);
//			regId		= getRegistrationId(context);
//
//
//			registerInBackground();
//
//		}else {
//			Log.i(TAG, "No valid Google Play Services APK found.");
//		}

        initDatabase();

        mSyncDb 	= new SyncDb(getDatabase());
        mProviderDb = new ProviderDb(getDatabase());
    }

    @Override
    protected void onPause() {
        if (mIsLoading && mTokenTask != null) {
            mTokenTask.cancel(true);
        }

        if (mIsLoading && mProviderTask != null) {
            mProviderTask.cancel(true);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSyncDb.reload(getDatabase());
        mProviderDb.reload(getDatabase());

        init();
    }

    private void init() {
//		if (!mProviderDb.providerTableExist()) {
//			mProviderDb.createTable();
//		}

        if (getAccessToken() != null) {
            new CountDownTimer(1000,100) {
                public void onTick(long millisUntilFinished) {}

                public void onFinish() {

                    startActivity(new Intent(getActivity(),LoginActivity.class));
                    finish();
                }
            }.start();


        } else {
            (mTokenTask = new TokenTask()).execute();
        }
    }

    protected void initDatabase() {
        Util.createAppDir();

        String database = Cons.DBPATH + Cons.DBNAME;
        int currVersion	= mSharedPref.getInt(Cons.DBVER_KEY, 0);

        try {
            Debug.i(Cons.TAG, "Current database version is " + String.valueOf(currVersion));

            if (Cons.DB_VERSION > currVersion) {
                File databaseFile  = new File(database);

                if (databaseFile.exists()) {
                    Debug.i(Cons.TAG, "Deleting current database " + Cons.DBNAME);

                    databaseFile.delete();
                }

                InputStream is	= getResources().getAssets().open(Cons.DBNAME);
                OutputStream os = new FileOutputStream(database);

                byte[] buffer	= new byte[1024];
                int length;

                Debug.i(Cons.TAG, "Start copying new database " + database + " version " + String.valueOf(Cons.DB_VERSION));

                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

                os.flush();
                os.close();
                is.close();

                Editor editor = mSharedPref.edit();

                editor.putInt(Cons.DBVER_KEY, Cons.DB_VERSION);
                editor.commit();
            } else {
                if (Cons.ENABLE_DEBUG) {
                    InputStream is	= new FileInputStream(database);
                    OutputStream os = new FileOutputStream(Util.getAppDir() + "/lisa.db");

                    byte[] buffer	= new byte[1024];
                    int length;

                    Debug.i(Cons.TAG, "[DEVONLY] Copying db " + database + " to " + Util.getAppDir() + "/lisa.db");

                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }

                    os.flush();
                    os.close();
                    is.close();
                }
            }
        } catch (SecurityException ex) {
            Debug.e(Cons.TAG, "Failed to delete current database " + Cons.DBNAME, ex);
        } catch (IOException ex) {
            Debug.e(Cons.TAG, "Failed to copy new database " + Cons.DBNAME + " version " + String.valueOf(Cons.DB_VERSION), ex);
        }

        enableDatabase();
    }



    public class TokenTask extends AsyncTask<URL, Integer, Long> {
        OauthToken reqToken;

        protected void onCancelled() {
            mIsLoading = false;
        }

        protected void onPreExecute() {
            mIsLoading = true;
        }

        protected Long doInBackground(URL... urls) {
            long result = 0;

            try {
                OauthConsumer consumer = new OauthConsumer(Cons.CONSUMER_KEY, Cons.CONSUMER_SECRET, "");
                OauthProvider provider = new OauthProvider(consumer, Cons.REQUEST_TOKEN_URL);

                Debug.i("Getting request token...");

                reqToken	= provider.getRequestToken();
                result 		= 1;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
            mIsLoading = false;

            if (result == 1 && reqToken != null) {
                saveRequestToken(reqToken);

                Debug.i("Token: " + reqToken.getToken());
                Debug.i("Secret: " + reqToken.getSecret());
                Debug.i("Verifier: " + reqToken.getVerifier());

                startActivity(new Intent(getActivity(), LoginActivity.class));

                finish();
//				(mProviderTask = new ProviderTask()).execute();
            } else {
                showToast("Failed to get request token.");

                finish();
            }
        }
    }
    public class ProviderTask extends AsyncTask<URL, Integer, Long> {
        ProviderWrapper wrapper;

        protected void onCancelled() {
            mIsLoading = false;
        }

        protected void onPreExecute() {
            mIsLoading = true;
        }

        protected Long doInBackground(URL... urls) {
            long result = 0;

            try {
                UserConnection conn = new UserConnection();

                wrapper = conn.getProvider();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
            mIsLoading = false;

            mProviderDb.update(wrapper.providerList);

            Editor editor = mSharedPref.edit();

            editor.putString(Cons.KEYWORD_REG, wrapper.keywordReg);
            editor.putString(Cons.KEYWORD_RESET, wrapper.keywordReset);

            editor.commit();

            Debug.i("KW 1 " + wrapper.keywordReg);
            Debug.i("KW 2 " + wrapper.keywordReset);

            startActivity(new Intent(getActivity(), LoginActivity.class));

            finish();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {

        return getSharedPreferences(REGID_SHARED,
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    @SuppressWarnings("unused")
    private void sendRegistrationIdToBackend(String regId) {
        String urlPost = Cons.GCM_REGISTER_URL+"open";

        Log.e("URL POST GCM LOGIN", urlPost);

        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("identifier", regId));
        pairs.add(new BasicNameValuePair("vip", Cons.COMUNITY_NAME));
        pairs.add(new BasicNameValuePair("communityId", Cons.COMUNITY_ID));

        DefaultHttpClient httpCLient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(urlPost);
        Log.d("Post Regist", "Post Regist state open");

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs));
            HttpResponse httpResponse = httpCLient.execute(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";

                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(Sender_id);
                    msg = "Device registered, registration ID=" + regId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend(regId);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d("Message Server", msg + "\n");

            }
        }.execute(null, null, null);

    }
}
