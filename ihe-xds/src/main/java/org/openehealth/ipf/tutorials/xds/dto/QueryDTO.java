package org.openehealth.ipf.tutorials.xds.dto;

import lombok.Data;

/**
 * 查询ITI18事务,各功能的参数,对应的实体为 QueryRegistry
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/16
 */
@Data
public class QueryDTO {

    private String patientId = "P110";

    private String patAssigningAuthorityId = "1.3.6.1.4.1.21367.2005.13.20.1000";



}
