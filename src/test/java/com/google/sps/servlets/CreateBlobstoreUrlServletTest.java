package com.google.sps;

import com.google.sps.servlets.CreateBlobstoreUrlServlet;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import org.junit.Assert;
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public final class CreateBlobstoreUrlServletTest {
  CreateBlobstoreUrlServlet createBlobstoreUrlServlet = new CreateBlobstoreUrlServlet();
  
  public BlobstoreService mockBlobstore(String uploadUrl) {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    when(blobstoreService.createUploadUrl("/postData")).thenReturn(uploadUrl);
    return blobstoreService;
  }

  public BlobstoreService mockBlobstoreArgumentError() {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    when(blobstoreService.createUploadUrl("/postData")).thenThrow(IllegalArgumentException.class);
    return blobstoreService;
  }

  public BlobstoreService mockBlobstoreError() {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    when(blobstoreService.createUploadUrl("/postData")).thenThrow(BlobstoreFailureException.class);
    return blobstoreService;
  }

  // Test that the getUploadUrl() returns the expected URL.
  @Test
  public void testValidUploadUrl() {
    String fakeUploadUrl = "www.fakeuploadurl.com";
    BlobstoreService mockBlobstoreService = mockBlobstore(fakeUploadUrl);
    String resultUrl = createBlobstoreUrlServlet.getUploadUrl(mockBlobstoreService);

    Assert.assertEquals(resultUrl, fakeUploadUrl);
  }

  // Test that the getUploadUrl() returns an empty URL when there is a path error.
  @Test
  public void testPathError() {
    BlobstoreService mockBlobstoreService = mockBlobstoreArgumentError();
    String resultUrl = createBlobstoreUrlServlet.getUploadUrl(mockBlobstoreService);

    Assert.assertEquals(resultUrl, "");
  }

  // Test that the getUploadUrl() returns an empty URL when there is an internal Blobstore error.
  @Test
  public void testBlobstoreError() {
    BlobstoreService mockBlobstoreService = mockBlobstoreError();
    String resultUrl = createBlobstoreUrlServlet.getUploadUrl(mockBlobstoreService);

    Assert.assertEquals(resultUrl, "");
  }
}
