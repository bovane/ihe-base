package org.openehealth.ipf.tutorials.xds.service;


import org.openehealth.ipf.tutorials.xds.dto.XdsProvidedRegisterDTO;

/**
 * @author bovane bovane.ch@gmail.com
 * @date 2024/2/20
 */
public interface XdsClientService {
    void iti4142ProvidedAndRegister(XdsProvidedRegisterDTO xdsProvidedRegisterDTO);

    void Iti42Register(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) throws Exception;

    void Iti43Retrieve(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) throws Exception;

    void Iti18Query(XdsProvidedRegisterDTO xdsProvidedRegisterDTO);
}
