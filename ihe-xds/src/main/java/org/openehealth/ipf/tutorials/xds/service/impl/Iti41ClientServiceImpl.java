package org.openehealth.ipf.tutorials.xds.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.ProvideAndRegisterDocumentSetRequestType;
import org.openehealth.ipf.commons.ihe.xds.core.requests.ProvideAndRegisterDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.rs.RegistryResponseType;
import org.openehealth.ipf.commons.ihe.xds.iti41.Iti41PortType;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.EbXML30Converters;
import org.openehealth.ipf.tutorials.xds.dto.ProvidedRegisterDTO;
import org.openehealth.ipf.tutorials.xds.helper.Iti41ProvidedRegisterHelper;
import org.openehealth.ipf.tutorials.xds.service.Iti41ClientService;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/18
 */
@Service
@Slf4j
public class Iti41ClientServiceImpl implements Iti41ClientService {
    @Override
    public void CreateProvidedAndRegister(ProvidedRegisterDTO providedRegisterDTO) throws MalformedURLException {
        String iti41ServiceWsdl = providedRegisterDTO.getIti41ServiceWsdl();
        String iti41ServiceNamespaceUri = providedRegisterDTO.getIti41ServiceNamespaceUri();
        String iti41ServiceLocalPart = providedRegisterDTO.getIti41ServiceLocalPart();

        // 创建WebService服务
        URL wsdlUrl = new URL(iti41ServiceWsdl);
        QName serviceName = new QName(iti41ServiceNamespaceUri, iti41ServiceLocalPart);
        javax.xml.ws.Service service = javax.xml.ws.Service.create(wsdlUrl, serviceName);
        Iti41PortType myWebService = service.getPort(Iti41PortType.class);

        // 创建提交集
        ProvideAndRegisterDocumentSet provide = Iti41ProvidedRegisterHelper.createProvideAndRegisterDocumentSet(providedRegisterDTO);

        // 测试文档的值是否正确
        log.error("测试文档内容是否设置进去");
        log.warn(String.valueOf(provide.getDocuments().size()));
        log.warn(provide.getDocuments().get(0).getDocumentEntry().getEntryUuid());
        log.warn(provide.getDocuments().get(0).getDocumentEntry().getUniqueId());
        log.warn(String.valueOf(provide.getDocuments().get(0).getDocumentEntry().getSize()));

        // 转换类型,符合WebService要求,并发送请求
        ProvideAndRegisterDocumentSetRequestType requestType = EbXML30Converters.convert(provide);
        RegistryResponseType registryResponseType = myWebService.documentRepositoryProvideAndRegisterDocumentSetB(requestType);
        log.info("---registryResponseType---" + registryResponseType.getStatus());

    }
}
