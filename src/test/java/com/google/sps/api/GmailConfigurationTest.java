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

package com.google.sps.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

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
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.sps.data.Email;
import com.google.sps.data.Post;
import com.google.sps.api.GmailAPI;
import com.google.sps.api.GmailConfiguration;
import com.google.sps.MemoryAppender;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.lang.Iterable;
import java.util.Properties;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;

/** Tests querying users and sending emails from an authorized account.*/
@RunWith(JUnit4.class)
public final class GmailConfigurationTest {

  private static final String TO = "test@google.com";
  private static final String SUBJECT = "test";
  private static final String CONTENT = "<h1>test</h1>";

  private static final String NAME = "test";
  private static final String EMAIL = "test@google.com";
  private static final String COLLEGE_A = "000000";
  private static final String COLLEGE_B = "000001";

  private static final String SUCCESS_MSG = "Successfully sent a new post email to: ";
  private static final String LOGGER_NAME = "com.google.sps.api";

  @Mock private static Gmail mockGmail;
  @Mock private static GmailAPI mockGmailAPI;
  @Mock private static PreparedQuery mockPreparedQuery;
  @Mock private static Iterable<Entity> mockEntityIterable;

  private static LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static DatastoreService datastore;
  private static GmailConfiguration gmailConfiguration;
  private static MemoryAppender memoryAppender;
  private static Post post;

  @Before
  public void setUp() throws Exception {
    helper.setUp();

    Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
    memoryAppender = new MemoryAppender();
    memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    logger.setLevel(Level.DEBUG);
    logger.addAppender(memoryAppender);
    memoryAppender.start();

    post = new Post();
    datastore = DatastoreServiceFactory.getDatastoreService();
    gmailConfiguration = new GmailConfiguration();
    MockitoAnnotations.initMocks(this);
  }

  @After
  public void tearDown() {
    memoryAppender.reset();
    memoryAppender.stop();
    helper.tearDown();
  }

  @Test
  public void userAttendingCollegeIntTest() throws Exception {
    Entity userEntity = new Entity("User", EMAIL);
    userEntity.setProperty("name", NAME);
    userEntity.setProperty("college", COLLEGE_A);
    datastore.put(userEntity);

    GmailConfiguration.notifyUsers(COLLEGE_A, post);

    Assert.assertEquals(1, memoryAppender.countEventsForLogger(LOGGER_NAME));
    Assert.assertTrue(memoryAppender.contains(SUCCESS_MSG + EMAIL, Level.INFO));    
    memoryAppender.reset();
  }

  @Test
  public void noUserAttendingCollegeIntTest() throws Exception {
    when(mockPreparedQuery.asIterable()).thenReturn(mockEntityIterable);
    GmailConfiguration.notifyUsers(COLLEGE_B, post);

    verify(mockPreparedQuery, never()).asIterable();
    Assert.assertEquals(0, memoryAppender.countEventsForLogger(LOGGER_NAME));
  }

  @Test
  public void sendEmailWithAuthorizedServiceIntTest() {
    GmailConfiguration.sendEmail(TO, SUBJECT, CONTENT);

    Assert.assertEquals(1, memoryAppender.countEventsForLogger(LOGGER_NAME));
    Assert.assertTrue(memoryAppender.contains(SUCCESS_MSG + TO, Level.INFO));    
  }

  @Test(expected = Exception.class)
  public void sendEmailWithUnauthorizedServiceIntTest() throws Exception {
    when(mockGmailAPI.getGmailService()).thenReturn(mockGmail);

    GmailConfiguration.sendEmail(TO, SUBJECT, CONTENT);

    verify(mockGmailAPI, times(1)).getGmailService();
    Assert.assertEquals(1, memoryAppender.countEventsForLogger(LOGGER_NAME));
  }
}
