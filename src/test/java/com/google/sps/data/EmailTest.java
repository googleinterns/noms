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
  
  public static final String WELCOME_PATH = "./assets/html_templates/WelcomeEmail.html";
  public static final String NEW_POST_PATH = "./assets/html_templates/NewPost.html";

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

    File file = new File(WELCOME_PATH);
    assertTrue(file.exists());
    

  }

  @Test(expected = IOException.class)
  public void getStringFromMisingHTML() {

  }

  @Test
  public void addNewPostWithInformation() {

  }

  @Test(expected = IOException.class)
  public void addNewPostWithNoInformation() {
    
  }
}
