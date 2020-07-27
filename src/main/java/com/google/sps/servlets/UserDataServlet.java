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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.data.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user")
public class UserDataServlet extends HttpServlet {

  /** Responsible for creating new User. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get the input from the form.
    String name = getParameter(request, "name", "");
    String email = getParameter(request, "email", "");
    String university = getParameter(request, "university", "");
    String subscription = request.getParameter("email-notif");
    boolean subscribe;

    if (subscription.equals("subscribe")) {
      subscribe = true;
    } else if (subscription.equals("unsubscribe")) {
      subscribe = false;
    }

    // Create an Entity type User.
    Entity userEntity = new Entity("User");
    userEntity.setProperty("name", name);
    userEntity.setProperty("email", email);
    userEntity.setProperty("university", university);

    // Store the User in Datastore and refresh page.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(userEntity);
    response.sendRedirect("/index.html");
  }

  /**
   * @return the request parameter, or the default value if the parameter was not specified by the
   *     client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
