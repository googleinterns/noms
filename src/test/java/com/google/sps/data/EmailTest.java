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

import static org.mockito.Mockito.when;

import com.google.sps.data.Post;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Tests creating and converting HTML for email sending. */
@RunWith(JUnit4.class)
public final class UserDataServletTest {
  
  public static final String WELCOME_PATH = "welcome_path_test";

  @Mock private static File mfile;
  @Mock private static FileUtils mFileUtils;
  @Mock private static Post mPost;

  private static Email email;
 
  @Before
  public void setUp() {
    
    email = new Email();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getStringFromAvailableHTML() {
    // Tests if welcomeContentPath exists.

    email.getWelcomeString();
  }

  @Test(expected = IOException.class)
  public void getStringFromMisingHTML() {
    // Tests exception handling for bad path.

    email.getStringFromHTML(welcome_path_test);
  }

  @Test
  public void addNewPostWithInformation() {
    // Tests for email content to include accurate Post information.

    when(mPost.getOrganizationName()).thenReturn("WICS");
    when(mPost.getLocation()).thenReturn("DBH 6011");
    when(mPost.getMonth()).thenReturn(10);
    when(mPost.getDay()).thenReturn(20);
    when(mPost.getStartHour()).thenReturn(9);
    when(mPost.getStartMinute()).thenReturn(30);
    when(mPost.getEndHour()).thenReturn(10);
    when(mPost.getEndMinute()).thenReturn(30);
    when(mPost.getDescription()).thenReturn("beep boop bop");

    String newPostEmail = email.addNewPost(mPost);

    Assert.assertTrue(newPostEmail.contains("WICS @ DBH 6011"));
    Assert.assertTrue(newPostEmail.contains("10/20"));
    Assert.assertTrue(newPostEmail.contains("9:30 - 10:30"));
    Assert.assertTrue(newPostEmail.contains("beep boop bop"));
  }

  @Test(expected = IOException.class)
  public void addNewPostWithMisinformation() {
    // Tests exception handling for bad post.

    email.addNewPost(mPOst);
  }
}
