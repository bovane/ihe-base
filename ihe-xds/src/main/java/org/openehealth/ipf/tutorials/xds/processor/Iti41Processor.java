/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.tutorials.xds.processor;

import cn.hutool.core.io.FileUtil;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.openehealth.ipf.commons.ihe.ws.cxf.NonReadingAttachmentMarshaller;
import org.openehealth.ipf.commons.ihe.xds.core.XdsJaxbDataBinding;
import org.openehealth.ipf.commons.ihe.xds.core.requests.ProvideAndRegisterDocumentSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;

import org.openehealth.ipf.commons.ihe.xds.core.metadata.Document;
/**
 * Processing XDS ITI-41 Provide And Register Document Set.
 *  
 * @author Tomasz Judycki
 */
@Component
public class Iti41Processor implements Processor {
    protected Logger log = LoggerFactory.getLogger(getClass());
	private static String filePath = "/Users/bovane/Documents/hos-app/logs/repository_document.xml";

	@Override
	public void process(Exchange exchange) throws Exception {
	    
		Message message = exchange.getIn();

		ProvideAndRegisterDocumentSet request = message.getBody(ProvideAndRegisterDocumentSet.class);

		List<Document> documentList = request.getDocuments();
		String documentXml;
		for (Document document : documentList) {
			documentXml = renderEbxml(document);
			log.warn(documentXml);
			log.warn(filePath);
			// 将结果字符串写入文件
			FileUtil.appendUtf8String(documentXml,filePath);
			FileUtil.appendUtf8String("\n",filePath);

		}
	}

	/**
	 * 将实体类转为 XML
	 *
	 * @author bovane
	 * [ebXml]
	 * @return java.lang.String
	 */
	public static String renderEbxml(Object ebXml) {
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
