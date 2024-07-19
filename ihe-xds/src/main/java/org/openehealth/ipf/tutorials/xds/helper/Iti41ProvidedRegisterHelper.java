package org.openehealth.ipf.tutorials.xds.helper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.openehealth.ipf.commons.ihe.ws.utils.LargeDataSource;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.*;
import org.openehealth.ipf.commons.ihe.xds.core.requests.ProvideAndRegisterDocumentSet;
import org.openehealth.ipf.tutorials.xds.ContentUtils;
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

    public static DataHandler createDataHandler() {
        return new DataHandler(new LargeDataSource());
    }

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
        DataHandler dataHandler = createDataHandler();
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
        request.getAssociations().add(docAssociation);
        request.getAssociations().add(folderAssociation);
        request.getAssociations().add(docFolderAssociation);
        request.setTargetHomeCommunityId(providedRegisterDTO.getTargetHomeCommunityId());
        return request;
    }

    private static Association createAssociationDocEntryToFolder(ProvidedRegisterDTO ProvidedRegisterDTO) {
        Association docFolderAssociation = new Association();
        docFolderAssociation.setAssociationType(AssociationType.HAS_MEMBER);
        docFolderAssociation.setSourceUuid(ProvidedRegisterDTO.getFolderDocEntrySourceUuid());
        docFolderAssociation.setTargetUuid(ProvidedRegisterDTO.getFolderDocEntryTargetUuid());
        docFolderAssociation.setEntryUuid(ProvidedRegisterDTO.getFolderDocEntryAssUuid());
        return docFolderAssociation;
    }

    private static Association createAssociationFolderToSubmissionSet(ProvidedRegisterDTO ProvidedRegisterDTO) {
        Association folderAssociation = new Association();
        folderAssociation.setAssociationType(AssociationType.HAS_MEMBER);
        folderAssociation.setSourceUuid(ProvidedRegisterDTO.getFolderSourceUuid());
        folderAssociation.setTargetUuid(ProvidedRegisterDTO.getFolderTargetUuid());
        folderAssociation.setEntryUuid(ProvidedRegisterDTO.getFolderAssUuid());
        folderAssociation.setPreviousVersion("110");
        return folderAssociation;
    }

    private static Association createAssociationDocEntryToSubmissionSet(ProvidedRegisterDTO ProvidedRegisterDTO) {
        Association docAssociation = new Association();
        docAssociation.setAssociationType(AssociationType.HAS_MEMBER);
        docAssociation.setSourceUuid(ProvidedRegisterDTO.getDocEntrySourceUuid());
        docAssociation.setTargetUuid(ProvidedRegisterDTO.getDocEntryTargetUuid());
        docAssociation.setLabel(AssociationLabel.ORIGINAL);
        docAssociation.setEntryUuid(ProvidedRegisterDTO.getDocEntryAssUuid());
        docAssociation.setPreviousVersion("111");
        return docAssociation;
    }

    public static Folder createFolder(Identifiable patientID, ProvidedRegisterDTO providedRegisterDTO) {
        Folder folder = new Folder();
        folder.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        folder.getCodeList().add(new Code("code7", new LocalizedString("code7"), "scheme7"));
        folder.setComments(new LocalizedString("comments3"));
        folder.setEntryUuid(providedRegisterDTO.getFolderEntryUuid());
        folder.setLastUpdateTime("19820910121315");
        folder.setPatientId(patientID);
        folder.setTitle(new LocalizedString("Folder 01", "en-US", "UTF8"));
        folder.setUniqueId(providedRegisterDTO.getFolderEntryUniqueId());
        return folder;
    }

    public static SubmissionSet createSubmissionSet(Identifiable patientID, ProvidedRegisterDTO providedRegisterDTO) {
        Recipient recipient = new Recipient();
        recipient.setOrganization(new Organization(providedRegisterDTO.getSubmitRecipientOrganizationName(), providedRegisterDTO.getSubmitRecipientOrganizationId(), (AssigningAuthority)null));

        Author author = new Author();
        author.setAuthorPerson(new Person(new Identifiable(providedRegisterDTO.getSubmitAuthorPersonId(), new AssigningAuthority(providedRegisterDTO.getSubmitAuthorPersonAssigningAuthorityId())), new XpnName(providedRegisterDTO.getSubmitAuthorPersonName(), (String)null, (String)null, (String)null, (String)null, (String)null)));

        SubmissionSet submissionSet = new SubmissionSet();
        submissionSet.getAuthors().add(author);
        submissionSet.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        submissionSet.setComments(new LocalizedString("comments1Test"));
        submissionSet.setContentTypeCode(new Code("code1", new LocalizedString("code1"), "scheme1"));
        submissionSet.setEntryUuid(providedRegisterDTO.getSubmissionSetEntryUuid());
        submissionSet.getIntendedRecipients().add(recipient);
        submissionSet.setPatientId(patientID);

        submissionSet.setSourceId(providedRegisterDTO.getSubmissionSetSourceId());
        submissionSet.setSubmissionTime("1980");
        submissionSet.setTitle(new LocalizedString("Submission Set 01", "en-US", "UTF8"));
        submissionSet.setUniqueId(providedRegisterDTO.getSubmissionUniqueId());
        submissionSet.setHomeCommunityId(providedRegisterDTO.getSubmissionHomeCommunityId());
        return submissionSet;
    }

    public static DocumentEntry createDocumentEntry(Identifiable patientID, ProvidedRegisterDTO providedRegisterDTO) {
        // 设置作者信息
        Author author = new Author();
        Name name = new XpnName();
        name.setFamilyName("Norbi");
        author.setAuthorPerson(new Person(new Identifiable("id2", new AssigningAuthority("1.2")), name));
        author.getAuthorInstitution().add(new Organization("authorOrg", (String)null, (AssigningAuthority)null));
        author.getAuthorRole().add(new Identifiable("role1", new AssigningAuthority("1.2.3.1", "ISO")));
        author.getAuthorRole().add(new Identifiable("role2"));
        author.getAuthorSpecialty().add(new Identifiable("spec1", new AssigningAuthority("1.2.3.3", "ISO")));
        author.getAuthorSpecialty().add(new Identifiable("spec2"));
        author.getAuthorTelecom().add(new Telecom("author1@acme.org"));
        author.getAuthorTelecom().add(new Telecom("author2@acme.org"));
        // 设置地址信息
        Address address = new Address();
        address.setStreetAddress("hier");
        // 设置患者基本信息
        PatientInfo patientInfo = new PatientInfo();
        patientInfo.getAddresses().add(address);
        patientInfo.setDateOfBirth("1980");
        patientInfo.setGender("M");
        patientInfo.getNames().add(new XpnName("Susi", (String)null, (String)null, (String)null, (String)null, (String)null));

        // 组装DocumentEntry
        DocumentEntry docEntry = new DocumentEntry();
        docEntry.getAuthors().add(author);
        docEntry.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        docEntry.setClassCode(new Code("code2", new LocalizedString("code2"), "scheme2"));
        docEntry.setComments(new LocalizedString("comment2"));
        docEntry.getConfidentialityCodes().add(new Code("code8", new LocalizedString("code8"), "scheme8"));
        docEntry.setCreationTime("1981");

        docEntry.setEntryUuid(providedRegisterDTO.getDocEntryUuid());
        docEntry.getEventCodeList().add(new Code("code9", new LocalizedString("code9"), "scheme9"));
        docEntry.setFormatCode(new Code("code3", new LocalizedString("code3"), "scheme3"));
        docEntry.setHash("1234567890123456789012345678901234567890");
        docEntry.setHealthcareFacilityTypeCode(new Code("code4", new LocalizedString("code4"), "scheme4"));
        docEntry.setLanguageCode("en-US");
        docEntry.setLegalAuthenticator(new Person(new Identifiable("legal", new AssigningAuthority("1.7")), new XpnName("Gustav", (String)null, (String)null, (String)null, (String)null, (String)null)));
        docEntry.setMimeType("application/octet-stream");

        docEntry.setPatientId(patientID);
        docEntry.setPracticeSettingCode(new Code("code5", new LocalizedString("code5"), "scheme5"));
        docEntry.setRepositoryUniqueId(providedRegisterDTO.getDocRepositoryUniqueId());
        docEntry.setServiceStartTime("198012");
        docEntry.setServiceStopTime("198101");
        docEntry.setSize(123L);
        // 设置源患者信息
        docEntry.setSourcePatientId(new Identifiable("source", new AssigningAuthority("4.1")));
        docEntry.setSourcePatientInfo(patientInfo);
        docEntry.setTitle(new LocalizedString("Document 01", "en-US", "UTF8"));
        docEntry.setTypeCode(new Code("code6", new LocalizedString("code6"), "scheme6"));

        docEntry.setUniqueId(providedRegisterDTO.getDocEntryUniqueId());
        docEntry.setUri("http://hierunten.com");
        docEntry.getReferenceIdList().add(new ReferenceId("ref-id-1", new CXiAssigningAuthority("ABCD", "1.1.2.3", "ISO"), "urn:ihe:iti:xds:2013:order"));
        docEntry.getReferenceIdList().add(new ReferenceId("ref-id-2", new CXiAssigningAuthority("DEFG", "2.1.2.3", "ISO"), "vendor-defined"));
        return docEntry;
    }
}
