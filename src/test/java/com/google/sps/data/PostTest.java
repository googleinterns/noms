package com.google.sps;

import com.google.sps.data.Post;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public final class PostTest {

  // Given inputs for an HTTP Request, tests if the conversion to a Post object is valid.
  // Tester must check if start/end hour and month are valid manually.
  public Post testRequestToPost(String collegeId, String organizationName, int month, int day, int startHour, int startMinute, String startAMorPM, int endHour, 
  int endMinute, String endAMorPM, String location, double lat, double lng, int numberOfPeopleItFeeds, String typeOfFood, String description) {

    // Construct Mock HTTP Request.
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("organizationName")).thenReturn(organizationName);
    when(request.getParameter("month")).thenReturn(Integer.toString(month));
    when(request.getParameter("day")).thenReturn(Integer.toString(day));
    when(request.getParameter("startHour")).thenReturn(Integer.toString(startHour));
    when(request.getParameter("startMinute")).thenReturn(Integer.toString(startMinute));
    when(request.getParameter("startAMorPM")).thenReturn(startAMorPM);
    when(request.getParameter("endHour")).thenReturn(Integer.toString(endHour));
    when(request.getParameter("endMinute")).thenReturn(Integer.toString(endMinute));
    when(request.getParameter("endAMorPM")).thenReturn(endAMorPM);
    when(request.getParameter("location")).thenReturn(location);
    when(request.getParameter("lat")).thenReturn(Double.toString(lat));
    when(request.getParameter("lng")).thenReturn(Double.toString(lng));
    when(request.getParameter("numberOfPeopleItFeeds")).thenReturn(Integer.toString(numberOfPeopleItFeeds));
    when(request.getParameter("typeOfFood")).thenReturn(typeOfFood);
    when(request.getParameter("description")).thenReturn(description);
    when(request.getParameter("collegeId")).thenReturn(collegeId);

    // Run requestToPost() on the Mock HTTP Request.
    Post testPost = new Post();
    testPost.requestToPost(request);

    // Check if requestToPost() delivered expected results.
    Assert.assertEquals(testPost.getOrganizationName(), organizationName);
    Assert.assertEquals(testPost.getDay(), day);
    Assert.assertEquals(testPost.getStartMinute(), startMinute);
    Assert.assertEquals(testPost.getEndMinute(), endMinute);
    Assert.assertEquals(testPost.getLocation(), location);
    Assert.assertEquals(testPost.getLat(), lat, 0);
    Assert.assertEquals(testPost.getLng(), lng, 0);
    Assert.assertEquals(testPost.getNumberOfPeopleItFeeds(), numberOfPeopleItFeeds);
    Assert.assertEquals(testPost.getTypeOfFood(), typeOfFood);
    Assert.assertEquals(testPost.getDescription(), description);
    Assert.assertEquals(testPost.getCollegeId(), collegeId);

    return testPost;
  }

  // Given inputs for an HTTP Request, convert to a post object without peforming any tests.
  // All inputs are given as strings in order to test parsing functionality.
  // All tests must be performed by the caller.
  public Post requestToPost(String collegeId, String organizationName, String month, String day, String startHour,
  String startMinute, String startAMorPM, String endHour, String endMinute, String endAMorPM, String location,
  String lat, String lng, String numberOfPeopleItFeeds, String typeOfFood, String description) {

    // Construct Mock HTTP Request.
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("organizationName")).thenReturn(organizationName);
    when(request.getParameter("month")).thenReturn(month);
    when(request.getParameter("day")).thenReturn(day);
    when(request.getParameter("startHour")).thenReturn(startHour);
    when(request.getParameter("startMinute")).thenReturn(startMinute);
    when(request.getParameter("startAMorPM")).thenReturn(startAMorPM);
    when(request.getParameter("endHour")).thenReturn(endHour);
    when(request.getParameter("endMinute")).thenReturn(endMinute);
    when(request.getParameter("endAMorPM")).thenReturn(endAMorPM);
    when(request.getParameter("location")).thenReturn(location);
    when(request.getParameter("lat")).thenReturn(lat);
    when(request.getParameter("lng")).thenReturn(lng);
    when(request.getParameter("numberOfPeopleItFeeds")).thenReturn(numberOfPeopleItFeeds);
    when(request.getParameter("typeOfFood")).thenReturn(typeOfFood);
    when(request.getParameter("description")).thenReturn(description);
    when(request.getParameter("collegeId")).thenReturn(collegeId);

    // Run requestToPost() on the Mock HTTP Request.
    Post testPost = new Post();
    testPost.requestToPost(request);

    return testPost;
  }

  // Test functionality of requestToPost(): an event in the morning.
  @Test
  public void testRequestToPostAM() {

    String collegeId = "122931";
    String organizationName = "SWE";
    int month = 8;
    int day = 24;
    int startHour = 4;
    int startMinute = 30;
    String startAMorPM = "am";
    int endHour = 5;
    int endMinute = 00;
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    double lat = 37.3476132;
    double lng = -121.9394005;
    int numberOfPeopleItFeeds = 20;
    String typeOfFood = "Chocolate cake";
    String description = "Birthday Party!!!";

    Post testPost = testRequestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertEquals(testPost.getStartHour(), startHour);
    Assert.assertEquals(testPost.getEndHour(), endHour);
  }

  // Test functionality of requestToPost(): an event starting in the morning ending in the afternoon.
  @Test
  public void testRequestToPostAMandPM() {

    String collegeId = "110653";
    String organizationName = "WICS";
    int month = 9;
    int day = 1;
    int startHour = 11;
    int startMinute = 00;
    String startAMorPM = "am";
    int endHour = 1;
    int endMinute = 00;
    String endAMorPM = "pm";
    String location = "Aldrich Park";
    double lat = 33.6460519;
    double lng = -117.8427446;
    int numberOfPeopleItFeeds = 5;
    String typeOfFood = "Pizza";
    String description = "We ordered too much pizza! Drop by during our workshop to pick it up";

    Post testPost = testRequestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertEquals(testPost.getStartHour(), startHour);
    Assert.assertEquals(testPost.getEndHour(), (endHour + 12));
  }

  // Test functionality of requestToPost(): an event in the evening.
  @Test
  public void testRequestToPostPM() {

    String collegeId = "209542";
    String organizationName = "Duck Club";
    int month = 10;
    int day = 30;
    int startHour = 5;
    int startMinute = 00;
    String startAMorPM = "pm";
    int endHour = 5;
    int endMinute = 15;
    String endAMorPM = "pm";
    String location = "Goss Stadium";
    double lat = 44.562842;
    double lng = -123.2771362;
    int numberOfPeopleItFeeds = 5;
    String typeOfFood = "Popcorn";
    String description = "We have some popcorn left after our weekly Duck watching meeting, come pick some up!";

    Post testPost = testRequestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertEquals(testPost.getStartHour(), (startHour + 12));
    Assert.assertEquals(testPost.getEndHour(), (endHour + 12));
  }
  
  // Test functionality of requestToPost(): testing the edge cases (12AM and 12PM).
  @Test
  public void testRequestToPost12AMPM() {

    String collegeId = "209542";
    String organizationName = "Duck Club";
    int month = 10;
    int day = 30;
    int startHour = 12;
    int startMinute = 10;
    String startAMorPM = "am";
    int endHour = 12;
    int endMinute = 30;
    String endAMorPM = "pm";
    String location = "Goss Stadium";
    double lat = 44.562842;
    double lng = -123.2771362;
    int numberOfPeopleItFeeds = 5;
    String typeOfFood = "Popcorn";
    String description = "We have some popcorn left after our weekly Duck watching meeting, come pick some up!";

    Post testPost = testRequestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertEquals(testPost.getStartHour(), 0);
    Assert.assertEquals(testPost.getEndHour(), 12);
  }

  // Test functionality of requestToPost(): an event in the first month.
  @Test
  public void testRequestToPostFirstMonth() {

    String collegeId = "122931";
    String organizationName = "SWE";
    int month = 1;
    int day = 1;
    int startHour = 4;
    int startMinute = 30;
    String startAMorPM = "am";
    int endHour = 5;
    int endMinute = 00;
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    double lat = 37.3476132;
    double lng = -121.9394005;
    int numberOfPeopleItFeeds = 20;
    String typeOfFood = "Chocolate cake";
    String description = "Birthday Party!!!";

    Post testPost = testRequestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertEquals(testPost.getMonth(), (month - 1));
  }

  // Test basic functionality of requestToPost(): an event in the last month.
  @Test
  public void testRequestToPostLastMonth() {

    String collegeId = "122931";
    String organizationName = "SWE";
    int month = 12;
    int day = 31;
    int startHour = 4;
    int startMinute = 30;
    String startAMorPM = "am";
    int endHour = 5;
    int endMinute = 00;
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    double lat = 37.3476132;
    double lng = -121.9394005;
    int numberOfPeopleItFeeds = 20;
    String typeOfFood = "Chocolate cake";
    String description = "Birthday Party!!!";

    Post testPost = testRequestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertEquals(testPost.getMonth(), (month - 1));
  }

  // Test an event with an invalid description due to the characters used.
  @Test
  public void testHtmlInjectionInDescription() {

    String collegeId = "122931";
    String organizationName = "SWE";
    String month = "12";
    String day = "31";
    String startHour = "4";
    String startMinute = "30";
    String startAMorPM = "am";
    String endHour = "5";
    String endMinute = "00";
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    String lat = "37.3476132";
    String lng = "-121.9394005";
    String numberOfPeopleItFeeds = "20";
    String typeOfFood = "Chocolate cake";
    String description = "</h3><a href='badsite.com'>I am malicious</a>";

    Post testPost = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertFalse(testPost.valid);
  }

  // Test an event with an invalid typeOfFood due to the characters used.
  @Test
  public void testSQLInjectionInFoodType() {

    String collegeId = "122931";
    String organizationName = "SWE";
    String month = "12";
    String day = "31";
    String startHour = "4";
    String startMinute = "30";
    String startAMorPM = "am";
    String endHour = "5";
    String endMinute = "00";
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    String lat = "37.3476132";
    String lng = "-121.9394005";
    String numberOfPeopleItFeeds = "20";
    String typeOfFood = "); DROP TABLE users";
    String description = "Birthday Party!!!";

    Post testPost = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertFalse(testPost.valid);
  }

    // Test an event with an invalid time due to the characters used.
  @Test
  public void testInvalidTime() {

    String collegeId = "122931";
    String organizationName = "SWE";
    String month = "12";
    String day = ";";
    String startHour = "4";
    String startMinute = "30";
    String startAMorPM = "am";
    String endHour = "5";
    String endMinute = "00";
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    String lat = "37.3476132";
    String lng = "-121.9394005";
    String numberOfPeopleItFeeds = "20";
    String typeOfFood = "); DROP TABLE users";
    String description = "Birthday Party!!!";

    Post testPost = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertFalse(testPost.valid);
  }

  // Test an event with an invalid lat/long due to the characters used.
  @Test
  public void testInvalidLatLong() {

    String collegeId = "122931";
    String organizationName = "SWE";
    String month = "12";
    String day = "31";
    String startHour = "4";
    String startMinute = "30";
    String startAMorPM = "am";
    String endHour = "5";
    String endMinute = "00";
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    String lat = "37..3476132";
    String lng = "--121.9394005";
    String numberOfPeopleItFeeds = "20";
    String typeOfFood = "Chocolate cake";
    String description = "Birthday Party!!!";

    Post testPost = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertFalse(testPost.valid);
  }

  // Test an event with invalid month value.
  @Test
  public void testInvalidMonthPost() {

    String collegeId = "122931";
    String organizationName = "SWE";
    String month = "13";
    String day = "31";
    String startHour = "4";
    String startMinute = "30";
    String startAMorPM = "am";
    String endHour = "5";
    String endMinute = "00";
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    String lat = "37.3476132";
    String lng = "-121.9394005";
    String numberOfPeopleItFeeds = "20";
    String typeOfFood = "Chocolate cake";
    String description = "Birthday Party!!!";

    Post testPost = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertFalse(testPost.valid);
  }

  // Test an event with an invalid start/end time order.
  @Test
  public void testInvalidStartHour() {

    String collegeId = "122931";
    String organizationName = "SWE";
    String month = "12";
    String day = "31";
    String startHour = "4";
    String startMinute = "30";
    String startAMorPM = "am";
    String endHour = "3";
    String endMinute = "00";
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    String lat = "37.3476132";
    String lng = "-121.9394005";
    String numberOfPeopleItFeeds = "20";
    String typeOfFood = "Chocolate cake";
    String description = "Birthday Party!!!";

    Post testPost = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertFalse(testPost.valid);
  }

  // Test an event with an invalid number of people fed.
  @Test
  public void testInvalidStartMinute() {

    String collegeId = "122931";
    String organizationName = "SWE";
    String month = "12";
    String day = "31";
    String startHour = "4";
    String startMinute = "30";
    String startAMorPM = "am";
    String endHour = "5";
    String endMinute = "15";
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    String lat = "37.3476132";
    String lng = "-121.9394005";
    String numberOfPeopleItFeeds = "-3";
    String typeOfFood = "Chocolate cake";
    String description = "Birthday Party!!!";

    Post testPost = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertFalse(testPost.valid);
  }

  // Test an event with a really long (and thus invalid) input.
  @Test
  public void testInvalidOrgNameLength() {

    String collegeId = "122931";
    String organizationName = "We are the organization of long-namedness and believe long names are good.";
    String month = "12";
    String day = "31";
    String startHour = "4";
    String startMinute = "30";
    String startAMorPM = "am";
    String endHour = "5";
    String endMinute = "15";
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    String lat = "37.3476132";
    String lng = "-121.9394005";
    String numberOfPeopleItFeeds = "-3";
    String typeOfFood = "Chocolate cake";
    String description = "Birthday Party!!!";

    Post testPost = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertFalse(testPost.valid);
  }

  @Test
  public void testGetShortDuration() {

    String collegeId = "122931";
    String organizationName = "Bread Club";
    String month = "12";
    String day = "31";
    String startHour = "4";
    String startMinute = "30";
    String startAMorPM = "am";
    String endHour = "4";
    String endMinute = "31";
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    String lat = "37.3476132";
    String lng = "-121.9394005";
    String numberOfPeopleItFeeds = "3";
    String typeOfFood = "Bread";
    String description = "Bread Club is officially giving away free bread!";

    Post testPost = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertEquals(1, testPost.getDuration());
  }

  @Test
  public void testGetLongDuration() {

    String collegeId = "122931";
    String organizationName = "Bread Club";
    String month = "12";
    String day = "31";
    String startHour = "4";
    String startMinute = "30";
    String startAMorPM = "am";
    String endHour = "4";
    String endMinute = "30";
    String endAMorPM = "pm";
    String location = "Benson Memorial Center";
    String lat = "37.3476132";
    String lng = "-121.9394005";
    String numberOfPeopleItFeeds = "3";
    String typeOfFood = "Bread";
    String description = "Bread Club is officially giving away free bread!";

    Post testPost = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertEquals(720, testPost.getDuration());
  }

  @Test
  public void testLowRank() {

    String collegeId = "122931";
    String organizationName = "Bread Club";
    String month = "12";
    String day = "30";
    String startHour = "1";
    String startMinute = "00";
    String startAMorPM = "am";
    String endHour = "1";
    String endMinute = "10";
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    String lat = "37.3476132";
    String lng = "-121.9394005";
    String numberOfPeopleItFeeds = "10";
    String typeOfFood = "Bread";
    String description = "Bread Club is officially giving away free bread!";

    Post testPost = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertEquals(0.05, testPost.getRank(), 0.02);
  }

  @Test
  public void testLowAverageRank() {

    String collegeId = "122931";
    String organizationName = "Bread Club";
    String month = "12";
    String day = "30";
    String startHour = "1";
    String startMinute = "00";
    String startAMorPM = "am";
    String endHour = "2";
    String endMinute = "00";
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    String lat = "37.3476132";
    String lng = "-121.9394005";
    String numberOfPeopleItFeeds = "150";
    String typeOfFood = "Bread";
    String description = "Bread Club is officially giving away free bread!";

    Post testPost = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertEquals(0.1, testPost.getRank(), 0.05);
  }

  @Test
  public void testHighAverageRank() {

    String collegeId = "122931";
    String organizationName = "Bread Club";
    String month = "12";
    String day = "30";
    String startHour = "1";
    String startMinute = "00";
    String startAMorPM = "am";
    String endHour = "3";
    String endMinute = "00";
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    String lat = "37.3476132";
    String lng = "-121.9394005";
    String numberOfPeopleItFeeds = "300";
    String typeOfFood = "Bread";
    String description = "Bread Club is officially giving away free bread!";

    Post testPost = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertEquals(0.4, testPost.getRank(), 0.05);
  }

  @Test
  public void testHighRank() {

    String collegeId = "122931";
    String organizationName = "Bread Club";
    String month = "12";
    String day = "31";
    String startHour = "12";
    String startMinute = "00";
    String startAMorPM = "am";
    String endHour = "11";
    String endMinute = "59";
    String endAMorPM = "pm";
    String location = "Benson Memorial Center";
    String lat = "37.3476132";
    String lng = "-121.9394005";
    String numberOfPeopleItFeeds = "10000";
    String typeOfFood = "Bread";
    String description = "Bread Club is officially giving away free bread!";

    Post testPost = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    Assert.assertEquals(1, testPost.getRank(), 0.02);
  }

  @Test
  public void testSorting() {

    String collegeId = "122931";
    String organizationName = "Bread Club";
    String month = "12";
    String day = "31";
    String startHour = "4";
    String startMinute = "30";
    String startAMorPM = "pm";
    String endHour = "9";
    String endMinute = "30";
    String endAMorPM = "pm";
    String location = "Benson Memorial Center";
    String lat = "37.3476132";
    String lng = "-121.9394005";
    String numberOfPeopleItFeeds = "100";
    String typeOfFood = "Bread";
    String description = "Bread Club is officially giving away free bread!";

    Post testPost1 = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);

    String numberOfPeopleItFeeds2 = "50";

    Post testPost2 = requestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds2, typeOfFood, description);

    Assert.assertEquals(1, testPost1.compareTo(testPost2));
  }
}
