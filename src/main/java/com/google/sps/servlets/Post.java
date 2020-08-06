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
    int timeSort = 0;

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

        // Calculate the start time in minutes
        timeSort = (Integer.parseInt(startHour) * 60) + Integer.parseInt(startMinute);
        if (startAMorPM.equals("pm")) {
            timeSort += 12 * 60;
        }
    }

    public Post() { }

    public static ArrayList<Post> queryToPosts(PreparedQuery queryResult, DatastoreService datastore) {
        // TODO: Update Time Zone based off University
        ArrayList<Post> currentPosts = new ArrayList<Post>();
        Calendar nowTime = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));

        for (Entity entity: queryResult.asIterable()) {
            /*
            Post newPost = new Post();
            newPost.entityToPost(entity);
            currentPosts.add(newPost);
            */

            // Create a calendar based off the post timing.
            Calendar postTime = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));

            int postTimeSort =  ((Long) entity.getProperty("timeSort")).intValue();
            int postMonth = Integer.parseInt(entity.getProperty("month").toString());
            int postDay = Integer.parseInt(entity.getProperty("day").toString());
            int postYear = 2000 + Integer.parseInt(entity.getProperty("year").toString());
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

    public void entityToPost(Entity entity) {
        organizationName = (String) entity.getProperty("organizationName");
        month = (String) entity.getProperty("month");
        day = (String) entity.getProperty("day");
        year = (String) entity.getProperty("year");
        startHour = (String) entity.getProperty("startHour");
        startMinute = (String) entity.getProperty("startMinute");
        startAMorPM = (String) entity.getProperty("startAMorPM");
        endHour = (String) entity.getProperty("endHour");
        endMinute = (String) entity.getProperty("endMinute");
        endAMoPM = (String) entity.getProperty("endAMorPM");
        location = (String) entity.getProperty("location");
        numberOfPeopleItFeeds = (String) entity.getProperty("numberOfPeopleItFeeds");
        typeOfFood = (String) entity.getProperty("typeOfFood");
        description = (String) entity.getProperty("description");
        timeSort = ((Long) entity.getProperty("timeSort")).intValue();
        collegeId = (String) entity.getKind();
        postId = entity.getKey().toString();
    }

    /* Creates a new entity with the college id. Sets all the properties. */
    public Entity postToEntity() {
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

        newPost.setProperty("timeSort", timeSort);

        return newPost;
    }
}
