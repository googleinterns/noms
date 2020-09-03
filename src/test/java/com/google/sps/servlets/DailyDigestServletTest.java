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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.Post;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;   
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Tests ranking posts for the Daily Digest. */
@RunWith(JUnit4.class)
public class DailyDigestServletTest {
  private static final String ORGANIZATION_NAME = "TEST ORGANIZATION NAME";
  private static final String DESCRIPTION = "TEST DESCRIPTION";
  private static final String LOCATION = "TEST LOCATION";
  private static final double LAT = 1.1;
  private static final double LNG = 1.1;
  private static final String TYPE_OF_FOOD = "TEST TYPE OF FOOD";
  private static final String COLLEGE_ID = "000000";

  // Get today's date (plus 1 because Calendar Months are indexed from 0).
  private static final Calendar TODAY = Calendar
    .getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
  private static final int MONTH = TODAY.get(Calendar.MONTH) + 1;
  private static final int DAY = TODAY.get(Calendar.DATE);

  private final LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static DailyDigestServlet dailyDigestServlet;
  private static DatastoreService datastore;

  @Mock private static PreparedQuery mockPreparedQuery;
  @Mock private static HttpServletRequest mockRequest;

  @Before
  public void setUp() throws Exception {
    helper.setUp();
    dailyDigestServlet = new DailyDigestServlet();
    datastore = DatastoreServiceFactory.getDatastoreService();
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /* Get a post created from requestToPost() by mocking a request. */
  private Post requestToPostHelper(String collegeId, String organizationName, int month, int day, 
      int startHour, int startMinute, String startAMorPM, int endHour, 
        int endMinute, String endAMorPM, String location, double lat, double lng, 
          int numberOfPeopleItFeeds, String typeOfFood, String description) {
    when(mockRequest.getParameter("organizationName")).thenReturn(organizationName);
    when(mockRequest.getParameter("month")).thenReturn(Integer.toString(month));
    when(mockRequest.getParameter("day")).thenReturn(Integer.toString(day));
    when(mockRequest.getParameter("startHour")).thenReturn(Integer.toString(startHour));
    when(mockRequest.getParameter("startMinute")).thenReturn(Integer.toString(startMinute));
    when(mockRequest.getParameter("startAMorPM")).thenReturn(startAMorPM);
    when(mockRequest.getParameter("endHour")).thenReturn(Integer.toString(endHour));
    when(mockRequest.getParameter("endMinute")).thenReturn(Integer.toString(endMinute));
    when(mockRequest.getParameter("endAMorPM")).thenReturn(endAMorPM);
    when(mockRequest.getParameter("location")).thenReturn(location);
    when(mockRequest.getParameter("lat")).thenReturn(Double.toString(lat));
    when(mockRequest.getParameter("lng")).thenReturn(Double.toString(lng));
    when(mockRequest.getParameter("numberOfPeopleItFeeds")).thenReturn(Integer.toString(numberOfPeopleItFeeds));
    when(mockRequest.getParameter("typeOfFood")).thenReturn(typeOfFood);
    when(mockRequest.getParameter("description")).thenReturn(description);
    when(mockRequest.getParameter("collegeId")).thenReturn(collegeId);
    Post testPost = new Post();
    testPost.requestToPost(mockRequest);
    return testPost;
  }

  @Test
  public void testRankPostsFromToday() {
    Post todayLowRankTestPost = 
      requestToPostHelper(COLLEGE_ID, ORGANIZATION_NAME, MONTH, DAY, 
        5, 00, "am", 5, 30, "pm", LOCATION, LAT, LNG, 
        50, TYPE_OF_FOOD, DESCRIPTION);
    Post todayHighRankTestPost = 
      requestToPostHelper(COLLEGE_ID, ORGANIZATION_NAME, MONTH, DAY, 
        5, 00, "pm", 5, 45, "pm", LOCATION, LAT, LNG, 
        100, TYPE_OF_FOOD, DESCRIPTION);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(todayLowRankTestPost.postToEntity("Post"));
    datastore.put(todayHighRankTestPost.postToEntity("Post"));
  
    Query q = new Query("Post").setFilter(dailyDigestServlet.getFilters(COLLEGE_ID));
    PreparedQuery pq = datastore.prepare(q);
    when(mockPreparedQuery.asIterable()).thenReturn(pq.asIterable());

    ArrayList<Post> rankedPosts = dailyDigestServlet.rankPosts(COLLEGE_ID);
    
    Assert.assertEquals(2, rankedPosts.size());
    Assert.assertEquals(0.55, rankedPosts.get(0).getRank(), 0.05);
    Assert.assertEquals(0.25, rankedPosts.get(1).getRank(), 0.05);
  }

  @Test
  public void testRankPostsFromAnotherDay() throws Exception {
    Post futureTestPost = 
      requestToPostHelper(COLLEGE_ID, ORGANIZATION_NAME, 9, 15, 
        5, 00, "pm", 5, 30, "pm", LOCATION, LAT, LNG, 
        50, TYPE_OF_FOOD, DESCRIPTION);       
    Post pastTestPost = 
      requestToPostHelper(COLLEGE_ID, ORGANIZATION_NAME, 8, 15, 
        5, 00, "pm", 5, 30, "pm", LOCATION, LAT, LNG, 
        100, TYPE_OF_FOOD, DESCRIPTION);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(futureTestPost.postToEntity("Post"));
    datastore.put(futureTestPost.postToEntity("Post"));

    Query q = new Query("Post").setFilter(dailyDigestServlet.getFilters(COLLEGE_ID));
    PreparedQuery pq = datastore.prepare(q);
    when(mockPreparedQuery.asIterable()).thenReturn(pq.asIterable());

    ArrayList<Post> rankedPosts = dailyDigestServlet.rankPosts(COLLEGE_ID);

    Assert.assertEquals(0, rankedPosts.size());
  }

  @Test
  public void testRankManyPosts() throws Exception {
    Post testPost1 = 
      requestToPostHelper(COLLEGE_ID, ORGANIZATION_NAME, MONTH, DAY, 
        5, 00, "pm", 9, 30, "pm", LOCATION, LAT, LNG, 
        150, TYPE_OF_FOOD, DESCRIPTION);
    Post testPost2 = 
      requestToPostHelper(COLLEGE_ID, ORGANIZATION_NAME, MONTH, DAY, 
        3, 00, "pm", 4, 00, "pm", LOCATION, LAT, LNG, 
        200, TYPE_OF_FOOD, DESCRIPTION);
    Post testPost3 = 
      requestToPostHelper(COLLEGE_ID, ORGANIZATION_NAME, MONTH, DAY, 
        5, 00, "pm", 5, 01, "pm", LOCATION, LAT, LNG, 
        10, TYPE_OF_FOOD, DESCRIPTION);
    Post testPost4 = 
      requestToPostHelper(COLLEGE_ID, ORGANIZATION_NAME, MONTH, DAY, 
        5, 00, "pm", 5, 45, "pm", LOCATION, LAT, LNG, 
        70, TYPE_OF_FOOD, DESCRIPTION);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(testPost1.postToEntity("Post"));
    datastore.put(testPost2.postToEntity("Post"));
    datastore.put(testPost3.postToEntity("Post"));
    datastore.put(testPost4.postToEntity("Post"));

    Query q = new Query("Post").setFilter(dailyDigestServlet.getFilters(COLLEGE_ID));
    PreparedQuery pq = datastore.prepare(q);
    when(mockPreparedQuery.asIterable()).thenReturn(pq.asIterable());

    ArrayList<Post> rankedPosts = dailyDigestServlet.rankPosts(COLLEGE_ID);

    Assert.assertEquals(3, rankedPosts.size());
    Assert.assertEquals(0.5, rankedPosts.get(0).getRank(), 0.05);
    Assert.assertEquals(0.38, rankedPosts.get(1).getRank(), 0.05);
    Assert.assertEquals(0.26, rankedPosts.get(2).getRank(), 0.05);
  }
}
