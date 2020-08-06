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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.PreparedQuery;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.Calendar;

public class Post {
    String postId = "";
    String organizationName = "";
    int month = 0;
    int day = 0;
    int year = 0;
    int startHour = 0;
    int startMinute = 0;
    int endHour = 0;
    int endMinute = 0;
    String location = "";
    Long lat = 0L;
    Long lng = 0L;
    String numberOfPeopleItFeeds = "";
    String typeOfFood = "";
    String description = "";
    String collegeId = "";
    int timeSort = 0;

    /* Fill in the important Post details from the POST request. */
    public Post(HttpServletRequest request, String collegeId) {
        organizationName = request.getParameter("organizationName");
        month = Integer.parseInt(request.getParameter("month"));
        day = Integer.parseInt(request.getParameter("day"));
        year = Integer.parseInt(request.getParameter("year"));
        startHour = Integer.parseInt(request.getParameter("startHour"));
        startMinute = Integer.parseInt(request.getParameter("startMinute"));
        endHour = Integer.parseInt(request.getParameter("endHour"));
        endMinute = Integer.parseInt(request.getParameter("endMinute"));
        location = request.getParameter("location");
        lat = Long.parseLong(request.getParameter("lat"));
        lng = Long.parseLong(request.getParameter("lng"));
        numberOfPeopleItFeeds = request.getParameter("numberOfPeopleItFeeds");
        typeOfFood = request.getParameter("typeOfFood");
        description = request.getParameter("description");
        this.collegeId = collegeId;

        String startAMorPM = request.getParameter("startAMorPM");
        if (startAMorPM.equals("pm")) {
            startHour += 12;
        }

        String endAMorPM = request.getParameter("endAMorPM");
        if (endAMorPM.equals("pm")) {
            endHour += 12;
        }

        // Translate the start time into minutes.
        timeSort = startHour * 60 + startMinute;
    }

    public Post() { }

    /* Translate the entities from the Datastore query to Post objects and return in an array. */
    public static ArrayList<Post> queryToPosts(PreparedQuery queryResult, DatastoreService datastore) {
        // TODO: Update Time Zone based off University
        ArrayList<Post> currentPosts = new ArrayList<Post>();
        Calendar nowTime = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));

        for (Entity entity: queryResult.asIterable()) {
            // Create a calendar based off the post timing.
            Calendar postTime = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));

            int postTimeSort = ((Long) entity.getProperty("timeSort")).intValue();
            int postMonth = ((Long) entity.getProperty("month")).intValue();
            int postDay = ((Long) entity.getProperty("day")).intValue();
            int postYear = 2000 + ((Long) entity.getProperty("year")).intValue();
            int postHour = postTimeSort / 60;
            int postMinute = postTimeSort % 60;

            postTime.set(postYear, postMonth, postDay, postHour, postMinute);
            
            // If the post time is before the current time, delete from Datastore.
            if (postTime.before(nowTime)) {
                datastore.delete(entity.getKey());
            }
            else {
                Post newPost = new Post();
                newPost.entityToPost(entity);
                currentPosts.add(newPost);
            }
            /*
            // If not, and the post is on the same day, create a Post object and add it to the ArrayList.
            else if (postYear == nowTime.get(Calendar.YEAR) && postMonth == nowTime.get(Calendar.MONTH) && postDay == nowTime.get(Calendar.DATE)) {
                Post newPost = new Post();
                newPost.entityToPost(entity);
                currentPosts.add(newPost);
            }  
            */
        }
        return currentPosts;
    }

    /* Translate an entity from Datastore to a Post object. */
    public void entityToPost(Entity entity) {
        organizationName = (String) entity.getProperty("organizationName");
        month = ((Long) entity.getProperty("month")).intValue();
        day = ((Long) entity.getProperty("day")).intValue();
        year = ((Long) entity.getProperty("year")).intValue();
        startHour = ((Long) entity.getProperty("startHour")).intValue();
        startMinute = ((Long) entity.getProperty("startMinute")).intValue();
        endHour = ((Long) entity.getProperty("endHour")).intValue();
        endMinute = ((Long) entity.getProperty("endMinute")).intValue();
        location = (String) entity.getProperty("location");
        lat = (Long) entity.getProperty("lat");
        lng = (Long) entity.getProperty("lng");
        numberOfPeopleItFeeds = (String) entity.getProperty("numberOfPeopleItFeeds");
        typeOfFood = (String) entity.getProperty("typeOfFood");
        description = (String) entity.getProperty("description");
        timeSort = ((Long) entity.getProperty("timeSort")).intValue();
        collegeId = (String) entity.getKind();
        postId = entity.getKey().toString();
    }

    /* Creates a new entity with the college id and the information from the POST request. Sets all the properties. */
    public Entity postToEntity() {
        Entity newPost = new Entity(collegeId);

        newPost.setProperty("organizationName", organizationName);

        newPost.setProperty("month", month);
        newPost.setProperty("day", day);
        newPost.setProperty("year", year);

        newPost.setProperty("startHour", startHour);
        newPost.setProperty("startMinute", startMinute);
        newPost.setProperty("endHour", endHour);
        newPost.setProperty("endMinute", endMinute);

        newPost.setProperty("location", location);
        newPost.setProperty("lat", lat);
        newPost.setProperty("lng", lng);

        newPost.setProperty("typeOfFood", typeOfFood);
        newPost.setProperty("numberOfPeopleItFeeds", numberOfPeopleItFeeds);
        newPost.setProperty("description", description);

        newPost.setProperty("timeSort", timeSort);

        return newPost;
    }
}
