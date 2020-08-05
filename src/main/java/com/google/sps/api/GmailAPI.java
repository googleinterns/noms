// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.api;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.sps.api.Token;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

/** Set up Gmail API credentials. */
public class GmailAPI {

  private static final String APPLICATION_NAME = "noms";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private static final String USER = "me";
  private static final File CREDENTIALS_FILE_PATH = new File(System.getProperty("user.dir") + "/credentials.json");

  private static String CLIENT_ID = "";
  private static String CLIENT_SECRET = "";
  private static Gmail service = null;

  /**
    * Create a Gmail service using credentials.
    *
    * @throws IOException
    * @throws GeneralSecurityException
    */
  public static Gmail getGmailService() throws IOException, GeneralSecurityException {

    // Gather client info from OAuth 2.0 credentials.
    InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH); 
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    CLIENT_ID = clientSecrets.getDetails().getClientId().toString();
    CLIENT_SECRET = clientSecrets.getDetails().getClientSecret().toString();

    // Set up credentials to use the Gmail API.
    Credential authorize = new GoogleCredential.Builder().setTransport(GoogleNetHttpTransport.newTrustedTransport())
      .setJsonFactory(JSON_FACTORY)
      .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
      .build()
      .setRefreshToken(Token.REFRESH_TOKEN)
      .setAccessToken(getAccessToken());

    // Build service.
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, authorize)
      .setApplicationName(APPLICATION_NAME).build();

    return service;
  }

  /**
  * Retrieve new access token due to the fact that it has a lifetime of 1 hour
  * through refresh token.
  *
  * @param refresh_token Authorized token to create access tokens.
  */
  private static String getAccessToken() 
      throws IOException, MalformedURLException, ProtocolException, UnsupportedEncodingException {

    // Gather POST parameters.
    Map<String, Object> params = new LinkedHashMap<>();
    params.put("grant_type", "refresh_token");
    params.put("client_id", CLIENT_ID);
    params.put("client_secret", CLIENT_SECRET); 
    params.put("refresh_token", Token.REFRESH_TOKEN);

    // Build POST request.
    StringBuilder postData = new StringBuilder();
    for (Map.Entry<String, Object> param : params.entrySet()) {
      if (postData.length() != 0) {
        postData.append('&');
      }
      postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
      postData.append('=');
      postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
    }

    byte[] postDataBytes = postData.toString().getBytes("UTF-8");

    // Send POST request for new access token.
    URL url = new URL("https://accounts.google.com/o/oauth2/token");
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setDoOutput(true);
    con.setUseCaches(false);
    con.setRequestMethod("POST");
    con.getOutputStream().write(postDataBytes);

    // Read POST response.
    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
    StringBuffer buffer = new StringBuffer();
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      buffer.append(line);
    }

    // Return select access token from POST response.
    JSONObject json = new JSONObject(buffer.toString());
    String accessToken = json.getString("access_token");
    return accessToken;
  }
}
