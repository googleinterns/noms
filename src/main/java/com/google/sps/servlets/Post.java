/* Post Object:
Holds the information in the cards
- Post ID
- Organization Name
- The date (month, day, year)
- The start time (hour, minute, am/pm)
- The end time (hour, minute, am/pm)
- location
- Number of people it feeds
- Type of food
- Description
- College Id
*/

package com.google.sps.servlets;
import javax.servlet.http.HttpServletRequest;
import com.google.appengine.api.datastore.Entity;

public class Post {
    String postId = "";
    String organizationName = "";
    String month = "";
    String day = "";
    String year = "";
    String startHour = "";
    String startMinute = "";
    String startAMorPM = "";
    String endHour = "";
    String endMinute = "";
    String endAMoPM = "";
    String location = "";
    String numberOfPeopleItFeeds = "";
    String typeOfFood = "";
    String description = "";
    String collegeId = "";

    public Post(HttpServletRequest request, String collegeId) {
        organizationName = request.getParameter("organizationName");
        month = request.getParameter("month");
        day = request.getParameter("day");
        year = request.getParameter("year");
        startHour = request.getParameter("startHour");
        startMinute = request.getParameter("startMinute");
        startAMorPM = request.getParameter("startAMorPM");
        endHour = request.getParameter("endHour");
        endMinute = request.getParameter("endMinute");
        endAMoPM = request.getParameter("endAMorPM");
        location = request.getParameter("location");
        numberOfPeopleItFeeds = request.getParameter("numberOfPeopleItFeeds");
        typeOfFood = request.getParameter("typeOfFood");
        description = request.getParameter("description");
        this.collegeId = collegeId;
    }

    /* Creates a new entity with the college id. Sets all the properties. */
    public Entity createEntity() {
        Entity newPost = new Entity(collegeId);

        newPost.setProperty("organizationName", organizationName);

        newPost.setProperty("month", month);
        newPost.setProperty("day", day);
        newPost.setProperty("year", year);

        newPost.setProperty("startHour", startHour);
        newPost.setProperty("startMinute", startMinute);
        newPost.setProperty("startAMorPM", startAMorPM); 

        newPost.setProperty("endHour", endHour);
        newPost.setProperty("endMinute", endMinute);
        newPost.setProperty("endAMorPM", endAMoPM); 

        newPost.setProperty("location", location);
        newPost.setProperty("typeOfFood", typeOfFood);
        newPost.setProperty("numberOfPeopleItFeeds", numberOfPeopleItFeeds);
        newPost.setProperty("description", description);

        return newPost;
    }
}