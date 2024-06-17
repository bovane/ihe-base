package org.openehealth.ipf.tutorials.xds;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openehealth.ipf.commons.audit.model.AuditMessage;
import org.openehealth.ipf.commons.audit.queue.AuditMessageQueue;
import org.openehealth.ipf.commons.audit.queue.RecordingAuditMessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/13
 */
@Component
public class MyProcessor implements Processor {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("auditMessageQueue")
    private AuditMessageQueue auditMessageQueue;


    @Override
    public void process(Exchange exchange) {
        log.warn("test processor");
        RecordingAuditMessageQueue recordingAuditMessageQueue = (RecordingAuditMessageQueue) auditMessageQueue;
        List<AuditMessage> auditMessages = recordingAuditMessageQueue.getMessages();
        auditMessages.forEach(auditMessage -> log.warn(auditMessage.toString()));
    }
}

