package com.visma.kalmar.api.s3;

public interface AWSS3Gateway {

    void uploadFileToS3(String fileName, String payload);
    String getFileFromS3(String fileName);
}
