package org.openehealth.ipf.tutorials.xds.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** ITI43事务,获取文档内容, 对应的实体为 RetrieveDocumentSet
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/16
 */
@Data
public class RetrieveDTO {

    @ApiModelProperty(value = "Iti43对应的WebService服务的Wsdl")
    private String iti43ServiceWsdl = "http://localhost:8080/services/iti43?wsdl";

    @ApiModelProperty(value = "Iti43对应的WebService服务的命名空间")
    private String iti43ServiceNamespaceUri = "http://service.ihe.imedway.com/";

    @ApiModelProperty(value = "Iti43对应的WebService服务的LocalPart")
    private String iti43ServiceLocalPart = "Iti43PortTypeImplService";

    /**
     * 用这三个参数,可以从存储库中,取一个文档
     */
    @ApiModelProperty(value = "存储库唯一ID")
    private String repositoryUniqueId = "";
    @ApiModelProperty(value = "文档唯一ID")
    private String documentUniqueId = "";
    @ApiModelProperty(value = "HomeCommunityID")
    private String homeCommunityId = "";

    @ApiModelProperty(value = "存储库URL")
    private String repositoryUrl;


}
