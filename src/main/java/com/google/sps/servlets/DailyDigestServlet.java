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
import com.google.sps.data.Post;

import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// More information can be found here: http://www.slf4j.org/manual.html.
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Servlet that sends daily digest emails to all users. */
@WebServlet("/dailyDigest")
public class DailyDigestServlet extends HttpServlet {

  private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  private static final Logger LOGGER = LoggerFactory.getLogger(UserDataServlet.class);

  /** GETs information about users to send daily digest emails. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Query all colleges from Datastore.

    // For each college, rankPosts()

    // For each arraylist, add those ranked posts into the email 



    // send that email
   
    
  }

  /**
    * Rank today's posts in relation to a specific college.
    *
    * @param collegeId of a college
    * @return 3 max of the most highly ranked posts
    */
  private static ArrayList<Post> rankPosts(String collegeId) {
    
    // 

  }
}
  