package org.openehealth.ipf.tutorials.xds.helper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.openehealth.ipf.commons.ihe.xds.core.requests.DocumentReference;
import org.openehealth.ipf.commons.ihe.xds.core.requests.RetrieveDocumentSet;
import org.openehealth.ipf.tutorials.xds.dto.RetrieveDTO;
import org.openehealth.ipf.tutorials.xds.dto.XdsProvidedRegisterDTO;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/22
 */
@Slf4j
@Data
public abstract class Iti43RetrieveHelper {

    /***
     * 根据文档ID和存储库ID获取文件内容
     *
     * @author bovane
     * [retrieveDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.requests.RetrieveDocumentSet
     */
    public static RetrieveDocumentSet createRetrieveDocumentSet(RetrieveDTO retrieveDTO) {
        RetrieveDocumentSet request = new RetrieveDocumentSet();
        DocumentReference documentReference = new DocumentReference();
        documentReference.setDocumentUniqueId(retrieveDTO.getDocumentUniqueId());
        documentReference.setRepositoryUniqueId(retrieveDTO.getRepositoryUniqueId());
        request.getDocuments().add(documentReference);
        return request;
    }
}
