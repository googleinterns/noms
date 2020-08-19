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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.sps.data.Email;
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
public final class EmailTest {
  
  public static final String PATH = "path_test";

  @Mock private static Post mPost;
  @Mock private static FileUtils mFileUtils;

  private static Email email;
 
  @Before
  public void setUp() {
    email = new Email();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getStringFromAvailableHTML() throws IOException {
    String welcomeEmail = email.getWelcomeString();

    Assert.assertTrue(welcomeEmail.contains("thank you so much for joining our mailing list!"));
    Assert.assertTrue(welcomeEmail.contains("github"));
    Assert.assertTrue(welcomeEmail.contains("https://step186-2020.uc.r.appspot.com/"));
  }

  @Test(expected = Exception.class)
  public void getStringFromMisingHTML() throws Exception {
    email.getStringFromHTML(PATH);
  }

  @Test
  public void addNewPostWithAMInformation() throws IOException {
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
    Assert.assertTrue(newPostEmail.contains("9:30AM - 10:30AM"));
    Assert.assertTrue(newPostEmail.contains("beep boop bop"));
  }

  @Test
  public void addNewPostWithPMInformation() throws IOException {
    when(mPost.getOrganizationName()).thenReturn("WICS");
    when(mPost.getLocation()).thenReturn("DBH 6011");
    when(mPost.getMonth()).thenReturn(10);
    when(mPost.getDay()).thenReturn(20);
    when(mPost.getStartHour()).thenReturn(17);
    when(mPost.getStartMinute()).thenReturn(30);
    when(mPost.getEndHour()).thenReturn(23);
    when(mPost.getEndMinute()).thenReturn(59);
    when(mPost.getDescription()).thenReturn("beep boop bop");

    String newPostEmail = email.addNewPost(mPost);

    Assert.assertTrue(newPostEmail.contains("WICS @ DBH 6011"));
    Assert.assertTrue(newPostEmail.contains("10/20"));
    Assert.assertTrue(newPostEmail.contains("5:30PM - 11:59PM"));
    Assert.assertTrue(newPostEmail.contains("beep boop bop"));
  }

  @Test(expected = Exception.class)
  public void addNewPostWithNoInformation() throws Exception {
    email.addNewPost(mPost);
  }
}
