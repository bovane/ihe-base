package org.openehealth.ipf.tutorials.xds.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.DefaultExchange;
import org.apache.commons.io.IOUtils;
import org.openehealth.ipf.commons.ihe.xds.core.SampleData;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AvailabilityStatus;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Identifiable;
import org.openehealth.ipf.commons.ihe.xds.core.requests.*;
import org.openehealth.ipf.commons.ihe.xds.core.requests.query.FindDocumentsQuery;
import org.openehealth.ipf.commons.ihe.xds.core.requests.query.QueryReturnType;
import org.openehealth.ipf.commons.ihe.xds.core.responses.QueryResponse;
import org.openehealth.ipf.commons.ihe.xds.core.responses.Response;
import org.openehealth.ipf.commons.ihe.xds.core.responses.RetrievedDocumentSet;
import org.openehealth.ipf.platform.camel.core.util.Exchanges;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.EbXML30Converters;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.XdsRenderingUtils;
import org.openehealth.ipf.tutorials.xds.ContentUtils;
import org.openehealth.ipf.tutorials.xds.datasource.XmlDataSource;
import org.openehealth.ipf.tutorials.xds.dto.XdsProvidedRegisterDTO;
import org.openehealth.ipf.tutorials.xds.service.XdsClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.openehealth.ipf.tutorials.xds.util.XdsUtil.printDataHandler;

/**
 * @author bovane bovane.ch@gmail.com
 * @date 2024/2/20
 */
@Service
@Slf4j
public class XdsClientServiceImpl implements XdsClientService {

    private final CamelContext camelContext;
    private final ProducerTemplate producerTemplate;

    @Autowired
    public XdsClientServiceImpl(CamelContext camelContext, ProducerTemplate producerTemplate) {
        this.camelContext = camelContext;
        this.producerTemplate = producerTemplate;
    }


    @Override
    @SneakyThrows
    public void iti4142ProvidedAndRegister(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        // for each call,设置Camel传输方式
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.setPattern(ExchangePattern.InOut);

        // generate documentEntry,这里的SampleData只创建单文档信息
        ProvideAndRegisterDocumentSet provide = SampleData.createProvideAndRegisterDocumentSet();
//        ProvideAndRegisterDocumentSet provide = CreateHelper.createProvideAndRegisterDocumentSet(xdsProvidedRegisterDTO);
        DocumentEntry documentEntry = provide.getDocuments().get(0).getDocumentEntry();

        // 添加额外元数据
        HashMap<String, List<String>> extra = new HashMap<>();
        extra.put("urn:abc", CollUtil.newArrayList("ddd"));
        documentEntry.setExtraMetadata(extra);

        // 添加患者信息
        Identifiable patientId = documentEntry.getPatientId();
        patientId.setId(UUID.randomUUID().toString());
        log.warn(patientId.getId());

        // 计算文档的Hash 和 Size
        // 一个Document对象是 由 DocumentEntry 对象以及一个DataHandler对象
        // 在 Apache Camel 中, DataHandler 是一个非常重要的概念,它代表了一个通用的数据容器,
        // 可以包含各种类型的数据,比如文件、二进制数据等。
        // 在 Apache Camel 中, DataHandler 通常用于在消息交换过程中传输附件或二进制数据。
        // getContent() 方法返回的是原始的数据对象,可能是 InputStream、byte[] 或其他类型
        documentEntry.setHash(String.valueOf(ContentUtils.sha1(provide.getDocuments().get(0).getContent(DataHandler.class))));
        documentEntry.setSize(Long.valueOf(String.valueOf(ContentUtils.size(provide.getDocuments().get(0).getContent(DataHandler.class)))));
        log.warn(documentEntry.getSize().toString());

        // 提供自定义的data handler 信息

        // 设置 documentEntry
        provide.getDocuments().get(0).setDocumentEntry(documentEntry);

        // 测试第一个文档的值是否设置进去
        log.error("测试第一个文档内容是否设置进去");
        log.warn(String.valueOf(provide.getDocuments().size()));
        log.warn(provide.getDocuments().get(0).getDocumentEntry().getEntryUuid());
        log.warn(provide.getDocuments().get(0).getDocumentEntry().getUniqueId());
        log.warn(String.valueOf(provide.getDocuments().get(0).getDocumentEntry().getSize()));

        // 添加第二个文档
//        Document second = new Document();
//        second.setDocumentEntry(SampleData.createDocumentEntry(patientId));
//        second.setDataHandler(createCoustomDataHandler());
//        // 重新设置Data Handler之后需要重新计算文档的hash 和 size
//        second.getDocumentEntry().setSize(Long.valueOf(String.valueOf(ContentUtils.size(second.getContent(DataHandler.class)))));
//        second.getDocumentEntry().setHash(String.valueOf(ContentUtils.sha1(second.getContent(DataHandler.class))));
//        provide.getDocuments().add(second);
//        // 测试第二个文档的值是否设置进去
//        log.error("测试第二个文档内容是否设置进去");
//        log.warn(String.valueOf(provide.getDocuments().size()));
//        log.warn(provide.getDocuments().get(1).getDocumentEntry().getEntryUuid());
//        log.warn(provide.getDocuments().get(1).getDocumentEntry().getUniqueId());
//        log.warn(String.valueOf(provide.getDocuments().get(1).getDocumentEntry().getSize()));

        // 发送请求
        exchange.getIn().setBody(provide);
        String iti41Endpoint = StrUtil.isNotEmpty(xdsProvidedRegisterDTO.getIti41EndpointUrl()) ? xdsProvidedRegisterDTO.getIti41EndpointUrl() : "xds-iti41://localhost:9091/services/xds-iti41";
//        log.error(XdsRenderingUtils.render(exchange));

        exchange = producerTemplate.send(iti41Endpoint, exchange);
        Exception exception = Exchanges.extractException(exchange);
        if (exception != null) {
            throw exception;
        }
        Response providedResponse = exchange.getMessage().getMandatoryBody(Response.class);
        log.warn(providedResponse.toString());


        log.error("=======开始测试查询======");

        // 设置查询
        FindDocumentsQuery query = new FindDocumentsQuery();
//        query.setPatientId(patientId);
        query.setPatientId(provide.getSubmissionSet().getPatientId());
        log.info(query.getPatientId().getId());
        // 设置状态
        List<AvailabilityStatus> statuses = CollUtil.newArrayList();
        statuses.add(AvailabilityStatus.APPROVED);
        query.setStatus(statuses);

        // 设置ITI18 事务的最外层查询体
        QueryRegistry queryRegistry = new QueryRegistry(query);
        queryRegistry.setReturnType(QueryReturnType.LEAF_CLASS);
        exchange.getIn().setBody(queryRegistry);   // "request" is what you created

        // 发送请求
        String endpointUrl = StrUtil.isNotEmpty(xdsProvidedRegisterDTO.getIti18EndpointUrl()) ? xdsProvidedRegisterDTO.getIti18EndpointUrl() : "xds-iti18://localhost:9091/services/xds-iti18";
        exchange = producerTemplate.send(endpointUrl, exchange);

        if (exception != null) {
            throw exception;
        }
        QueryResponse queryResponse = exchange.getMessage().getMandatoryBody(QueryResponse.class);

        log.warn("查询的响应为: ");
        var queryEbXml = EbXML30Converters.convert(queryResponse);
        log.info(XdsRenderingUtils.renderEbxml(queryEbXml));

        // 查看查询返回结果
        log.warn(queryResponse.toString());
        log.warn(String.valueOf(queryResponse.getDocuments().size()));
        log.warn(String.valueOf(queryResponse.getDocumentEntries().size()));
        log.warn(queryResponse.getDocumentEntries().get(0).getEntryUuid());
        log.warn(String.valueOf(queryResponse.getDocumentEntries().get(0).getSize()));
        log.warn(queryResponse.getDocumentEntries().get(0).getExtraMetadata().toString());
    }

    @Override
    public void Iti42Register(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) throws Exception {
        // for each call
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.setPattern(ExchangePattern.InOut);

        RegisterDocumentSet registerDocumentSet = SampleData.createRegisterDocumentSet();
        log.warn("当前注册文档集的大小为为:"+registerDocumentSet.getDocumentEntries().size());
        DocumentEntry documentEntry = registerDocumentSet.getDocumentEntries().get(0);

        // 添加患者信息
        Identifiable patientId = documentEntry.getPatientId();
        patientId.setId(UUID.randomUUID().toString());
        log.warn(patientId.getId());
        // 设置文档唯一ID
        documentEntry.setUniqueId("4.3.2.1");

        registerDocumentSet.getDocumentEntries().set(0,documentEntry);
        // 查看值是否设置进去
        log.info(registerDocumentSet.getDocumentEntries().get(0).getUniqueId());
        log.info(registerDocumentSet.getDocumentEntries().get(0).getUri());
        log.info(registerDocumentSet.getDocumentEntries().get(0).getPatientId().getId());


        // 发送请求
        exchange.getIn().setBody(registerDocumentSet);
        String iti42Endpoint = StrUtil.isNotEmpty(xdsProvidedRegisterDTO.getIti42EndpointUrl()) ? xdsProvidedRegisterDTO.getIti42EndpointUrl() : "xds-iti42://localhost:9091/services/xds-iti42";
//        log.error(XdsRenderingUtils.render(exchange));

        exchange = producerTemplate.send(iti42Endpoint, exchange);
        Exception exception = Exchanges.extractException(exchange);
        if (exception != null) {
            throw exception;
        }
        Response providedResponse = exchange.getMessage().getMandatoryBody(Response.class);
        log.warn(providedResponse.toString());

        // 测试查询
        FindDocumentsQuery findDocumentsQuery = new FindDocumentsQuery();
        findDocumentsQuery.setPatientId(patientId);
        // 设置状态
        List<AvailabilityStatus> status = CollUtil.newArrayList();
        status.add(AvailabilityStatus.APPROVED);
        findDocumentsQuery.setStatus(status);

        // 设置最外层封装查询体
        QueryRegistry queryRegistry = new QueryRegistry(findDocumentsQuery);
        queryRegistry.setReturnType(QueryReturnType.LEAF_CLASS);
        exchange.getIn().setBody(queryRegistry);

        // 发送请求

        // 发送请求
        String endpointUrl = StrUtil.isNotEmpty(xdsProvidedRegisterDTO.getIti18EndpointUrl()) ? xdsProvidedRegisterDTO.getIti18EndpointUrl() : "xds-iti18://localhost:9091/services/xds-iti18";
        exchange = producerTemplate.send(endpointUrl, exchange);

        if (exception != null) {
            throw exception;
        }
        QueryResponse queryResponse = exchange.getMessage().getMandatoryBody(QueryResponse.class);

        // 查看查询返回
        log.warn(queryResponse.toString());
        log.warn(String.valueOf(queryResponse.getDocuments().size()));
        log.warn(String.valueOf(queryResponse.getDocumentEntries().size()));
        log.warn(queryResponse.getDocumentEntries().get(0).getEntryUuid());
        log.warn(queryResponse.getDocumentEntries().get(0).getUniqueId());
        log.warn(String.valueOf(queryResponse.getDocumentEntries().get(0).getSize()));

    }

    @Override
    public void Iti43Retrieve(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) throws Exception {
        // for each call,设置Camel的传输方式
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.setPattern(ExchangePattern.InOut);

        // generate documentEntry,这里的SampleData只创建单文档信息
        ProvideAndRegisterDocumentSet provide = SampleData.createProvideAndRegisterDocumentSet();
        DocumentEntry documentEntry = provide.getDocuments().get(0).getDocumentEntry();

        // 添加患者信息
        Identifiable patientId = documentEntry.getPatientId();
        patientId.setId(UUID.randomUUID().toString());
        log.warn(patientId.getId());
        // 设置文档唯一ID
        documentEntry.setUniqueId("4.3.2.1");


        // 计算文档的Hash 和 Size
        // 一个Document对象是 由 DocumentEntry 对象以及一个DataHandler对象
        // 在 Apache Camel 中, DataHandler 是一个非常重要的概念,它代表了一个通用的数据容器,
        // 可以包含各种类型的数据,比如文件、二进制数据等。
        // 在 Apache Camel 中, DataHandler 通常用于在消息交换过程中传输附件或二进制数据。
        // getContent() 方法返回的是原始的数据对象,可能是 InputStream、byte[] 或其他类型
        documentEntry.setHash(String.valueOf(ContentUtils.sha1(provide.getDocuments().get(0).getContent(DataHandler.class))));
        documentEntry.setSize(Long.valueOf(String.valueOf(ContentUtils.size(provide.getDocuments().get(0).getContent(DataHandler.class)))));
        log.warn(documentEntry.getSize().toString());


        // 设置 documentEntry
        provide.getDocuments().get(0).setDocumentEntry(documentEntry);

        // 测试值是否设置进去
        log.error("测试文档内容是否设置进去");
        log.warn(String.valueOf(provide.getDocuments().size()));
        log.warn(provide.getDocuments().get(0).getDocumentEntry().getEntryUuid());
        log.warn(provide.getDocuments().get(0).getDocumentEntry().getUniqueId());
        log.warn(String.valueOf(provide.getDocuments().get(0).getDocumentEntry().getSize()));

        // 发送请求
        exchange.getIn().setBody(provide);
        String iti41Endpoint = StrUtil.isNotEmpty(xdsProvidedRegisterDTO.getIti41EndpointUrl()) ? xdsProvidedRegisterDTO.getIti41EndpointUrl() : "xds-iti41://localhost:9091/services/xds-iti41";

        exchange = producerTemplate.send(iti41Endpoint, exchange);
        Exception exception = Exchanges.extractException(exchange);
        if (exception != null) {
            throw exception;
        }
        Response providedResponse = exchange.getMessage().getMandatoryBody(Response.class);
        log.warn(providedResponse.toString());


        log.error("=======开始测试取文档======");
        // 设置取文档的查询体
        RetrieveDocumentSet retrieveDocumentSet = new RetrieveDocumentSet();
        DocumentReference documentReference = new DocumentReference();
        documentReference.setDocumentUniqueId(provide.getDocuments().get(0).getDocumentEntry().getUniqueId());
        documentReference.setRepositoryUniqueId("something");
        retrieveDocumentSet.getDocuments().add(documentReference);
        retrieveDocumentSet.getDocuments().add(documentReference);

        // 发送请求
        exchange.getIn().setBody(retrieveDocumentSet);
        String iti43Endpoint = StrUtil.isNotEmpty(xdsProvidedRegisterDTO.getIti43EndpointUrl()) ? xdsProvidedRegisterDTO.getIti43EndpointUrl() : "xds-iti43://localhost:9091/services/xds-iti43";
//        log.error(XdsRenderingUtils.render(exchange));

        exchange = producerTemplate.send(iti43Endpoint, exchange);

        RetrievedDocumentSet retrieveResponse = exchange.getMessage().getMandatoryBody(RetrievedDocumentSet.class);
        log.warn(providedResponse.toString());
        // 查看附件中的内容
        InputStream inputStream = retrieveResponse.getDocuments().get(0).getDataHandler().getInputStream();
        String data = IOUtils.toString(inputStream);

        log.warn(data);
        System.out.println(data.length());


        DataHandler dataHandler = retrieveResponse.getDocuments().get(0).getDataHandler();
        printDataHandler(dataHandler);



    }

    @Override
    public void Iti18Query(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {

    }



    static DataHandler createCoustomDataHandler() {
        return new DataHandler(new XmlDataSource());
    }
}
