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

// This file provides the JavaScript induced on the landing page (index.html)

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
  const collegeLocations = await (await fetch('./assets/college-locations.json')).json();

  // Grab the datalist and remove its ID (destroying the select-datalist relationship),
  // to improve performance while adding the options to the datalist.
  const collegeDataList = document.getElementById('colleges');
  collegeDataList.removeAttribute('id');

  // Add all colleges as datalist options. We use document fragments because the
  // DOM is slow if we add each option individually and let the DOM update in between.
  const frag = document.createDocumentFragment();
  collegeLocations.forEach((location) => {
    const newOption = document.createElement('option');
    newOption.setAttribute('data-value', location.UNITID);
    newOption.value = location.NAME;

    frag.appendChild(newOption);
  });

  // Add the options and restore the select-datalist relationship.
  collegeDataList.appendChild(frag);
  collegeDataList.setAttribute('id', 'colleges');

  // When users select an option from the dropdown, send them to that page.
  document.getElementById('colleges-input').addEventListener('change', navigateUserToCollegePage);
}

/**
 * When the user selects a college from the dropdown, we immediately
 * navigate them to the appropriate college's page.
 */
function navigateUserToCollegePage() {
  const collegename = document.getElementById('colleges-input').value;
  const option = document.querySelector(`#colleges option[value='${collegename}']`);

  // Only navigate to the page if that college exists in our list of colleges.
  // TODO: display 'We haven't heard of that college!"/similar to the user if not recognized.
  if (option) {
    const collegeid = option.dataset.value;
    window.location.href = `/find-events.html?collegeid=${collegeid}`;
  }
}
