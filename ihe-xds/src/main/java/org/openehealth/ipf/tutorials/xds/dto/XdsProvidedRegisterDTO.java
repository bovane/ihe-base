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
    private String patientId;
    private String assigningAuthorityId;

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
    // 添加组织
    private String organizationName;
    private String idNumber;
    // 添加作者信息

    /**
     * 创建 DocumentEntry 所需的值
     */
    private String repositoryUniqueId;
    private String docEntryUniqueId;

    /**
     * 创建 Folder 所需的值
     */
    private String folderUuid;
    private String folderUniqueId;

    /**
     * 创建联系 createAssociationDocEntryToSubmissionSet
     * 创建文档 Entry 与 SubmissionSet 之间的联系
     * */
    private String docEntrySourceUuid;
    private String docEntryTargetUuid;
    private String docEntryAssUuid;

    /**
     * 创建联系 createAssociationFolderToSubmissionSet
     * 创建文件夹 Folder 与 SubmissionSet 之间的联系
     * */
    private String folderSourceUuid;
    private String folderTargetUuid;
    private String folderAssUuid;

    /**
     * 创建联系 createAssociationDocEntryToFolder
     * 创建文件夹 Folder 与 DocEntry 之间的联系
     * */
    private String folderDocEntrySourceUuid;
    private String folderDocEntryTargetUuid;
    private String folderDocEntryAssUuid;




    private String document;
}
