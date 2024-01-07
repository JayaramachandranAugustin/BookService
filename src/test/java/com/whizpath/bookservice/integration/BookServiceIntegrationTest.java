package com.whizpath.bookservice.integration;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

import static io.restassured.RestAssured.given;

public class BookServiceIntegrationTest extends BaseIntegrationTest{
    @Test
    public void testUpload() throws FileNotFoundException {
        RequestSpecification requestSpecification=new RequestSpecBuilder()
                .setBaseUri("http://localhost:"+port)
                .setBasePath("/book-service/api").addMultiPart("image", ResourceUtils.getFile("classpath:design.jpg"))
                .addQueryParam("isbn","9781449373320")
                .addQueryParam("bookName","Designing data-intensive applications: the big ideas behind reliable, scalable, and maintainable systems ")
                .addQueryParam("authorName","Martin Kleppmann")
                .addQueryParam("yearPublished","2017").log(LogDetail.ALL).build();

        given().spec(requestSpecification).when().post("/upload").then().statusCode(HttpStatus.OK.value());
    }
}
