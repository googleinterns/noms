package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/createBlobstoreUrl")
public class CreateBlobstoreUrlServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String uploadUrl = blobstoreService.createUploadUrl("/postData") ;

    String currentDir = System.getProperty("user.dir");
    String homeDir = System.getProperty("user.home");

    response.setContentType("text/html");
    response.getWriter().println(uploadUrl);
    // response.getWriter().println(currentDir + " " + homeDir);

  }
}
