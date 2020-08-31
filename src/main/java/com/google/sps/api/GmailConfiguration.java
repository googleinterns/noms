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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.sps.data.Email;
import com.google.sps.data.Post;
import com.google.sps.servlets.PostDataServlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Properties;
import java.lang.Iterable;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

// More information can be found here: http://www.slf4j.org/manual.html.
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Create and send emails from an authorized account. */
public class GmailConfiguration {

  private static final String FROM = "me";
  private static final Logger LOGGER = LoggerFactory.getLogger(GmailConfiguration.class);
  private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
  
  /**
    * Create a MimeMessage using the parameters provided.
    *
    * @param to email address of the receiver
    * @param subject subject of the email
    * @param bodyText body text of the email
    * @return the MimeMessage to be used to send email
    * @throws MessagingException
    */
  private static MimeMessage createEmail(String to, String subject, String bodyText) 
      throws MessagingException {

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    MimeMessage email = new MimeMessage(session);

    email.setFrom(new InternetAddress(FROM));
    email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
    email.setSubject(subject);
    email.setContent(bodyText, "text/html;charset=utf-8");

    return email;
  }

  /**
    * Create a message from an email.
    *
    * @param emailContent email to be set to raw of message
    * @return message containing a base64url encoded email
    * @throws IOException
    * @throws MessagingException
    */
  private static Message createMessageWithEmail(MimeMessage emailContent)
      throws MessagingException, IOException {

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    emailContent.writeTo(buffer);
    byte[] bytes = buffer.toByteArray();
    String encodedEmail = Base64.encodeBase64URLSafeString(bytes);

    Message message = new Message();
    message.setRaw(encodedEmail);

    return message;
  }

  /**
    * Send an email from the user's mailbox to its recipient.
    *
    * @param service authorized Gmail API instance.
    * @param emailContent email to be sent.
    * @return sent message
    * @throws MessagingException
    * @throws IOException
    */
  private static Message sendMessage(Gmail service, MimeMessage emailContent)
      throws MessagingException, IOException {
    
    Message message = createMessageWithEmail(emailContent);
    message = service.users().messages().send(FROM, message).execute();

    return message;
  }

  /**	
    * Send emails from areeta@google.com.	
    *	
    * @param to email address of the receiver
    * @param subject subject of the email
    * @param bodyText body text of the email
    */
  public static void sendEmail(String to, String subject, String content) {

    try {
      
      Gmail service = GmailAPI.getGmailService();	
      MimeMessage Mimemessage = createEmail(to, subject, content);	
      Message message = createMessageWithEmail(Mimemessage);	
      message = service.users().messages().send(FROM, message).execute();	
      LOGGER.info("Successfully sent a new post email to: " + to);

    } catch (Exception e) {

      LOGGER.error("Unable to send messsage due to: " + e.toString());
    }
  }	

  /**
    * Send emails to all users associated with the specific college about a new post.
    *
    * @param collegeId unique id of a college
    * @throws IOException
    */
  public static void notifyUsers(String collegeId, Post newPost) throws IOException {
    
    // Email the users to notify them that a new post has been added.
    for (Entity user : getAllUsersForACollege(collegeId)) {
      String email = user.getKey().getName().toString();
      sendEmail(email, Email.newPostSubject, Email.addNewPost(newPost));	
    }
  }

  /**
    * Send emails to all users associated with the specific college with ranked posts.
    *
    * @param collegeId unique id of a college
    * @throws IOException
    */
  public static void notifyUsers(String collegeId, ArrayList<Post> rankedPosts) throws IOException {
    
    // Email the users to notify them with ranked posts for the day.
    for (Entity user : getAllUsersForACollege(collegeId)) {
      String email = user.getKey().getName().toString();
      sendEmail(email, Email.dailyDigestSubject, Email.addRankedPosts(rankedPosts));	
    }
  }

  /**
    * Find all users for a specific college through querying datastore.
    *
    * @param collegeId unique id of a college
    * @return iterable of user entities
    */
  private static Iterable<Entity> getAllUsersForACollege(String collegeId) {
    
    Filter collegeFilter = new FilterPredicate("college", FilterOperator.EQUAL, collegeId);
    Query q = new Query("User").setFilter(collegeFilter);
    PreparedQuery pq = datastore.prepare(q);
    return pq.asIterable();
  }
}
