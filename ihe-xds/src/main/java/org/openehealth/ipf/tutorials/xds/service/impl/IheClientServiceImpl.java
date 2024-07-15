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
import org.openehealth.ipf.tutorials.xds.ContentUtils;
import org.openehealth.ipf.tutorials.xds.CreateHelper;
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

        ProvideAndRegisterDocumentSet provide = CreateHelper.createProvideAndRegisterDocumentSet(xdsProvidedRegisterDTO);

        DocumentEntry documentEntry = provide.getDocuments().get(0).getDocumentEntry();

        // 添加额外元数据
        HashMap<String, List<String>> extra = new HashMap<>();
        extra.put("urn:abc", CollUtil.newArrayList("ddd"));
        documentEntry.setExtraMetadata(extra);

        // 添加患者信息
        Identifiable patientId = documentEntry.getPatientId();
        patientId.setId(UUID.randomUUID().toString());
        log.warn(patientId.getId());

        // 提供自定义的data handler 信息
        DataHandler dataHandler = new DataHandler(new PdfDataSource(xdsProvidedRegisterDTO.getFilePath(), xdsProvidedRegisterDTO.getContentType() ,xdsProvidedRegisterDTO.getName()));
        provide.getDocuments().get(0).setDataHandler(dataHandler);

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

        // 测试第一个文档的值是否设置进去
        log.error("测试第一个文档内容是否设置进去");
        log.warn(String.valueOf(provide.getDocuments().size()));
        log.warn(provide.getDocuments().get(0).getDocumentEntry().getEntryUuid());
        log.warn(provide.getDocuments().get(0).getDocumentEntry().getUniqueId());
        log.warn(String.valueOf(provide.getDocuments().get(0).getDocumentEntry().getSize()));

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

        // 查看查询返回结果
        log.warn(queryResponse.toString());
        log.warn(String.valueOf(queryResponse.getDocuments().size()));
        log.warn(String.valueOf(queryResponse.getDocumentEntries().size()));
        log.warn(queryResponse.getDocumentEntries().get(0).getEntryUuid());
        log.warn(String.valueOf(queryResponse.getDocumentEntries().get(0).getSize()));
        log.warn(queryResponse.getDocumentEntries().get(0).getExtraMetadata().toString());

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
