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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.sps.data.Email;
import com.google.sps.api.GmailConfiguration;
import com.google.sps.data.Post;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.Calendar;
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
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Query all colleges from Datastore.
    // for ( Entity college : datastore.getAllColleges ) : String collegeId = (String) college.getProperty("collegeId");
    ArrayList<Post> rankedPosts = rankPosts(collegeId);
    if (rankedPosts.size() > 0) {
      GmailConfiguration.sendEmail(email, Email.dailyDigestSubject, Email.addRankedPosts(rankedPosts));	
    }
  }

  /**
    * Rank today's posts in relation to a specific college.
    *
    * @param collegeId of a college
    * @return 3 max of the most highly ranked posts
    */
  private static ArrayList<Post> rankPosts(String collegeId) throws TooManyResultsException {

    ArrayList<Post> rankedPosts = new ArrayList<Post>();
    
    // Get today's date.
    Calendar today = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
    int month = today.get(Calendar.MONTH);
    int day = today.get(Calendar.DATE);

    // Filter through a college to find posts that happen today.
    Filter monthFilter = new FilterPredicate("month", FilterOperator.EQUAL, month);
    Filter dayFilter = new FilterPredicate("day", FilterOperator.EQUAL, day);
    CompositeFilter monthAndDayFilter = CompositeFilterOperator.and(monthFilter, dayFilter);

    // Use amount of people an event can feed as most impactful rank element to sort.
    Query q = new Query(collegeId).setFilter(monthAndDayFilter)
      .addSort("numberOfPeopleItFeeds", SortDirection.DESCENDING);
    PreparedQuery pq = datastore.prepare(q);

    // Add the posts to return our ranked elemetns.
    for (Entity entity: pq.asIterable(FetchOptions.Builder.withLimit(3))) {
      Post newPost = new Post();
      newPost.entityToPost(entity);
      rankedPosts.add(newPost);
    }

    return rankedPosts;
  }
}
  