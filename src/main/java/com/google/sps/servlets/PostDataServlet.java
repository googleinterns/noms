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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyContainer;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.api.GmailConfiguration;
import com.google.sps.data.Email;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Servlet that stores and retrieves posts. */
@WebServlet("/postData")
public class PostDataServlet extends HttpServlet {
  
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /* Convert an ArrayList of Post objects to JSON. */
  private String listToJson(ArrayList<Post> alist) {
    Gson gson = new Gson();
    String json = gson.toJson(alist);
    return json;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String collegeId = request.getParameter("collegeId");

    // Queries Datastore with the college ID and receives posts such that the soonest events are shown first.
    Query query = new Query(collegeId).addSort("timeSort", SortDirection.ASCENDING);
    PreparedQuery results = datastore.prepare(query);
    ArrayList<Post> posts = Post.queryToPosts(results, datastore);

    // Prepares the relevant posts in JSON.
    String json = listToJson(posts);
    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  /* On the POST command, serializes the request information into Post objects. */
  /* Stores the post as entity in Datastore, with the collegeId as the kind. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException { 
    String collegeId = request.getParameter("collegeId");
    Post newPost = new Post();
    newPost.requestToPost(request, collegeId);
    Entity newPostEntity = newPost.postToEntity();
    datastore.put(newPostEntity);

    // Find all users that attend the college.
    Filter universityFilter = new FilterPredicate("university", FilterOperator.EQUAL, collegeId);
    Query q = new Query("User").setFilter(universityFilter);
    PreparedQuery pq = datastore.prepare(q);

    // Email the users to notify them that a new post has been added.
    for (Entity user : pq.asIterable()) {
      String email= (user.getKey().getName()).toString();
      GmailConfiguration.sendEmail(email, Email.newPostSubject, Email.addNewPost(newPost));	
    }

    String redirectURL ="/find-events.html?" + "collegeid=" + collegeId;
    response.sendRedirect(redirectURL);
  }
}
