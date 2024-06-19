package org.openehealth.ipf.tutorials.xds.dto;

import lombok.Data;

/**
 * @author bovane bovane.ch@gmail.com
 * @date 2024/2/20
 */
@Data
public class XdsProvidedRegisterDTO {
    private String patientId;
    private String assigningAuthorityId;

    private String iti18EndpointUrl;
    private String iti41EndpointUrl;
    private String iti42EndpointUrl;
    private String iti43EndpointUrl;

    private String document;
}
