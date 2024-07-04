package org.openehealth.ipf.tutorials.xds.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

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

    }
}
