package net.appdevjoy.testRestJms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/")
public class HelloRestController {
    private JmsTemplate jmsTemplate;
    private final AtomicLong counter = new AtomicLong();
    private final Random rand = new Random();

    public HelloRestController(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @GetMapping("/hello") 
    public ResponseEntity<String> hello() {
        log.info("hello called " + counter.get());
            int randInt = rand.nextInt(10);
            try {
                TimeUnit.SECONDS.sleep(randInt);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String jmsMsg = "Rest Controller slept: " +randInt+ " sent from: " + counter.getAndIncrement(); 
            jmsTemplate.convertAndSend("senQ", jmsMsg);;
            String retMsg = (String) jmsTemplate.receiveAndConvert("retQ");
            return new ResponseEntity<String>("Returning from restCall, from jms[" + retMsg + "]\n", HttpStatus.OK);
    }
}
