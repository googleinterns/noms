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

// This file will be included on every page, and contains items
// that are applicable application-wide, such as nav-bar functionality

//
// Event listener registration
//

// Hooks the onLoad function to the DOMContentLoaded event.
document.addEventListener('DOMContentLoaded', onLoad);

//
// Functions
//

/**
 * Fires as soon as the DOM is loaded.
 */
function onLoad() {
  // Upon clicking, the hamburger menu should open the navigation.
  document.querySelector('.burger i').addEventListener('click', toggleNav);
}

/**
 * Toggles navigation bar to be responsive.
 */
function toggleNav() {
  const burger = document.querySelector('.burger i');
  const nav = document.querySelector('.nav');
  burger.classList.toggle('fa-bars');
  burger.classList.toggle('fa-times');
  nav.classList.toggle('nav-active');
}
