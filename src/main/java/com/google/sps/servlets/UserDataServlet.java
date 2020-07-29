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
import com.google.appengine.api.datastore.EntityNotFoundException;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user")
public class UserDataServlet extends HttpServlet {

  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /** Responsible for add a new User into Datastore */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get the input from the form.
    String name = request.getParameter("name");
    String email = request.getParameter("email");
    String university = request.getParameter("university");
    String subscription = request.getParameter("email-notif");
    boolean subscribe = true;

    // Check if user wants to unsubscribe.
    if (subscription.equals("unsubscribe")) {
      subscribe = false;
    }

    // Create an Entity type User.
    Entity userEntity = new Entity("User", email);
    userEntity.setProperty("name", name);
    userEntity.setProperty("university", university);
    userEntity.setProperty("subscribe", subscribe);

    // Store the User in Datastore and Datastore automatically checks for duplicates
    // and will update the user information accordingly to new information based on emails
    datastore.put(userEntity);
    response.sendRedirect("/index.html");
  }
}
