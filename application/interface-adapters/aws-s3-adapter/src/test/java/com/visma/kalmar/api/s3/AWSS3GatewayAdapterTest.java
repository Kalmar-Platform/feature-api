package com.visma.kalmar.api.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.visma.kalmar.api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AWSS3GatewayAdapterTest {

  private static final String BUCKET_NAME = "kalmar-openweb-registration-eu-north-1";
  private static final String FILE_NAME = "test-file.json";
  private static final String PAYLOAD = "{\"key\":\"value\"}";

  @Mock private AmazonS3 amazonS3;

  private AWSS3GatewayAdapter awss3GatewayAdapter;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    awss3GatewayAdapter = new AWSS3GatewayAdapter(amazonS3);
  }

  @Test
  void uploadFileToS3_Success_UploadsFileCorrectly() {
    awss3GatewayAdapter.uploadFileToS3(FILE_NAME, PAYLOAD);

    ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
    verify(amazonS3, times(1)).putObject(requestCaptor.capture());

    PutObjectRequest capturedRequest = requestCaptor.getValue();
    assertEquals(BUCKET_NAME, capturedRequest.getBucketName());
    assertEquals(FILE_NAME, capturedRequest.getKey());
    assertNotNull(capturedRequest.getInputStream());
    assertNotNull(capturedRequest.getMetadata());
    assertEquals("application/json", capturedRequest.getMetadata().getContentType());
  }

  @Test
  void uploadFileToS3_AmazonServiceException_ThrowsRuntimeException() {
    AmazonServiceException exception = new AmazonServiceException("S3 error");
    when(amazonS3.putObject(any(PutObjectRequest.class))).thenThrow(exception);

    RuntimeException thrown =
        assertThrows(
            RuntimeException.class, () -> awss3GatewayAdapter.uploadFileToS3(FILE_NAME, PAYLOAD));

    assertEquals("Failed to upload file to S3", thrown.getMessage());
    verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
  }

  @Test
  void uploadFileToS3_GenericException_ThrowsRuntimeException() {
    when(amazonS3.putObject(any(PutObjectRequest.class)))
        .thenThrow(new RuntimeException("Unexpected error"));

    RuntimeException thrown =
        assertThrows(
            RuntimeException.class, () -> awss3GatewayAdapter.uploadFileToS3(FILE_NAME, PAYLOAD));

    assertEquals("Failed to upload file to S3", thrown.getMessage());
    verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
  }

  @Test
  void getFileFromS3_Success_ReturnsFileContent() throws IOException {
    String expectedContent = "{\"data\":\"test\"}";
    ByteArrayInputStream contentStream = new ByteArrayInputStream(expectedContent.getBytes());
    S3ObjectInputStream s3InputStream = new S3ObjectInputStream(contentStream, null);

    S3Object s3Object = mock(S3Object.class);
    when(s3Object.getObjectContent()).thenReturn(s3InputStream);

    when(amazonS3.doesObjectExist(BUCKET_NAME, FILE_NAME)).thenReturn(true);
    when(amazonS3.getObject(BUCKET_NAME, FILE_NAME)).thenReturn(s3Object);

    String result = awss3GatewayAdapter.getFileFromS3(FILE_NAME);

    assertEquals(expectedContent, result);
    verify(amazonS3, times(1)).doesObjectExist(BUCKET_NAME, FILE_NAME);
    verify(amazonS3, times(1)).getObject(BUCKET_NAME, FILE_NAME);
  }

  @Test
  void getFileFromS3_FileNotFound_ThrowsResourceNotFoundException() {
    when(amazonS3.doesObjectExist(BUCKET_NAME, FILE_NAME)).thenReturn(false);

    ResourceNotFoundException thrown =
        assertThrows(
            ResourceNotFoundException.class, () -> awss3GatewayAdapter.getFileFromS3(FILE_NAME));

    assertTrue(thrown.getMessage().contains("Registration file not found"));
    verify(amazonS3, times(1)).doesObjectExist(BUCKET_NAME, FILE_NAME);
    verify(amazonS3, never()).getObject(anyString(), anyString());
  }

  @Test
  void getFileFromS3_IOException_ThrowsRuntimeException() throws IOException {
    S3ObjectInputStream s3InputStream = mock(S3ObjectInputStream.class);
    when(s3InputStream.read(any(byte[].class))).thenThrow(new IOException("Read error"));

    S3Object s3Object = mock(S3Object.class);
    when(s3Object.getObjectContent()).thenReturn(s3InputStream);

    when(amazonS3.doesObjectExist(BUCKET_NAME, FILE_NAME)).thenReturn(true);
    when(amazonS3.getObject(BUCKET_NAME, FILE_NAME)).thenReturn(s3Object);

    RuntimeException thrown =
        assertThrows(RuntimeException.class, () -> awss3GatewayAdapter.getFileFromS3(FILE_NAME));

    assertTrue(thrown.getMessage().contains("Failed to read file from S3"));
    verify(amazonS3, times(1)).doesObjectExist(BUCKET_NAME, FILE_NAME);
    verify(amazonS3, times(1)).getObject(BUCKET_NAME, FILE_NAME);
  }

  @Test
  void getFileFromS3_AmazonServiceException_ThrowsRuntimeException() {
    AmazonServiceException exception = new AmazonServiceException("S3 error");
    when(amazonS3.doesObjectExist(BUCKET_NAME, FILE_NAME)).thenReturn(true);
    when(amazonS3.getObject(BUCKET_NAME, FILE_NAME)).thenThrow(exception);

    assertThrows(AmazonServiceException.class, () -> awss3GatewayAdapter.getFileFromS3(FILE_NAME));

    verify(amazonS3, times(1)).doesObjectExist(BUCKET_NAME, FILE_NAME);
    verify(amazonS3, times(1)).getObject(BUCKET_NAME, FILE_NAME);
  }
}
