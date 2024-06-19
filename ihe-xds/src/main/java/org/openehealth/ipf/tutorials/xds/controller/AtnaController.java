package org.openehealth.ipf.tutorials.xds.controller;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.openehealth.ipf.commons.audit.model.AuditMessage;
import org.openehealth.ipf.commons.audit.queue.AuditMessageQueue;
import org.openehealth.ipf.commons.audit.queue.RecordingAuditMessageQueue;
import org.openehealth.ipf.tutorials.xds.audit.DbRecordingAuditMessageQueue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/17
 */
@RestController
@RequestMapping("/atna")
@Slf4j
public class AtnaController {

    @GetMapping("/list")
    @ResponseBody
    public void listAtna() {
        AuditMessageQueue auditMessageQueue = SpringUtil.getBean("auditMessageQueue");
        log.warn(auditMessageQueue.toString());
//        RecordingAuditMessageQueue recordingAuditMessageQueue = (RecordingAuditMessageQueue) auditMessageQueue;
        DbRecordingAuditMessageQueue recordingAuditMessageQueue = (DbRecordingAuditMessageQueue) auditMessageQueue;
        List<AuditMessage> auditMessages = recordingAuditMessageQueue.getMessages();
        auditMessages.forEach(auditMessage -> log.warn(auditMessage.toString()));
    }
}
