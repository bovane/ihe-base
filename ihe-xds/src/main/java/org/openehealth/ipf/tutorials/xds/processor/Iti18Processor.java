package org.openehealth.ipf.tutorials.xds.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.*;
import org.openehealth.ipf.commons.ihe.xds.core.requests.QueryRegistry;
import org.openehealth.ipf.commons.ihe.xds.core.requests.query.FindDocumentsQuery;
import org.openehealth.ipf.commons.ihe.xds.core.requests.query.Query;
import org.openehealth.ipf.commons.ihe.xds.core.requests.query.QueryType;
import org.openehealth.ipf.commons.ihe.xds.core.responses.*;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/4
 */
@Component
@Slf4j
public class Iti18Processor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        log.warn("现在请求来到ITI18");
        Message message = exchange.getIn();
        QueryRegistry request = message.getBody(QueryRegistry.class); //ITI-18 type of request class
    }
}
