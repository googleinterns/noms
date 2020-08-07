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

public final class Email {

  public static final String welcomeSubject = "⭐ noms: welcome to your free food finder!";
  public static final String dailyDigestSubject = "⭐ noms: daily digest of free food!";

  public static final String welcomeContent = "<body style=\"margin: 3rem\">" +
    "<h1>welcome to noms</h1>" +
    "<p> thank you so much for subscribing to our mailing list of free food!";

  public static final String dailyDigestContent = "<body style=\"margin: 3rem\">" +
    "<h1>daily digest of free food @ university of california-irvine</h1>" +
    "<div style=\"background: green; border-radius: 15px;" + 
    "<p>pad thai @ aldrich park</p>";

  URL path = ClassLoader.getSystemResource("WelcomeEmail.html");
  File input = new File(path.toURI());
  Document document = Jsoup.parse(input, "UTF-8");
  public static final String welcomeContent = document;
}
