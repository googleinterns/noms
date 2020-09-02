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
  static final String SUCCESS_SERVLET = "/postData";
  
  public BlobstoreService mockBlobstore(String uploadUrl) {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    when(blobstoreService.createUploadUrl(SUCCESS_SERVLET)).thenReturn(uploadUrl);
    return blobstoreService;
  }

  public BlobstoreService mockBlobstoreArgumentError() {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    when(blobstoreService.createUploadUrl(SUCCESS_SERVLET)).thenThrow(IllegalArgumentException.class);
    return blobstoreService;
  }

  public BlobstoreService mockBlobstoreError() {
    BlobstoreService blobstoreService = mock(BlobstoreService.class);
    when(blobstoreService.createUploadUrl(SUCCESS_SERVLET)).thenThrow(BlobstoreFailureException.class);
    return blobstoreService;
  }

  @Test
  public void testValidUploadUrl() {
    String fakeUploadUrl = "www.fakeuploadurl.com";
    BlobstoreService mockBlobstoreService = mockBlobstore(fakeUploadUrl);
    String resultUrl = createBlobstoreUrlServlet.getUploadUrl(mockBlobstoreService);

    Assert.assertEquals(resultUrl, fakeUploadUrl);
  }

  @Test
  public void testPathError() {
    BlobstoreService mockBlobstoreService = mockBlobstoreArgumentError();
    String resultUrl = createBlobstoreUrlServlet.getUploadUrl(mockBlobstoreService);

    Assert.assertEquals(resultUrl, "");
  }

  @Test
  public void testBlobstoreError() {
    BlobstoreService mockBlobstoreService = mockBlobstoreError();
    String resultUrl = createBlobstoreUrlServlet.getUploadUrl(mockBlobstoreService);

    Assert.assertEquals(resultUrl, "");
  }
}
