package com.example.toshiba.ternakku.http;


import android.util.Log;

import com.example.toshiba.ternakku.http.exeption.LisaException;
import com.example.toshiba.ternakku.oauth.OauthAccessToken;
import com.example.toshiba.ternakku.oauth.OauthConsumer;
import com.example.toshiba.ternakku.util.Cons;
import com.example.toshiba.ternakku.util.Debug;

import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class QuestionConnection extends HttpConn {

    public QuestionConnection(OauthConsumer consumer,
                              OauthAccessToken acessToken) {
        super(consumer, acessToken);
    }

    public void postQuestion(String content) throws Exception, LisaException {

        try {
            Debug.i("Posting question...");
            Debug.i(content);

            List<NameValuePair> params = new ArrayList<NameValuePair>(1);

            params.add(new BasicNameValuePair("content", content));


            String response 	= connectPost(Cons.CONVERSATION_URL + "/content/questions", params);

            JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();

            Debug.i("Response " + response);

            if (!jsonObj.isNull("code")) {
                throw new LisaException(jsonObj.getString("message"));
            }

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    public void postQuestion(String content, String image) throws Exception, LisaException {

        try {
            Debug.i("Posting question with image...");
            Debug.i(content);
            Debug.i(image);

            MultipartEntityBuilder builder 	= MultipartEntityBuilder.create();

            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            builder.addBinaryBody("attachment", new File(image));
            builder.addTextBody("content", content, ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8));


            List<NameValuePair> params 		= new ArrayList<NameValuePair>(1);

            params.add(new BasicNameValuePair("content", content));


            String response 	= connectPostMultiPart(Cons.CONVERSATION_URL + "/content/questions", params, builder.build());

            JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();

            Debug.i("Response " + response);

            if (!jsonObj.isNull("code")) {
                throw new LisaException(jsonObj.getString("message"));
            }

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }
    public void putStoreTags(String location, String idStore) throws Exception, LisaException {

        try {
            Log.e("Store", "Edit Store Tags");

            List<NameValuePair> params 		= new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("custom-location", location));
            params.add(new BasicNameValuePair("tags", location.replaceAll(" ", "")));

            String response 	= connectPut(Cons.CONVERSATION_URL + "/content/" + idStore, params);

            JSONObject jsonObj 	= (JSONObject) new JSONTokener(response).nextValue();

            Debug.i("Response " + response);

            if (!jsonObj.isNull("code")) {
                throw new LisaException(jsonObj.getString("message"));
            }

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }
}

