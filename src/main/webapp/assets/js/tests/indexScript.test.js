// ESLint doesn't know that our functions are defined in other files.
/* eslint-disable no-undef */

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
        {
          LAT: 23,
          LON: 23,
        },
        {
          LAT: 23.2,
          LON: 22.8,
        },
      ];
      expect(getRepresentativeCollegeSample(collegeLocations).length).to.be.equal(1);
    });

    it('should return all locations if they are far enough apart from each other', function() {
      const collegeLocations = [
        {
          LAT: 0,
          LON: 0,
        },
        {
          LAT: 4,
          LON: 4,
        },
        {
          LAT: 15,
          LON: 15,
        },
        {
          LAT: 35,
          LON: 35,
        },
        {
          LAT: 45,
          LON: 45,
        },
      ];
      expect(getRepresentativeCollegeSample(collegeLocations).length).to.be.equal(5);
    });

    it('should return only some locations if others are too close to each other', function() {
      const collegeLocations = [
        {
          LAT: 0,
          LON: 0,
        },
        {
          LAT: 0.5,
          LON: 0.5,
        },
        {
          LAT: 15,
          LON: 15,
        },
        {
          LAT: 35,
          LON: 35,
        },
        {
          LAT: 35.5,
          LON: 34.5,
        },
      ];
      expect(getRepresentativeCollegeSample(collegeLocations).length).to.be.equal(3);
    });
  });
});
