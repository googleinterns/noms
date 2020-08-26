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

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Wraps the regex class and a select group of patterns for convenience. */
public final class InputPattern {

  public static final Pattern DOUBLE = Pattern.compile("^-{0,1}[0-9]+\\.[0-9]+$");
  public static final Pattern GOOGLE_EMAIL = Pattern.compile("^[a-zA-Z0-9._-]+@google.com$");
  public static final Pattern PERSON_NAME = Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$");
  public static final Pattern POSITIVE_INTEGER = Pattern.compile("^[0-9]+$");
  public static final Pattern TEXT = Pattern.compile("^[a-zA-Z0-9 .,\\n!]+$");

  // Make the constructor private to emulate a static class.
  // Since this is a utility class, instantiating an instance isn't meaningful.
  private InputPattern() {} 
}
