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

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.sps.data.Email;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

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

/** Create and send emails from an authorized account. */
public class GmailConfiguration {

  private static final String FROM = "me";
  
  /**
    * Create a MimeMessage using the parameters provided.
    *
    * @param to email address of the receiver
    * @param subject subject of the email
    * @param bodyText body text of the email
    * @return the MimeMessage to be used to send email
    * @throws MessagingException
    */
  public static MimeMessage createEmail(String to, String subject, String bodyText) 
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
  public static Message createMessageWithEmail(MimeMessage emailContent)
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
  public static Message sendMessage(Gmail service, MimeMessage emailContent)
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
      message = service.users().messages().send("me", message).execute();	

    } catch (Exception e) {

      System.out.println("ERROR: Unable to send message : " + e.toString());

    }
  }	
}
