package org.openehealth.ipf.tutorials.xds.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.support.DefaultExchange;
import org.apache.commons.io.IOUtils;
import org.openehealth.ipf.commons.ihe.xds.core.requests.ProvideAndRegisterDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.requests.QueryRegistry;
import org.openehealth.ipf.commons.ihe.xds.core.requests.RetrieveDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.requests.query.QueryReturnType;
import org.openehealth.ipf.commons.ihe.xds.core.responses.QueryResponse;
import org.openehealth.ipf.commons.ihe.xds.core.responses.Response;
import org.openehealth.ipf.commons.ihe.xds.core.responses.RetrievedDocumentSet;
import org.openehealth.ipf.platform.camel.core.util.Exchanges;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.EbXML30Converters;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.XdsRenderingUtils;
import org.openehealth.ipf.tutorials.xds.CreateXdsHelper;
import org.openehealth.ipf.tutorials.xds.dto.XdsProvidedRegisterDTO;
import org.openehealth.ipf.tutorials.xds.service.IheClientService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/15
 */
@Slf4j
@Service
public class IheClientServiceImpl implements IheClientService {


    private final CamelContext camelContext;
    private final ProducerTemplate producerTemplate;

    public IheClientServiceImpl(CamelContext camelContext, ProducerTemplate producerTemplate) {
        this.camelContext = camelContext;
        this.producerTemplate = producerTemplate;
    }

    @Override
    public void iti4142JustSingleDocument(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) throws Exception {
        // for each call,设置Camel传输方式
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.setPattern(ExchangePattern.InOut);

        ProvideAndRegisterDocumentSet provide = CreateXdsHelper.createDocumentOnly(xdsProvidedRegisterDTO);

        // 测试文档的值是否设置进去
        log.error("测试第一个文档内容是否设置进去");
        log.warn("Document 文档数量为: " + provide.getDocuments().size());
        log.warn("Document 文档的UUID为: " + provide.getDocuments().get(0).getDocumentEntry().getEntryUuid());
        log.warn("Document 文档的唯一ID为: " +provide.getDocuments().get(0).getDocumentEntry().getUniqueId());
        log.warn("Document 文档的大小为:(size字符大小) " + provide.getDocuments().get(0).getDocumentEntry().getSize());

        // 发送请求
        exchange.getIn().setBody(provide);
        String iti41Endpoint = StrUtil.isNotEmpty(xdsProvidedRegisterDTO.getIti41EndpointUrl()) ? xdsProvidedRegisterDTO.getIti41EndpointUrl() : "xds-iti41://localhost:9091/services/xds-iti41";
        log.error("完整的的请求体为: ");
        log.info(XdsRenderingUtils.render(exchange));

        // 发送到 entry point iti41
        exchange = producerTemplate.send(iti41Endpoint, exchange);
        Exception exception = Exchanges.extractException(exchange);
        if (exception != null) {
            throw exception;
        }
        Response providedResponse = exchange.getMessage().getMandatoryBody(Response.class);
        log.warn("注册并提供文档的响应为:");
        var providedReponseEbxml = EbXML30Converters.convert(providedResponse);
        log.info(XdsRenderingUtils.renderEbxml(providedReponseEbxml));


        log.error("=======开始测试查询======");

        // 设置查询
        QueryRegistry queryRegistry = CreateXdsHelper.createFindDocumentsQuery(provide.getSubmissionSet().getPatientId());
        queryRegistry.setReturnType(QueryReturnType.LEAF_CLASS);
        exchange.getIn().setBody(queryRegistry);

        // 发送请求 到 ITI18, 这里是找到meta data,元数据信息
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


        log.error("=======开始测试取文档 ITI43======");
        // 设置取文档的查询体
        RetrieveDocumentSet retrieveDocumentSet = CreateXdsHelper.createRetrieveDocumentSet(xdsProvidedRegisterDTO);

        // 发送请求
        exchange.getIn().setBody(retrieveDocumentSet);
        String iti43Endpoint = StrUtil.isNotEmpty(xdsProvidedRegisterDTO.getIti43EndpointUrl()) ? xdsProvidedRegisterDTO.getIti43EndpointUrl() : "xds-iti43://localhost:9091/services/xds-iti43";

        exchange = producerTemplate.send(iti43Endpoint, exchange);

        RetrievedDocumentSet retrieveResponse = exchange.getMessage().getMandatoryBody(RetrievedDocumentSet.class);
        log.warn(providedResponse.toString());
        log.warn("取的的文件数量为: " + retrieveResponse.getDocuments().size());
        // 查看附件中的内容 (即文档内容)
        InputStream inputStream = retrieveResponse.getDocuments().get(0).getDataHandler().getInputStream();
        String data = IOUtils.toString(inputStream);

        log.warn(data);
        System.out.println(data.length());



    }

    @Override
    public void iti4142MultipleDocument(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) throws Exception {
        // for each call,设置Camel传输方式
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.setPattern(ExchangePattern.InOut);

        ProvideAndRegisterDocumentSet provide = CreateXdsHelper.createThreeDocumentOnly(xdsProvidedRegisterDTO);

        // 测试第一个XML文档的值是否设置进去
        log.error("测试第一个XML文档内容是否设置进去");
        log.warn("Document 文档数量为: " + provide.getDocuments().size());
        log.warn("Document 文档的UUID为: " + provide.getDocuments().get(0).getDocumentEntry().getEntryUuid());
        log.warn("Document 文档的唯一ID为: " +provide.getDocuments().get(0).getDocumentEntry().getUniqueId());
        log.warn("Document 文档的大小为:(size字符大小) " + provide.getDocuments().get(0).getDocumentEntry().getSize());

        // 测试第二个Text文档的值是否设置进去
        log.error("测试第二个Text文档内容是否设置进去");
        log.warn("Document 文档数量为: " + provide.getDocuments().size());
        log.warn("Document 文档的UUID为: " + provide.getDocuments().get(1).getDocumentEntry().getEntryUuid());
        log.warn("Document 文档的唯一ID为: " +provide.getDocuments().get(1).getDocumentEntry().getUniqueId());
        log.warn("Document 文档的大小为:(size字符大小) " + provide.getDocuments().get(1).getDocumentEntry().getSize());


        // 发送请求
        exchange.getIn().setBody(provide);
        String iti41Endpoint = StrUtil.isNotEmpty(xdsProvidedRegisterDTO.getIti41EndpointUrl()) ? xdsProvidedRegisterDTO.getIti41EndpointUrl() : "xds-iti41://localhost:9091/services/xds-iti41";
        log.error("完整的的请求体为: ");
//        log.info(XdsRenderingUtils.render(exchange));

        // 发送到 entry point iti41
        exchange = producerTemplate.send(iti41Endpoint, exchange);
        Exception exception = Exchanges.extractException(exchange);
        if (exception != null) {
            throw exception;
        }
        Response providedResponse = exchange.getMessage().getMandatoryBody(Response.class);
        log.warn("注册并提供文档的响应为:");
        var providedReponseEbxml = EbXML30Converters.convert(providedResponse);
//        log.info(XdsRenderingUtils.renderEbxml(providedReponseEbxml));


        log.error("=======开始测试查询======");

        // 设置查询
        QueryRegistry queryRegistry = CreateXdsHelper.createFindDocumentsQuery(provide.getSubmissionSet().getPatientId());
        queryRegistry.setReturnType(QueryReturnType.LEAF_CLASS);
        exchange.getIn().setBody(queryRegistry);

        // 发送请求 到 ITI18, 这里是找到meta data,元数据信息
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


        log.error("=======开始测试取文档 ITI43======");
        // 设置取文档的查询体
        RetrieveDocumentSet retrieveDocumentSet = CreateXdsHelper.createRetrieveDocumentSet(xdsProvidedRegisterDTO);

        // 发送请求
        exchange.getIn().setBody(retrieveDocumentSet);
        String iti43Endpoint = StrUtil.isNotEmpty(xdsProvidedRegisterDTO.getIti43EndpointUrl()) ? xdsProvidedRegisterDTO.getIti43EndpointUrl() : "xds-iti43://localhost:9091/services/xds-iti43";

        exchange = producerTemplate.send(iti43Endpoint, exchange);

        RetrievedDocumentSet retrieveResponse = exchange.getMessage().getMandatoryBody(RetrievedDocumentSet.class);
        log.warn(retrieveResponse.toString());
        log.warn("取的的文件数量为: " + retrieveResponse.getDocuments().size());
        // 查看第一个XML附件中的内容 (即文档内容)
        InputStream inputStream = retrieveResponse.getDocuments().get(0).getDataHandler().getInputStream();
        String data = IOUtils.toString(inputStream);

        log.warn(data);
        System.out.println(data.length());

        // 查看第二个Text附件中的内容 (即文档内容)

        // 设置取文档的查询体
        XdsProvidedRegisterDTO xdsProvidedRegisterDTOText = new XdsProvidedRegisterDTO();
        RetrieveDocumentSet retrieveDocumentSet1 = CreateXdsHelper.createRetrieveDocumentSet(xdsProvidedRegisterDTOText);

        // 发送请求
        exchange.getIn().setBody(retrieveDocumentSet1);

        exchange = producerTemplate.send(iti43Endpoint, exchange);

        RetrievedDocumentSet retrieveResponse1 = exchange.getMessage().getMandatoryBody(RetrievedDocumentSet.class);
        log.warn(retrieveResponse1.toString());
        log.warn("取的的文件数量为: " + retrieveResponse1.getDocuments().size());
        InputStream inputStreamText = retrieveResponse1.getDocuments().get(0).getDataHandler().getInputStream();
        String dataText = IOUtils.toString(inputStreamText);

        log.warn(dataText);
        System.out.println(dataText.length());

    }

    @Override
    public void iti4142AssociationDocument(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {

    }

    @Override
    public void Iti42Register(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {

    }

    @Override
    public void Iti43Retrieve(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {

    }

    @Override
    public void Iti18Query(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {

    }
}
