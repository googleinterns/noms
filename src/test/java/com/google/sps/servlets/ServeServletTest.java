package com.google.sps;

import com.google.sps.servlets.ServeServlet;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import org.junit.Assert;
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.blobstore.BlobKey;
import static org.mockito.Matchers.any;

@RunWith(JUnit4.class)
public final class ServeServletTest {
  ServeServlet serveServlet = new ServeServlet();
  
  public HttpServletRequest mockRequest(String blobKey) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("blobKey")).thenReturn(blobKey);
    return request;
  }

  public HttpServletResponse mockResponse() {
    HttpServletResponse response = mock(HttpServletResponse.class);
    return response;
  }

  public HttpServletRequest emptyRequest() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    return request;
  }

  public BlobstoreService mockBlobstoreServe() {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    when(blobstoreService.serve(any(BlobKey.class), any(HttpServletResponse.class))).thenReturn();
    return blobstoreService;
  }

  public BlobstoreService mockBlobstoreIOException() {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    when(blobstoreService.serve(any(BlobKey.class), any(HttpServletResponse.class))).thenThrow(IOException.class);
    return blobstoreService;
  }

  public BlobstoreService mockBlobstoreError() {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    when(blobstoreService.serve(any(BlobKey.class), any(HttpServletResponse.class))).thenThrow(IllegalStateException.class);
    return blobstoreService;
  }

  // Test when everything works.
  @Test
  public void testValidServe() {
    HttpServletRequest mockRequest = mockRequest("fakeBlobKey");
    BlobstoreService mockBlobstoreService = mockBlobstoreServe();
    HttpServletResponse mockResponse = mockResponse();

    serveServlet.serveImage(mockRequest, mockResponse, mockBlobstoreService);
  }

  // Test when blobKey parameter is empty.
  @Test
  public void testEmptyRequest() {
    HttpServletRequest mockRequest = emptyRequest();
    BlobstoreService mockBlobstoreService = mockBlobstoreServe();
    HttpServletResponse mockResponse = mockResponse();

    serveServlet.serveImage(mockRequest, mockResponse, mockBlobstoreService);
  }

  // Test serve error: java.io.IOException.
  @Test
  public void testIOException() {
    HttpServletRequest mockRequest = mockRequest("fakeBlobKey");
    BlobstoreService mockBlobstoreService = mockBlobstoreIOException();
    HttpServletResponse mockResponse = mockResponse();

    serveServlet.serveImage(mockRequest, mockResponse, mockBlobstoreService);
  }

  // Test serve error: java.lang.IllegalStateException.
  @Test
  public void testIllegalState() {
    HttpServletRequest mockRequest = mockRequest("fakeBlobKey");
    BlobstoreService mockBlobstoreService = mockBlobstoreError();
    HttpServletResponse mockResponse = mockResponse();

    serveServlet.serveImage(mockRequest, mockResponse, mockBlobstoreService);
  }
}
