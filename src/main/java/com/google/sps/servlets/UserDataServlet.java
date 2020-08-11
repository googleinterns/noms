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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.data.Email;
import com.google.sps.api.GmailConfiguration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/user")
public class UserDataServlet extends HttpServlet {

  private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final Logger LOGGER = Logger.getLogger(UserDataServlet.class.getName());

  /** Add/update a User into Datastore */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get the input from the form.
    String name = request.getParameter("name");
    String email = request.getParameter("email");
    String college = request.getParameter("cID");
    String subscription = request.getParameter("email-notif");

    // Check if user wants to unsubscribe.
    if (subscription.equals("unsubscribe")) {

      // Remove user if in database.
      Key userKey = KeyFactory.createKey("User", email);
      try {
        datastore.get(userKey);
        datastore.delete(userKey);
      } catch (EntityNotFoundException e) {
        LOGGER.log(Level.SEVERE, "User wants to unsubscribe but was never subscribed in the first place. ", e);
      }

    } else {

      Entity userEntity = new Entity("User", email);
      userEntity.setProperty("name", name);
      userEntity.setProperty("college", college);

      // Datastores updates the entity if it existed before based on email key.
      datastore.put(userEntity);

      // Send a welcome email.
      GmailConfiguration.sendEmail(email, Email.welcomeSubject, Email.welcomeContent);	
    }

    response.sendRedirect("/index.html");
  }
}
