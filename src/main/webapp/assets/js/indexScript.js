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
// Globals
//

let collegeLocations = null;

//
// Constants
//

const US_GEOGRAPHICAL_CENTER = { lat: 39.50, lng: -98.35 };

//
// Functions
//

/**
 * Fires as soon as the DOM is loaded.
 */
async function onLoad() {
  collegeLocations = await (await fetch('./assets/college-locations.json')).json();

  addMapToPage();

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

  // When users select an option from the dropdown, send them to that page.
  document.getElementById('colleges-input').addEventListener('change', navigateUserToCollegePage);
}

/**
* Tries to add the map to the page. The map URL calls the initMap() function as
* its callback, which then positions the map and adds markers. If we are unable
* to retrieve the secret for the map, then we display an error to the user.
*/
function addMapToPage() {
 getSecretFor('javascript-maps-api').then((key) => {
   if (key === null) {
     const mapElement = document.getElementById('map-info-container');
     const errorElement = document.createElement('div');
     errorElement.setAttribute('id', 'map-error');
     errorElement.innerText = `An error occured while fetching the credentials
                               needed to view the map. Try refreshing the page;
                               if the error persists, please contact us above.`;
     mapElement.appendChild(errorElement);
     return;
   }

   const script = document.createElement('script');
   script.src = `https://maps.googleapis.com/maps/api/js?key=${key}&callback=initMap`;
   script.defer = true;
   script.async = true;
   window.initMap = initMap;
   document.head.appendChild(script);
 });
}

/**
 * Initializes the embedded Google Maps map.
 */
function initMap() {
  // Turn off the labels on the map and change the water color
  // so that the map fits the landing page's aesthetic better.
  map = new google.maps.Map(document.getElementById('map'),
      {
        center: {lat: US_GEOGRAPHICAL_CENTER.lat, lng: US_GEOGRAPHICAL_CENTER},
        zoom: 3,
        disableDefaultUI: true,
        styles: [
          {
            featureType: 'water',
            elementType: 'geometry',
            stylers: [{color: '#e3e3e3'}]
          },
          {
            featureType: 'all',
            elementType: 'labels',
            stylers: [
              { visibility: 'off' }
            ]
          }
        ],
      },
  );

  // We only show 300 of the colleges to the user because a 
  // map with 7000 pins doesn't look good.
  const lessColleges =
    new Array(30)
      .fill(0)
      .map((c) => Math.floor(Math.random() * collegeLocations.length))
      .map((i) => collegeLocations[i]);
  
  for (const college of lessColleges) {
    new google.maps.Marker({
      position: {lat: college.LAT, lng: college.LON},
      map: map,
      title: college.NAME,
    });
  }
}

/**
 * When the user selects a college from the dropdown, we immediately
 * navigate them to the appropriate college's page.
 */
function navigateUserToCollegePage() {
  const collegeName = document.getElementById('colleges-input').value;
  const option = document.querySelector(`#colleges option[value='${collegeName}']`);

  // Only navigate to the page if that college exists in our list of colleges.
  // TODO: display 'We haven't heard of that college!"/similar to the user if not recognized.
  if (option) {
    const collegeId = option.dataset.value;
    window.location.href = `/find-events.html?collegeid=${collegeId}`;
  }
}
