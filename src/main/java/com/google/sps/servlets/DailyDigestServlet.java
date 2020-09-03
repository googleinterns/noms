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

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

import com.google.sps.data.Email;
import com.google.sps.api.GmailConfiguration;
import com.google.sps.data.Post;

import java.lang.Math;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;   
import java.util.TimeZone;
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

  /** GETs information about users to send daily digest emails. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Query all colleges from Datastore.
    Query q = new Query("College");
    PreparedQuery pq = datastore.prepare(q);
    for (Entity college : pq.asIterable()) {

      // Send users a daily digest email about the top ranked 3 posts.
      String collegeId = college.getKey().getName().toString();
      ArrayList<Post> rankedPosts = rankPosts(collegeId);
      if (rankedPosts.size() > 0) {
        GmailConfiguration.notifyUsers(collegeId, rankedPosts);
      }
    }
  }

  /**
    * Rank a college's posts based on today's date, how many people it can
    * feed, and the length of an event.
    *
    * @param collegeId of a college
    * @return 3 max of the most highly ranked posts
    */
  public static ArrayList<Post> rankPosts(String collegeId) throws TooManyResultsException {
    ArrayList<Post> posts = new ArrayList<Post>();
    
    // Filters for given college and today's date.
    Query q = new Query("Post").setFilter(getFilters(collegeId));
    PreparedQuery pq = datastore.prepare(q);
    for (Entity entity : pq.asIterable()) {
      Post newPost = new Post();
      newPost.entityToPost(entity);
      posts.add(newPost);
    }

    // Get top 3 ranked posts by sorting the posts.
    Collections.sort(posts, Collections.reverseOrder());
    int size = Math.min(3, posts.size());
    ArrayList<Post> rankedPosts = new ArrayList<Post>();
    for(int i = 0; i < size; i++) {
      rankedPosts.add(posts.get(i));
    }

    return rankedPosts;
  }

  /**
    * Create a filter that limits results to today's posts and to a college.
    *
    * @return month AND day AND college filter
    */
  public static CompositeFilter getFilters(String collegeId) {
    // Get today's date (no need to index because Post automatically index to start from montn 0).
    Calendar today = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
    int month = today.get(Calendar.MONTH);
    int day = today.get(Calendar.DATE);

    // Filter through a college to find posts that happen today.
    Filter monthFilter = new FilterPredicate("month", FilterOperator.EQUAL, month);
    Filter dayFilter = new FilterPredicate("day", FilterOperator.EQUAL, day);
    Filter collegeFilter = new FilterPredicate("collegeId", FilterOperator.EQUAL, collegeId);

    CompositeFilter filters = new CompositeFilter(CompositeFilterOperator.AND, 
      Arrays.<Filter>asList(monthFilter, dayFilter, collegeFilter));

    return filters;
  }
}
