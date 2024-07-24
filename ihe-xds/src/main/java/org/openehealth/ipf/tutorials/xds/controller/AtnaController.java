package org.openehealth.ipf.tutorials.xds.controller;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.openehealth.ipf.commons.audit.model.AuditMessage;
import org.openehealth.ipf.commons.audit.queue.AuditMessageQueue;
import org.openehealth.ipf.commons.audit.queue.RecordingAuditMessageQueue;
import org.openehealth.ipf.tutorials.xds.DataStore;
import org.openehealth.ipf.tutorials.xds.audit.DbRecordingAuditMessageQueue;
import org.openehealth.ipf.tutorials.xds.service.DataStoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/17
 */
@RestController
@RequestMapping("/atna")
@Slf4j
public class AtnaController {
    @Resource
    private DataStoreService dataStoreService;

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
    @GetMapping("/datastore")
    @ResponseBody
    public void listDataStore() {
//        DataStore dataStore = SpringUtil.getBean("dataStore");
//        var entries = (CopyOnWriteArrayList)dataStore.getEntries();
//        var documents = (ConcurrentHashMap)dataStore.getDocuments();
//        var indexs =  (LinkedHashMap<String, ConcurrentHashMap>)dataStore.getIndexes();
//        log.warn(entries.get(0).toString());
        this.dataStoreService.listDataStore();
    }

}
