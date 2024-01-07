package com.whizpath.bookservice.db;

import com.whizpath.bookservice.configuration.ApplicationProperties;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Template s3Template;
    private final ApplicationProperties applicationProperties;

    public S3Resource upload(MultipartFile image, String key){
        try{
            return s3Template.upload(applicationProperties.bucket(), key, image.getInputStream());
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
