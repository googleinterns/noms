// // Copyright 2019 Google LLC
// //
// // Licensed under the Apache License, Version 2.0 (the "License");
// // you may not use this file except in compliance with the License.
// // You may obtain a copy of the License at
// //
// //     https://www.apache.org/licenses/LICENSE-2.0
// //
// // Unless required by applicable law or agreed to in writing, software
// // distributed under the License is distributed on an "AS IS" BASIS,
// // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// // See the License for the specific language governing permissions and
// // limitations under the License.

// package com.google.sps.servlets;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.doNothing;
// import static org.mockito.Mockito.doThrow;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.spy;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.validateMockitoUsage;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import ch.qos.logback.classic.Level;
// import ch.qos.logback.classic.Logger;
// import ch.qos.logback.classic.LoggerContext;

// import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
// import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
// import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
// import com.google.sps.data.Email;
// import com.google.sps.api.GmailConfiguration;
// import com.google.sps.MemoryAppender;

// import java.io.IOException;
// import java.security.GeneralSecurityException;
// import javax.servlet.annotation.WebServlet;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

// import org.junit.After;
// import org.junit.Assert;
// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.junit.runners.JUnit4;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.mockito.runners.MockitoJUnitRunner;
// import org.mockito.Spy;

// import org.slf4j.LoggerFactory;

// @RunWith(JUnit4.class)
// public final class UserDataServletTest {

//   private static final String USER_LOGGER_NAME = "com.google.sps.servlets";
//   private static final String EMAIL_LOGGER_NAME = "com.google.sps.api";

//   private static final String SUB_SUCCESS_MSG = 
//     "User was subscribed to email notifications and added to Datastore.";
//   private static final String UNSUB_SUCCESS_MSG = 
//     "User was unsubscribed and removed from Datastore.";
//   private static final String UNSUB_FAIL_MSG = 
//     "User was unable to unsubscribe but was never subscribed.";
//   private static final String EMAIL_SUCCESS = 
//     "Successfully sent a new post email to: ";

//   private static final String NAME = "test";
//   private static final String EMAIL_A = "testa@google.com";
//   private static final String EMAIL_B = "testb@google.com";
//   private static final String COLLEGE_A = "000001";
//   private static final String COLLEGE_B = "000002";
//   private static final String UNSUB = "unsubscribe";
//   private static final String SUB = "subscribe";

//   @Mock private static HttpServletRequest mockRequest;
//   @Mock private static HttpServletResponse mockResponse;
//   @Mock private GmailConfiguration mockGmailConfiguration;

//   private static UserDataServlet userDataServlet;
//   private static MemoryAppender memoryAppender;
//   private static LocalServiceTestHelper helper =
//     new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

//   @Before
//   public void setUp() {
//     helper.setUp();

//     Logger user_logger = (Logger) LoggerFactory.getLogger(USER_LOGGER_NAME);
//     Logger email_logger = (Logger) LoggerFactory.getLogger(EMAIL_LOGGER_NAME);
//     memoryAppender = new MemoryAppender();
//     memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
//     user_logger.setLevel(Level.DEBUG);
//     user_logger.addAppender(memoryAppender);
//     email_logger.setLevel(Level.DEBUG);
//     email_logger.addAppender(memoryAppender);
//     memoryAppender.start();
//     userDataServlet = new UserDataServlet();

//     MockitoAnnotations.initMocks(this);
//   }

//   @After
//   public void tearDown() {
//     memoryAppender.reset();
//     memoryAppender.stop();
//     helper.tearDown();
//   }

//   @After
//   public void validate() {
//     validateMockitoUsage();
//   }

//   @Test
//   public void newUserSubscribe() throws Exception {
//     when(mockRequest.getParameter("name")).thenReturn(NAME);
//     when(mockRequest.getParameter("email")).thenReturn(EMAIL_A);
//     when(mockRequest.getParameter("cID")).thenReturn(COLLEGE_A);
//     when(mockRequest.getParameter("email-notif")).thenReturn(SUB);

//     userDataServlet.doPost(mockRequest, mockResponse);

//     Assert.assertEquals(1, memoryAppender.countEventsForLogger(USER_LOGGER_NAME));
//     Assert.assertEquals(1, memoryAppender.countEventsForLogger(EMAIL_LOGGER_NAME));
//     Assert.assertTrue(memoryAppender.contains(EMAIL_SUCCESS + EMAIL_A, Level.INFO));
//     Assert.assertTrue(memoryAppender.contains(SUB_SUCCESS_MSG, Level.INFO));
//     memoryAppender.reset();
//   }

//   @Test
//   public void newUserUnsubscribe() throws Exception {
//     when(mockRequest.getParameter("name")).thenReturn(NAME);
//     when(mockRequest.getParameter("email")).thenReturn(EMAIL_B);
//     when(mockRequest.getParameter("cID")).thenReturn(COLLEGE_A);
//     when(mockRequest.getParameter("email-notif")).thenReturn(UNSUB);

//     userDataServlet.doPost(mockRequest, mockResponse);

//     Assert.assertEquals(1, memoryAppender.countEventsForLogger(USER_LOGGER_NAME));
//     Assert.assertTrue(memoryAppender.contains(UNSUB_FAIL_MSG, Level.WARN));
//     memoryAppender.reset();
//   }

//   @Test
//   public void oldUserSubscribe() throws Exception {
//     when(mockRequest.getParameter("name")).thenReturn(NAME);
//     when(mockRequest.getParameter("email")).thenReturn(EMAIL_A);
//     when(mockRequest.getParameter("cID")).thenReturn(COLLEGE_B);
//     when(mockRequest.getParameter("email-notif")).thenReturn(SUB);

//     userDataServlet.doPost(mockRequest, mockResponse);

//     Assert.assertEquals(1, memoryAppender.countEventsForLogger(USER_LOGGER_NAME));
//     Assert.assertEquals(1, memoryAppender.countEventsForLogger(EMAIL_LOGGER_NAME));
//     Assert.assertTrue(memoryAppender.contains(EMAIL_SUCCESS + EMAIL_A, Level.INFO));
//     Assert.assertTrue(memoryAppender.contains(SUB_SUCCESS_MSG, Level.INFO));
//     memoryAppender.reset();
//   }

//   @Test
//   public void oldUserUnsubscribe() throws Exception {
//     when(mockRequest.getParameter("name")).thenReturn(NAME);
//     when(mockRequest.getParameter("email")).thenReturn(EMAIL_A);
//     when(mockRequest.getParameter("cID")).thenReturn(COLLEGE_A);
//     when(mockRequest.getParameter("email-notif")).thenReturn(SUB);  

//     userDataServlet.doPost(mockRequest, mockResponse);

//     when(mockRequest.getParameter("name")).thenReturn(NAME);
//     when(mockRequest.getParameter("email")).thenReturn(EMAIL_A);
//     when(mockRequest.getParameter("cID")).thenReturn(COLLEGE_A);
//     when(mockRequest.getParameter("email-notif")).thenReturn(UNSUB);  

//     userDataServlet.doPost(mockRequest, mockResponse);
    
//     Assert.assertEquals(2, memoryAppender.countEventsForLogger(USER_LOGGER_NAME));
//     Assert.assertTrue(memoryAppender.contains(SUB_SUCCESS_MSG, Level.INFO));
//     Assert.assertTrue(memoryAppender.contains(UNSUB_SUCCESS_MSG, Level.INFO));
//     memoryAppender.reset();
//   }
// }
