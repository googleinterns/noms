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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that stores and retrieves posts. */
@WebServlet("/postdata")
public class PostDataServlet extends HttpServlet {

    /* Convert an ArrayList of Post objects to JSON. */
    private String listToJson(ArrayList<Post> alist) {
        Gson gson = new Gson();
        String json = gson.toJson(alist);
        return json;
    }

     /* 
      * On the GET request, retrieves all the posts from Datastore and convers them to JSON. 
      * If a post is outdated, deletes it.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        String collegeId = request.getParameter("collegeId");

        // Retrieves the Review comments with a Query.
        Query query = new Query(collegeId).addSort("timeSort", SortDirection.ASCENDING);
        PreparedQuery results = datastore.prepare(query);
        ArrayList<Post> posts = Post.queryToPosts(results, datastore);

        String json = listToJson(posts);
        response.setContentType("application/json");
        response.getWriter().println(json);
    }

    /* On the POST command, stores the name and review as a Review entity in Datastore. */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        String collegeId = request.getParameter("collegeId");
        Post newPost = new Post(request, collegeId);
        Entity newPostEntity = newPost.postToEntity();
        datastore.put(newPostEntity);

        String redirectURL ="/find-events.html?" + "collegeid=" + collegeId;
        response.sendRedirect(redirectURL);
    }
}