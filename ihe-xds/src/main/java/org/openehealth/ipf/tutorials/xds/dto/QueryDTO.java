package org.openehealth.ipf.tutorials.xds.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询ITI18事务,各功能的参数,对应的实体为 QueryRegistry
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/16
 */
@Data
@ApiModel(description = "ITI18事务文档查询所需要的参数")
public class QueryDTO {

    @ApiModelProperty(value = "Iti18对应的WebService服务的Wsdl")
    private String iti18ServiceWsdl = "http://localhost:8080/services/iti18?wsdl";

    @ApiModelProperty(value = "Iti18对应的WebService服务的命名空间")
    private String iti18ServiceNamespaceUri = "http://service.ihe.imedway.com/";

    @ApiModelProperty(value = "Iti18对应的WebService服务的LocalPart")
    private String iti18ServiceLocalPart = "Iti18PortTypeImplService";

    /**
     * 查询文档元数据所需参数,主要通过患者ID查 文档注册库
     */

    @ApiModelProperty(value = "患者ID")
    private String patientId = "P110";

    @ApiModelProperty(value = "分配患者ID的机构ID,有ID的地方,就需要有AssigningAuthority,二者成对出现")
    private String patAssigningAuthorityId = "1.3.6.1.4.1.21367.2005.13.20.1000";

    @ApiModelProperty(value = "查询HomeCommunityId")
    private String queryHomeCommunityId = "12.21.41";

    @ApiModelProperty(value = "查询文档的状态")
    private String queryStatus = "Approved";

    @ApiModelProperty(value = "查询文档的类型")
    private String queryDocumentEntryType = "stable";

    @ApiModelProperty(value = "文档元数据创建时间")
    private String documentEntryCreateTimeFrom = "";

    @ApiModelProperty(value = "文档元数据创建时间")
    private String documentEntryCreateTimeTO = "";

    @ApiModelProperty(value = "服务开始时间")
    private String serviceStartTimeFrom;
    @ApiModelProperty(value = "服务开始时间")
    private String serviceStartTimeTo;

    @ApiModelProperty(value = "服务停止时间")
    private String serviceStopTimeFrom;

    @ApiModelProperty(value = "服务停止时间")
    private String serviceStopTimeTo;

    @ApiModelProperty(value = "医疗健康类型代码")
    private String  healthcareFacilityTypeCode;

    @ApiModelProperty(value = "查询事件代码")
    private String eventCode = "";

    @ApiModelProperty(value = "文档秘级代码")
    private String confidentialityCode = "";

    @ApiModelProperty(value = "文档作者")
    private String authorPerson = "";

    @ApiModelProperty(value = "格式代码")
    private String formatCode = "";



    /**
     * 查询提交集submissionSet 所需参数,主要通过患者ID等查 文档注册库
     * 注意一些重复参数,如患者ID、author信息,用上面的即可
     */

    @ApiModelProperty(value = "提交集SourceID")
    private String submissionSourceId = "";

    @ApiModelProperty(value = "提交集提交时间")
    private String submissionTimeFrom = "";

    @ApiModelProperty(value = "提交集提交时间")
    private String submissionTimeTo = "";

    @ApiModelProperty(value = "提交集ContentType")
    private String submissionContentType = "";

    /**
     * 查询文件夹所需的参数
     */
    @ApiModelProperty(value = "文件夹最后更新时间")
    private String folderLastUpdateTimeFrom = "";

    @ApiModelProperty(value = "文件夹最后更新时间")
    private String folderLastUpdateTimeTo = "";

    @ApiModelProperty(value = "文件夹代码")
    private String folderCode = "";

    /**
     * 查询联系Association所需的参数
     * 复用,前面的HomeCommunityId
     */
    @ApiModelProperty(value = "Association-UUID")
    private String associationUuid = "";

    /**
     * 获取 DocumentsAndAssociations
     * 复用,前面的HomeCommunityId
     */
    @ApiModelProperty(value = "文档Entry-UUID")
    private String documentEntryUuid = "";

    @ApiModelProperty(value = "文档Entry-UniqueID")
    private String documentEntryUniqueId = "";

    /**
     * 获取 submissionSet所需参数
     * 复用前面的 前面的HomeCommunityId
     */
    @ApiModelProperty(value = "提交集UUID")
    private String submissionSetUuid;

    /**
     * GetFolderAndContents所需参数
     * 复用前面的formatCode、confidentialityCode等代码
     */
    @ApiModelProperty(value = "文件夹UUID")
    private String folderEntryUuid = "";

    @ApiModelProperty(value = "文件夹唯一ID")
    private String folderEntryUniqueId = "";

    @ApiModelProperty(value = "文档类型")
    private String documentEntryType = "";

    /**
     * getFoldersForDocument所需参数
     * 复用前面的documentEntryUuid等代码
     */

    /**
     * getAllQuery所需参数
     * 复用前面的参数
     */


}
