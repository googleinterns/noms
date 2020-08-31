package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import java.io.IOException;
import java.net.URL;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

@WebServlet("/serve")
public class ServeServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    BlobKey blobKey = new BlobKey(request.getParameter("blob-key"));
    blobstoreService.serve(blobKey, response);

    // BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    // BlobKey blobKey = new BlobKey(request.getParameter("blob-key"));

    // if (blobKey == null) {
    //   return;
    // }

    // // If the user did not select a file, we cannot generate a URL.
    // BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    // if (blobInfo.getSize() == 0) {
    //   blobstoreService.delete(blobKey);
    //   return;
    // }

    // // Use ImagesService to get a URL that points to the uploaded file.
    // ImagesService imagesService = ImagesServiceFactory.getImagesService();
    // ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
    // String url = imagesService.getServingUrl(options);

    // response.setContentType("text/html");
    // response.getWriter().println(url);
  }
}
