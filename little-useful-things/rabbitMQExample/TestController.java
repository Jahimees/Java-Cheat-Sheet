package com.example.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/start")
public class TestController {

    @Autowired
    RabbitTemplate template;

    @GetMapping
    public String queue1() throws InterruptedException {
//        for (int i = 0; i < 20; i++) {
//            template.convertAndSend("queue1", "Hello rabbit1");
//            Thread.sleep(1000);
//            template.convertAndSend("queue1", "Hello rabbit2");
//            Thread.sleep(1000);
//            template.convertAndSend("queue1", "Hello rabbit3");
//            Thread.sleep(1000);
//            template.convertAndSend("queue1", "Hello rabbit4");
//        }

//        template.setExchange("exchange");
//        template.convertAndSend("Hello1");

        template.setExchange("direct-exchange");
        template.convertAndSend("info", "Info");
        template.convertAndSend("error", "error");
        template.convertAndSend("warning", "warning");
        return "sent";
    }
}
