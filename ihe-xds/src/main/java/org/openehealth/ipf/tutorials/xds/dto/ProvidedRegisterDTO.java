package org.openehealth.ipf.tutorials.xds.dto;

import lombok.Data;

/**
 * ITI41和42事务 文档提交和注册的所需参数 对应的实体为 ProvideAndRegisterDocumentSet
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/16
 */
@Data
public class ProvidedRegisterDTO {

    /**
     * 患者ID + "AssigningAuthority"标准,共同组成 Identifiable 对象
     * AssigningAuthority = universalId + ISO
     * 其中ISO标准是默认的,写死,不需要修改
     * "AssigningAuthority"描述了负责分配某种标识符的权威机构或组织
     * 如 : P110^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO
     * 因此有ID的地方,就需要有AssigningAuthority,二者成对出现
     */
    private String patientId = "P110";

    private String patAssigningAuthorityId = "1.3.6.1.4.1.21367.2005.13.20.1000";

    private String targetHomeCommunityId = "urn:oid:1.2.3.4.5.6.2333.23";

    /**
     * 组成SubmissionSet对象 需要的一些数据
     */

    // 添加接收者 Recipient 信息, Recipient 由 Person对象、Organization对象、Telecom对象组装

    private String submitRecipientPersonId = "1";
    private String submitRecipientPersonName = "zjTest";

    private String submitOrganizationName = "Ihe-ZJ";
    private String submitOrganizationId = "1.1"; // 用于组装 AssigningAuthority

    // 添加 Author,即提交集的Author信息 AuthorPerson 对象 = Identifiable + Name 对象
    private String submitAuthorPersonId = "Mediway";
    private String submitAuthorPersonAssigningAuthorityId = "1.2.3.4.5.6.7.8.9";

    private String submitAuthorPersonName = "dhcc";

    private String submitComments = "IHE-DHCC-comments1";

    // 添加 ContentTypeCode = code + displayName + schemeName
    private String submissionContentTypeCode = "code1";
    private String submissionContentTypeCodeDisplayName = "code1";
    private String submissionContentTypeCodeSchemeName = "scheme1";

    // 添加 SubmissionSet entry uuid 等信息 (关键元数据信息)
    private String submissionSetEntryUuid = "submissionSet01";
    private String submissionSetSourceId = "1.2.3";
    private String submissionUniqueId = "1.123";
    private String submissionHomeCommunityId = "urn:oid:1.2.3.4.5.6.2333.23";

    // 添加 其它提交集 元数据信息
    private String submissionTime = "1980";
    private String submissionTitle = "Submission Set 01";
    private String submissionTang = "en-US";
    private String submissionCharset = "UTF-8";

    /**
     * 创建 DocumentEntry 所需的值,如文档作者、地址、患者基本信息等
     * 注意这里的患者ID,是全局患者ID,在ProvidedRegister均保持一致
     */

    // 添加文档作者信息,这部分不太重要
    private String docFamilyName = "Norbi";
    private String docAuthorId = "id2";
    private String docAuthorAssigningAuthorityId = "1.2";
    private String docAuthorOrganizationName = "imediway";
    private String docAuthorRoleId = "role1";
    private String docAuthorRoleAssigningAuthorityId = "1.2.3.1";
    private String docAuthorSpecialtyId = "spec1";
    private String docAuthorSpecialtyAssigningAuthorityId  = "1.2.3.3";
    private String docAuthorTelecom = "author2@acme.org";

    // 添加源患者的相关信息
    private String docPatientAddress = "hier";
    private String docPatientBirthDate = "1980";
    private String docPatientGender = "M";
    private String docPatientName = "Susi";

    // 添加DocEntry 其它元信息;如文档分类级别等代码 这些不太重要,创建时保持默认即可
    private String docClassCode = "code2";
    private String docClassDisplayName = "code2";
    private String docClassSchemeName = "scheme2";

    // 添加DocEntry 关键元信息
    private String docComments = "docEntryTestComments";

    private String docCreateTime = "1981";

    private String docLanguageCode = "en-US";
    private String docMimeType = "application/octet-stream";
    // 唯一标识文档
    private String docEntryUuid = "document01";
    private String docRepositoryUniqueId = "1.2.3.4";
    private String docEntryUniqueId = "32848902348";

    private String docServiceStartTime = "198012";
    private String docServiceStopTime = "198112";
    private String docTitle = "Document 01";
    private String docTitleLang = "en-US";
    private String docTitleCharset = "UTF-8";
    private String docTypeCode = "code6";
    private String docTypeDisplayName = "code6";
    private String docTypeSchemeName = "scheme06";
    private String docUri = "http://hierunten.com";
    // 这里ID 和 AssigningAuthority都是成对出现的
    // 因为id是后面 AssigningAuthority 这个组织分配的
    private String docReferenceId = "ref-id-1";
    private String docReferenceCXiAssigningAuthorityNamespaceId = "ABCD";
    private String docReferenceCXiAssigningAuthorityId = "1.1.2.3";




    /**
     * 创建 Folder 所需的值
     */
    // Folder Code 信息 = code + displayName + schemeName
    private String folderCode = "code7";
    private String folderCodeDisplayName = "code7";
    private String folderCodeSchemeName = "scheme7";

    // Folder 其它元数据信息
    private String folderComments = "Folder-Test comments";
    private String folderLastUpdateTime = "19820910121315";
    private String folderTitle = "Folder 01";
    private String folderLang = "en-US";
    private String folderCharset = "UTF-8";

    // Folder 关键元数据信息
    private String folderEntryUuid = "folder01";
    private String folderEntryUniqueId = "1.48574589";


    /**
     * 创建联系 createAssociationDocEntryToSubmissionSet
     * 创建文档 Entry 与 SubmissionSet 之间的联系 HasMember 类型
     * */
    private String docEntrySourceUuid = "submissionSet01";
    private String docEntryTargetUuid = "document01";
    private String docEntryAssUuid = "docAss";

    /**
     * 创建联系 createAssociationFolderToSubmissionSet
     * 创建文件夹 Folder 与 SubmissionSet 之间的联系 HasMember 类型
     * */
    private String folderSourceUuid = "submissionSet01";
    private String folderTargetUuid = "folder01";
    private String folderAssUuid = "folderAss";

    /**
     * 创建联系 createAssociationDocEntryToFolder
     * 创建文件夹 Folder 与 DocEntry 之间的联系 HasMember 类型
     * */
    private String folderDocEntrySourceUuid = "folder01";
    private String folderDocEntryTargetUuid = "document01";
    private String folderDocEntryAssUuid = "docFolderAss";

}
