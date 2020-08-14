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
/* eslint-disable no-unused-vars */
/* global google, getSecretFor */

// This file will be included only on the 'feed' (find-events) page,
// and contains items specific to the map and posts on that page.

//
// Types
//

/**
 * An object containing information about a location,
 * whether an entire college, building, or street.
 * @typedef {Object} LocationInfo
 * @property {string} name - The name of the location.
 * @property {number} lat - The latitude of the location.
 * @property {number} long - The longitude of the location.
 * @property {string} city - The city the location is in.
 */

/**
 * An object containing information about a post.
 * @typedef {Object} PostInfo
 * @property {number} id - The unique id of the post.
 * @property {string} organizationName - The name of the organization making the post.
 * @property {Date} postDateTime - The date that the post was made.
 * @property {Date} eventStartTime - The starting time of the event described in the post.
 * @property {Date} eventEndTime - The ending time of the event described in the post.
 * @property {LocationInfo} location - The location of the event.
 * @property {number} numOfPeopleFoodWillFeed - The number of people the event's food can feed.
 * @property {string} foodType - A short 1-3 word description of the type of food served.
 * @property {string} description - A longer description of the entire event.
 */

//
// Global objects
//

/** @type {LocationInfo} */
let collegeLocation = null;

/** @type {Array<PostInfo>} */
let posts = null;

/** @type {google.maps.Map} */
let map;

/** @type {string} */
let cachedModalAddress = '';

//
// Elements
//

/** @type {HTMLElement} */
let modalCard;

/** @type {HTMLElement} */
let createPostButton;

/** @type {HTMLElement} */
let modal;

/** @type {HTMLElement} */
let closeModalButton;

/** @type {HTMLElement} */
let submitModalButton;

/** @type {HTMLElement} */
let modalForm;

/** @type {HTMLButtonElement} */
let toggleLegendButton;

//
// Constants
//

const MARKER_WIDTH_MINMAX = {min: 28, max: 70};
const MARKER_HEIGHT_MINMAX = {min: 45, max: 113};

//
// Event listener registration
//

// Hooks the onLoad function to the DOMContentLoaded event.
document.addEventListener('DOMContentLoaded', onLoad);

document.addEventListener('mouseup', onClickAnywhere);

document.addEventListener('keyup', onKeyUp);

//
// Functions
//

/**
 * Fires as soon as the DOM is loaded.
 */
async function onLoad() {
  // Show a spinning loading icon to user on map.
  addMapSpinner();

  // Get the elements from the DOM after they have loaded.
  createPostButton = document.getElementById('create-post-button');
  modal = document.getElementById('modal-background');
  closeModalButton = document.getElementById('modal-close');
  submitModalButton = document.getElementById('modal-submit');
  modalForm = document.getElementById('modal-form');
  toggleLegendButton = document.getElementById('toggle-legend-button');
  modalCard = document.getElementById('modal-create-post');

  // Event Listeners that need the DOM elements.
  createPostButton.addEventListener('click', showModal);
  closeModalButton.addEventListener('click', closeModal);
  submitModalButton.addEventListener('click', submitModal);
  toggleLegendButton.addEventListener('click', toggleLegend);

  // Get the college id from the query string parameters.
  const collegeId = (new URLSearchParams(window.location.search)).get('collegeid');

  // If no college ID was provided, redirect to the landing page.
  if (!collegeId) {
    window.location.href = '/';
    return;
  }

  // Add simple location information to the page - we do this before
  // loading the posts because it will be much faster.
  collegeLocation = await fetchCollegeLocation(collegeId);
  document.getElementById('find-events-title').innerText +=
  ` @ ${collegeLocation.name}`.toLowerCase();

  // Add the posts to the page, after which we can add the map
  // as well because the map relies on the post information existing.
  posts = await fetchPosts(collegeId);
  addPosts(posts);

  // Add the embedded map to the page.
  addMapToPage();
}

/**
 * Adds the loading spinner on the map.
 */
function addMapSpinner() {
  const map = document.getElementById('map-info-container');
  const spinner = document.createElement('div');
  spinner.setAttribute('id', 'map-spinner');
  spinner.setAttribute('class', 'spinner');
  map.appendChild(spinner);
}

/**
 * Removes the loading spinner on the map.
 */
function removeMapSpinner() {
  const map = document.getElementById('map-info-container');
  const spinner = document.getElementById('map-spinner');
  map.removeChild(spinner);
}

/**
 * Adds our custom buttons (such as 'legend') to the map after it has loaded.
 */
function addMapButtons() {
  const mapButtonsContainer = document.getElementById('map-buttons-container');
  mapButtonsContainer.style.display = 'flex';
}

/**
 * Tries to add the map to the page. The map URL calls the initMap() function as
 * its callback, which then positions the map and adds markers. If we are unable
 * to retrieve the secret for the map, then we display an error to the user.
 */
function addMapToPage() {
  getSecretFor('javascript-maps-api').then((key) => {
    if (key === null) {
      removeMapSpinner();
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
 * Handles mouse clicks anywhere on the page.
 * @param {any} event - The mouseclick event.
 */
function onClickAnywhere(event) {
  // If the user clicked somewhere, we don't consider them a keyboard-user anymore.
  document.body.classList.remove('keyboard-active');
}

/**
 * Handles keyup events on the general page.
 * @param {any} event - The keydown event.
 */
function onKeyUp(event) {
  // If the key is a tab, consider the user a keyboard user.
  // This allows us to separate "active" classes due to keyboard navigation from
  // "active" classes due to simply clicking on something.
  if (event.keyCode == 9) {
    document.body.classList.add('keyboard-active');
  }
}

/**
 * Translates a location from its name to a pair of latitude and longitudes.
 * @param {string} address - The address to translate to lat/long.
 * @param {function} apiToCall - Represents the option to dependency-inject a mock api call.
 * @param {array} locality - Represents the option to inject the current college's location.
 * @return {Promise<LocationInfo>} or null if no such location exists or an error occurs.
 */
async function translateLocationToLatLong(
    address,
    apiToCall = fetchTranslateLocation,
    locality = collegeLocation) {
  try {
    // Add the college's city if it isn't already present in the query so that
    // geolocator has a better chance at succeeding in finding lat/longs.
    const city = locality ? locality.city : '';
    const response = await apiToCall(address.includes(city) ? address : `${address}, ${city}`);

    if (!response) {
      throw new Error('POST failed for unknown reasons.');
    }

    // If we get a non-200 status response, then fail.
    if (!response.ok) {
      const error = await response.text();
      throw new Error(response.status + ' ' + error);
    }

    const result = await response.json();

    // If our result is empty, then we didn't have any results at all.
    if (Object.keys(result).length === 0) {
      throw new Error('No results returned with query');
    }

    // If our result doesn't have certain properties, then there's no valid lat/long.
    if (!('geometry' in result) || !('location' in result.geometry)) {
      throw new Error('No latitude/longitude was returned with that query.');
    }

    // If all of the above checks passed, we can return the lat/long to the caller.
    return {
      name: 'formattedAddress' in result ? result.formattedAddress : address,
      lat: result.geometry.location.lat,
      long: result.geometry.location.lng,
    };
  } catch (err) {
    console.warn(err);
    return null;
  }
}

/**
 * The API call for translateLocationToLatLong(). Has been factored out of the
 * function so that mock API calls may be passed in for testing purposes.
 * @param {string} address - The address to translate to lat/long.
 * @return {Promise<any>} - The API's response.
 */
async function fetchTranslateLocation(address) {
  try {
    const response = await fetch('/translateLocation', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
      },
      body: createSearchParamsFromObject({location: address}),
    });
    return response;
  } catch (err) {
    console.warn(err);
    return null;
  }
}

/**
 * Creates search params for any object, to send fetch requests with.
 * @param {any} obj - The generic object to create seachparams for.
 * @return {string} - The searchparams.
 */
function createSearchParamsFromObject(obj) {
  return Object.keys(obj).map((key) => {
    return encodeURIComponent(key) + '=' + encodeURIComponent(obj[key]);
  }).join('&');
}

/**
 * Fetches the current college's location information from the local JSON storing it.
 * @param {number} collegeid - The ID of the college we want the lat/long for.
 * @return {Promise<LocationInfo>} - The college's location and information.
 */
async function fetchCollegeLocation(collegeid) {
  const locations = await (await fetch('./assets/college-locations.json')).json();
  const collegeInfo = locations.find((l) => parseInt(l.UNITID) === parseInt(collegeid));
  const newLocation = {
    name: collegeInfo.NAME,
    lat: parseFloat(collegeInfo.LAT),
    long: parseFloat(collegeInfo.LON),
    city: collegeInfo.CITY,
  };
  return newLocation;
}

/**
 * A GET request that fetches all posts on this current day.
 * @param {number} collegeId - The ID of the college we want posts for.
 * @return {array} - The posts.
 */
async function fetchPosts(collegeId) {
  // Send the college Id.
  const url = '/postData?collegeId=' + collegeId;
  const response = await fetch(url);
  const message = await response.json();

  const posts = [];

  for (let i = 0; i < message.length; i++) {
    const year = message[i]['year'];
    const month = message[i]['month'];
    const day = message[i]['day'];
    const startHour = message[i]['startHour'];
    const endHour = message[i]['endHour'];
    const startMinute = message[i]['startMinute'];
    const endMinute = message[i]['endMinute'];

    const post = {
      id: message[i]['postId'],
      organizationName: message[i]['organizationName'],
      postDateTime: new Date(),
      eventStartTime: new Date(year, month, day, startHour, startMinute, 0, 0),
      eventEndTime: new Date(year, month, day, endHour, endMinute, 0, 0),
      location: {
        name: message[i]['location'],
        lat: message[i]['lat'],
        long: message[i]['lng'],
      },
      numOfPeopleFoodWillFeed: message[i]['numberOfPeopleItFeeds'],
      foodType: message[i]['typeOfFood'],
      description: message[i]['description'],
    };
    posts.push(post);
  }
  return posts;
}

/**
 * Initializes the embedded Google Maps map.
 */
function initMap() {
  // Remove the 'loading map' spinner (not strictly necessary since the
  // API consumes the entire 'map' element, but can't hurt).
  removeMapSpinner();
  addMapButtons();

  // Get the college of the page and center the map on it.
  map = new google.maps.Map(document.getElementById('map'),
      {
        center: {lat: collegeLocation.lat, lng: collegeLocation.long},
        zoom: 17,
        mapTypeControl: false,
      },
  );

  // Get all posts on the page and show them as markers.
  posts.forEach((post) => {
    const now = new Date();
    const width = getMapMarkerIconSize(post.numOfPeopleFoodWillFeed, 'width');
    const height = getMapMarkerIconSize(post.numOfPeopleFoodWillFeed, 'height');
    const icon = {
      url: getMapMarkerIconUrl(now, post.eventStartTime),
      scaledSize: new google.maps.Size(width, height),
      origin: new google.maps.Point(0, 0),
    };

    if (post.location.lat && post.location.long) {
      const marker = new google.maps.Marker({
        position: {lat: post.location.lat, lng: post.location.long},
        map: map,
        title: post.eventStartTime > new Date() ?
          `${post.organizationName} (not started yet)` :
          `${post.organizationName} (happening now)`,
        opacity: getMapMarkerOpacity(now, post.eventStartTime, post.eventEndTime),
        icon: icon,
      });

      marker.addListener('click', function() {
        const postElement = document.getElementById(post.id);
        postElement.scrollIntoView({block: 'center'});
        postElement.style.boxShadow = '0 1px 20px #939393, 0 -1px 20px #939393';

        // JS does not have native sleep(), so we can spoof the behavior with Promises.
        new Promise((r) => setTimeout(r, 1000)).then(() => {
          postElement.style.boxShadow = '0 1px 10px lightgrey, 0 -1px 10px lightgrey';
        });
      });
    }
  });

  // Get the user's position and show it as a marker, if they consent and their browser supports
  // geolocation. If anything goes wrong, just default to not showing them their location.
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(addUserToMap, () => {});
  }
}

/**
 * Adds the user to the map based on their browser's reported location.
 * @param {any} position - The position returned by the browser's geolocator.
 */
function addUserToMap(position) {
  const icon = {
    url: './assets/svg/bluemarker.svg',
    scaledSize: new google.maps.Size(30, 30),
    anchor: new google.maps.Point(15, 15),
    origin: new google.maps.Point(0, 0),
  };

  new google.maps.Marker({
    position: {lat: position.coords.latitude, lng: position.coords.longitude},
    map: map,
    title: 'That\'s you!',
    icon: icon,
  });
}

/**
 * Calculates the opacity of a given map marker.
 * @param {Date} now - The current time.
 * @param {Date} eventStartTime - The start time of the event.
 * @param {Date} eventEndTime - The end time of the event.
 * @return {number} - The opacity to show the marker as.
 */
function getMapMarkerOpacity(now, eventStartTime, eventEndTime) {
  const startTime = eventStartTime;
  const endTime = eventEndTime;

  // If the date is in the future, our marker should have full opacity.
  if (startTime > now) {
    return 1;
  // If the event is over, our marker should not be on the map at all.
  } else if (endTime <= now) {
    return 0;
  }

  // Else, we know that the event is currently happening. In that case, we need to map
  // the range between 1 to 0 to the range of time between eventStartTime and eventEndTime.
  const adjusted = 1 - ((now - startTime) / (endTime - startTime));
  return adjusted;
}

/**
 * Returns the appropriate marker icon for a marker.
 * Grey if the event hasn't started yet, red otherwise.
 * @param {Date} now - The current time.
 * @param {Date} eventStartTime - The start time of the event.
 * @return {string} - The location of the marker icon.
 */
function getMapMarkerIconUrl(now, eventStartTime) {
  if (eventStartTime > now) {
    return './assets/svg/greymarker.svg';
  }

  return './assets/svg/redmarker.svg';
}

/**
 * Returns the appropriate marker size for a single dimension of a marker, based on the number
 * of people the food will feed. Uses a logistic function so that incredibly large and small
 * numbers still have reasonable icon sizes.
 * @param {number} numOfPeopleFoodWillFeed - The number of people the event's food will feed.
 * @param {string} dimensionType - The dimension type, either 'width' or 'height'.
 * @return {number} - The marker's dimension size.
 */
function getMapMarkerIconSize(numOfPeopleFoodWillFeed, dimensionType) {
  // Sanity check input for invalid values.
  if (dimensionType !== 'width' && dimensionType !== 'height') {
    return null;
  }

  // Apply the logistic function to the input.
  // The number of people is the x-value, and the bounds for the height
  // and width determine the logistic function's horizontal asymptotes.
  if (dimensionType === 'width') {
    return applyLogisticFunction(numOfPeopleFoodWillFeed, MARKER_WIDTH_MINMAX);
  } else {
    return applyLogisticFunction(numOfPeopleFoodWillFeed, MARKER_HEIGHT_MINMAX);
  }
}

/**
 * Applies a logistic function to the input. The function is designed such that x-values
 * over 40 level out to bounds.max, and x-values under 5 level out to bounds.min.
 * @param {number} xValue - The x-value to evaluate the function at.
 * @param {{min: number, max: number}} bounds - The asymptotes of the logistic function.
 * @return {number} - The output of the logistic function (i.e. the y-value).
 */
function applyLogisticFunction(xValue, bounds) {
  return Math.round((bounds.max - bounds.min)/
      (Math.exp(-((xValue - 28)/6)) + 1) + bounds.min);
}

/**
 * Adds posts to the page.
 * @param {array} posts
 */
async function addPosts(posts) {
  const allPosts = document.getElementById('all-posts');
  for (let i = 0; i < posts.length; i++) {
    const post = posts[i];
    const titleText = post.organizationName + ' @ ' + post.location.name;
    const subtitleText = post.foodType + ' | ' +
      post.eventStartTime.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'}) +
      '-' + post.eventEndTime.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
    const descriptionText = post.description;

    // Create card.
    const postCard = document.createElement('div');
    postCard.setAttribute('class', 'post-card');
    postCard.setAttribute('id', post.id);
    postCard.setAttribute('tabindex', '0');

    // Create and add title.
    const title = document.createElement('h2');
    title.setAttribute('class', 'card-title');
    title.setAttribute('tabindex', '0');
    title.innerText = titleText;
    postCard.appendChild(title);

    // Create and add subtitle.
    const subtitle = document.createElement('h3');
    subtitle.setAttribute('class', 'card-subtitle');
    subtitle.setAttribute('tabindex', '0');
    subtitle.innerText = subtitleText;
    postCard.appendChild(subtitle);

    // Create and add description.
    const description = document.createElement('p');
    description.setAttribute('class', 'card-description');
    description.setAttribute('tabindex', '0');
    description.innerText = descriptionText;
    postCard.appendChild(description);

    // Add card to the page.
    allPosts.append(postCard);
  }
}

/**
 * Shows the modal.
 * @return {void}
 */
function showModal() {
  if (modal) {
    modal.style.display = 'block';
    modalCard.focus();
    submitModalButton.disabled = false;
  }
}

/**
 * Closes and resets the modal, refocuses to create post button.
 * @return {void}
 */
function closeModal() {
  if (modal) {
    modal.style.display = 'none';
    modalForm.reset();
    createPostButton.focus();
  }
}

/**
 * On click of the submit button, sends modal data to the servlet.
 * @return {void}
 */
async function submitModal() {
  // Disable multiple submissions.
  submitModalButton.disabled = true;

  if (validateModal()) {
    checkLocationAndSubmit();
  }
  else {
    submitModalButton.disabled = false;
  }
}

function validateModal() {
  const invalidIds = [];
  const formElements = modalForm.elements;

  validateModalText(invalidIds, formElements);
  validateModalDate(invalidIds, formElements);
  validateModalTime(invalidIds,formElements);
  console.log(invalidIds);

  if (invalidIds.length === 0) {
    return true;
  }
  else {
    return false;
  }

  // If one of the fields is empty, don't submit.
  // Uses formElement.length - 1 to exclude the button element.
  
//   for (let i = 0; i < formElements.length - 1; i++) {
//     if (formElements[i].value.length == 0) {
//       return false;
//     }
//   }
//   return true;
}

function validateModalDate(invalidIds, formElements) {
  const month = formElements.namedItem('modal-month').value;
  const day = formElements.namedItem('modal-day').value;
  monthDayLengths = [31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
  if (month > 12 || month < 1) {
    invalidIds.push('modal-month');
  }
  else if (day < 1 || day > monthDayLengths[month - 1]) {
    invalidIds.push('modal-day');
  }
}

function validateModalTime(invalidIds, formElements) {
  const startHour = formElements.namedItem('modal-start-hour').value;
  const startMinute = formElements.namedItem('modal-start-minute').value;
  const startAMorPM = formElements.namedItem('start-am-or-pm').value;
  const endHour = formElements.namedItem('modal-end-hour').value;
  const endMinute = formElements.namedItem('modal-end-minute').value;
  const endAMorPM = formElements.namedItem('end-am-or-pm').value;

  // Check if the hours fall between 1-12.
  if (startHour < 1 || startHour > 12) {
    invalidIds.push('modal-start-hour');
  }
  if (endHour < 1 || endHour > 12) {
    invalidIds.push('modal-end-hour');
  }
  // Check if the minutes fall between 0 - 60.
  if (startMinute < 0 || startMinute >= 60 || isBlank(startMinute)) {
    invalidIds.push('modal-start-minute');
  }
  if (endMinute < 0 || endMinute >= 60 || isBlank(startMinute)) {
    invalidIds.push('modal-end-minute');
  }
  // Check if the end time is after the start time.
  if ((startAMorPM === 'pm' && endAMorPM === 'am') || 
  (startAMorPM == endAMorPM && 
  (endHour < startHour || (endHour === startHour && endMinute < startMinute)))) {
    invalidIds.push('modal-start-hour');
    invalidIds.push('modal-start-minute');
    invalidIds.push('start-am-or-pm');
    invalidIds.push('modal-end-hour');
    invalidIds.push('modal-end-minute');
    invalidIds.push('end-am-or-pm');
  }
}

function isBlank(input) {
  const trimmed = input.trim();
  if (trimmed) {
    return false;
  }
  else {
    return true;
  }
}

function validateModalText(invalidIds, formElements) {
  for (let i = 0; i < formElements.length; i++) {
    if (formElements[i].type === 'text' || formElements[i].type === 'textarea') {
      if(isBlank(formElements[i].value)) {
        invalidIds.push(formElements[i].id);
      }
    }
  }
}

// function validateModalText(formElements) {
//   const organizationName  
// }

async function checkLocationAndSubmit() {
  const collegeId = (new URLSearchParams(window.location.search)).get('collegeid');

  if (modalForm && collegeId) {
    const modalLocation = document.getElementById('modal-location').value;
    const latLngResult = await translateLocationToLatLong(modalLocation);

    let url;

    // If the address entered isn't the same one as the one we last checked,
    // then we can assume the user edited their address and we should display
    // a fresh error if this new address is also invalid.
    if (!latLngResult && modalLocation !== cachedModalAddress) {
      cachedModalAddress = modalLocation;
      const modalError = document.createElement('div');
      modalError.setAttribute('id', 'modal-error');
      modalError.innerText =
        `We couldn't find address '${modalLocation}'. ` +
        'Please check your address for errors. ' +
        'If you wish to submit anyway, no pin will be added to the map.';
      document.getElementById('modal-form')
          .insertBefore(modalError, document.getElementById('modal-submit'));
      submitModalButton.disabled = false;
    // Else, if the invalid address is the same as we last checked
    // or the address is just plain valid, then add the post to the Datastore.
    } else {
      const modalError = document.getElementById('modal-error');
      if (modalError) {
        document.getElementById('modal-form').removeChild(modalError);
      }

      // (0,0) denotes a nonexistent lat/long, since it's a location in the ocean + is falsy.
      const lat = latLngResult ? latLngResult.lat : 0;
      const lng = latLngResult ? latLngResult.long : 0;
      url = `/postData?collegeId=${collegeId}&lat=${lat}&lng=${lng}`;
      modalForm.action = url;
      modalForm.submit();
      createPostButton.focus();
    }
  }
}

/**
 * Closes the modal if user clicks outside.
 * @param {myEvent} event - the modal background being clicked.
 * @listens myEvent
 * @return {void}
 */
window.onclick = function(event) {
  if (modal && event.target == modal) {
    modal.style.display = 'none';
    createPostButton.focus();
  }
};

document.addEventListener('keydown', function(e) {
  const isTabPressed = e.key === 'Tab' || e.keyCode === 9;

  if (!isTabPressed) {
    return;
  }
  // If user is trying to go to the previous element, make sure it wraps to the bottom.
  if (e.shiftKey) { // If shift key pressed for shift + tab combination.
    if (document.activeElement === modalCard) {
      submitModalButton.focus();
      e.preventDefault();
    }
  } else { // If user is trying to go to the next element, make sure it wraps to the top.
    if (document.activeElement === submitModalButton) {
      modalCard.focus();
      e.preventDefault();
    }
  }
});

/**
 * Toggles the legend next to the map.
 */
function toggleLegend() {
  const legend = document.getElementById('map-legend');
  if (legend.computedStyleMap().get('z-index') == 0) {
    legend.style.zIndex = 1;
    legend.style.display = 'block';
  } else {
    legend.style.zIndex = 0;
    legend.style.display = 'none';
  }
}
