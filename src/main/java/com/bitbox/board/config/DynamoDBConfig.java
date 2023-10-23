package com.bitbox.board.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories(basePackages = "com.bitbox.board.repository")
public class DynamoDBConfig {

  @Value("${cloud.aws.credentials.access-key}")
  private String accessKey;

  @Value("${cloud.aws.credentials.secret-key}")
  private String secretKey;

  @Value("${cloud.aws.region.static}")
  private String region;

  public AWSCredentials amazonAWSCredentials() {
    return new BasicAWSCredentials(accessKey, secretKey);
  }

  public AWSCredentialsProvider amazonAWSCredentialsProvider() {
    return new AWSStaticCredentialsProvider(amazonAWSCredentials());
  }

  @Bean
  public AmazonDynamoDB amazonDynamoDB() {
    return AmazonDynamoDBClientBuilder.standard().withCredentials(amazonAWSCredentialsProvider())
        .withRegion(region).build();
  }

}