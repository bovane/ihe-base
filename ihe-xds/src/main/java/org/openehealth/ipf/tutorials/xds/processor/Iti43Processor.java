package org.openehealth.ipf.tutorials.xds.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/4
 */
@Component
@Slf4j
public class Iti43Processor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("当前流程来到Iti43,取文档=========");
    }
}
