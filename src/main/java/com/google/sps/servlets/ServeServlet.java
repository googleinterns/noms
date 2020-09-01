package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import java.net.URL;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/serve")
public class ServeServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    serveImage(request, response, blobstoreService);
  }

  // Adds helper function to provide visibility for testing.
  public void serveImage(HttpServletRequest request, HttpServletResponse response, BlobstoreService blobstoreService) throws IOException {
    if (request.getParameter("blobKey") != null) {
      BlobKey blobKey = new BlobKey(request.getParameter("blobKey"));
      blobstoreService.serve(blobKey, response);
    }
  }
}
