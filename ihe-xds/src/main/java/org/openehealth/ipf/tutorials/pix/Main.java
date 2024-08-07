package org.openehealth.ipf.tutorials.pix;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import net.ihe.gazelle.hl7v3.prpain201301UV02.PRPAIN201301UV02Type;
import net.ihe.gazelle.hl7v3transformer.HL7V3Transformer;

import javax.xml.bind.JAXBException;
import java.io.InputStream;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/26
 */
@Slf4j
public class Main {
    public static void main(String[] args) throws JAXBException {
//        InputStream is = FileUtil.getInputStream("translation/pixfeed/v3/PIX_FEED_REG_Maximal_Request.xml");
        InputStream is = FileUtil.getInputStream("translation/pixfeed/v3/PIX_FEED_REG_Maximal_Request.xml");

        PRPAIN201301UV02Type message = HL7V3Transformer.unmarshallMessage(PRPAIN201301UV02Type.class,is);
        log.warn(message.getITSVersion());



    }
}
