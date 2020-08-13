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
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.sps.data.Email;
import com.google.sps.data.Post;
import com.google.sps.servlets.PostDataServlet;
import com.google.sps.api.GmailConfiguration;
import com.google.sps.api.GmailAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
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

/** Tests querying users and sending emails from an authorized account.*/
@RunWith(MockitoJUnitRunner.class)
public final class GmailConfigurationTest {

  private static final String FROM = "me";
  private static final String TO = "test@google.com";
  private static final String SUBJECT = "test";
  private static final String BODYTEXT = "<h1>test</h1>";

  private static GmailConfiguration mGmailConfiguration;
  private static Post mPost;
  private static Gmail mGmail;

  private static LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
  private static DatastoreService datastore;

  @Before
  public void setUp() throws Exception {

    helper.setUp();

    datastore = DatastoreServiceFactory.getDatastoreService();
    mGmailConfiguration= mock(GmailConfiguration.class);
    mPost = mock(Post.class);
    mGmail = mock(Gmail.class);
  }

  @Override
  @After
  public void tearDown() {

    helper.tearDown();
  }

  @Test
  public void noUsersAttendingCollege() {

  }

  @Test
  public void usersAttendingCollege() {
  }
}
