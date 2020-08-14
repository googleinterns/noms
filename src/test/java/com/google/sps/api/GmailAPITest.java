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

import static org.mockito.Mockito.when;

import com.google.sps.data.Post;

import java.io.File;
import java.io.IOException;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Tests retrieving access tokens based on refresh token. */
@RunWith(JUnit4.class)
public final class GmailAPITest {

  @Test
  public void POSTrequestSuccess() {
    // POST request is successfully built, sent, and returns a nresponse.
    
  }

  @Test(expected = UnsupportedEncodingException.class)
  public void POSTrequestFailedWithEncoding() {
    // POST request build fails on encoding.


  }

  @Test(expected = IOException.class)
  public void POSTrequestFailedWithResponse() {
    // POST request fails on retrieving a response.


  }
}
