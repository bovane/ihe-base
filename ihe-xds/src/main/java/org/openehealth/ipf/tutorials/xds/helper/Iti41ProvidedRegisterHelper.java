package org.openehealth.ipf.tutorials.xds.helper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.openehealth.ipf.commons.ihe.ws.utils.LargeDataSource;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.*;
import org.openehealth.ipf.commons.ihe.xds.core.requests.ProvideAndRegisterDocumentSet;
import org.openehealth.ipf.tutorials.xds.ContentUtils;
import org.openehealth.ipf.tutorials.xds.datasource.CustomDataSource;
import org.openehealth.ipf.tutorials.xds.dto.ProvidedRegisterDTO;

import javax.activation.DataHandler;

/**
 * 用于创建Iti41客户端所需的实体类
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/19
 */
@Data
@Slf4j
public abstract class Iti41ProvidedRegisterHelper {

    /***
     * 加载本地文件,作为文件流传输
     *
     * @author bovane
     * [providedRegisterDTO]
     * @return javax.activation.DataHandler
     */
    public static DataHandler createDataHandler(ProvidedRegisterDTO providedRegisterDTO) {
        return new DataHandler(new CustomDataSource(providedRegisterDTO.getFilePath(),providedRegisterDTO.getContentType(),providedRegisterDTO.getName()));
    }

    /***
     * 创建一个完整的ProvideAndRegisterDocumentSet,包括对应的Association关系
     *
     * @author bovane
     * [providedRegisterDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.requests.ProvideAndRegisterDocumentSet
     */
    public static ProvideAndRegisterDocumentSet createProvideAndRegisterDocumentSet(ProvidedRegisterDTO providedRegisterDTO) {
        // 创建患者标识信息
        Identifiable patientID = new Identifiable(providedRegisterDTO.getPatientId(), new AssigningAuthority(providedRegisterDTO.getPatAssigningAuthorityId()));
        // 创建提交集合信息
        SubmissionSet submissionSet = createSubmissionSet(patientID, providedRegisterDTO);
        // 创建DocumentEntry
        DocumentEntry docEntry = createDocumentEntry(patientID, providedRegisterDTO);
        // 创建文件夹
        Folder folder = createFolder(patientID, providedRegisterDTO);
        // 创建关联关系
        Association docAssociation = createAssociationDocEntryToSubmissionSet(providedRegisterDTO);
        Association folderAssociation = createAssociationFolderToSubmissionSet(providedRegisterDTO);
        Association docFolderAssociation = createAssociationDocEntryToFolder(providedRegisterDTO);
        // 创建 DataHandler,即传输文档内容, content 的内容来自 DataHandler
        DataHandler dataHandler = createDataHandler(providedRegisterDTO);
        Document doc = new Document(docEntry, dataHandler);

        // 计算文档的Hash值和Size
        // 计算文档的Hash 和 Size
        // 一个Document对象是 由 DocumentEntry 对象以及一个DataHandler对象
        // 在 Apache Camel 中, DataHandler 是一个非常重要的概念,它代表了一个通用的数据容器,
        // 可以包含各种类型的数据,比如文件、二进制数据等。
        // 在 Apache Camel 中, DataHandler 通常用于在消息交换过程中传输附件或二进制数据。
        // getContent() 方法返回的是原始的数据对象,可能是 InputStream、byte[] 或其他类型
        doc.getDocumentEntry().setHash(String.valueOf(ContentUtils.sha1(doc.getContent(DataHandler.class))));
        doc.getDocumentEntry().setSize(Long.valueOf(String.valueOf(ContentUtils.size(doc.getContent(DataHandler.class)))));
        log.warn(doc.getDocumentEntry().getSize().toString());

        // 组装ProvideAndRegisterDocumentSet
        ProvideAndRegisterDocumentSet request = new ProvideAndRegisterDocumentSet();
        request.setSubmissionSet(submissionSet);
        request.getDocuments().add(doc);
        request.getFolders().add(folder);
        // 添加Association关系,类型为HasMember
        request.getAssociations().add(docAssociation);
        request.getAssociations().add(folderAssociation);
        request.getAssociations().add(docFolderAssociation);
        request.setTargetHomeCommunityId(providedRegisterDTO.getTargetHomeCommunityId());
        return request;
    }

    /***
     * 创建DocEntry —— Folder之间的Association
     * Source —— 文件夹UUID
     * Target —— 文件UUID
     * @author bovane
     * [ProvidedRegisterDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.metadata.Association
     */
    private static Association createAssociationDocEntryToFolder(ProvidedRegisterDTO ProvidedRegisterDTO) {
        Association docFolderAssociation = new Association();
        docFolderAssociation.setAssociationType(AssociationType.HAS_MEMBER);
        docFolderAssociation.setSourceUuid(ProvidedRegisterDTO.getFolderDocEntrySourceUuid());
        docFolderAssociation.setTargetUuid(ProvidedRegisterDTO.getFolderDocEntryTargetUuid());
        docFolderAssociation.setEntryUuid(ProvidedRegisterDTO.getFolderDocEntryAssUuid());
        return docFolderAssociation;
    }

    /***
     * 创建Folder —— SubmissionSet 之间的Association
     * Source —— 提交集UUID
     * Target —— 文件夹UUID
     * @author bovane
     * [ProvidedRegisterDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.metadata.Association
     */
    private static Association createAssociationFolderToSubmissionSet(ProvidedRegisterDTO ProvidedRegisterDTO) {
        Association folderAssociation = new Association();
        folderAssociation.setAssociationType(AssociationType.HAS_MEMBER);
        folderAssociation.setSourceUuid(ProvidedRegisterDTO.getFolderSubmissionSetSourceUuid());
        folderAssociation.setTargetUuid(ProvidedRegisterDTO.getFolderSubmissionSetTargetUuid());
        folderAssociation.setEntryUuid(ProvidedRegisterDTO.getFolderSubmissionSetAssUuid());
        folderAssociation.setPreviousVersion("110");
        return folderAssociation;
    }

    /***
     * 创建DocEntry —— SubmissionSet 之间的Association
     * Source —— 提交集UUID
     * Target —— 文件EntryUUID
     * @author bovane
     * [ProvidedRegisterDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.metadata.Association
     */
    private static Association createAssociationDocEntryToSubmissionSet(ProvidedRegisterDTO ProvidedRegisterDTO) {
        Association docAssociation = new Association();
        docAssociation.setAssociationType(AssociationType.HAS_MEMBER);
        docAssociation.setSourceUuid(ProvidedRegisterDTO.getDocEntrySubmissionSetSourceUuid());
        docAssociation.setTargetUuid(ProvidedRegisterDTO.getDocEntrySubmissionSetTargetUuid());
        docAssociation.setLabel(AssociationLabel.ORIGINAL);
        docAssociation.setEntryUuid(ProvidedRegisterDTO.getDocEntrySubmissionSetAssUuid());
        docAssociation.setPreviousVersion("111");
        return docAssociation;
    }

    /***
     * 创建文件夹信息
     *
     * @author bovane
     * [patientID, providedRegisterDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.metadata.Folder
     */
    public static Folder createFolder(Identifiable patientID, ProvidedRegisterDTO providedRegisterDTO) {
        Folder folder = new Folder();
        folder.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        folder.getCodeList().add(new Code(providedRegisterDTO.getFolderCode(), new LocalizedString(providedRegisterDTO.getFolderCodeDisplayName()), providedRegisterDTO.getFolderCodeSchemeName()));
        folder.setComments(new LocalizedString(providedRegisterDTO.getFolderComments()));
        folder.setEntryUuid(providedRegisterDTO.getFolderEntryUuid());
        folder.setLastUpdateTime(providedRegisterDTO.getFolderLastUpdateTime());
        folder.setPatientId(patientID);
        folder.setTitle(new LocalizedString(providedRegisterDTO.getFolderTitle(), providedRegisterDTO.getFolderLang(), providedRegisterDTO.getFolderCharset()));
        folder.setUniqueId(providedRegisterDTO.getFolderEntryUniqueId());
        return folder;
    }

    /***
     * 创建提交集实体
     *
     * @author bovane
     * [patientID, providedRegisterDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.metadata.SubmissionSet
     */
    public static SubmissionSet createSubmissionSet(Identifiable patientID, ProvidedRegisterDTO providedRegisterDTO) {
        Recipient recipient = new Recipient();
        recipient.setOrganization(new Organization(providedRegisterDTO.getSubmitRecipientOrganizationName(), providedRegisterDTO.getSubmitRecipientOrganizationId(), (AssigningAuthority)null));

        Author author = new Author();
        author.setAuthorPerson(new Person(new Identifiable(providedRegisterDTO.getSubmitAuthorPersonId(), new AssigningAuthority(providedRegisterDTO.getSubmitAuthorPersonAssigningAuthorityId())), new XpnName(providedRegisterDTO.getSubmitAuthorPersonName(), (String)null, (String)null, (String)null, (String)null, (String)null)));

        SubmissionSet submissionSet = new SubmissionSet();
        submissionSet.getAuthors().add(author);
        submissionSet.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        submissionSet.setComments(new LocalizedString(providedRegisterDTO.getSubmitComments()));
        submissionSet.setContentTypeCode(new Code(providedRegisterDTO.getSubmissionContentTypeCode(), new LocalizedString(providedRegisterDTO.getSubmissionContentTypeCodeDisplayName()), providedRegisterDTO.getSubmissionContentTypeCodeSchemeName()));
        submissionSet.setEntryUuid(providedRegisterDTO.getSubmissionSetEntryUuid());
        submissionSet.getIntendedRecipients().add(recipient);
        submissionSet.setPatientId(patientID);

        submissionSet.setSourceId(providedRegisterDTO.getSubmissionSetSourceId());
        submissionSet.setSubmissionTime(providedRegisterDTO.getSubmissionTime());
        submissionSet.setTitle(new LocalizedString(providedRegisterDTO.getSubmissionTitle(), providedRegisterDTO.getSubmissionLang(), providedRegisterDTO.getSubmissionCharset()));
        submissionSet.setUniqueId(providedRegisterDTO.getSubmissionUniqueId());
        submissionSet.setHomeCommunityId(providedRegisterDTO.getSubmissionHomeCommunityId());
        return submissionSet;
    }

    /***
     * 创建 DocumentEntry 信息
     *
     * @author bovane
     * [patientID, providedRegisterDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.metadata.DocumentEntry
     */
    public static DocumentEntry createDocumentEntry(Identifiable patientID, ProvidedRegisterDTO providedRegisterDTO) {
        // 设置作者信息
        Author author = new Author();
        Name name = new XpnName();
        name.setFamilyName(providedRegisterDTO.getDocFamilyName());
        author.setAuthorPerson(new Person(new Identifiable(providedRegisterDTO.getDocAuthorId(), new AssigningAuthority(providedRegisterDTO.getDocAuthorAssigningAuthorityId())), name));
        author.getAuthorInstitution().add(new Organization(providedRegisterDTO.getDocAuthorOrganizationName(), (String)null, (AssigningAuthority)null));
        author.getAuthorRole().add(new Identifiable(providedRegisterDTO.getDocAuthorRoleId(), new AssigningAuthority(providedRegisterDTO.getDocAuthorRoleAssigningAuthorityId(), "ISO")));
        author.getAuthorRole().add(new Identifiable("role2"));
        author.getAuthorSpecialty().add(new Identifiable(providedRegisterDTO.getDocAuthorSpecialtyId(), new AssigningAuthority(providedRegisterDTO.getDocAuthorSpecialtyAssigningAuthorityId(), "ISO")));
        author.getAuthorSpecialty().add(new Identifiable("spec2"));
        author.getAuthorTelecom().add(new Telecom(providedRegisterDTO.getDocAuthorTelecom()));
        author.getAuthorTelecom().add(new Telecom("author2@acme.org"));
        // 设置地址信息
        Address address = new Address();
        address.setStreetAddress(providedRegisterDTO.getDocPatientAddress());
        // 设置患者基本信息
        PatientInfo patientInfo = new PatientInfo();
        patientInfo.getAddresses().add(address);
        patientInfo.setDateOfBirth(providedRegisterDTO.getDocPatientBirthDate());
        patientInfo.setGender(providedRegisterDTO.getDocPatientGender());
        patientInfo.getNames().add(new XpnName(providedRegisterDTO.getDocPatientName(), (String)null, (String)null, (String)null, (String)null, (String)null));

        // 组装DocumentEntry
        DocumentEntry docEntry = new DocumentEntry();
        docEntry.getAuthors().add(author);
        docEntry.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        docEntry.setClassCode(new Code(providedRegisterDTO.getDocClassCode(), new LocalizedString(providedRegisterDTO.getDocClassDisplayName()), providedRegisterDTO.getDocClassSchemeName()));
        docEntry.setComments(new LocalizedString(providedRegisterDTO.getDocComments()));
        docEntry.getConfidentialityCodes().add(new Code(providedRegisterDTO.getDocConfidentialityCode(), new LocalizedString(providedRegisterDTO.getDocConfidentialityDisplayName()), providedRegisterDTO.getDocConfidentialitySchemeName()));
        docEntry.setCreationTime(providedRegisterDTO.getDocCreateTime());


        docEntry.getEventCodeList().add(new Code(providedRegisterDTO.getDocEventCode(), new LocalizedString(providedRegisterDTO.getDocEventDisplayName()), providedRegisterDTO.getDocEventSchemeName()));
        docEntry.setFormatCode(new Code(providedRegisterDTO.getDocFormatCode(), new LocalizedString(providedRegisterDTO.getDocFormatDisplayName()), providedRegisterDTO.getDocEventSchemeName()));

        docEntry.setEntryUuid(providedRegisterDTO.getDocEntryUuid());
        docEntry.setHash("1234567890123456789012345678901234567890");
        docEntry.setHealthcareFacilityTypeCode(new Code("code4", new LocalizedString("code4"), "scheme4"));
        docEntry.setLanguageCode(providedRegisterDTO.getDocLanguageCode());
        docEntry.setLegalAuthenticator(new Person(new Identifiable("legal", new AssigningAuthority("1.7")), new XpnName("Gustav", (String)null, (String)null, (String)null, (String)null, (String)null)));
        docEntry.setMimeType("application/octet-stream");

        docEntry.setPatientId(patientID);
        docEntry.setPracticeSettingCode(new Code("code5", new LocalizedString("code5"), "scheme5"));
        docEntry.setRepositoryUniqueId(providedRegisterDTO.getDocRepositoryUniqueId());
        docEntry.setServiceStartTime(providedRegisterDTO.getDocServiceStartTime());
        docEntry.setServiceStopTime(providedRegisterDTO.getDocServiceStopTime());
        docEntry.setSize(123L);
        // 设置源患者信息
        docEntry.setSourcePatientId(new Identifiable(providedRegisterDTO.getDocSourcePatientId(), new AssigningAuthority(providedRegisterDTO.getDocSourcePatientIdAssigningAuthorityId())));
        docEntry.setSourcePatientInfo(patientInfo);
        docEntry.setTitle(new LocalizedString(providedRegisterDTO.getDocTitle(), providedRegisterDTO.getDocTitleLang(), providedRegisterDTO.getDocTitleCharset()));
        docEntry.setTypeCode(new Code(providedRegisterDTO.getDocTypeCode(), new LocalizedString(providedRegisterDTO.getDocTypeDisplayName()), providedRegisterDTO.getDocTypeSchemeName()));

        docEntry.setUniqueId(providedRegisterDTO.getDocEntryUniqueId());
        docEntry.setUri(providedRegisterDTO.getDocUri());
        docEntry.getReferenceIdList().add(new ReferenceId(providedRegisterDTO.getDocReferenceId(), new CXiAssigningAuthority(providedRegisterDTO.getDocReferenceCXiAssigningAuthorityNamespaceId(), providedRegisterDTO.getDocReferenceCXiAssigningAuthorityId(), "ISO"), "urn:ihe:iti:xds:2013:order"));
        docEntry.getReferenceIdList().add(new ReferenceId("ref-id-2", new CXiAssigningAuthority("DEFG", "2.1.2.3", "ISO"), "vendor-defined"));
        return docEntry;
    }
}
