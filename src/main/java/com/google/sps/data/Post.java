/* Post Object:
Holds the information in the cards
  - Post ID
  - Organization Name
  - The date (month, day, year)
  - The start time (hour, minute)
  - The end time (hour, minute)
  - location (lat, lng)
  - Number of people it feeds
  - Type of food
  - Description
  - College Id
*/

package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import org.apache.commons.io.FileUtils;
import java.util.Base64;
import java.io.IOException;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class Post {

    private String postId = "";
    private String organizationName = "";
    private int month = 0;
    private int day = 0;
    private int year = 0;
    private int startHour = 0;
    private int startMinute = 0;
    private int endHour = 0;
    private int endMinute = 0;
    private String location = "";
    private double lat = 0.0;
    private double lng = 0.0;
    private String numberOfPeopleItFeeds = "";
    private String typeOfFood = "";
    private String description = "";
    private String collegeId = "";
    private int timeSort = 0;
    private String imageServingUrl = "";

    /* Fill in the important Post details from the POST request. */
    public void requestToPost(HttpServletRequest request, String collegeId) throws IOException{
        organizationName = request.getParameter("organizationName");
        month = Integer.parseInt(request.getParameter("month")) - 1; // Months are indexed at 0.
        day = Integer.parseInt(request.getParameter("day"));
        startHour = Integer.parseInt(request.getParameter("startHour"));
        startMinute = Integer.parseInt(request.getParameter("startMinute"));
        endHour = Integer.parseInt(request.getParameter("endHour"));
        endMinute = Integer.parseInt(request.getParameter("endMinute"));
        location = request.getParameter("location");
        lat = Double.parseDouble(request.getParameter("lat"));
        lng = Double.parseDouble(request.getParameter("lng"));
        numberOfPeopleItFeeds = request.getParameter("numberOfPeopleItFeeds");
        typeOfFood = request.getParameter("typeOfFood");
        description = request.getParameter("description");
        this.collegeId = collegeId;

        // Adjust the start and end hour based on whether the hour is AM or PM.
        startHour = startHour % 12;
        String startAMorPM = request.getParameter("startAMorPM");
        if (startAMorPM.equals("pm")) {
            startHour += 12;
        }
        endHour = endHour % 12;
        String endAMorPM = request.getParameter("endAMorPM");
        if (endAMorPM.equals("pm")) {
            endHour += 12;
        }

        // Translate the start time into minutes to allow for sorting.
        timeSort = startHour * 60 + startMinute;

        // Set the year. Right now time zone is set to "America/Los_Angeles".
        Calendar nowTime = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        year = nowTime.get(Calendar.YEAR);

        // Store serving url.
        imageServingUrl = getUploadedFileUrl(request, "foodImage");
        if (imageServingUrl == null) {
          // Get a backup image, encoded in Base 64.
	  String pathToStockPhoto = System.getProperty("user.home") + "/noms/src/main/java/com/google/sps/data/forkandknife.png";
          byte[] fileContent = FileUtils.readFileToByteArray(new File(pathToStockPhoto));
          String encodedString = Base64.getEncoder().encodeToString(fileContent);
          imageServingUrl = "data:image/png;base64," + encodedString; 
        }
    }

    /* Translate the entities from the Datastore query to Post objects and return in an array. */
    public static ArrayList<Post> queryToPosts(PreparedQuery queryResult, DatastoreService datastore) {
        ArrayList<Post> currentPosts = new ArrayList<Post>();

        // Create a calendar based off the current time zone.
        // TODO: Update Time Zone based off student and university location, instead of "America/Los_Angeles".

        Calendar nowTime = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));

        for (Entity entity: queryResult.asIterable()) {

            // Create a calendar based off the post timing.
            Calendar postTime = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
            int postMonth = Integer.parseInt(entity.getProperty("month").toString());
            int postDay = Integer.parseInt(entity.getProperty("day").toString());
            int postYear = Integer.parseInt(entity.getProperty("year").toString());
            int postHour = Integer.parseInt(entity.getProperty("endHour").toString());
            int postMinute = Integer.parseInt(entity.getProperty("endMinute").toString());
            postTime.set(postYear, postMonth, postDay, postHour, postMinute);
            
            // If the post time is before the current time, delete from Datastore.
            if (postTime.before(nowTime)) {
                datastore.delete(entity.getKey());
            }
            // Only add the post to result if it is on the same day.
            else if (postYear == nowTime.get(Calendar.YEAR) && postMonth == nowTime.get(Calendar.MONTH) && postDay == nowTime.get(Calendar.DATE)) {
                Post newPost = new Post();
                newPost.entityToPost(entity);
                currentPosts.add(newPost);
            } 
        }
        return currentPosts;
    }

    /* Translate the fields of an entity from Datastore to a Post object. */
    public void entityToPost(Entity entity) {
        organizationName = (String) entity.getProperty("organizationName");
        month = Integer.parseInt(entity.getProperty("month").toString());
        day = Integer.parseInt(entity.getProperty("day").toString());
        year = Integer.parseInt(entity.getProperty("year").toString());
        startHour = Integer.parseInt(entity.getProperty("startHour").toString());
        startMinute = Integer.parseInt(entity.getProperty("startMinute").toString());
        endHour = Integer.parseInt(entity.getProperty("endHour").toString());
        endMinute = Integer.parseInt(entity.getProperty("endMinute").toString());
        location = (String) entity.getProperty("location");
        lat = Double.parseDouble(entity.getProperty("lat").toString());
        lng = Double.parseDouble(entity.getProperty("lng").toString());
        numberOfPeopleItFeeds = (String) entity.getProperty("numberOfPeopleItFeeds");
        typeOfFood = (String) entity.getProperty("typeOfFood");
        description = (String) entity.getProperty("description");
        timeSort = Integer.parseInt(entity.getProperty("timeSort").toString());
        collegeId = (String) entity.getKind();
        postId = entity.getKey().toString();
        imageServingUrl = (String) entity.getProperty("imageServingUrl");
    }

    /* Creates a new entity with the college ID and the Post information. Sets all the properties. */
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
        newPost.setProperty("imageServingUrl", imageServingUrl);

        return newPost;
    }

    /* Class Getters. */
    public String getOrganizationName() {
      return organizationName;
    }

    public int getMonth() {
      return month;
    }

    public int getDay() {
      return day;
    }

    public int getStartHour() {
      return startHour;
    }

    public int getStartMinute() {
      return startMinute;
    }

    public int getEndHour() {
      return endHour;
    }

    public int getEndMinute() {
      return endMinute;
    }

    public String getLocation() {
      return location;
    }

    public double getLat() {
      return lat;
    }

    public double getLng() {
      return lng;
    }

    public String getNumberOfPeopleItFeeds() {
      return numberOfPeopleItFeeds;
    }

    public String getTypeOfFood() {
      return typeOfFood;
    }

    public String getDescription() {
      return description;
    }

    public String getCollegeId() {
      return collegeId;
    }

      /**
   * Returns a URL that points to the uploaded file, or null if the user didn't upload a file.
   */
  private String getUploadedFileUrl(HttpServletRequest request, String formInputElementName){
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // User submitted form without selecting a file, so we can't get a URL. (devserver)
    if(blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    if (blobKey == null) {
      return null;
    }

    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // We could check the validity of the file here, e.g. to make sure it's an image file
    // https://stackoverflow.com/q/10779564/873165

    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
    if (options == null) {
      return null;
    }
    return imagesService.getServingUrl(options);
  }
}
