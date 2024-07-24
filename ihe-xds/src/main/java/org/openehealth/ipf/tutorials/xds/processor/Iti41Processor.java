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
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.openehealth.ipf.commons.ihe.ws.cxf.NonReadingAttachmentMarshaller;
import org.openehealth.ipf.commons.ihe.xds.core.XdsJaxbDataBinding;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.EbXMLFactory30;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.*;
import org.openehealth.ipf.commons.ihe.xds.core.requests.ProvideAndRegisterDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.transform.requests.ProvideAndRegisterDocumentSetTransformer;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.EbXML30Converters;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.XdsRenderingUtils;
import org.openehealth.ipf.tutorials.constant.IheConstant;
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

		log.warn("接收到到请求体为: ");
//		log.warn(XdsRenderingUtils.renderEbxml(requestEbxml));

		SubmissionSet submissionSet = request.getSubmissionSet();
		log.info("提交集合的UUID是: " + submissionSet.getEntryUuid());

		List<Folder> folders = request.getFolders();
		log.warn("folders的数量为:" + folders.size());
		// 如果有文件夹,那么先创建文件夹
		folders.forEach(folder -> {
			log.warn("folder的内容为" + folder.toString());
			// 文件夹的名称
			String folderEntryId = folder.getEntryUuid();
			// 创建文件夹
			String folderPath = IheConstant.BASE_FILE_PATH + folderEntryId;
			log.warn("完整的文件夹路径为" + folderPath);
			// 如果文件夹不存在,那么创建文件夹
			if (!FileUtil.isDirectory(folderPath)) {
				FileUtil.mkdir(folderPath);
			}
		});

		List<Document> documentList = request.getDocuments();
		log.info("文档的数量为:" + documentList.size());
		// 取到documentEntry
		documentList.forEach(document -> {
			log.info("文档Entry的UUID是: " +document.getDocumentEntry().getEntryUuid());
			log.warn("文档的内容是:" + document.toString());
			DataHandler dataHandler = document.getContent(DataHandler.class);
			byte[] content = (byte[]) ContentUtils.getContent(dataHandler);
			// 打印文件内容
			log.warn(String.valueOf(content.length));
			log.warn(IOUtils.toString(content));

			log.warn("当前data handler里面存的文件类型是: " + document.getDataHandler().getContentType());
			log.warn("当前data handler里面存的文件名称是: " + document.getDataHandler().getName());

            document.setContent(DataHandler.class,
					new DataHandler(new ByteArrayDataSource(content, dataHandler.getContentType())));

		});

		List<Association> associations = request.getAssociations();
		log.warn("Association的数量为: " + associations.size());
		associations.forEach(association -> {
			log.info("打印Association: " + association.toString());
			log.info("打印Association的EntryUUID: " + association.getEntryUuid());
			log.warn("Association的SourceId: "+ association.getSourceUuid());
			log.warn("Association的TargetId: "+ association.getTargetUuid());
		});

		// 如果有Association 关系,那么将对应的关系存入数据库,存入数据库后 (元数据信息存入数据库)
		// 最后存储实际的文档(有文件夹信息就存入文件夹、没有文件夹Association,那么就存在默认文件夹中)


		// 原封不动的将 request的内容存放到 body 里面
		exchange.getOut().setBody(request);

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
