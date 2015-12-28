package com.example.toshiba.ternakku.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.toshiba.ternakku.R;
import com.example.toshiba.ternakku.http.UserConnection;
import com.example.toshiba.ternakku.http.exeption.LisaException;
import com.example.toshiba.ternakku.model.User;
import com.example.toshiba.ternakku.oauth.OauthToken;
import com.example.toshiba.ternakku.util.BaseActivity;
import com.example.toshiba.ternakku.util.Cons;
import com.example.toshiba.ternakku.util.Debug;
import com.example.toshiba.ternakku.util.Util;

import java.net.URL;

/**
 * Created by Toshiba on 12/4/2015.
 */
public class LoginActivity extends BaseActivity {

    private String mIdentifier;
    private String mPassword;
    private LoginTask mLoginTask;
    private boolean mIsLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        final EditText identifierEt = (EditText) findViewById(R.id.usernamelogin);
        final EditText passwordEt 	= (EditText) findViewById(R.id.passwordlogin);

        Button loginBtn				= (Button) findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mIdentifier = identifierEt.getText().toString().trim();
                mPassword	= passwordEt.getText().toString().trim();

                if (mIdentifier.equals("") || mPassword.equals("")) {
                    showInfo(getString(R.string.text_phone_pass_required));
                    return;
                }

                (mLoginTask = new LoginTask()).execute();
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Cons.RESULT_CLOSE_ALL) {
            finish();
        }
    }

    @Override
    public void onPause() {
        if (mLoginTask != null && mIsLoading) {
            mLoginTask.cancel(true);
        }

        super.onPause();
    }
    public class LoginTask extends AsyncTask<URL, Integer, Long> {
        User user;
        String error = "";

        ProgressDialog progressDlg;

        public LoginTask() {
            progressDlg = new ProgressDialog(getActivity());

            progressDlg.setMessage("wait");
            progressDlg.setCancelable(false);
        }

        protected void onCancelled() {
            mIsLoading = false;

            if (progressDlg.isShowing()) {
                progressDlg.dismiss();
            }
        }

        protected void onPreExecute() {
            mIsLoading = true;

            progressDlg.show();
        }

        protected Long doInBackground(URL... urls) {
            long result = 0;

            try {
                Debug.i("Logging in...");

                OauthToken token 	= getRequestToken();

                UserConnection conn = new UserConnection();

                user	= conn.login(token.getToken(), mIdentifier, mPassword);
                result 	= 1;
            } catch (LisaException e) {
                e.printStackTrace();
                error  = e.getError();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
            progressDlg.dismiss();

            mIsLoading = false;

            if (result == 1 && user != null) {
                saveUser(user);

                Debug.i("Access Token: " + user.accessToken.getToken());
                Debug.i("Token Secret: " + user.accessToken.getSecret());

                ShowToastNew(getString(R.string.text_welcome) + " " + user.fullName);

                Intent intent  = new Intent(getActivity(), MainActivity.class);

                intent.putExtra(Util.getIntentName("login"), false);

                startActivity(intent);

                finish();
            } else {
                error = (error.equals("")) ? getString(R.string.text_login_failed) : error;

                if (error.equals("AccountNotRegistered")) {
                    error = error.replace("AccountNotRegistered", getString(R.string.akun_not_registered));
                }else if (error.equals("IncorrectPassword")) {
                    error = error.replace("IncorrectPassword", getString(R.string.akun_incorrect_password));
                }else if (error.equals("NotAllowed")) {
                    error = error.replace("NotAllowed", getString(R.string.akun_not_allowed));
                }

                showError(error);
            }
        }
    }
}



