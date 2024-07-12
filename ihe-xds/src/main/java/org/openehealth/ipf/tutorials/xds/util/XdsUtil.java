package org.openehealth.ipf.tutorials.xds.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/12
 */
@Slf4j
public class XdsUtil {
    public static void printDataHandler(DataHandler dataHandler) {
        String mimeType = dataHandler.getContentType();
        System.out.println("MIME Type: " + mimeType);

        String fileName = dataHandler.getName();
        System.out.println("File Name: " + fileName);

        DataSource dataSource = dataHandler.getDataSource();
        try (InputStream inputStream = dataSource.getInputStream()) {
            // 读取输入流中的数据
            byte[] bytes = IOUtils.toByteArray(inputStream);
            System.out.println("Data Length: " + bytes.length);
            log.warn("流中的数据为:" );
            IOUtils.toString(inputStream);
            // 根据实际需求处理数据
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
