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

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public final class Email {

  public static final String welcomeSubject = "⭐ noms: welcome to your free food finder!";
  public static final String dailyDigestSubject = "⭐ noms: daily digest of free food!";
  public static final String welcomeContentPath = "/home/areeta/noms/src/main/java/com/google/sps/data/WelcomeEmail.html";

  public static String getStringFromHTML(String path) throws IOException {
    File HTMLfile = new File(path);
    String str = FileUtils.readFileToString(HTMLfile, "utf-8");
    return str;
  }
}
