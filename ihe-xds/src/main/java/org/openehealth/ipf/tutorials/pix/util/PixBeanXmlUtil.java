package org.openehealth.ipf.tutorials.pix.util;

import lombok.extern.slf4j.Slf4j;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/26
 */
@Slf4j
public class PixBeanXmlUtil {

    /**
     * 实体类 -> XML
     *
     * @author bovane
     * [obj]
     * @return java.lang.String
     */
    public static String marshallMessage(Object obj) {
        String xml = "";
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF8");
            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            xml = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * xml -> 对应的实体类
     *
     * @author bovane
     * [messageType, is]
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T unmarshallMessage(Class<T> messageType, InputStream is) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(messageType);
        Unmarshaller u = jc.createUnmarshaller();
        return (T) u.unmarshal(is);
    }

}
