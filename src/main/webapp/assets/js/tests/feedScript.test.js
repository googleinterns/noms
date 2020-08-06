// ESLint doesn't know that our functions are defined in other files.
/* eslint-disable no-undef */

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
      nock('')
          .post('/translateLocation', 'location=' + encodeURIComponent(address))
          .reply(200, {
            formattedAddress: 'Southwest Jefferson Way, Corvallis OR, 97331',
            geometry: {
              location: {
                lat: 44.5649688,
                lng: -123.2789571,
              },
            },
          });
      const result = {
        name: 'Southwest Jefferson Way, Corvallis OR, 97331',
        lat: 44.5649688,
        long: -123.2789571,
      };
      expect(await translateLocationToLatLong(address)).to.be.eql(result);
    });
  });
});