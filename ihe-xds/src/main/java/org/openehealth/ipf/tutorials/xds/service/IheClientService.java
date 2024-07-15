package org.openehealth.ipf.tutorials.xds.service;

import org.apache.camel.InvalidPayloadException;
import org.openehealth.ipf.tutorials.xds.dto.XdsProvidedRegisterDTO;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/15
 */
public interface IheClientService {
    void iti4142JustSingleDocument(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) throws Exception;

    void iti4142MultipleDocument(XdsProvidedRegisterDTO xdsProvidedRegisterDTO);

    void iti4142AssociationDocument(XdsProvidedRegisterDTO xdsProvidedRegisterDTO);

    void Iti42Register(XdsProvidedRegisterDTO xdsProvidedRegisterDTO);

    void Iti43Retrieve(XdsProvidedRegisterDTO xdsProvidedRegisterDTO);

    void Iti18Query(XdsProvidedRegisterDTO xdsProvidedRegisterDTO);
}
