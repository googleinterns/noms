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

import com.google.sps.servlets.Post;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/** Creates and converts HTML templates for email sending. */
public final class Email {

  public static final String welcomeSubject = "⭐ noms: welcome to your free food finder!";
  public static final String newPostSubject = "⭐ noms: new free food near you!";
  public static final String welcomeContentPath = "/home/areeta/noms/src/main/java/com/google/sps/data/WelcomeEmail.html";
  public static final String newPostPath = "/home/areeta/noms/src/main/java/com/google/sps/data/NewPost.html";

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
    * Add new post information to emails.
    *
    * @param post new post object
    * @return string of HTML file with new post
    * @throws IOException
    */
  public static String addNewPost(Post post) throws IOException {

    String emailContent = getStringFromHTML(newPostPath);
    emailContent = emailContent.replace("[organizationName]", post.organizationName);
    emailContent = emailContent.replace("[location]", post.location);
    emailContent = emailContent.replace("[month]", post.month);
    emailContent = emailContent.replace("[day]", post.day);
    emailContent = emailContent.replace("[startHour]", post.startHour);
    emailContent = emailContent.replace("[startMinute]", post.startMinute);
    emailContent = emailContent.replace("[endHour]", post.endHour);
    emailContent = emailContent.replace("[endMinute]", post.endMinute);
    emailContent = emailContent.replace("[description]", post.description);
    return emailContent;
  }
}
