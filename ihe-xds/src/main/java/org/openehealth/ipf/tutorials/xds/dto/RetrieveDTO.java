package org.openehealth.ipf.tutorials.xds.dto;

import lombok.Data;

/** ITI43事务,获取文档内容, 对应的实体为 RetrieveDocumentSet
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/16
 */
@Data
public class RetrieveDTO {

    /**
     * 用这三个参数,可以从存储库中,取一个文档
     */
    private String repositoryUniqueId = "";
    private String documentUniqueId = "";
    private String homeCommunityId = "";

}
