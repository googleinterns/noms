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
 * @property {string} eventStartTime - The starting time of the event described in the post.
 * @property {string} eventEndTime - The ending time of the event described in the post.
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

  // Add the embedded map to the page.
  const script = document.createElement('script');
  script.src = `https://maps.googleapis.com/maps/api/js?key=AIzaSyB_iMemNeUtO8D7kGTmagTgluRlsiFTcDA&callback=initMap`;
  script.defer = true;
  script.async = true;
  window.initMap = initMap;
  document.head.appendChild(script);
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
 * This will be removed once our backend has actual posts.
 * @return {array} - The posts
 */
function fetchFakePosts() {
  const fakePosts = [];

  for (let i = 0; i < 5; i++) {
    const post = {
      organizationName: `Organization ${i}`,
      postDateTime: (new Date()).setHours((new Date()).getHours - i),
      eventStartTime: '5:00pm',
      eventEndTime: '7:00pm',
      location: {
        name: `Office ${i}`,
        lat: 37.348545 + (i + Math.random()*10 - 5) / 5000,
        long: -121.9386406 + (i + Math.random()*10 - 5)/ 5000,
      },
      numOfPeopleFoodWillFeed: (10 - i),
      foodType: 'Thai Food',
      description: 'Hello! We have food.',
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
    new google.maps.Marker({
      position: {lat: post.location.lat, lng: post.location.long},
      map: map,
      title: post.organizationName,
    });
  });

/* eslint-enable no-undef */
}

/* Responsive navigation bar */
const burger = document.querySelector('.burger i');
const nav = document.querySelector('.nav');

/**
 * Toggles navigation bar to be responsive
 */
function toggleNav() {
  burger.classList.toggle('fa-bars');
  burger.classList.toggle('fa-times');
  nav.classList.toggle('nav-active');
}
burger.addEventListener('click', toggleNav);

/**
 * Adds posts to the page (uses mock data).
 */
function addPosts() {
  const titleText = 'ACM @ Kenna Hall';
  const subtitleText = 'Burritos | 6:00pm-8:00pm';
  const descriptionText = 'Come join the ACM for free burritos and to learn more' +
    'about what our club does! All are welcome to join the club happenings, ' +
    'regardless of major or year. The event itself goes from 6:00pm - 7:00pm, ' +
    'and leftovers will be available until 8:00pm. We have vegatarian and halal options available.';

  const allPosts = document.getElementById('all-posts');

  for (let i = 0; i < 10; i++) {
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
  }
}
