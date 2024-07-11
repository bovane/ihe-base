package org.openehealth.ipf.tutorials.xds;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.openehealth.ipf.commons.ihe.ws.cxf.NonReadingAttachmentMarshaller;
import org.openehealth.ipf.commons.ihe.xds.core.XdsJaxbDataBinding;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Document;
import org.openehealth.ipf.tutorials.constant.IheConstant;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/21
 */
@Slf4j
public class XmlU {
    public static void convertToXml(Object obj) {
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
//        log.warn(IheConstant.REPOSITORY_FILE_PATH);
        log.info(result);
        // 将结果字符串写入文件
//        FileUtil.appendUtf8String(result,IheConstant.REPOSITORY_FILE_PATH);
//        FileUtil.appendUtf8String("\n",IheConstant.REPOSITORY_FILE_PATH);
    }

    public static void saveDocumentFile(Document document) {
        String mimeType = document.getDataHandler().getContentType();
        String fileName = document.getDataHandler().getName();
        log.warn("当前data handler里面存的文件类型是: " + mimeType);
        log.warn("当前data handler里面存的文件名称是: " + fileName);
        String filePath = "";
        if (mimeType.equals("test/plain")) {

        }

    }


}
