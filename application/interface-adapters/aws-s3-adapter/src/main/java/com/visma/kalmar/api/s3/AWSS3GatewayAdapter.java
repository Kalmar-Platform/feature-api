package com.visma.kalmar.api.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.visma.kalmar.api.exception.ResourceNotFoundException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AWSS3GatewayAdapter implements AWSS3Gateway {

  private final AmazonS3 amazonS3;

  private static final String BUCKET_NAME = "kalmar-openweb-registration-eu-north-1";

  public AWSS3GatewayAdapter(AmazonS3 amazonS3) {
    this.amazonS3 = amazonS3;
  }

  public void uploadFileToS3(String fileName, String payload) {
    try {
      byte[] contentBytes = payload.getBytes(StandardCharsets.UTF_8);
      ByteArrayInputStream inputStream = new ByteArrayInputStream(contentBytes);

      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(contentBytes.length);
      metadata.setContentType("application/json");

      PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, fileName, inputStream, metadata);

      amazonS3.putObject(request);
    } catch (Exception e) {
      throw new RuntimeException("Failed to upload file to S3", e);
    }
  }

  public String getFileFromS3(String fileName) {
    try {
      if (!amazonS3.doesObjectExist(BUCKET_NAME, fileName)) {
        throw new ResourceNotFoundException(
            "Registration file", "Registration file not found for id: " + fileName);
      }

      S3Object s3Object = amazonS3.getObject(BUCKET_NAME, fileName);
      S3ObjectInputStream inputStream = s3Object.getObjectContent();

      StringBuilder contentBuilder = new StringBuilder();
      try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
        char[] buffer = new char[1024];
        int charsRead;
        while ((charsRead = reader.read(buffer)) != -1) {
          contentBuilder.append(buffer, 0, charsRead);
        }
      }

      return contentBuilder.toString();
    } catch (IOException e) {
      throw new RuntimeException("Failed to read file from S3: " + fileName, e);
    }
  }
}
