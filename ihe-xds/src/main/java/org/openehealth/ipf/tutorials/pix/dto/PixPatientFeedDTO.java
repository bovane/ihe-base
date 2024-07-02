package org.openehealth.ipf.tutorials.pix.dto;

import lombok.Data;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/27
 */
@Data
public class PixPatientFeedDTO {
    private String message;
    private String host = "localhost";
    private Integer port = 9091;
    private String pathAndParameters = "services/xds-iti44-service1";
    private Boolean validate = Boolean.FALSE;
}
