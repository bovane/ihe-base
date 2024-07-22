package org.openehealth.ipf.tutorials.xds.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ITI41和42事务 文档提交和注册的所需参数 对应的实体为 ProvideAndRegisterDocumentSet
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/16
 */
@Data
@ApiModel(description = "ITI41和42事务 文档提交和注册的所需参数")
public class ProvidedRegisterDTO {


    @ApiModelProperty(value = "Iti41对应的WebService服务的Wsdl")
    private String iti41ServiceWsdl = "http://localhost:8080/services/iti41?wsdl";

    @ApiModelProperty(value = "Iti41对应的WebService服务的命名空间")
    private String iti41ServiceNamespaceUri = "http://service.ihe.imedway.com/";

    @ApiModelProperty(value = "Iti41对应的WebService服务的LocalPart")
    private String iti41ServiceLocalPart = "Iti41PortTypeImplService";

    /**
     * 患者ID + "AssigningAuthority"标准,共同组成 Identifiable 对象
     * AssigningAuthority = universalId + ISO
     * 其中ISO标准是默认的,写死,不需要修改
     * "AssigningAuthority"描述了负责分配某种标识符的权威机构或组织
     * 如 : P110^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO
     * 因此有ID的地方,就需要有AssigningAuthority,二者成对出现
     */
    @ApiModelProperty(value = "患者ID")
    private String patientId = "P110";

    @ApiModelProperty(value = "分配患者ID的机构ID,有ID的地方,就需要有AssigningAuthority,二者成对出现")
    private String patAssigningAuthorityId = "1.3.6.1.4.1.21367.2005.13.20.1000";

    @ApiModelProperty(value = "提交集接受者HomeCommunityId")
    private String targetHomeCommunityId = "urn:oid:1.2.3.4.5.6.2333.23";

    /**
     * 组成SubmissionSet对象 需要的一些数据
     */

    // 添加接收者 Recipient 信息, Recipient 由 Person对象、Organization对象、Telecom对象组装
    @ApiModelProperty(value = "提交集接收者ID")
    private String submitRecipientPersonId = "1";
    @ApiModelProperty(value = "提交集接收者名称")
    private String submitRecipientPersonName = "zjTest";

    @ApiModelProperty(value = "提交集接收者组织名称")
    private String submitRecipientOrganizationName = "Ihe-ZJ";
    @ApiModelProperty(value = "提交集接收者组织ID")
    private String submitRecipientOrganizationId = "1.1"; // 用于组装 AssigningAuthority

    // 添加 Author,即提交集的Author信息 AuthorPerson 对象 = Identifiable + Name 对象
    @ApiModelProperty(value = "提交集作者ID")
    private String submitAuthorPersonId = "Mediway";
    @ApiModelProperty(value = "提交集作者ID的分配机构ID")
    private String submitAuthorPersonAssigningAuthorityId = "1.2.3.4.5.6.7.8.9";
    @ApiModelProperty(value = "提交集作者名称")
    private String submitAuthorPersonName = "dhcc";

    @ApiModelProperty(value = "提交集备注")
    private String submitComments = "IHE-DHCC-comments1";

    // 添加 ContentTypeCode = code + displayName + schemeName
    @ApiModelProperty(value = "提交集类型代码")
    private String submissionContentTypeCode = "code1";
    @ApiModelProperty(value = "提交集类型名称")
    private String submissionContentTypeCodeDisplayName = "code1";
    @ApiModelProperty(value = "提交集类型主题")
    private String submissionContentTypeCodeSchemeName = "scheme1";

    // 添加 SubmissionSet entry uuid 等信息 (关键元数据信息)
    @ApiModelProperty(value = "提交集UUID(当作提交集的名称)")
    private String submissionSetEntryUuid = "submissionSet01";
    @ApiModelProperty(value = "提交集SourceID")
    private String submissionSetSourceId = "1.2.3";
    @ApiModelProperty(value = "提交集唯一ID")
    private String submissionUniqueId = "1.123";
    @ApiModelProperty(value = "提交集HomeCommunityId")
    private String submissionHomeCommunityId = "urn:oid:1.2.3.4.5.6.2333.23";

    // 添加 其它提交集 元数据信息
    @ApiModelProperty(value = "提交集提交时间")
    private String submissionTime = "1980";
    @ApiModelProperty(value = "提交集的Title")
    private String submissionTitle = "Submission Set 01";
    @ApiModelProperty(value = "提交集语言")
    private String submissionLang = "en-US";
    @ApiModelProperty(value = "提交集字符集")
    private String submissionCharset = "UTF-8";

    /**
     * 创建 DocumentEntry 所需的值,如文档作者、地址、患者基本信息等
     * 注意这里的患者ID,是全局患者ID,在ProvidedRegister均保持一致
     */

    // 添加文档作者信息,这部分不太重要
    @ApiModelProperty(value = "文档作者名称")
    private String docFamilyName = "Norbi";
    @ApiModelProperty(value = "文档作者ID")
    private String docAuthorId = "id2";
    @ApiModelProperty(value = "文档作者ID分配机构ID")
    private String docAuthorAssigningAuthorityId = "1.2";
    @ApiModelProperty(value = "文档作者机构名称")
    private String docAuthorOrganizationName = "imediway";
    @ApiModelProperty(value = "文档作者角色ID")
    private String docAuthorRoleId = "role1";
    @ApiModelProperty(value = "文档作者角色ID分配机构ID")
    private String docAuthorRoleAssigningAuthorityId = "1.2.3.1";
    @ApiModelProperty(value = "文档作者专业领域ID")
    private String docAuthorSpecialtyId = "spec1";
    @ApiModelProperty(value = "文档作者专业领域ID分配机构ID")
    private String docAuthorSpecialtyAssigningAuthorityId  = "1.2.3.3";
    @ApiModelProperty(value = "文档作者联系方式")
    private String docAuthorTelecom = "author2@acme.org";

    // 添加源患者的相关信息
    @ApiModelProperty(value = "文档源患者地址")
    private String docPatientAddress = "hier";
    @ApiModelProperty(value = "文档源患者出生日期")
    private String docPatientBirthDate = "1980";
    @ApiModelProperty(value = "文档源患者性别")
    private String docPatientGender = "M";
    @ApiModelProperty(value = "文档源患者名称")
    private String docPatientName = "Susi";
    @ApiModelProperty(value = "Source患者ID")
    private String docSourcePatientId = "source";
    @ApiModelProperty(value = "Source患者ID的分配机构的ID")
    private String docSourcePatientIdAssigningAuthorityId = "4.1";


    // 添加DocEntry 其它元信息;如文档分类级别等代码 这些不太重要,创建时保持默认即可
    @ApiModelProperty(value = "文档分类级别代码")
    private String docClassCode = "code2";
    @ApiModelProperty(value = "文档分类级别名称")
    private String docClassDisplayName = "code2";
    @ApiModelProperty(value = "文档分类级别主题")
    private String docClassSchemeName = "scheme2";

    @ApiModelProperty(value = "文档保密级别代码")
    private String docConfidentialityCode = "code8";
    @ApiModelProperty(value = "文档保密级别名称")
    private String docConfidentialityDisplayName = "code8";
    @ApiModelProperty(value = "文档保密级别主题")
    private String docConfidentialitySchemeName = "scheme8";

    @ApiModelProperty(value = "文档事件代码")
    private String docEventCode = "code9";
    @ApiModelProperty(value = "文档事件名称")
    private String docEventDisplayName = "code9";
    @ApiModelProperty(value = "文档事件主题")
    private String docEventSchemeName = "scheme9";

    @ApiModelProperty(value = "文档格式代码")
    private String docFormatCode = "code3";
    @ApiModelProperty(value = "文档格式名称")
    private String docFormatDisplayName = "code3";
    @ApiModelProperty(value = "文档格式主题")
    private String docFormatSchemeName = "scheme3";



    // 添加DocEntry 关键元信息
    @ApiModelProperty(value = "文档备注")
    private String docComments = "docEntryTestComments";
    @ApiModelProperty(value = "文档创建时间")
    private String docCreateTime = "1981";
    @ApiModelProperty(value = "文档语言代码")
    private String docLanguageCode = "en-US";
    @ApiModelProperty(value = "文档MimeType")
    private String docMimeType = "application/octet-stream";
    // 唯一标识文档
    @ApiModelProperty(value = "文档UUID(可当作文档名称)")
    private String docEntryUuid = "document01";
    @ApiModelProperty(value = "文档存储库唯一ID")
    private String docRepositoryUniqueId = "1.2.3.4";
    @ApiModelProperty(value = "文档唯一ID")
    private String docEntryUniqueId = "32848902348";
    @ApiModelProperty(value = "文档服务开始时间")
    private String docServiceStartTime = "198012";
    @ApiModelProperty(value = "文档服务停止时间")
    private String docServiceStopTime = "198112";
    @ApiModelProperty(value = "文档标题")
    private String docTitle = "Document 01";
    @ApiModelProperty(value = "文档标题语言")
    private String docTitleLang = "en-US";
    @ApiModelProperty(value = "文档标题字符集")
    private String docTitleCharset = "UTF-8";
    @ApiModelProperty(value = "文档类型代码")
    private String docTypeCode = "code6";
    @ApiModelProperty(value = "文档类型名称")
    private String docTypeDisplayName = "code6";
    @ApiModelProperty(value = "文档类型主题")
    private String docTypeSchemeName = "scheme06";
    @ApiModelProperty(value = "文档URI")
    private String docUri = "http://hierunten.com";
    // 这里ID 和 AssigningAuthority都是成对出现的
    // 因为id是后面 AssigningAuthority 这个组织分配的
    @ApiModelProperty(value = "文档引用ID")
    private String docReferenceId = "ref-id-1";
    @ApiModelProperty(value = "文档引用ID分配机构命名空间ID")
    private String docReferenceCXiAssigningAuthorityNamespaceId = "ABCD";
    @ApiModelProperty(value = "文档分配机构ID")
    private String docReferenceCXiAssigningAuthorityId = "1.1.2.3";


    /**
     * 创建 Folder 所需的值
     */
    // Folder Code 信息 = code + displayName + schemeName
    @ApiModelProperty(value = "文件夹代码")
    private String folderCode = "code7";
    @ApiModelProperty(value = "文件夹代码名称")
    private String folderCodeDisplayName = "code7";
    @ApiModelProperty(value = "文件夹代码主题")
    private String folderCodeSchemeName = "scheme7";

    // Folder 其它元数据信息
    @ApiModelProperty(value = "文件夹备注")
    private String folderComments = "Folder-Test comments";
    @ApiModelProperty(value = "文件夹最后更新时间")
    private String folderLastUpdateTime = "19820910121315";
    @ApiModelProperty(value = "文件夹标题")
    private String folderTitle = "Folder 01";
    @ApiModelProperty(value = "文件夹语言")
    private String folderLang = "en-US";
    @ApiModelProperty(value = "文件夹字符集")
    private String folderCharset = "UTF-8";

    // Folder 关键元数据信息
    @ApiModelProperty(value = "文件夹UUID")
    private String folderEntryUuid = "folder01";
    @ApiModelProperty(value = "文件夹唯一ID")
    private String folderEntryUniqueId = "1.48574589";


    /**
     * 创建联系 createAssociationDocEntryToSubmissionSet
     * 创建文档 Entry 与 SubmissionSet 之间的联系 HasMember 类型
     * */
    @ApiModelProperty(value = "文档提交集源UUID-为提交集的UUID")
    private String docEntrySubmissionSetSourceUuid = "submissionSet01";
    @ApiModelProperty(value = "文档提交集目的UUID-为文档的UUID")
    private String docEntrySubmissionSetTargetUuid = "document01";
    @ApiModelProperty(value = "文档-提交集关联的UUID")
    private String docEntrySubmissionSetAssUuid = "docAss";

    /**
     * 创建联系 createAssociationFolderToSubmissionSet
     * 创建文件夹 Folder 与 SubmissionSet 之间的联系 HasMember 类型
     * */
    @ApiModelProperty(value = "文件夹提交集源UUID-为提交集的UUID")
    private String folderSubmissionSetSourceUuid = "submissionSet01";
    @ApiModelProperty(value = "文件夹提交集目的UUID-为文件夹UUID")
    private String folderSubmissionSetTargetUuid = "folder01";
    @ApiModelProperty(value = "文件夹-提交集关联的UUID")
    private String folderSubmissionSetAssUuid = "folderAss";

    /**
     * 创建联系 createAssociationDocEntryToFolder
     * 创建文件夹 Folder 与 DocEntry 之间的联系 HasMember 类型
     * */
    @ApiModelProperty(value = "文档文件夹源UUID-为文件夹UUID")
    private String folderDocEntrySourceUuid = "folder01";
    @ApiModelProperty(value = "文档文件夹目的UUID-为文件UUID")
    private String folderDocEntryTargetUuid = "document01";
    @ApiModelProperty(value = "文档-文件夹关联的UUID")
    private String folderDocEntryAssUuid = "docFolderAss";

    /**
     * 文件内容 —— 文件相关信息
     */
    @ApiModelProperty(value = "文件路径")
    private String filePath = "/Users/bovane/Documents/hos-app/logs/test.txt";
    @ApiModelProperty(value = "contentType必须要符合标准")
    private String contentType = "test/plain";
    @ApiModelProperty(value = "文件流名称")
    private String name = "testText";

}
