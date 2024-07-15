package org.openehealth.ipf.tutorials.xds.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.support.DefaultExchange;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.AvailabilityStatus;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Identifiable;
import org.openehealth.ipf.commons.ihe.xds.core.requests.ProvideAndRegisterDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.requests.QueryRegistry;
import org.openehealth.ipf.commons.ihe.xds.core.requests.query.FindDocumentsQuery;
import org.openehealth.ipf.commons.ihe.xds.core.requests.query.QueryReturnType;
import org.openehealth.ipf.commons.ihe.xds.core.responses.QueryResponse;
import org.openehealth.ipf.commons.ihe.xds.core.responses.Response;
import org.openehealth.ipf.platform.camel.core.util.Exchanges;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.EbXML30Converters;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.XdsRenderingUtils;
import org.openehealth.ipf.tutorials.xds.ContentUtils;
import org.openehealth.ipf.tutorials.xds.CreateHelper;
import org.openehealth.ipf.tutorials.xds.CreateXdsHelper;
import org.openehealth.ipf.tutorials.xds.XmlUtil;
import org.openehealth.ipf.tutorials.xds.datasource.PdfDataSource;
import org.openehealth.ipf.tutorials.xds.dto.XdsProvidedRegisterDTO;
import org.openehealth.ipf.tutorials.xds.service.IheClientService;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

        // 发送请求 到 ITI18
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
    }

    @Override
    public void iti4142MultipleDocument(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {

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
