package com.whizpath.bookservice.model;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.UUID;
@DynamoDbBean
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Book {
    private UUID id;
    private String isbn;
    private String bookName;
    private String authorName;
    private int yearPublished;
    private String imageUrl;

    @DynamoDbPartitionKey
    public UUID getId(){
        return id;
    }

}
