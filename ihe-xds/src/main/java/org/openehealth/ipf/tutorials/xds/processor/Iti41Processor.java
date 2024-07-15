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

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.openehealth.ipf.commons.ihe.ws.cxf.NonReadingAttachmentMarshaller;
import org.openehealth.ipf.commons.ihe.xds.core.XdsJaxbDataBinding;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.EbXMLFactory30;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Document;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Folder;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.SubmissionSet;
import org.openehealth.ipf.commons.ihe.xds.core.requests.ProvideAndRegisterDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.transform.requests.ProvideAndRegisterDocumentSetTransformer;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.EbXML30Converters;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.XdsRenderingUtils;
import org.openehealth.ipf.tutorials.xds.ByteArrayDataSource;
import org.openehealth.ipf.tutorials.xds.ContentUtils;
import org.openehealth.ipf.tutorials.xds.util.XdsUtil;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
/**
 * Processing XDS ITI-41 Provide And Register Document Set.
 *  
 * @author Tomasz Judycki
 */
@Component
@Slf4j
public class Iti41Processor implements Processor {

	@Override
	public void process(Exchange exchange) {

		log.warn("目前请求达到ITI41,将在这里进行文档存储++++++++++++++++++++");

		Message message = exchange.getIn();
		ProvideAndRegisterDocumentSet request = message.getBody(ProvideAndRegisterDocumentSet.class);
		// 使用Converter 将实体类转为 EB XML 实体类
		var requestEbxml = EbXML30Converters.convert(request);

		SubmissionSet submissionSet = request.getSubmissionSet();

		List<Folder> folders = request.getFolders();
		log.warn("folders的数量为:" + folders.size());

		List<Document> documentList = request.getDocuments();
		log.info("文档的数量为:" + documentList.size());
		// 取到documentEntry
		documentList.forEach(document -> {
			log.warn("文档的内容是:" + document.toString());
			DataHandler dataHandler = document.getContent(DataHandler.class);
			byte[] content = (byte[]) ContentUtils.getContent(dataHandler);
			log.warn(String.valueOf(content.length));
			log.warn(IOUtils.toString(content));
			log.warn("当前data handler里面存的文件类型是: " + document.getDataHandler().getContentType());
			log.warn("当前data handler里面存的文件名称是: " + document.getDataHandler().getName());

            document.setContent(DataHandler.class,
					new DataHandler(new ByteArrayDataSource(content, dataHandler.getContentType())));


		});
		// 原封不动的将 request的内容存放到 body 里面
		exchange.getOut().setBody(request);

	}

	/**
	 * 将EB XML实体类转为 XML
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

	public static String transformer(ProvideAndRegisterDocumentSet provideAndRegisterDocumentSet) {
		String result = "";
		ProvideAndRegisterDocumentSetTransformer provideAndRegisterDocumentSetTransformer = new ProvideAndRegisterDocumentSetTransformer(new EbXMLFactory30());
		var ebxml = provideAndRegisterDocumentSetTransformer.toEbXML(provideAndRegisterDocumentSet);
		ebxml.removeDocument("document01");
		var request = ebxml.getInternal();
		log.info("是否删除了文档 document01?");
		result = XdsRenderingUtils.renderEbxml(request);
		log.warn(result);
		return result;
	}
}
