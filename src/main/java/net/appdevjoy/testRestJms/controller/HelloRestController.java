package net.appdevjoy.testRestJms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.JMSException;
import javax.jms.Message;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/")
public class HelloRestController {
    private JmsTemplate jmsTemplate;
    private final AtomicInteger counter = new AtomicInteger();
    private final Random rand = new Random();

    public HelloRestController(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @GetMapping("/hello") 
    public ResponseEntity<String> hello() {
        int tmpCounter = counter.getAndIncrement();
        log.info("hello called " + tmpCounter);
            int randInt = rand.nextInt(10);
            try {
                TimeUnit.SECONDS.sleep(randInt);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String jmsMsg = "Rest Controller sent from: " + tmpCounter + " slept: " +randInt; 
           
            jmsTemplate.convertAndSend("senQ", jmsMsg, new CorrelationIdPostProcessor(String.valueOf(tmpCounter)));
            String retMsg = (String) jmsTemplate.receiveSelectedAndConvert("retQ","JMSCorrelationID = '" + tmpCounter + "'");
            // String retMsg = (String) jmsTemplate.receiveAndConvert("retQ");
            return new ResponseEntity<String>("Returning from restCall thisCounter: "+tmpCounter+", from jms[" + retMsg + "]\n", HttpStatus.OK);
    }

    private class CorrelationIdPostProcessor implements MessagePostProcessor {
        private final String correlationId;

        public CorrelationIdPostProcessor(final String correlationId) {
            this.correlationId = correlationId;
        }

        @Override
        public Message postProcessMessage(final Message msg)
                throws JMSException {
            msg.setJMSCorrelationID(correlationId);
            return msg;
        }
    }
}
