package com.whizpath.bookservice.controller;

import com.whizpath.bookservice.configuration.ApplicationProperties;
import com.whizpath.bookservice.db.BookDynamoDBRepository;
import com.whizpath.bookservice.db.S3StorageService;
import com.whizpath.bookservice.messaging.MessageSender;
import com.whizpath.bookservice.model.Book;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/book-service/api")
@RequiredArgsConstructor
public class BookController {
    private final S3StorageService s3StorageService;
    private final BookDynamoDBRepository bookDynamoDBRepository;
    private final MessageSender messageSender;
    private final ApplicationProperties applicationProperties;

    @PostMapping("upload")
    public ResponseEntity<String> upload(@RequestParam("image")MultipartFile image, @RequestParam String isbn,
                                         @RequestParam String bookName, @RequestParam String authorName, @RequestParam int yearPublished){
    try{
        S3Resource s3Resource =s3StorageService.upload(image, bookName);
        Book book = Book.builder().id(UUID.randomUUID()).bookName(bookName).authorName(authorName).isbn(isbn).imageUrl(s3Resource.getURL().toString()).yearPublished(yearPublished).build();
        bookDynamoDBRepository.save(book);
        messageSender.publish(applicationProperties.queue(), book);
        return new ResponseEntity<>(String.format("{'id':'%s', 'imageUrl':'%s'}",book.getId(), book.getImageUrl()),HttpStatus.OK);
    }catch (IOException exception){
        return new ResponseEntity<String>(String.format("{'errorMessage':'%s'}",exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    }

}
