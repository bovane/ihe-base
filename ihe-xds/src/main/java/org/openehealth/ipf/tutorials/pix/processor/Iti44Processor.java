package org.openehealth.ipf.tutorials.pix.processor;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import net.ihe.gazelle.hl7v3.prpain201301UV02.PRPAIN201301UV02Type;
import net.ihe.gazelle.hl7v3transformer.HL7V3Transformer;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/26
 */
@Component
@Slf4j
public class Iti44Processor implements Processor {

    private static final String ACK = FileUtil.readString("translation/pixfeed/v3/Ack.xml", StandardCharsets.UTF_8);
    @Override
    public void process(Exchange exchange) throws Exception {
        Message message = exchange.getIn();
        // 得到xml消息,转为实体类
        String xml = message.getBody().toString();
        InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        PRPAIN201301UV02Type prpain201301UV02Type = HL7V3Transformer.unmarshallMessage(PRPAIN201301UV02Type.class,is);
        log.warn(prpain201301UV02Type.getITSVersion());
        exchange.getOut().setBody(ACK);
    }
}
