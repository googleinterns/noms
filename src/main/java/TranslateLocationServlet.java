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

package com.google.sps.servlets;

import com.google.cloud.language.v1.Document;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GaeRequestHandler;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that translates locations written in words to latitude and longitude. */
@WebServlet("/translateLocation")
public class TranslateLocationServlet extends HttpServlet {

  /** POST a location and get back a latitude and longitude. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get the input from the form.
    String location = getParameter(request, "location", "");

    // Check for validity.
    if (location.isEmpty()) {
      response.setStatus(400);
      return;
    }

    String projectId = "step186-2020";
    String secretId = "geocoding-api-key";
    String versionId = "latest";
    String geocodingApiKey = "";

    // Retrieve the secret key for the Geocoding API.
    try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
      SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, versionId);
      AccessSecretVersionResponse secretResponse = client.accessSecretVersion(secretVersionName);
      geocodingApiKey = secretResponse.getPayload().getData().toStringUtf8();
    }

    // Try to get the latitude and longitude for the given address.
    try {
      GeoApiContext context = new GeoApiContext.Builder(new GaeRequestHandler.Builder()).apiKey(geocodingApiKey).build();
      GeocodingResult[] results =  GeocodingApi.geocode(context, location).await();
      Gson gson = new GsonBuilder().setPrettyPrinting().create();

      response.setStatus(200);
      response.setContentType("text/json; charset=UTF-8");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().println(gson.toJson(results[0]));
    }
    catch (Exception e) {
      response.setStatus(500);
      return;
    }
  }

  /**
   * @return the request parameter, or the default value if the parameter was not specified by the
   *     client.
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null || value.isEmpty()) {
      return defaultValue;
    }
    return value;
  }
}
