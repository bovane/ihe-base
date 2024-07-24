package org.openehealth.ipf.tutorials.xds.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Association;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Folder;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.SubmissionSet;
import org.openehealth.ipf.commons.ihe.xds.core.requests.RegisterDocumentSet;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.EbXML30Converters;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.XdsRenderingUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/4
 */
@Component
@Slf4j
public class Iti42Processor implements Processor {
    @Override
    public void process(Exchange exchange) {
        log.error("目前请求达到iti42,将在这里进行注册元数据存储=========");
        Message message = exchange.getIn();
        RegisterDocumentSet registerDocumentSet = message.getBody(RegisterDocumentSet.class);
        // 使用Converter 将实体类转为 EB XML 实体类
        var registerEbXml = EbXML30Converters.convert(registerDocumentSet);
//        log.info(XdsRenderingUtils.renderEbxml(registerEbXml));

        log.info("查看RegisterDocumentSet中的相关信息=======");

        SubmissionSet submissionSet = registerDocumentSet.getSubmissionSet();
        log.warn("提交集合的UUID是: " + submissionSet.getEntryUuid());

        log.warn("Folder信息如下所示: ");
        List<Folder> folders = registerDocumentSet.getFolders();
        log.warn("folders的数量为:" + folders.size());
        folders.forEach(folder -> {
            log.info(folder.toString());

        });

        log.info("DocumentEntry信息如下所示: ");
        List<DocumentEntry> documentEntries = registerDocumentSet.getDocumentEntries();
        log.info("文档的数量为:" + documentEntries.size());
        documentEntries.forEach(documentEntry -> {
            log.info(documentEntry.toString());
        });

        log.info("Association信息如下所示: ");
        List<Association> associations = registerDocumentSet.getAssociations();
        associations.forEach(association -> {
            log.info("打印Association: " + association.toString());
            log.info("打印Association的EntryUUID: " + association.getEntryUuid());
            log.warn("Association的SourceId: "+ association.getSourceUuid());
            log.warn("Association的TargetId: "+ association.getTargetUuid());
        });

    }
}
