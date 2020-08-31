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
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.Post;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;   
import java.util.TimeZone;

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
  private static final double LAT = 0.1;
  private static final double LNG = 0.1;
  private static final String TYPE_OF_FOOD = "TEST TYPE OF FOOD";
  private static final String COLLEGE_ID = "TEST COLLEGE ID";

  private static Calendar TODAY = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
  private static int MONTH = TODAY.get(Calendar.MONTH);
  private static int DAY = TODAY.get(Calendar.DATE);

  Post todayLowRankTestPost = new Post(ORGANIZATION_NAME, MONTH, DAY, 5, 0, 5, 30, LOCATION, LAT, LNG, 
                            50, TYPE_OF_FOOD, DESCRIPTION, COLLEGE_ID);
  Post todayHighRankTestPost = new Post(ORGANIZATION_NAME, MONTH, DAY, 5, 0, 5, 45, LOCATION, LAT, LNG, 
                            100, TYPE_OF_FOOD, DESCRIPTION, COLLEGE_ID);
  Post futureTestPost = new Post(ORGANIZATION_NAME, 9, 15, 5, 0, 5, 30, LOCATION, LAT, LNG, 
                            50, TYPE_OF_FOOD, DESCRIPTION, COLLEGE_ID);                      
  Post pastTestPost = new Post(ORGANIZATION_NAME, 8, 15, 5, 0, 5, 30, LOCATION, LAT, LNG, 
                            50, TYPE_OF_FOOD, DESCRIPTION, COLLEGE_ID);

  private final LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static DailyDigestServlet dailyDigestServlet;
  private static DatastoreService datastore;

  @Mock private static PreparedQuery mockPreparedQuery;

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

  @Test
  public void testRankPostsFromToday() {

    datastore.put(todayLowRankTestPost.postToEntity());
    datastore.put(todayHighRankTestPost.postToEntity());
    Query q = new Query(COLLEGE_ID).setFilter(dailyDigestServlet.getTodayFilter());
    PreparedQuery pq = datastore.prepare(q);
    when(mockPreparedQuery.asIterable()).thenReturn(pq.asIterable());

    ArrayList<Post> rankedPosts = dailyDigestServlet.rankPosts(COLLEGE_ID);
    
    Assert.assertEquals(2, rankedPosts.size());
    Assert.assertEquals(80, rankedPosts.get(0).getRank());
    Assert.assertEquals(145, rankedPosts.get(1).getRank());
  }

  @Test
  public void testRankPostsFromAnotherDay() throws Exception {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(futureTestPost.postToEntity());
    datastore.put(pastTestPost.postToEntity());
    Query q = new Query(COLLEGE_ID).setFilter(dailyDigestServlet.getTodayFilter());
    PreparedQuery pq = datastore.prepare(q);
    when(mockPreparedQuery.asIterable()).thenReturn(pq.asIterable());

    ArrayList<Post> rankedPosts = dailyDigestServlet.rankPosts(COLLEGE_ID);

    Assert.assertEquals(0, rankedPosts.size());
  }

  @Test
  public void testRankManyPosts() throws Exception {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(todayLowRankTestPost.postToEntity());
    datastore.put(todayHighRankTestPost.postToEntity());
    datastore.put(futureTestPost.postToEntity());
    datastore.put(pastTestPost.postToEntity());
    Query q = new Query(COLLEGE_ID).setFilter(dailyDigestServlet.getTodayFilter());
    PreparedQuery pq = datastore.prepare(q);
    when(mockPreparedQuery.asIterable()).thenReturn(pq.asIterable());

    ArrayList<Post> rankedPosts = dailyDigestServlet.rankPosts(COLLEGE_ID);

    Assert.assertEquals(2, rankedPosts.size());
    Assert.assertEquals(80, rankedPosts.get(0).getRank());
    Assert.assertEquals(145, rankedPosts.get(1).getRank());
  }
}
