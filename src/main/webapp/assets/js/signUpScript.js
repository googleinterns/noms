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

// This file provides the JavaScript induced on the sign up page (sign-up.html) which is
// a replica from the main page (index.html) without additional functionality to load posts.

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
async function onLoad() {
  document.getElementById('name-input').addEventListener('keydown', limitCharacterInput);

  const collegeLocations = await (await fetch('./assets/college-locations.json')).json();

  // Grab the datalist and remove its ID (destroying the select-datalist relationship),
  // to improve performance while adding the options to the datalist.
  const collegeDataList = document.getElementById('colleges');
  collegeDataList.removeAttribute('id');

  // Add all colleges as datalist options. We use a document fragment because the
  // DOM is slow if we add each option individually and let the DOM update in between.
  const fragment = document.createDocumentFragment();
  collegeLocations.forEach((location) => {
    const newOption = document.createElement('option');
    newOption.setAttribute('data-value', location.UNITID);
    newOption.value = location.NAME;

    fragment.appendChild(newOption);
  });

  // Add the options and restore the select-datalist relationship.
  collegeDataList.appendChild(fragment);
  collegeDataList.setAttribute('id', 'colleges');

  // When users select an option from the dropdown, set the college id.
  document.getElementById('colleges-input-form').addEventListener('change', setCollegeID);
}

/**
 * When the user selects a college from the dropdown, we immediately
 * set the ID to be that college's unique ID.
 */
function setCollegeID() {
  const collegeName = document.getElementById('colleges-input-form').value;
  const option = document.querySelector(`#colleges option[value='${collegeName}']`);

  // Set hidden input to hold unique college id.
  // TODO: display 'We haven't heard of that college!"/similar to the user if not recognized.
  if (option) {
    const collegeId = option.dataset.value;
    const selectedCollege = document.getElementById('cID');
    selectedCollege.value = collegeId;
  }
}

/**
 * Limits the input of a textbox to a specified regex.
 * @param {KeyboardEvent} e - The keypress event.
 */
function limitCharacterInput(e) {
  const regex = RegExp('[a-zA-Z .,\'-]');

  if (!regex.test(e.key) && e.key != 'backspace' && e.key.length == 1) {
    e.preventDefault();
  }
}
