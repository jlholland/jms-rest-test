package net.appdevjoy.testRestJms.component;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JmsProcessor {
    private JmsTemplate jmsTemplate;
    private final AtomicLong counter = new AtomicLong();
    private final Random rand = new Random();

    public JmsProcessor(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @JmsListener(destination = "senQ")
    public void receiveAndReturn(String msg) {
        log.info("Received Msg: " + msg);
            int randInt = rand.nextInt(10);
            try {
                TimeUnit.SECONDS.sleep(randInt);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        String retMsg = "JMS Received Msg -- " + msg + " jmsMsgCount: " + counter.getAndIncrement() + " slept: " + randInt;
        jmsTemplate.convertAndSend("retQ", retMsg);
    }
}
