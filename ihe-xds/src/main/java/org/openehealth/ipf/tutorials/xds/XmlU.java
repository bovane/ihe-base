package org.openehealth.ipf.tutorials.xds;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.openehealth.ipf.commons.ihe.ws.cxf.NonReadingAttachmentMarshaller;
import org.openehealth.ipf.commons.ihe.xds.core.XdsJaxbDataBinding;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Document;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/21
 */
@Slf4j
public class XmlU {
    private static String filePath = "/Users/bovane/Documents/hos-app/logs/repository.xml";
    public static void convertToXml(Document obj, String encoding) {
        String result = null;
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setAttachmentMarshaller(new NonReadingAttachmentMarshaller());
            marshaller.setListener(new XdsJaxbDataBinding.MarshallerListener());
            marshaller.setProperty("jaxb.formatted.output", true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            result = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.warn(filePath);
//        log.error(result);
        // 将结果字符串写入文件
        FileUtil.appendUtf8String(result,filePath);
        FileUtil.appendUtf8String("\n",filePath);
    }


}
