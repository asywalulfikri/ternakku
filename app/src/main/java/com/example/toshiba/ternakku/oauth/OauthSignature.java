package com.example.toshiba.ternakku.oauth;


import com.example.toshiba.ternakku.util.Base64;
import com.example.toshiba.ternakku.util.Debug;
import com.example.toshiba.ternakku.util.EncodeUtil;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class OauthSignature {
	public static final String MAC_NAME = "HmacSHA1";

	public String createSignatureBase(String method, String url, String queryString) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(method);
		sb.append("&");
		sb.append(EncodeUtil.encode(url));
		sb.append("&");
		sb.append(EncodeUtil.encode(queryString));
		
		return sb.toString();
	}

	public String createRequestSignature(String signatureBase, String consumerSecret, String tokenSecret)
		throws Exception {
		
		try {
            String keyString 	= EncodeUtil.encode(consumerSecret) + '&' + EncodeUtil.encode(tokenSecret);
            
            Debug.i("key " + keyString);
            
            byte[] keyBytes 	= keyString.getBytes("UTF-8");

            SecretKey key 		= new SecretKeySpec(keyBytes, MAC_NAME);
            Mac mac 			= Mac.getInstance(MAC_NAME);
            
            mac.init(key);

            byte[] text 		= signatureBase.getBytes("UTF-8");

            return Base64.encodeBytes(mac.doFinal(text)).trim();
        } catch (GeneralSecurityException e) {
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
	}
}