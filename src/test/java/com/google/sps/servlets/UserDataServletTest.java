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
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.data.Email;
import com.google.sps.api.GmailConfiguration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/** Tests user subscription and unsubscription. */
@RunWith(JUnit4.class)
public final class UserDataServletTest {

  private static final String LOGGER_NAME = "com.google.sps.servlets";
  private static final String SUB_SUCCESS_MSG = 
    "User was subscribed to email notifications and added to Datastore.";
  private static final String UNSUB_SUCCESS_MSG = 
    "User was unsubscribed and removed from Datastore.";
  private static final String UNSUB_FAIL_MSG = 
    "User was unable to unsubscribe but was never subscribed.";

  private static final String NEW_USER_NAME = "test1";
  private static final String NEW_USER_EMAIL = "test1@google.com";
  private static final String NEW_USER_COLLEGE = "000001";

  private static final String OLD_USER_NAME = "test2";
  private static final String OLD_USER_EMAIL = "test2@google.com";
  private static final String OLD_USER_COLLEGE = "000002";

  private static final String UNSUB = "unsubscribe";
  private static final String SUB = "subscribe";

  @Mock private static HttpServletRequest mRequest;
  @Mock private static HttpServletResponse mResponse;
  private static UserDataServlet userDataServlet;
  private static DatastoreService datastore;
  private static MemoryAppender memoryAppender;
  private LocalServiceTestHelper helper =
    new LocalServiceTestHelper(
      new LocalDatastoreServiceTestConfig()
        .setDefaultHighRepJobPolicyUnappliedJobPercentage(0),
          new LocalUserServiceTestConfig(),
            new LocalURLFetchServiceTestConfig());

  @Before
  public void setUp() {
    
    helper.setUp();

    Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
    memoryAppender = new MemoryAppender();
    memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    logger.setLevel(Level.DEBUG);
    logger.addAppender(memoryAppender);
    memoryAppender.start();

    userDataServlet = new UserDataServlet();
    datastore = DatastoreServiceFactory.getDatastoreService();

    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    memoryAppender.reset();
    memoryAppender.stop();
    helper.tearDown();
  }

  @Test
  public void newUserSubscribe() {

    helper.setEnvEmail(NEW_USER_EMAIL).setEnvAuthDomain("google.com").setEnvIsLoggedIn(false);

    when(mRequest.getParameter("name")).thenReturn(NEW_USER_NAME);
    when(mRequest.getParameter("email")).thenReturn(NEW_USER_EMAIL);
    when(mRequest.getParameter("cID")).thenReturn(NEW_USER_COLLEGE);
    when(mRequest.getParameter("email-notif")).thenReturn(SUB);

    userDataServlet.doPost(mRequest, mResponse);

    Assert.assertTrue(memoryAppender.countEventsForLogger(LOGGER_NAME)).isEqualTo(1);
    Assert.assertTrue(memoryAppender.contains(SUB_SUCCESS_MSG, Level.INFO)).isTrue());
  }

  @Test
  public void newUserUnsubscribe() {
    
  }

  @Test
  public void oldUserSubscribe() {

  }

  @Test
  public void oldUserUnsubscribe() {

  }

}
