package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/createBlobstoreUrl")
public class CreateBlobstoreUrlServlet extends HttpServlet {
  private static final Logger log = Logger.getLogger(CreateBlobstoreUrlServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Creates an upload URL for sending the form submission.
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String uploadUrl = getUploadUrl(blobstoreService);

    response.setContentType("text/html");
    response.getWriter().println(uploadUrl);
  }

  public String getUploadUrl(BlobstoreService blobstoreService) {
    String uploadUrl = "";
    try {
      uploadUrl = blobstoreService.createUploadUrl("/postData");
    } catch (Exception e) {
      log.severe(e.toString());
    }
    return uploadUrl;
  }
}
