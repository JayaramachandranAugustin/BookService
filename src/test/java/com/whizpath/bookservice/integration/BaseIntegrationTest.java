package com.whizpath.bookservice.integration;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@RunWith(SpringRunner.class)
public class BaseIntegrationTest {

    @LocalServerPort
    Integer port;

    static LocalStackContainer localStackContainer=new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.3"));

    static final String BUCKET_NAME = UUID.randomUUID().toString();
    static final String QUEUE_NAME = UUID.randomUUID().toString();

    static {
        Startables.deepStart(localStackContainer).join();
        try{
            localStackContainer.execInContainer("awslocal", "s3", "mb" , "s3://"+BUCKET_NAME);
            localStackContainer.execInContainer("awslocal", "sqs", "create-queue" , "--queue-name",QUEUE_NAME);
            localStackContainer.execInContainer("awslocal", "dynamodb", "create-table", "--table-name", "book", "--key-schema", "AttributeName=id,KeyType=HASH",
                    "--attribute-definitions", "AttributeName=id,AttributeType=S", "--provisioned-throughput", "ReadCapacityUnits=5, WriteCapacityUnits=5");
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
        dynamicPropertyRegistry.add("book-service.bucket",()-> BUCKET_NAME);
        dynamicPropertyRegistry.add("book-service.queue",()-> QUEUE_NAME);
        dynamicPropertyRegistry.add("spring.cloud.aws.region.static",()-> localStackContainer.getRegion());
        dynamicPropertyRegistry.add("spring.cloud.aws.credentials.access-key",()-> localStackContainer.getAccessKey());
        dynamicPropertyRegistry.add("spring.cloud.aws.credentials.secret-key",()-> localStackContainer.getSecretKey());

        dynamicPropertyRegistry.add("spring.cloud.aws.s3.endpoint",()-> localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        dynamicPropertyRegistry.add("spring.cloud.aws.sqs.endpoint",()-> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS).toString());
        dynamicPropertyRegistry.add("spring.cloud.aws.dynamodb.endpoint",()-> localStackContainer.getEndpointOverride(LocalStackContainer.Service.DYNAMODB).toString());
    }
}
