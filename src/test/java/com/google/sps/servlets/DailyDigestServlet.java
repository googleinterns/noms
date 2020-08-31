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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.api.GmailConfiguration;
import com.google.sps.data.Email;
import com.google.sps.servlets.DailyDigestServlet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(JUnit4.class)
public final class UserDataServletTest {

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

  private static DailyDigestServlet dailyDigestServlet;
  private static LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static DatastoreService datastore;

  @Before
  public void setUp() {
    helper.setUp();
    dailyDigestServlet = new DailyDigestServlet();
    datastore = DatastoreServiceFactory.getDatastoreService();
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    memoryAppender.reset();
    memoryAppender.stop();
    helper.tearDown();
  }

  @After
  public void validate() {
    validateMockitoUsage();
  }

  @Test
  public void testRankPostsFromToday() throws Exception {

    datastore.put(todayLowRankTestPost);
    datastore.put(todayHighRankTestPost);

    ArrayList<Post> rankedPosts = dailyDigestServlet.rankPosts(COLLEGE_ID);

    Assert.assertEquals(2, rankedPosts.size());
    Assert.assertEquals(todayHighRankTestPost, rankedPosts.get(0));
    Assert.assertEquals(todayLowRankTestPost, rankedPosts.get(1));
  }

  @Test
  public void testRankPostsFromAnotherDay() throws Exception {

    datastore.put(futureTestPost);
    datastore.put(pastTestPost);

    ArrayList<Post> rankedPosts = dailyDigestServlet.rankPosts(COLLEGE_ID);

    Assert.assertEquals(0, rankedPosts.size());
  }

  @Test
  public void testRankManyPosts() throws Exception {

    datastore.put(todayLowRankTestPost);
    datastore.put(todayHighRankTestPost);
    datastore.put(futureTestPost);
    datastore.put(pastTestPost);

    ArrayList<Post> rankedPosts = dailyDigestServlet.rankPosts(COLLEGE_ID);

    Assert.assertEquals(2, rankedPosts.size());
    Assert.assertEquals(todayHighRankTestPost, rankedPosts.get(0));
    Assert.assertEquals(todayLowRankTestPost, rankedPosts.get(1));
  }
}
