package com.whizpath.bookservice.messaging;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSender {
    private final SqsTemplate sqsTemplate;

    public void publish(String queueName, Object payload){
        sqsTemplate.send( to -> to.queue(queueName).payload(payload.toString()));
    }
}
