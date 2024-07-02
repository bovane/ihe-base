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
import org.openehealth.ipf.commons.ihe.xds.core.SampleData;
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
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.XdsRenderingUtils;
import org.openehealth.ipf.tutorials.xds.ContentUtils;
import org.openehealth.ipf.tutorials.xds.dto.XdsProvidedRegisterDTO;
import org.openehealth.ipf.tutorials.xds.service.XdsClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
        // for each call
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.setPattern(ExchangePattern.InOut);

        // generate documentEntry
        ProvideAndRegisterDocumentSet provide = SampleData.createProvideAndRegisterDocumentSet();
        DocumentEntry documentEntry = provide.getDocuments().get(0).getDocumentEntry();

        Identifiable patientId = documentEntry.getPatientId();
        HashMap<String, List<String>> extra = new HashMap<>();
        extra.put("urn:abc", CollUtil.newArrayList("ddd"));
        documentEntry.setExtraMetadata(extra);
        patientId.setId(UUID.randomUUID().toString());
        log.warn(patientId.getId());
        documentEntry.setUniqueId("4.3.2.1");
        log.error(provide.getDocuments().get(0).getContent(DataHandler.class).toString());
        log.error("xxxl");
        documentEntry.setHash(String.valueOf(ContentUtils.sha1(provide.getDocuments().get(0).getContent(DataHandler.class))));
        documentEntry.setSize(Long.valueOf(String.valueOf(ContentUtils.size(provide.getDocuments().get(0).getContent(DataHandler.class)))));
        log.warn(documentEntry.getSize().toString());
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
//        log.error(XdsRenderingUtils.render(exchange));
        Response response1 = exchange.getMessage().getMandatoryBody(Response.class);
        log.warn(response1.toString());


        log.error("=======开始测试查询======");

        // set up a query
        FindDocumentsQuery query = new FindDocumentsQuery();
        query.setPatientId(patientId);
        log.info(query.getPatientId().getId());
        // setup status
        List<AvailabilityStatus> statuses = CollUtil.newArrayList();
        statuses.add(AvailabilityStatus.APPROVED);
        query.setStatus(statuses);

        QueryRegistry queryRegistry = new QueryRegistry(query);
        queryRegistry.setReturnType(QueryReturnType.LEAF_CLASS);
        exchange.getIn().setBody(queryRegistry);   // "request" is what you created

        String endpointUrl = StrUtil.isNotEmpty(xdsProvidedRegisterDTO.getIti18EndpointUrl()) ? xdsProvidedRegisterDTO.getIti18EndpointUrl() : "xds-iti18://localhost:9091/services/xds-iti18";
        exchange = producerTemplate.send(endpointUrl, exchange);
//        log.error(XdsRenderingUtils.render(exchange));

        if (exception != null) {
            throw exception;
        }
        QueryResponse queryResponse = exchange.getMessage().getMandatoryBody(QueryResponse.class);

        // send the query to endpoints
        log.warn(queryResponse.toString());
        log.warn(String.valueOf(queryResponse.getDocuments().size()));
        log.warn(String.valueOf(queryResponse.getDocumentEntries().size()));
        log.warn(queryResponse.getDocumentEntries().get(0).getEntryUuid());
        log.warn(String.valueOf(queryResponse.getDocumentEntries().get(0).getSize()));
        log.warn(queryResponse.getDocumentEntries().get(0).getExtraMetadata().toString());
    }
}
