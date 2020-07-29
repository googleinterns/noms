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

// This file will be included only on the 'feed' (find-events) page,
// and contains items specific to the map and posts on that page.

//
// Types
//

/**
 * An object containing information about a location,
 * whether an entire college, building, or street.
 * @typedef {Object} LocationInfo
 * @property {string} name - The name of the location
 * @property {number} lat - The latitude of the location
 * @property {number} long - The longitude of the location
 */

/**
 * An object containing information about a post.
 * @typedef {Object} PostInfo
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

//
// Functions
//

/**
 * Fires as soon as the DOM is loaded.
 */
function onLoad() {
  // In the future, there will be real GET requests here, but for now, just fake ones.
  // These global variables will be assigned here and never assigned again.
  posts = fetchFakePosts();
  collegeLocation = fetchFakeCollegeLocation();

  if (document.getElementById('all-posts')) {
    addPosts(posts);
  }

  // Add the embedded map to the page.
  getSecretFor('javascript-maps-api').then((key) => {
    // TODO: If the key returns null, we should show a placeholder div with error text.

    const script = document.createElement('script');
    script.src = `https://maps.googleapis.com/maps/api/js?key=${key}&callback=initMap`;
    script.defer = true;
    script.async = true;
    window.initMap = initMap;
    document.head.appendChild(script);
  });
}

/**
 * Gets the secret value corresponding to a secret ID from GCP secrets store.
 * @param {string} secretid - The secret's id, as defined in the secrets store.
 */
async function getSecretFor(secretid) {
  try {
    const response = await fetch('/secretsManager?id=' + secretid, {method: 'POST'});
    if (!response.ok) {
      throw new Error(response.status);
    } else {
      return await response.text();
    }
  } catch (err) {
    console.warn(err);
    return;
  }
}

/**
 * Translates a location from its name to a pair of latitude and longitudes.
 * @param {string} address - The address to translate to lat/long.
 * @return {LocationInfo} or null if no such location exists or an error occurs.
 */
async function translateLocationToLatLong(address) {
  try {
    const response = await fetch('/translateLocation', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
      },
      body: createSearchParamsFromObject({location: address}),
    });

    // If we get a non-200 status response, then fail.
    if (!response.ok) {
      const error = await response.text();
      throw new Error(response.status + ' ' + error);
    }

    const result = await response.json();
    console.log(result);

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
 * A fake implementation of a GET request for the college of the page we are on.
 * This will be removed once our backend has actual college information.
 * @return {LocationInfo} - The college's location and information.
 */
function fetchFakeCollegeLocation() {
  const newLocation = {
    name: 'Santa Clara University',
    lat: 37.348545,
    long: -121.9386406,
  };
  return newLocation;
}

/**
 * A fake implementation of a GET request that fetches all posts.
 * One post's event hasn't started yet, one has ended, and the
 * other three are in various stages of being completed.
 * This will be removed once our backend has actual posts.
 * @return {array} - The posts.
 */
function fetchFakePosts() {
  const fakePosts = [];
  for (let i = 0; i < 5; i++) {
    const post = {
      organizationName: `Organization ${i}`,
      postDateTime: new Date(new Date().setHours(new Date().getHours() - i)),
      eventStartTime: new Date(new Date().setMinutes(new Date().getMinutes() + (i-3)*8)),
      eventEndTime: new Date(new Date().setMinutes(new Date().getMinutes() + (i-0.5)*10)),
      location: {
        name: `Office ${i}`,
        lat: 37.348545 + (i + Math.random()*10 - 5) / 5000,
        long: -121.9386406 + (i + Math.random()*10 - 5)/ 5000,
      },
      numOfPeopleFoodWillFeed: (30 - i*5),
      foodType: 'Thai Food',
      description: 'Come join the ACM for free burritos and to learn more ' +
        'about what our club does! All are welcome to join the club happenings, ' +
        'regardless of major or year. ' +
        'We have vegatarian and halal options available.',
    };
    fakePosts.push(post);
  }
  return fakePosts;
}

/**
 * Initializes the embedded Google Maps map.
 */
function initMap() {
  /* eslint-disable no-undef */
  // Get the college of the page and center the map on it.
  map = new google.maps.Map(document.getElementById('map'),
      {
        center: {lat: collegeLocation.lat, lng: collegeLocation.long},
        zoom: 17,
      },
  );

  // Get all posts on the page and show them as markers.
  posts.forEach((post) => {
    const width = getMapMarkerIconSize(post.numOfPeopleFoodWillFeed, 'width');
    const height = getMapMarkerIconSize(post.numOfPeopleFoodWillFeed, 'height');
    const icon = {
      url: getMapMarkerIconUrl(post.eventStartTime),
      scaledSize: new google.maps.Size(width, height),
      origin: new google.maps.Point(0, 0),
    };

    new google.maps.Marker({
      position: {lat: post.location.lat, lng: post.location.long},
      map: map,
      title: post.organizationName,
      opacity: getMapMarkerOpacity(post.eventStartTime, post.eventEndTime),
      icon: icon,
    });
  });

/* eslint-enable no-undef */
}

/**
 * Calculates the opacity of a given map marker.
 * @param {Date} eventStartTime - The start time of the event.
 * @param {Date} eventEndTime - The end time of the event.
 * @return {number} - The opacity to show the marker as.
 */
function getMapMarkerOpacity(eventStartTime, eventEndTime) {
  const now = new Date();
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
 * @param {Date} eventStartTime - The start time of the event.
 * @return {string} - The location of the marker icon.
 */
function getMapMarkerIconUrl(eventStartTime) {
  const now = new Date();

  if (eventStartTime > now) {
    return './assets/greymarker.svg';
  }

  return './assets/redmarker.svg';
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
      (Math.exp(-((xValue - 25)/6)) + 1) + bounds.min);
}

/**
 * Adds posts to the page (uses mock data).
 * @param {array} posts
 */
function addPosts(posts) {
  const allPosts = document.getElementById('all-posts');
  posts.forEach((post) => {
    const titleText = post.organizationName + ' @ ' + post.location.name;
    const subtitleText = post.foodType + ' | ' + post.eventStartTime.toLocaleTimeString('en-US') +
      '-' + post.eventEndTime.toLocaleTimeString('en-US');
    const descriptionText = post.description;

    // Create card.
    const postCard = document.createElement('div');
    postCard.setAttribute('class', 'post-card');

    // Create and add title.
    const title = document.createElement('h2');
    title.setAttribute('class', 'card-title');
    title.innerText = titleText;
    postCard.appendChild(title);

    // Create and add subtitle.
    const subtitle = document.createElement('h3');
    subtitle.setAttribute('class', 'card-subtitle');
    subtitle.innerText = subtitleText;
    postCard.appendChild(subtitle);

    // Create and add description.
    const description = document.createElement('p');
    description.setAttribute('class', 'card-description');
    description.innerText = descriptionText;
    postCard.appendChild(description);

    // Add card to the page.
    allPosts.append(postCard);
  });
}
