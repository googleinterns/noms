package com.google.sps;

import com.google.sps.data.Post;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public final class PostTest {

  public void testRequestToPost(String collegeId, String organizationName, int month, int day, int startHour, int startMinute, String startAMorPM, int endHour, 
  int endMinute, String endAMorPM, String location, Double lat, Double lng, String numberOfPeopleItFeeds, String typeOfFood, String description) {

    // Construct Mock HTTP Request
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
    when(request.getParameter("numberOfPeopleItFeeds")).thenReturn(numberOfPeopleItFeeds);
    when(request.getParameter("typeOfFood")).thenReturn(typeOfFood);
    when(request.getParameter("description")).thenReturn(description);

    Post testPost = new Post();
    testPost.requestToPost(request, collegeId);

    // Check if requestToPost() delivered expected results.
    if (!testPost.getOrganizationName().equals(organizationName)) {
      Assert.fail("organization name failed");
    }
    if (testPost.getMonth() != (month - 1)) {
      Assert.fail("month failed");
    }
    if (testPost.getDay() != day) {
      Assert.fail("day failed");
    }
    if (startAMorPM.equals("pm")) {
      if (testPost.getStartHour() != (startHour + 12)) {
        Assert.fail("start hour failed");
      }
    } else {
      if (testPost.getStartHour() != startHour) {
        Assert.fail("start hour failed");
      } 
    }
    if (testPost.getStartMinute() != startMinute) {
      Assert.fail("start minute failed");
    }
    if (endAMorPM.equals("pm")) {
      if (testPost.getEndHour() != (endHour + 12)) {
        Assert.fail("end hour failed");
      }
    } else {
      if (testPost.getEndHour() != endHour) {
        Assert.fail("end hour failed");
      } 
    }
    if (testPost.getEndMinute() != endMinute) {
      Assert.fail("end minute failed");
    }
    if (!testPost.getLocation().equals(location)) {
      Assert.fail("location failed");
    }
    if (!testPost.getLat().equals(lat)) {
      Assert.fail("lat failed");
    }
    if (!testPost.getLng().equals(lng)) {
      Assert.fail("lng failed");
    }
    if (!testPost.getNumberOfPeopleItFeeds().equals(numberOfPeopleItFeeds)) {
      Assert.fail("number of people it feeds failed");
    }
    if (!testPost.getTypeOfFood().equals(typeOfFood)) {
      Assert.fail("type of food failed");
    }
    if (!testPost.getDescription().equals(description)) {
      Assert.fail("description failed");
    }
  }

  // Test basic functionality of requestToPost(): an event in the morning
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
    Double lat = 37.3476132;
    Double lng = -121.9394005;
    String numberOfPeopleItFeeds = "20";
    String typeOfFood = "Chocolate cake";
    String description = "Birthday Party!!!";

    testRequestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);
  }

  // Test basic functionality of requestToPost(): an event in the morning ending in the afternoon
   @Test
  public void testRequestToPostAMandPM() {

    String collegeId = "110653";
    String organizationName = "WICS";
    int month = 9;
    int day = 01;
    int startHour = 11;
    int startMinute = 00;
    String startAMorPM = "am";
    int endHour = 1;
    int endMinute = 00;
    String endAMorPM = "pm";
    String location = "Aldrich Park";
    Double lat = 33.6460519;
    Double lng = -117.8427446;
    String numberOfPeopleItFeeds = "5";
    String typeOfFood = "Pizza";
    String description = "We ordered too much pizza! Drop by during our workshop to pick it up";

    testRequestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);
  }

  // Test basic functionality of requestToPost(): an event in the evening
  @Test
  public void testRequestToPostPM() {

    String collegeId = "122931";
    String organizationName = "SWE";
    int month = 8;
    int day = 24;
    int startHour = 4;
    int startMinute = 30;
    String startAMorPM = "am";
    int endHour = 5;
    int endMinute = 0;
    String endAMorPM = "am";
    String location = "Benson Memorial Center";
    Double lat = 37.3476132;
    Double lng = -121.9394005;
    String numberOfPeopleItFeeds = "20";
    String typeOfFood = "Chocolate cake";
    String description = "Birthday Party!!!";

    testRequestToPost(collegeId, organizationName, month, day, startHour, startMinute, startAMorPM, endHour, 
    endMinute, endAMorPM, location, lat, lng, numberOfPeopleItFeeds, typeOfFood, description);
  }

  // No tests needed for queryToPost, etc bc google api
  // Invalid options tested in javascript

  // Test basic functionality: postToEntity()

  // ENd month, beginning month
  
}
