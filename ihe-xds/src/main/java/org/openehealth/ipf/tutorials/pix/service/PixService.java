package org.openehealth.ipf.tutorials.pix.service;

import org.openehealth.ipf.tutorials.pix.dto.PixPatientFeedDTO;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/6/27
 */
public interface PixService {
    void iti44PatientFeed(PixPatientFeedDTO pixPatientFeedDTO) throws Exception;
}
