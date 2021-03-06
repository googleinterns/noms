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

package com.google.sps;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.Collections;
import java.util.List;

/** Manipulates a list automatically populated by the logging. */
public class MemoryAppender extends ListAppender<ILoggingEvent> {

  /** Clears the list. */
  public void reset() {
    list.clear();
  }

  /**
    * Checks if list contains a certain log message.
    *
    * @param string logging message string
    * @param level security level of logging message
    * @return boolean if contains
    */
  public boolean contains(String string, Level level) {
    return this.list.stream()
      .anyMatch(event -> event.getMessage().toString().contains(string) 
        && event.getLevel().equals(level));
  }

  /**
    * Counts amount of events generated by a certain logger.
    *
    * @param loggerName string of the logger's name
    * @return number of events
    */
  public int countEventsForLogger(String loggerName) {
    return (int) this.list.stream()
      .filter(event -> event.getLoggerName().contains(loggerName)).count();
  }
}
