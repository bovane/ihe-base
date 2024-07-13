package org.openehealth.ipf.tutorials.xds.dto;

import lombok.Data;

/**
 * @author bovane bovane.ch@gmail.com
 * @date 2024/2/20
 */
@Data
public class XdsProvidedRegisterDTO {
    /**
     * 患者ID + assigningAuthorityId + ISO 标准,共同组成 Identifiable 对象
     * 如 : P110^^^&1.3.6.1.4.1.21367.2005.13.20.1000&ISO
     */
    private String patientId = "id3";
    private String assigningAuthorityId = "1.3";

    /**
     * 服务终端URL
     * */
    private String iti18EndpointUrl;
    private String iti41EndpointUrl;
    private String iti42EndpointUrl;
    private String iti43EndpointUrl;

    /**
     * 创建 SubmissionSet 所需的值
     */

    private String organizationName = "org";
    private String idNumber = "123456";

    // 添加AssigningAuthority信息
    private String universalId = "1.1";
    private String universalIdType = "ISO";

    // 添加Author信息
    private String authorId = "id1";

    // 添加 SubmissionSet entry uuid
    private String submissionSetEntryUuid = "submissionSet01";
    private String submissionSetSourceId = "1.2.3";
    private String submissionUniqueId = "1.123";
    private String submissionHomeCommunityId = "urn:oid:1.2.3.4.5.6.2333.23";

    /**
     * 创建 DocumentEntry 所需的值
     */
    private String docEntryUuid = "document01";
    private String repositoryUniqueId = "1.2.3.4";
    private String docEntryUniqueId = "32848902348";

    /**
     * 创建 Folder 所需的值
     */
    private String folderUuid = "folder01";

    private String folderUniqueId = "1.48574589";

    /**
     * 创建联系 createAssociationDocEntryToSubmissionSet
     * 创建文档 Entry 与 SubmissionSet 之间的联系
     * */
    private String docEntrySourceUuid = "submissionSet01";
    private String docEntryTargetUuid = "document01";
    private String docEntryAssUuid = "docAss";

    /**
     * 创建联系 createAssociationFolderToSubmissionSet
     * 创建文件夹 Folder 与 SubmissionSet 之间的联系
     * */
    private String folderSourceUuid = "submissionSet01";
    private String folderTargetUuid = "folder01";
    private String folderAssUuid = "folderAss";

    /**
     * 创建联系 createAssociationDocEntryToFolder
     * 创建文件夹 Folder 与 DocEntry 之间的联系
     * */
    private String folderDocEntrySourceUuid = "folder01";
    private String folderDocEntryTargetUuid = "document01";
    private String folderDocEntryAssUuid = "docFolderAss";


    private String targetHomeCommunityId = "urn:oid:1.2.3.4.5.6.2333.23";


    private String document;
}
