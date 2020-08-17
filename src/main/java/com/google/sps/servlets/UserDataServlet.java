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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Servlet that adds, updates, and deletes Users in Datastore */
@WebServlet("/user")
public class UserDataServlet extends HttpServlet {

  private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final Logger LOGGER = LoggerFactory.getLogger(UserDataServlet.class);

  /** POST a user's information. */
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
        LOGGER.info("User was unsubscribed and removed from Datastore.");
      } catch (EntityNotFoundException e) {
        LOGGER.warn("User was unable to unsubscribe but was never subscribed.");
      }

    } else {

      Entity userEntity = new Entity("User", email);
      userEntity.setProperty("name", name);
      userEntity.setProperty("college", college);

      // Datastores updates the entity if it existed before based on email key.
      datastore.put(userEntity);
      LOGGER.info("User was subscribed to email notifications and added to Datastore.");

      // Send a welcome email.
      GmailConfiguration.sendEmail(email, Email.welcomeSubject, Email.getWelcomeString());	
    }

    response.sendRedirect("/index.html");
  }
}
