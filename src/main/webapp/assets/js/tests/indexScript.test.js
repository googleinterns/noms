// ESLint doesn't know that our functions are defined in other files.
/* eslint-disable no-undef */

const LOCATION_0 = {LAT: 0, LON: 0};
const LOCATION_4 = {LAT: 4, LON: 4};
const LOCATION_15 = {LAT: 15, LON: 15};
const LOCATION_35 = {LAT: 35, LON: 35};
const LOCATION_45 = {LAT: 45, LON: 45};

/**
 * Test suite for all functions that control the distribution of map markers.
 */
describe('Distribution of Map Markers', function() {
  /**
   * Tests for getRepresentativeCollegeSample().
   */
  describe('#getRepresentativeCollegeSample()', function() {
    it('should return only one location if two are too close to each other', function() {
      const collegeLocations = [
        LOCATION_0,
        {
          LAT: LOCATION_0.LAT + 0.5,
          LON: LOCATION_0.LON - 0.5,
        },
      ];
      expect(getRepresentativeCollegeSample(collegeLocations).length).to.be.equal(1);
    });

    it('should return all locations if they are far enough apart from each other', function() {
      const collegeLocations = [
        LOCATION_0,
        LOCATION_4,
        LOCATION_15,
        LOCATION_35,
        LOCATION_45,
      ];
      expect(getRepresentativeCollegeSample(collegeLocations).length).to.be.equal(5);
    });

    it('should return only some locations if others are too close to each other', function() {
      const collegeLocations = [
        LOCATION_0,
        {
          LAT: LOCATION_0.LAT + 0.4,
          LON: LOCATION_0.LON - 0.5,
        },
        LOCATION_15,
        LOCATION_35,
        {
          LAT: LOCATION_35.LAT + 0.2,
          LON: LOCATION_35.LON - 0.3,
        },
      ];
      expect(getRepresentativeCollegeSample(collegeLocations).length).to.be.equal(3);
    });
  });
});
