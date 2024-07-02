package org.openehealth.ipf.tutorials.pix.processor;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import net.ihe.gazelle.hl7v3.prpain201301UV02.PRPAIN201301UV02Type;
import net.ihe.gazelle.hl7v3.prpain201302UV02.PRPAIN201302UV02Type;
import net.ihe.gazelle.hl7v3transformer.HL7V3Transformer;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
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
        // 得到xml消息,这里可以根据 XML 字符串的根目录标签,来判断是注册、修改、合并的消息类型
        String xml = message.getBody().toString();
        String rootTag = getRootTagName(xml);
        log.error(rootTag);
        log.error(xml);
        InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        // PRPA_AR201301UV02 注册患者
        // PRPA_AR201302UV02 为XDS 更新文档注册库中的患者信息
        // PRPA_AR201304UV02 患者合并

        PRPAIN201302UV02Type prpain201302UV02Type = HL7V3Transformer.unmarshallMessage(PRPAIN201302UV02Type.class,is);
        log.warn(prpain201302UV02Type.getITSVersion());
        exchange.getOut().setBody(ACK);
    }

    /**
     * 获取XML字符串的根Tag
     *
     * @author bovane
     * [xmlString]
     * @return java.lang.String
     */
    public static String getRootTagName(String xmlString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlString));
            Document doc = builder.parse(is);
            Element rootElement = doc.getDocumentElement();
            return rootElement.getTagName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
