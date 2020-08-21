// ESLint doesn't know that our functions are defined in other files.
/* eslint-disable no-undef */

const MEMORIAL_UNION_FORMATTED_ADDRESS = 'Southwest Jefferson Way, Corvallis OR, 97331';
const MEMORIAL_UNION_GEOMETRY = {
  location: {
    lat: 44.5649688,
    lng: -123.2789571,
  },
};

// Times
// (note: hours aren't accurate but demonstrate relationships between values)
const TIME_0900 = new Date(1000000);
const TIME_1200 = new Date(1050000);
const TIME_1500 = new Date(1080000);
const TIME_2000 = new Date(1120000);
const TIME_2200 = new Date(1150000);
const TIME_2300 = new Date(1190000);

// Post defaults
const ORG_NAME_DEFAULT = 'ACM-W';
const POST_DATETIME_DEFAULT = TIME_0900;
const EVENT_START_TIME_DEFAULT = TIME_1200;
const EVENT_END_TIME_DEFAULT = TIME_2000;
const LOCATION_DEFAULT =
  {
    name: 'Memorial Union',
    lat: 44.5646872,
    long: -123.2789571,
    city: 'Corvallis',
  };
const NUM_PEOPLE_DEFAULT = 10;
const FOODTYPE_DEFAULT = 'Sandwiches';
const DESCRIPTION_DEFAULT = 'Come pick up all of these extra sandwiches we have!';

// Locations
const AT_MEMORIAL_UNION = {lat: 44.5646872, long: -123.2789571};
const AT_STUDENT_EXPERIENCE_CENTER = {lat: 44.5649397, long: -123.2781688};
const AT_LINUS_PAULING_INSTITUTE = {lat: 44.5663558, long: -123.282799};
const AT_MCNARY_FIELD = {lat: 44.5657475, long: -123.2712236};
const FAR_AWAY = {lat: 126.4264, long: -48.0283};

/**
 * Test suite for all functions that control the placement/sizing of map markers.
 */
describe('Sizing/Placement of Map Markers', function() {
  /**
   * Tests for getMapMarkerOpacity().
   */
  describe('#getMapMarkerOpacity()', function() {
    it('should return 1 (fully opaque) when the start date is in the future', function() {
      const now = new Date(10000000);
      const futureDateStart = new Date(10000010);
      const futureDateEnd = new Date(10000015);
      expect(getMapMarkerOpacity(now, futureDateStart, futureDateEnd)).to.be.equal(1);
    });

    it('should return 0 (fully transparent) when the end date is in the past', function() {
      const now = new Date(10000020);
      const pastDateStart = new Date(10000010);
      const pastDateEnd = new Date(10000015);
      expect(getMapMarkerOpacity(now, pastDateStart, pastDateEnd)).to.be.equal(0);
    });

    it('should return 0.5 when the event is exactly halfway finished', function() {
      const now = new Date(10000005);
      const startDate = new Date(10000000);
      const endDate = new Date(10000010);
      expect(getMapMarkerOpacity(now, startDate, endDate)).to.be.equal(0.5);
    });

    it('should return 0.8 when the event is 1/5 finished', function() {
      const now = new Date(10000002);
      const startDate = new Date(10000000);
      const endDate = new Date(10000010);
      expect(getMapMarkerOpacity(now, startDate, endDate)).to.be.equal(0.8);
    });
  });

  /**
   * Tests for getMapMarkerIconUrl().
   */
  describe('#getMapMarkerIconUrl()', function() {
    it('should return the grey marker when the start date is in the future', function() {
      const now = new Date(10000000);
      const startDate = new Date(10000010);
      expect(getMapMarkerIconUrl(now, startDate)).to.include('greymarker');
    });

    it('should return the red marker when the start date is right now', function() {
      const now = new Date(10000000);
      const startDate = new Date(10000000);
      expect(getMapMarkerIconUrl(now, startDate)).to.include('redmarker');
    });

    it('should return the red marker when the start date is in the past', function() {
      const now = new Date(10000020);
      const startDate = new Date(10000010);
      expect(getMapMarkerIconUrl(now, startDate)).to.include('redmarker');
    });
  });

  /**
   * Tests for applyLogisticFunction().
   */
  describe('#applyLogisticFunction()', function() {
    it('should return the lower asymptote at low x-values', function() {
      expect(applyLogisticFunction(0, {min: 5, max: 30})).to.equal(5);
    });

    it('should return the higher asymptote at high x-values', function() {
      expect(applyLogisticFunction(60, {min: 5, max: 30})).to.equal(30);
    });

    it('should return the lower asymptote at negative x-values', function() {
      expect(applyLogisticFunction(-5, {min: 5, max: 30})).to.equal(5);
    });

    it('should return the higher asymptote at really big x-values', function() {
      expect(applyLogisticFunction(100000, {min: 5, max: 30})).to.equal(30);
    });
  });

  /**
   * Tests for getMapMarkerIconSize().
   */
  describe('#getMapMarkerIconSize()', function() {
    it('should return null if the dimensionType isn\'t \'width\' or \'height\'', function() {
      expect(getMapMarkerIconSize(0, 'garbage')).to.be.null;
    });

    it('should return the minimum if the event won\'t feed many people', function() {
      expect(getMapMarkerIconSize(0, 'width')).to.equal(MARKER_WIDTH_MINMAX.min);
    });

    it('should return the max if the event will feed many people', function() {
      expect(getMapMarkerIconSize(100, 'height')).to.equal(MARKER_HEIGHT_MINMAX.max);
    });
  });
});

/**
 * Test suite for geolocation functionality on the page.
 */
describe('Geolocation Functionality', function() {
  /**
   * Tests for translateLocationToLatLong().
   */
  describe('#translateLocationToLatLong()', function() {
    it('should return the result if it gets back any api results', async function() {
      const address = 'Memorial Union, Corvallis';
      const mockApiFunction = async (_) => {
        return {
          ok: true,
          json: async function() {
            return {
              formattedAddress: MEMORIAL_UNION_FORMATTED_ADDRESS,
              geometry: MEMORIAL_UNION_GEOMETRY,
            };
          },
        };
      };
      const mockLocality = {
        city: 'Corvallis',
      };
      const result = {
        name: MEMORIAL_UNION_FORMATTED_ADDRESS,
        lat: MEMORIAL_UNION_GEOMETRY.location.lat,
        long: MEMORIAL_UNION_GEOMETRY.location.lng,
      };
      expect(await translateLocationToLatLong(address, mockApiFunction, mockLocality))
          .to.be.eql(result);
    });

    it('should add the city to the query if it wasn\'t already present', async function() {
      const address = 'Memorial Union';
      const mockApiFunction = async (address) => {
        return {
          ok: true,
          json: async function() {
            if (address === 'Memorial Union, Corvallis') {
              return {
                formattedAddress: MEMORIAL_UNION_FORMATTED_ADDRESS,
                geometry: MEMORIAL_UNION_GEOMETRY,
              };
            } else {
              return {};
            }
          },
        };
      };
      const mockLocality = {
        city: 'Corvallis',
      };
      const result = {
        name: MEMORIAL_UNION_FORMATTED_ADDRESS,
        lat: MEMORIAL_UNION_GEOMETRY.location.lat,
        long: MEMORIAL_UNION_GEOMETRY.location.lng,
      };
      expect(await translateLocationToLatLong(address, mockApiFunction, mockLocality))
          .to.be.eql(result);
    });

    it('shouldn\'t add the city to the query if it was already present', async function() {
      const address = 'Memorial Union, Corvallis';
      const mockApiFunction = async (address) => {
        return {
          ok: true,
          json: async function() {
            if (address === 'Memorial Union, Corvallis') {
              return {
                formattedAddress: MEMORIAL_UNION_FORMATTED_ADDRESS,
                geometry: MEMORIAL_UNION_GEOMETRY,
              };
            } else {
              return {};
            }
          },
        };
      };
      const result = {
        name: MEMORIAL_UNION_FORMATTED_ADDRESS,
        lat: MEMORIAL_UNION_GEOMETRY.location.lat,
        long: MEMORIAL_UNION_GEOMETRY.location.lng,
      };
      expect(await translateLocationToLatLong(address, mockApiFunction)).to.be.eql(result);
    });

    it('should return null if the api returns a non-200 response', async function() {
      const address = 'Memorial Union, Corvallis';
      const mockApiFunction = async (_) => {
        return {
          ok: false,
          status: 400,
          text: async function() {
            return 'Request failed';
          },
        };
      };
      expect(await translateLocationToLatLong(address, mockApiFunction)).to.be.null;
    });

    it('should return null if the api didn\'t return any results', async function() {
      const address = 'Memorial Union, Corvallis';
      const mockApiFunction = async (_) => {
        return {
          ok: true,
          json: async function() {
            return {};
          },
        };
      };
      expect(await translateLocationToLatLong(address, mockApiFunction)).to.be.null;
    });

    it('should return null if the api didn\'t return a lat/lng', async function() {
      const address = 'Memorial Union, Corvallis';
      const mockApiFunction = async (_) => {
        return {
          ok: true,
          json: async function() {
            return {
              formattedAddress: MEMORIAL_UNION_FORMATTED_ADDRESS,
              geometry: {},
            };
          },
        };
      };
      expect(await translateLocationToLatLong(address, mockApiFunction)).to.be.null;
    });
  });
});

/**
 * Test suite for post filtering functionality.
 */
describe('Post Filtering', function() {
  /**
   * Tests for filterPosts().
   */
  describe('#filterPosts()', function() {
    it('should return all generic posts when given default filters', function() {
      const posts = [];
      for (let i = 1; i < 5; i ++) {
        posts.push(newPost(i));
      }
      const now = TIME_1500; // Middle of default event
      const userLocation = AT_MEMORIAL_UNION;
      const filters = newFilter();

      const result = filterPosts(posts, now, userLocation, filters);
      expect(result.length).to.be.equal(4);
      expect(extractIDs(result)).to.deep.equal([1, 2, 3, 4]);
    });

    it('should return all posts when given default filters, even outliers', function() {
      const posts = [];
      posts.push(newPost(1, 0, 0, 0, 0, FAR_AWAY, 0, 0, 0)); // Location diff. > 3 miles
      posts.push(newPost(2, 0, 0, 0, 0, 0, 100, 0, 0)); // Num. people > 40
      const now = TIME_0900;
      const userLocation = AT_MEMORIAL_UNION;
      const filters = newFilter(3);

      const result = filterPosts(posts, now, userLocation, filters);
      expect(result.length).to.be.equal(2);
      expect(extractIDs(result)).to.deep.equal([1, 2]);
    });

    it('should only return posts with a given keyword in the foodtype or description', function() {
      const posts = [];
      posts.push(newPost(1, 0, 0, 0, 0, 0, 0, 'Bear', 'This is a description.'));
      posts.push(newPost(2, 0, 0, 0, 0, 0, 0, 'Different', 'This mentions bear.'));
      posts.push(newPost(3));
      posts.push(newPost(4, 'Bear Club', 0, 0, 0, 0, 0, 0, 0));
      const now = TIME_0900;
      const userLocation = AT_MEMORIAL_UNION;
      const filters = newFilter(0, 0, 0, ['bear']);

      const result = filterPosts(posts, now, userLocation, filters);
      expect(result.length).to.be.equal(3);
      expect(extractIDs(result)).to.deep.equal([1, 2, 4]);
    });

    it('should return posts with any of multiple given keywords', function() {
      const posts = [];
      posts.push(newPost(1, 0, 0, 0, 0, 0, 0, 'Bear', 'This is a description.'));
      posts.push(newPost(2, 0, 0, 0, 0, 0, 0, 'Different', 'This mentions bear.'));
      posts.push(newPost(3, 0, 0, 0, 0, 0, 0, 'Globes', 'Global society'));
      posts.push(newPost(4));
      posts.push(newPost(5, 0, 0, 0, 0, 0, 0, 'Plant', 'Eat our plants, plase.'));
      posts.push(newPost(6, 0, 0, 0, 0, 0, 0, 'Different', 'plant@gmail.com'));
      posts.push(newPost(7, 'Bear Club', 0, 0, 0, 0, 0, 0, 0));
      const now = TIME_0900;
      const userLocation = AT_MEMORIAL_UNION;
      const filters = newFilter(0, 0, 0, ['bear', 'plant']);

      const result = filterPosts(posts, now, userLocation, filters);
      expect(result.length).to.be.equal(5);
      expect(extractIDs(result)).to.deep.equal([1, 2, 5, 6, 7]);
    });

    it('shouldn\'t return any posts if the keyword(s) don\'t match anything', function() {
      const posts = [];
      posts.push(newPost(1, 0, 0, 0, 0, 0, 0, 'Bear', 'This is a description.'));
      posts.push(newPost(2, 0, 0, 0, 0, 0, 0, 'Different', 'This mentions bear.'));
      posts.push(newPost(3));
      const now = TIME_0900;
      const userLocation = AT_MEMORIAL_UNION;
      const filters = newFilter(0, 0, 0, ['garbage']);

      const result = filterPosts(posts, now, userLocation, filters);
      expect(result.length).to.be.equal(0);
      expect(extractIDs(result)).to.deep.equal([]);
    });

    it('should only return posts happening right now if happeningNow = true', function() {
      const posts = [];
      posts.push(newPost(1, 0, 0, TIME_0900, TIME_1200, 0, 0, 0, 0)); // Happened earlier
      posts.push(newPost(2)); // Default event (happening 'now')
      posts.push(newPost(3, 0, 0, TIME_2200, TIME_2300, 0, 0, 0, 0)); // Happened later
      const now = TIME_1500; // Middle of the default event
      const userLocation = AT_MEMORIAL_UNION;
      const filters = newFilter(0, true, 0, 0);

      const result = filterPosts(posts, now, userLocation, filters);
      expect(result.length).to.be.equal(1);
      expect(extractIDs(result)).to.deep.equal([2]);
    });

    it('should return all posts happening if happeningNow = false', function() {
      const posts = [];
      posts.push(newPost(1, 0, 0, TIME_0900, TIME_1200, 0, 0, 0, 0)); // Happened earlier
      posts.push(newPost(2)); // Default event (happening 'now')
      posts.push(newPost(3, 0, 0, TIME_2200, TIME_2300, 0, 0, 0, 0)); // Happened later
      const now = TIME_1500; // Middle of the default event
      const userLocation = AT_MEMORIAL_UNION;
      const filters = newFilter(0, false, 0, 0);

      const result = filterPosts(posts, now, userLocation, filters);
      expect(result.length).to.be.equal(3);
      expect(extractIDs(result)).to.deep.equal([1, 2, 3]);
    });

    it('should only return posts within the distance radius filter = 1.5', function() {
      const posts = [];
      posts.push(newPost(1, 0, 0, 0, 0, FAR_AWAY, 0, 0, 0));
      posts.push(newPost(2, 0, 0, 0, 0, AT_MCNARY_FIELD, 0, 0, 0));
      posts.push(newPost(3, 0, 0, 0, 0, AT_MEMORIAL_UNION, 0, 0, 0));
      posts.push(newPost(4, 0, 0, 0, 0, AT_STUDENT_EXPERIENCE_CENTER, 0, 0, 0));
      const now = TIME_1500;
      const userLocation = AT_LINUS_PAULING_INSTITUTE;
      const filters = newFilter(0, 0, 1.5, 0);

      const result = filterPosts(posts, now, userLocation, filters);
      expect(result.length).to.be.equal(3);
      expect(extractIDs(result)).to.deep.equal([2, 3, 4]);
    });

    it('should only return posts within the distance radius filter = 0.4', function() {
      const posts = [];
      posts.push(newPost(1, 0, 0, 0, 0, FAR_AWAY, 0, 0, 0));
      posts.push(newPost(2, 0, 0, 0, 0, AT_MCNARY_FIELD, 0, 0, 0));
      posts.push(newPost(3, 0, 0, 0, 0, AT_MEMORIAL_UNION, 0, 0, 0));
      posts.push(newPost(4, 0, 0, 0, 0, AT_STUDENT_EXPERIENCE_CENTER, 0, 0, 0));
      const now = TIME_1500;
      const userLocation = AT_LINUS_PAULING_INSTITUTE;
      const filters = newFilter(0, 0, 0.4, 0);

      const result = filterPosts(posts, now, userLocation, filters);
      expect(result.length).to.be.equal(2);
      expect(extractIDs(result)).to.deep.equal([3, 4]);
    });

    it('should return all posts if the user\'s location isn\'t defined', function() {
      const posts = [];
      posts.push(newPost(1, 0, 0, 0, 0, FAR_AWAY, 0, 0, 0));
      posts.push(newPost(2, 0, 0, 0, 0, AT_MCNARY_FIELD, 0, 0, 0));
      posts.push(newPost(3, 0, 0, 0, 0, AT_MEMORIAL_UNION, 0, 0, 0));
      posts.push(newPost(4, 0, 0, 0, 0, AT_STUDENT_EXPERIENCE_CENTER, 0, 0, 0));
      const now = TIME_1500;
      const userLocation = null;
      const filters = newFilter(0, 0, 0.4, 0);

      const result = filterPosts(posts, now, userLocation, filters);
      expect(result.length).to.be.equal(4);
      expect(extractIDs(result)).to.deep.equal([1, 2, 3, 4]);
    });

    it('should return all events above the numPeople threshold = 0', function() {
      const posts = [];
      posts.push(newPost(1));
      posts.push(newPost(2, 0, 0, 0, 0, 0, 5, 0, 0));
      posts.push(newPost(3, 0, 0, 0, 0, 0, 50, 0, 0));
      posts.push(newPost(4, 0, 0, 0, 0, 0, 100, 0, 0));
      posts.push(newPost(5, 0, 0, 0, 0, 0, 1000, 0, 0));
      const now = TIME_1500;
      const userLocation = AT_MEMORIAL_UNION;
      const filters = newFilter();

      const result = filterPosts(posts, now, userLocation, filters);
      expect(result.length).to.be.equal(5);
      expect(extractIDs(result)).to.deep.equal([1, 2, 3, 4, 5]);
    });

    it('should return only events above the numPeople threshold = 15', function() {
      const posts = [];
      posts.push(newPost(1));
      posts.push(newPost(2, 0, 0, 0, 0, 0, 5, 0, 0));
      posts.push(newPost(3, 0, 0, 0, 0, 0, 50, 0, 0));
      posts.push(newPost(4, 0, 0, 0, 0, 0, 100, 0, 0));
      posts.push(newPost(5, 0, 0, 0, 0, 0, 1000, 0, 0));
      const now = TIME_1500;
      const userLocation = AT_MEMORIAL_UNION;
      const filters = newFilter(15, 0, 0, 0);

      const result = filterPosts(posts, now, userLocation, filters);
      expect(result.length).to.be.equal(3);
      expect(extractIDs(result)).to.deep.equal([3, 4, 5]);
    });

    it('should return the intersection of multiple filters, not the union', function() {
      const posts = [];
      posts.push(newPost(1)); // Fits all filters
      posts.push(newPost(2, 0, 0, TIME_2200, TIME_2300, 0, 0, 0, 0)); // Too late
      posts.push(newPost(3, 0, 0, 0, 0, AT_MCNARY_FIELD, 0, 0, 0)); // Too far away
      posts.push(newPost(4, 0, 0, TIME_2200, TIME_2300, AT_MCNARY_FIELD, 0, 0, 0)); // Both
      posts.push(newPost(5, 0, 0, 0, 0, 0, 2, 0, 0)); // Not enough people
      posts.push(newPost(6, 0, 0, TIME_0900, TIME_1200, 0, 50, 0, 0)); // Enough people, too early
      const now = TIME_1500;
      const userLocation = AT_LINUS_PAULING_INSTITUTE;
      const filters = newFilter(10, true, 0.5, ['']);

      const result = filterPosts(posts, now, userLocation, filters);
      expect(result.length).to.be.equal(1);
      expect(extractIDs(result)).to.deep.equal([1]);
    });
  });
});

/**
 * Creates a filter object with which to filter posts by.
 * If falsy values are passed to any parameter, page defaults are used.
 * @param {Number} numPeople - The minimum number of people the event can feed.
 * @param {Boolean} happeningNow - Whether the event is happening right now.
 * @param {Number} distance - The maximum distance from the user to the event.
 * @param {Array<String>} keywords - Keywords to include.
 * @return {Object} - The new filter object.
 */
function newFilter(numPeople = 0, happeningNow = 0, distance = 0, keywords = 0) {
  return {
    numPeople: numPeople ? numPeople : NUM_PEOPLE_FILTER_DEFAULT,
    happeningNow: happeningNow ? happeningNow : HAPPENING_NOW_FILTER_DEFAULT,
    distance: distance ? distance : DISTANCE_FILTER_DEFAULT,
    keywords: keywords ? keywords : KEYWORDS_FILTER_DEFAULT,
  };
}

/**
 * Creates a new post. If falsy values are passed to any parameter
 * (except ID, which is required), defaults are used.
 * @param {number} id - The unique id of the post.
 * @param {string} organizationName - The name of the organization making the post.
 * @param {Date} postDateTime - The date that the post was made.
 * @param {Date} eventStartTime - The starting time of the event described in the post.
 * @param {Date} eventEndTime - The ending time of the event described in the post.
 * @param {LocationInfo} location - The location of the event.
 * @param {number} numOfPeople - The number of people the event's food can feed.
 * @param {string} foodType - A short 1-3 word description of the type of food served.
 * @param {string} description - A longer description of the entire event.
 * @return {Object} - The new post object.
 */
function newPost(
    id,
    organizationName = 0,
    postDateTime = 0,
    eventStartTime = 0,
    eventEndTime = 0,
    location = 0,
    numOfPeople = 0,
    foodType = 0,
    description = 0) {
  return {
    id: id,
    organizationName: organizationName ? organizationName : ORG_NAME_DEFAULT,
    postDateTime: postDateTime ? postDateTime : POST_DATETIME_DEFAULT,
    eventStartTime: eventStartTime ? eventStartTime : EVENT_START_TIME_DEFAULT,
    eventEndTime: eventEndTime ? eventEndTime : EVENT_END_TIME_DEFAULT,
    location: location ? location : LOCATION_DEFAULT,
    numOfPeopleFoodWillFeed: numOfPeople ? numOfPeople : NUM_PEOPLE_DEFAULT,
    foodType: foodType ? foodType : FOODTYPE_DEFAULT,
    description: description ? description : DESCRIPTION_DEFAULT,
  };
}

/**
 * Returns an array of all IDs in an array of posts, sorted ascending.
 * @param {Array<PostInfo>} posts - The posts to extract an ID from.
 * @return {Array<Number>} - An array of the IDs in the posts.
 */
function extractIDs(posts) {
  return posts.map((p) => p.id).sort();
}
