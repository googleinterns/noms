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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.sps.data.Email;
import com.google.sps.api.GmailConfiguration;
import com.google.sps.MemoryAppender;

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
import org.slf4j.LoggerFactory;

@RunWith(JUnit4.class)
public final class UserDataServletTest {

  private static final String LOGGER_NAME = "com.google.sps.servlets";
  private static final String SUB_SUCCESS_MSG = 
    "User was subscribed to email notifications and added to Datastore.";
  private static final String UNSUB_SUCCESS_MSG = 
    "User was unsubscribed and removed from Datastore.";
  private static final String UNSUB_FAIL_MSG = 
    "User was unable to unsubscribe but was never subscribed.";

  private static final String NAME = "test";
  private static final String EMAIL_A = "testa@google.com";
  private static final String EMAIL_B = "testb@google.com";
  private static final String COLLEGE_A = "000001";
  private static final String COLLEGE_B = "000002";
  private static final String UNSUB = "unsubscribe";
  private static final String SUB = "subscribe";

  @Mock private static HttpServletRequest mRequest;
  @Mock private static HttpServletResponse mResponse;
  @Mock private static GmailConfiguration mGmailConfiguration;

  private static UserDataServlet userDataServlet;
  private static MemoryAppender memoryAppender;
  private static LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

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

    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    memoryAppender.reset();
    memoryAppender.stop();
    helper.tearDown();
  }

  @Test
  public void newUserSubscribe() throws Exception {
    when(mRequest.getParameter("name")).thenReturn(NAME);
    when(mRequest.getParameter("email")).thenReturn(EMAIL_A);
    when(mRequest.getParameter("cID")).thenReturn(COLLEGE_A);
    when(mRequest.getParameter("email-notif")).thenReturn(SUB);

    userDataServlet.doPost(mRequest, mResponse);

    Assert.assertEquals(1, memoryAppender.countEventsForLogger(LOGGER_NAME));
    Assert.assertTrue(memoryAppender.contains(SUB_SUCCESS_MSG, Level.INFO));
    memoryAppender.reset();
  }

  @Test
  public void newUserUnsubscribe() throws Exception {
    when(mRequest.getParameter("name")).thenReturn(NAME);
    when(mRequest.getParameter("email")).thenReturn(EMAIL_B);
    when(mRequest.getParameter("cID")).thenReturn(COLLEGE_A);
    when(mRequest.getParameter("email-notif")).thenReturn(UNSUB);

    userDataServlet.doPost(mRequest, mResponse);

    Assert.assertEquals(1, memoryAppender.countEventsForLogger(LOGGER_NAME));
    Assert.assertTrue(memoryAppender.contains(UNSUB_FAIL_MSG, Level.WARN));
    memoryAppender.reset();
  }

  @Test
  public void oldUserSubscribe() throws Exception {
    when(mRequest.getParameter("name")).thenReturn(NAME);
    when(mRequest.getParameter("email")).thenReturn(EMAIL_A);
    when(mRequest.getParameter("cID")).thenReturn(COLLEGE_B);
    when(mRequest.getParameter("email-notif")).thenReturn(SUB);

    userDataServlet.doPost(mRequest, mResponse);

    Assert.assertEquals(1, memoryAppender.countEventsForLogger(LOGGER_NAME));
    Assert.assertTrue(memoryAppender.contains(SUB_SUCCESS_MSG, Level.INFO));
    memoryAppender.reset();
  }

  @Test
  public void oldUserUnsubscribe() throws Exception {
    when(mRequest.getParameter("name")).thenReturn(NAME);
    when(mRequest.getParameter("email")).thenReturn(EMAIL_A);
    when(mRequest.getParameter("cID")).thenReturn(COLLEGE_A);
    when(mRequest.getParameter("email-notif")).thenReturn(SUB);  

    userDataServlet.doPost(mRequest, mResponse);

    when(mRequest.getParameter("name")).thenReturn(NAME);
    when(mRequest.getParameter("email")).thenReturn(EMAIL_A);
    when(mRequest.getParameter("cID")).thenReturn(COLLEGE_A);
    when(mRequest.getParameter("email-notif")).thenReturn(UNSUB);  

    userDataServlet.doPost(mRequest, mResponse);
    
    Assert.assertEquals(2, memoryAppender.countEventsForLogger(LOGGER_NAME));
    Assert.assertTrue(memoryAppender.contains(SUB_SUCCESS_MSG, Level.INFO));
    Assert.assertTrue(memoryAppender.contains(UNSUB_SUCCESS_MSG, Level.INFO));
    memoryAppender.reset();
  }
}
