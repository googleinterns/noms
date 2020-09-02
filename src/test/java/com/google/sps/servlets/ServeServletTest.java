package com.google.sps;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.sps.servlets.ServeServlet;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.Test;
import org.mockito.Mock;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(value = BlockJUnit4ClassRunner.class)
public final class ServeServletTest {
  ServeServlet serveServlet = new ServeServlet();
  @Mock HttpServletResponse mockResponse;
  static final String FAKE_BLOB_KEY = "fakeBlobKey";
  
  // Mock an HTTP Servlet Request with the given string as a parameter.
  public HttpServletRequest mockRequest(String blobKey) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("blobKey")).thenReturn(blobKey);
    return request;
  }

  // Mock an empty HTTP Servlet Request.
  public HttpServletRequest emptyRequest() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    return request;
  }

  // Mock a normal Blobstore serve(), which has return type void.
  public BlobstoreService mockBlobstoreServe() throws IOException {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    doNothing().when(blobstoreService).serve(any(BlobKey.class), any(HttpServletResponse.class));
    return blobstoreService;
  }

  // Mock a Blobstore serve() which throws an IOException.
  public BlobstoreService mockBlobstoreIOException() throws IOException {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    doThrow(IOException.class).when(blobstoreService).serve(any(BlobKey.class), eq(mockResponse));
    return blobstoreService;
  }

  // Mock a Blobstore serve() which throws an IllegalStateException.
  public BlobstoreService mockBlobstoreError() throws IOException {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    doThrow(IllegalStateException.class).when(blobstoreService).serve(any(BlobKey.class), eq(mockResponse));
    return blobstoreService;
  }

  @Test
  public void testValidServe() throws IOException {
    HttpServletRequest mockRequest = mockRequest(FAKE_BLOB_KEY);
    BlobstoreService mockBlobstoreService = mockBlobstoreServe();

    serveServlet.serveImage(mockRequest, mockResponse, mockBlobstoreService);
    verify(mockBlobstoreService).serve(any(BlobKey.class), eq(mockResponse));
  }

  @Test
  public void testEmptyRequest() throws IOException {
    HttpServletRequest mockRequest = emptyRequest();
    BlobstoreService mockBlobstoreService = mockBlobstoreServe();

    serveServlet.serveImage(mockRequest, mockResponse, mockBlobstoreService);
    verify(mockBlobstoreService, never()).serve(any(BlobKey.class), eq(mockResponse));
  }

  @Test (expected = IOException.class)
  public void testIOException() throws IOException {
    HttpServletRequest mockRequest = mockRequest(FAKE_BLOB_KEY);
    BlobstoreService mockBlobstoreService = mockBlobstoreIOException();

    serveServlet.serveImage(mockRequest, mockResponse, mockBlobstoreService);
  }

  @Test (expected = IllegalStateException.class)
  public void testIllegalState() throws IOException {
    HttpServletRequest mockRequest = mockRequest(FAKE_BLOB_KEY);
    BlobstoreService mockBlobstoreService = mockBlobstoreError();

    serveServlet.serveImage(mockRequest, mockResponse, mockBlobstoreService);
  }
}
