package com.visma.kalmar.api.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.visma.kalmar.api.s3.AWSS3Gateway;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AWSS3Properties.class)
public class AmazonS3Config {

  private final AWSS3Properties awsS3Properties;

  public AmazonS3Config(AWSS3Properties awsS3Properties) {
    this.awsS3Properties = awsS3Properties;
  }

  @Bean
  public AmazonS3 getAmazonS3Client() {
    return AmazonS3ClientBuilder.standard()
        .withRegion(Regions.fromName(awsS3Properties.region()))
        .build();
  }

  @Bean
  public AWSS3Gateway awsS3Gateway(AmazonS3 amazonS3) {
    return new com.visma.kalmar.api.s3.AWSS3GatewayAdapter(amazonS3);
  }
}
