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

/** Class representing a user. */
public class User {

  /** Information from the user */
  private final String name;
  private final String email;
  private final String university;
  private final boolean subscribe;

  public User(String name, String email, String university, boolean subscribe) {
    this.name = name;
    this.email = email;
    this.university = university;
    this.subscribe = subscribe;
  }

  public boolean getSubscribe() {
    return this.subscribe;
  }
}
