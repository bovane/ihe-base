package org.openehealth.ipf.tutorials.xds.audit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.openehealth.ipf.commons.audit.AuditContext;
import org.openehealth.ipf.commons.audit.model.AuditMessage;
import org.openehealth.ipf.commons.audit.queue.AbstractMockedAuditMessageQueue;
import org.openehealth.ipf.tutorials.xds.entity.XdsAudit;
import org.openehealth.ipf.tutorials.xds.service.XdsAuditService;

import javax.annotation.Resource;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/17
 */
@Slf4j
public class DbRecordingAuditMessageQueue implements AbstractMockedAuditMessageQueue {
    private final List<AuditMessage> messages = new CopyOnWriteArrayList();

    @Resource
    private XdsAuditService xdsAuditService;

    public DbRecordingAuditMessageQueue() {
    }

    public void audit(AuditContext auditContext, AuditMessage... auditMessages) {
        log.error("测试自定义审计消息队列");
        List<AuditMessage> temp = Arrays.asList(auditMessages);
        this.messages.addAll(temp);
        List<XdsAudit> xdsAudits = temp.stream()
                .map(auditMessage -> {
                    XdsAudit xdsAudit = new XdsAudit();
                    xdsAudit.setAuditMessage(auditMessage.toString());
                    log.error(auditMessage.toString());
                    return xdsAudit;
                })
                .collect(Collectors.toList());
        this.xdsAuditService.saveBatch(xdsAudits);

    }

    public List<AuditMessage> getMessages() {
        return Collections.unmodifiableList(this.messages);
    }

    public Optional<AuditMessage> getFirstMessage() {
        return this.getMessages().stream().findFirst();
    }

    public void clear() {
        this.messages.clear();
    }
}

