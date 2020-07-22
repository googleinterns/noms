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
 * An object contain information contained in a post.
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
// Global "static" variables, attached to the window
//

/* eslint-disable no-var */

/** @type {LocationInfo} */
var collegeLocation = null;

/** @type {Array<PostInfo>} */
var posts = null;

/* eslint-enable no-var */

//
// Functions
//

/**
 * Gets the college location for the page we are on.
 * @return {LocationInfo} - The college's location and information.
 */
function getCollegeLocation() {
  if (collegeLocation === null) {
    const newLocation = {
      name: 'Santa Clara University',
      lat: 37.348545,
      long: -121.9386406,
    };
    collegeLocation = newLocation;
  }
  return collegeLocation;
}

/**
 * Get the all posts for the page we are on.
 * @return {array} all posts
 */
function getPosts() {
  // Only go to the effort of fetching posts if we haven't done so previously.
  if (posts === undefined || posts === null) {
    // In the future, we can get these values using the query string parameters.
    const currentCollegeId = 2;

    // In the future, there will be a real GET request here,
    // but for now we use a fake one with hardcoded posts.
    posts = fetchFakePosts(currentCollegeId);
  }
  return posts;
}

/**
 * A fake implementation of a GET request that fetches all posts.
 * @param {number} collegeId - The id of the college to get posts from
 * @return {array} - The posts
 */
function fetchFakePosts(collegeId) {
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

let map;

/* eslint-disable no-undef, no-unused-vars */
/**
 * Initializes the embedded Google Maps map.
 */
function initMap() {
  const collegeLocation = getCollegeLocation();

  map = new google.maps.Map(document.getElementById('map'),
      {
        center: {lat: collegeLocation.lat, lng: collegeLocation.long},
        zoom: 17,
      },
  );

  const posts = getPosts();

  posts.forEach((post) => {
    new google.maps.Marker({
      position: {lat: post.location.lat, lng: post.location.long},
      map: map,
      title: post.organizationName,
    });
  });


  console.log(posts);
}
/* eslint-enable no-undef, no-unused-vars */
