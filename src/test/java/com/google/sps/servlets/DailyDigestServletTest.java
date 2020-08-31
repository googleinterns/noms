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

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.data.Post;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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

  Post todayLowRankTestPost = new Post(ORGANIZATION_NAME, 8, 31, 5, 0, 5, 30, LOCATION, LAT, LNG, 
                            50, TYPE_OF_FOOD, DESCRIPTION, COLLEGE_ID);
  Post todayHighRankTestPost = new Post(ORGANIZATION_NAME, 8, 31, 5, 0, 5, 45, LOCATION, LAT, LNG, 
                            100, TYPE_OF_FOOD, DESCRIPTION, COLLEGE_ID);
  Post futureTestPost = new Post(ORGANIZATION_NAME, 9, 15, 5, 0, 5, 30, LOCATION, LAT, LNG, 
                            50, TYPE_OF_FOOD, DESCRIPTION, COLLEGE_ID);                      
  Post pastTestPost = new Post(ORGANIZATION_NAME, 8, 15, 5, 0, 5, 30, LOCATION, LAT, LNG, 
                            50, TYPE_OF_FOOD, DESCRIPTION, COLLEGE_ID);

  private final LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static DailyDigestServlet dailyDigestServlet;

  @Before
  public void setUp() throws Exception {
    helper.setUp();
    dailyDigestServlet = new DailyDigestServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testRankPostsFromToday() throws Exception {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(todayLowRankTestPost.postToEntity());
    datastore.put(todayHighRankTestPost.postToEntity());

    ArrayList<Post> rankedPosts = dailyDigestServlet.rankPosts(COLLEGE_ID);

    Assert.assertEquals(2, rankedPosts.size());
    Assert.assertEquals(todayHighRankTestPost, rankedPosts.get(0));
    Assert.assertEquals(todayLowRankTestPost, rankedPosts.get(1));
  }

  @Test
  public void testRankPostsFromAnotherDay() throws Exception {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(futureTestPost.postToEntity());
    datastore.put(pastTestPost.postToEntity());

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

    ArrayList<Post> rankedPosts = dailyDigestServlet.rankPosts(COLLEGE_ID);

    Assert.assertEquals(2, rankedPosts.size());
    Assert.assertEquals(todayHighRankTestPost, rankedPosts.get(0));
    Assert.assertEquals(todayLowRankTestPost, rankedPosts.get(1));
  }
}
