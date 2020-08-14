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

/** Creates and converts HTML templates for email sending. */
public final class Email {

  public static final String welcomeSubject = "⭐ noms: welcome to your free food finder!";
  public static final String newPostSubject = "⭐ noms: new free food near you!";

  // Set paths from /noms/target folder to accommodate maven run/deploy.
  private static final String welcomeContentPath = "./assets/html_templates/WelcomeEmail.html";
  private static final String newPostPath = "./assets/html_templates/NewPost.html";

  /**
    * Convert HTML to String for MimeMessage to configure it into emails.
    *
    * @param path path of HTML file
    * @return string version of HTML file
    * @throws IOException
    */
  public static String getStringFromHTML(String path) throws IOException {

    File HTMLfile = new File(path);
    String str = FileUtils.readFileToString(HTMLfile, "utf-8");
    return str;
  }

  /**
    * Convert welcome content to HTML.
    *
    * @return string version of welcome HTML file
    * @throws IOException
    */
  public static String getWeclomeString() throws IOException {

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

    // TODO: Include AM/PM, date formatted, and time zone to be more user friendly.
    String emailContent = getStringFromHTML(newPostPath);
    emailContent = emailContent.replace("[organizationName]", post.getOrganizationName());
    emailContent = emailContent.replace("[location]", post.getLocation());
    emailContent = emailContent.replace("[month]",  Integer.toString(post.getMonth()));
    emailContent = emailContent.replace("[day]", Integer.toString(post.getMonth()));
    emailContent = emailContent.replace("[startHour]", Integer.toString(post.getStartHour()));
    emailContent = emailContent.replace("[startMinute]", Integer.toString(post.getStartMinute()));
    emailContent = emailContent.replace("[endHour]", Integer.toString(post.getEndHour()));
    emailContent = emailContent.replace("[endMinute]", Integer.toString(post.getEndMinute()));
    emailContent = emailContent.replace("[description]", post.getDescription());
    return emailContent;
  }
}
