package com.example.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class RabbitMqListener {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @RabbitListener(queues = {"queue1"})
    public void processQueue1(String message) throws InterruptedException {
        logger.info("1 listener: " + message);
        Thread.sleep(200);
    }

    @RabbitListener(queues = "queue2")
    public void processQueue2(String message) {
        logger.info("2 listener: " + message);
    }

    @RabbitListener(queues = {"queue1", "queue3"})
    public void duplicate(String message) throws InterruptedException {
        logger.info("3 and 1 listener: " + message);
        Thread.sleep(100);
    }

    @RabbitListener(queues = "queue3")
    public void processQueue3(String message) {
        logger.info("3 listener: " + message);
    }
}
