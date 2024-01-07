package com.whizpath.bookservice.db;

import com.whizpath.bookservice.model.Book;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookDynamoDBRepository {
    private final DynamoDbTemplate dynamoDbTemplate;

    public void save(Book book){
        dynamoDbTemplate.save(book);
    }

    public List<Book> findAll(){
        return dynamoDbTemplate.scanAll(Book.class).items().stream().toList();
    }
}
