package net.appdevjoy.testRestJms.component;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

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
    public void receiveAndReturn(Message msg) {
        log.info("Received Msg: " + msg);
        int randInt = rand.nextInt(10);
        try {
            TimeUnit.SECONDS.sleep(randInt);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String retMsg;
        try {
            String body = null;
             if (msg instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) msg;
                body = textMessage.getText();

                // Process the message body
                System.out.println("Received message: " + body);

            } else {
                // Handle other message types if needed
                System.out.println("Received a non-text message");
            }
            log.info("msgBody: " + body);
            retMsg = "JMS Received Msg -- CorrelationId: " + msg.getJMSCorrelationID() + " body: " + body+ " jmsMsgCount: " + counter.getAndIncrement()
                    + " slept: " + randInt;
            jmsTemplate.convertAndSend("retQ", retMsg, new CorrelationIdPostProcessor(msg.getJMSCorrelationID()));
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
