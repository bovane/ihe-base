package org.openehealth.ipf.tutorials.xds;

import lombok.extern.slf4j.Slf4j;
import org.openehealth.ipf.commons.ihe.ws.cxf.NonReadingAttachmentMarshaller;
import org.openehealth.ipf.commons.ihe.xds.core.XdsJaxbDataBinding;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/21
 */
@Slf4j
public class XmlUtil {

    /**
     * 将EB XML实体类转为 XML
     *
     * @author bovane
     * [ebXml]
     * @return java.lang.String
     */
    public static String renderEbXml(Object ebXml) {
        try {
            StringWriter writer = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(ebXml.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setAttachmentMarshaller(new NonReadingAttachmentMarshaller());
            marshaller.setListener(new XdsJaxbDataBinding.MarshallerListener());
            marshaller.setProperty("jaxb.formatted.output", true);
            marshaller.marshal(ebXml, writer);
            return writer.toString();
        } catch (JAXBException var3) {
            throw new RuntimeException(var3);
        }
    }


}
