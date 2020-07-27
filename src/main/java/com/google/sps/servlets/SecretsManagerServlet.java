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

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that retrieves secrets for client-side use. Keeps keys out of source control. */
@WebServlet("/secretsManager")
public class SecretsManagerServlet extends HttpServlet {

  /** POST a secret unique id and get back the corresponding secret value. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get the input from the form.
    String secretId = getParameter(request, "id", "");

    // Check for validity.
    if (secretId.isEmpty()) {
      response.setStatus(400);
      return;
    }

    String projectId = "step186-2020";
    String versionId = "latest";
    String geocodingApiKey = "";

    // Retrieve the secret key for the Geocoding API.
    try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
      SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, versionId);
      AccessSecretVersionResponse secretResponse = client.accessSecretVersion(secretVersionName);
      String secret = secretResponse.getPayload().getData().toStringUtf8();

      response.setStatus(200);
      response.setCharacterEncoding("UTF-8");
      response.getWriter().println(secret);
    } catch (Exception e) {
      response.setStatus(400);
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
