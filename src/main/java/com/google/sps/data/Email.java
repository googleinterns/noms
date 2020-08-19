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

package com.google.sps.data;

import com.google.sps.data.Post;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import java.util.Formatter;

/** Creates and converts HTML templates for email sending. */
public final class Email {

  public static final String welcomeSubject = "⭐ noms: welcome to your free food finder!";
  public static final String newPostSubject = "⭐ noms: new free food near you!";
  private static final String welcomeContentPath = "WelcomeEmail.html";
  private static final String newPostPath = "NewPost.html";

  /**
    * Convert HTML to String for MimeMessage to configure it into emails.
    *
    * @param path path of HTML file
    * @return string version of HTML file
    * @throws IOException
    */
  public static String getStringFromHTML(String path) throws IOException {

    ClassLoader classLoader = Email.class.getClassLoader();
    File HTMLfile = new File(classLoader.getResource(path).getFile());
    String str = FileUtils.readFileToString(HTMLfile, "utf-8");
    return str;
  }

  /**
    * Convert welcome content to HTML.
    *
    * @return string version of welcome HTML file
    * @throws IOException
    */
  public static String getWelcomeString() throws IOException {

    return getStringFromHTML(welcomeContentPath);
  }

  /**
    * Add new post information to emails.
    *
    * @param post new post object
    * @return string of HTML file with new post
    * @throws IOException
    */
  public static String addNewPost(Post post) throws IOException {

    String emailContent = getStringFromHTML(newPostPath);
    emailContent = emailContent.replace("[organizationName]", post.getOrganizationName());
    emailContent = emailContent.replace("[location]", post.getLocation());
    emailContent = emailContent.replace("[month]",  Integer.toString(post.getMonth()));
    emailContent = emailContent.replace("[day]", Integer.toString(post.getDay()));
    emailContent = emailContent.replace("[startTime]", getFormattedTime(post.getStartHour(), post.getStartMinute()));
    emailContent = emailContent.replace("[endTime]", getFormattedTime(post.getEndHour(), post.getEndMinute()));
    emailContent = emailContent.replace("[description]", post.getDescription());
    return emailContent;
  }

  /**
    * Convert hour and minute to easier to see timestamps.
    *
    * @param hour of event
    * @param minute of event
    * @return string of time in XX:XX PM/AM or X:XX PM/AM format.
    */
  private static String getFormattedTime(int hour, int minute) {

    String formattedTime = "";
    String amOrPm = "";

    // Account for 24-hour period.
    if (hour <= 12) {
      formattedTime = Integer.toString(hour) + ":";
      amOrPm = "AM";
    } else {
      formattedTime = Integer.toString(hour - 12) + ":";
      amOrPm = "PM";
    }

    // Account for 1 digit times which makes format different.
    // Ex. 1:00 looks like 1:0 instead.
    formattedTime += String.format("%02d", minute);
    
    return formattedTime + amOrPm;
  }
}
