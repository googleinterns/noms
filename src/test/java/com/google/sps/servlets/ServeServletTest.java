package com.google.sps;

import com.google.sps.servlets.ServeServlet;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import org.junit.Assert;
import org.junit.runners.JUnit4;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.blobstore.BlobKey;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;


// JUnit4.class
@RunWith(value = BlockJUnit4ClassRunner.class)
public final class ServeServletTest {
  ServeServlet serveServlet = new ServeServlet();
  @Mock HttpServletResponse mockResponse;
  
  public HttpServletRequest mockRequest(String blobKey) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("blobKey")).thenReturn(blobKey);
    return request;
  }

  public HttpServletRequest emptyRequest() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    return request;
  }

  public BlobstoreService mockBlobstoreServe() throws IOException {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    doNothing().when(blobstoreService).serve(any(BlobKey.class), any(HttpServletResponse.class));
    return blobstoreService;
  }

  public BlobstoreService mockBlobstoreIOException() throws IOException {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    doThrow(IOException.class).when(blobstoreService).serve(any(BlobKey.class), eq(mockResponse));
    return blobstoreService;
  }

  public BlobstoreService mockBlobstoreError() throws IOException {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    doThrow(IllegalStateException.class).when(blobstoreService).serve(any(BlobKey.class), eq(mockResponse));
    return blobstoreService;
  }

  // Test when everything works.
  @Test
  public void testValidServe() throws IOException {
    HttpServletRequest mockRequest = mockRequest("fakeBlobKey");
    BlobstoreService mockBlobstoreService = mockBlobstoreServe();

    serveServlet.serveImage(mockRequest, mockResponse, mockBlobstoreService);
    verify(mockBlobstoreService).serve(any(BlobKey.class), eq(mockResponse));
  }

  // Test when blobKey parameter is empty.
  @Test
  public void testEmptyRequest() throws IOException {
    HttpServletRequest mockRequest = emptyRequest();
    BlobstoreService mockBlobstoreService = mockBlobstoreServe();

    serveServlet.serveImage(mockRequest, mockResponse, mockBlobstoreService);
    verify(mockBlobstoreService, never()).serve(any(BlobKey.class), eq(mockResponse));
  }

  // Test serve() error: java.io.IOException.
  @Test (expected = IOException.class)
  public void testIOException() throws IOException {
    HttpServletRequest mockRequest = mockRequest("fakeBlobKey");
    BlobstoreService mockBlobstoreService = mockBlobstoreIOException();

    serveServlet.serveImage(mockRequest, mockResponse, mockBlobstoreService);
  }

  // Test serve() error: java.lang.IllegalStateException.
  @Test (expected = IllegalStateException.class)
  public void testIllegalState() throws IOException {
    HttpServletRequest mockRequest = mockRequest("fakeBlobKey");
    BlobstoreService mockBlobstoreService = mockBlobstoreError();

    serveServlet.serveImage(mockRequest, mockResponse, mockBlobstoreService);
  }
}
