package org.openehealth.ipf.tutorials.xds.helper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.*;
import org.openehealth.ipf.commons.ihe.xds.core.requests.*;
import org.openehealth.ipf.commons.ihe.xds.core.requests.query.FindDocumentsQuery;
import org.openehealth.ipf.tutorials.xds.ContentUtils;
import org.openehealth.ipf.tutorials.xds.datasource.CustomDataSource;
import org.openehealth.ipf.tutorials.xds.dto.ProvidedRegisterDTO;
import org.openehealth.ipf.tutorials.xds.dto.XdsProvidedRegisterDTO;

import javax.activation.DataHandler;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/15
 */
@Data
@Slf4j
public abstract class CreateXdsHelper {

    /**
     * 只创建文档和提交集合
     * 会有 document 和 提交集之间的Association
     *
     * @author bovane
     * [xdsProvidedRegisterDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.requests.ProvideAndRegisterDocumentSet
     */
    public static ProvideAndRegisterDocumentSet createDocumentOnly(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        // 创建患者标识信息
        Identifiable patientID = new Identifiable(xdsProvidedRegisterDTO.getPatientId(), new AssigningAuthority(xdsProvidedRegisterDTO.getAssigningAuthorityId()));

        // 创建提交集合信息
        SubmissionSet submissionSet = createSubmissionSet(patientID, xdsProvidedRegisterDTO);

        // 创建DocumentEntry
        DocumentEntry docEntry = createDocumentEntry(patientID, xdsProvidedRegisterDTO);

        // 设置DataHandler,即文档的内容
        DataHandler dataHandler = new DataHandler(new CustomDataSource(xdsProvidedRegisterDTO.getFilePath(), xdsProvidedRegisterDTO.getContentType() ,xdsProvidedRegisterDTO.getName()));
        Document doc = new Document(docEntry, dataHandler);

        // 组装ProvideAndRegisterDocumentSet
        ProvideAndRegisterDocumentSet request = new ProvideAndRegisterDocumentSet();
        request.setSubmissionSet(submissionSet);
        request.getDocuments().add(doc);
        request.setTargetHomeCommunityId("urn:oid:1.2.3.4.5.6.2333.23");

//        Association association = createAssociationDocEntryToSubmissionSetSubmitAssociation(xdsProvidedRegisterDTO);
//        request.getAssociations().add(association);

        // 计算文档的Hash 和 Size
        // 一个Document对象是 由 DocumentEntry 对象以及一个DataHandler对象
        // 在 Apache Camel 中, DataHandler 是一个非常重要的概念,它代表了一个通用的数据容器,
        // 可以包含各种类型的数据,比如文件、二进制数据等。
        // 在 Apache Camel 中, DataHandler 通常用于在消息交换过程中传输附件或二进制数据。
        // getContent() 方法返回的是原始的数据对象,可能是 InputStream、byte[] 或其他类型
        request.getDocuments().get(0).getDocumentEntry().setHash(String.valueOf(ContentUtils.sha1(request.getDocuments().get(0).getContent(DataHandler.class))));
        request.getDocuments().get(0).getDocumentEntry().setSize(Long.valueOf(String.valueOf(ContentUtils.size(request.getDocuments().get(0).getContent(DataHandler.class)))));
        return request;
    }

    private static Association createAssociationDocEntryToSubmissionSetSubmitAssociation(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        Association docAssociation = new Association();
        docAssociation.setAssociationType(AssociationType.SUBMIT_ASSOCIATION);
//        docAssociation.setAssociationType(AssociationType.HAS_MEMBER);
        docAssociation.setSourceUuid(xdsProvidedRegisterDTO.getDocEntrySourceUuid());
        docAssociation.setTargetUuid(xdsProvidedRegisterDTO.getDocEntryTargetUuid());
        docAssociation.setLabel(AssociationLabel.ORIGINAL);
        docAssociation.setEntryUuid(xdsProvidedRegisterDTO.getDocEntryAssUuid());
        docAssociation.setPreviousVersion("111");
        return docAssociation;
    }

    /**
     * 只创建文档和提交集合
     * 会有 document 和 提交集之间的Association
     *
     * @author bovane
     * [xdsProvidedRegisterDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.requests.ProvideAndRegisterDocumentSet
     */
    public static ProvideAndRegisterDocumentSet createThreeDocumentOnly(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) throws IOException {
        ProvideAndRegisterDocumentSet provide = CreateXdsHelper.createDocumentOnly(xdsProvidedRegisterDTO);
        // 现在再添加两个文档,还需要添加text 和 PDF文档
        Identifiable patientID = provide.getSubmissionSet().getPatientId();

        // 创建DocumentEntry, 这里重新创建一个 XdsProvidedRegisterDTO 对象,即用默认值
        XdsProvidedRegisterDTO xdsProvidedRegisterDTOText = new XdsProvidedRegisterDTO();
        // 这里假设是两个文档属于同一个患者
        DocumentEntry docEntry = createDocumentEntry(patientID, xdsProvidedRegisterDTOText);

        // 设置DataHandler,即文档的内容,这里是txt文档
        DataHandler dataHandler = new DataHandler(new CustomDataSource(xdsProvidedRegisterDTOText.getFilePath(), xdsProvidedRegisterDTOText.getContentType() ,xdsProvidedRegisterDTOText.getName()));
        Document second = new Document(docEntry, dataHandler);
        provide.getDocuments().add(second);
        // 设置第二个文档的hash 和 size
        provide.getDocuments().get(1).getDocumentEntry().setHash(String.valueOf(ContentUtils.sha1(provide.getDocuments().get(1).getContent(DataHandler.class))));
        provide.getDocuments().get(1).getDocumentEntry().setSize(Long.valueOf(String.valueOf(ContentUtils.size(provide.getDocuments().get(1).getContent(DataHandler.class)))));

        // 测试第一个XML文档的值是否设置进去
        log.error("测试第一个XML文档内容是否设置进去");
        log.warn("Document 文档数量为: " + provide.getDocuments().size());
        log.warn("Document 文档的UUID为: " + provide.getDocuments().get(0).getDocumentEntry().getEntryUuid());
        log.warn("Document 文档的唯一ID为: " +provide.getDocuments().get(0).getDocumentEntry().getUniqueId());
        log.warn("Document 文档的大小为:(size字符大小) " + provide.getDocuments().get(0).getDocumentEntry().getSize());
        log.warn("第一个文档文档的内容为: " + IOUtils.toString(provide.getDocuments().get(0).getDataHandler().getInputStream()));

        // 测试第二个Text文档的值是否设置进去
        log.error("测试第二个Text文档内容是否设置进去");
        log.warn("Document 文档数量为: " + provide.getDocuments().size());
        log.warn("Document 文档的UUID为: " + provide.getDocuments().get(1).getDocumentEntry().getEntryUuid());
        log.warn("Document 文档的唯一ID为: " +provide.getDocuments().get(1).getDocumentEntry().getUniqueId());
        log.warn("Document 文档的大小为:(size字符大小) " + provide.getDocuments().get(1).getDocumentEntry().getSize());
        log.warn("第二个文档文档的内容为: " + IOUtils.toString(provide.getDocuments().get(1).getDataHandler().getInputStream()));
        return provide;
    }



    public static SubmissionSet createSubmissionSet(Identifiable patientID, XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        Recipient recipient = new Recipient();
        recipient.setOrganization(new Organization(xdsProvidedRegisterDTO.getOrganizationName(), xdsProvidedRegisterDTO.getIdNumber(), (AssigningAuthority)null));

        Author author = new Author();
        author.setAuthorPerson(new Person(new Identifiable(xdsProvidedRegisterDTO.getAuthorId(), new AssigningAuthority("1.1")), new XpnName("Otto", (String)null, (String)null, (String)null, (String)null, (String)null)));

        SubmissionSet submissionSet = new SubmissionSet();
        submissionSet.getAuthors().add(author);
        submissionSet.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        submissionSet.setComments(new LocalizedString("comments1Test"));
        submissionSet.setContentTypeCode(new Code("code1", new LocalizedString("code1"), "scheme1"));
        submissionSet.setEntryUuid(xdsProvidedRegisterDTO.getSubmissionSetEntryUuid());
        submissionSet.getIntendedRecipients().add(recipient);
        submissionSet.setPatientId(patientID);

        submissionSet.setSourceId(xdsProvidedRegisterDTO.getSubmissionSetSourceId());
        submissionSet.setSubmissionTime("1980");
        submissionSet.setTitle(new LocalizedString("Submission Set 01", "en-US", "UTF8"));
        submissionSet.setUniqueId(xdsProvidedRegisterDTO.getSubmissionUniqueId());
        submissionSet.setHomeCommunityId(xdsProvidedRegisterDTO.getSubmissionHomeCommunityId());
        return submissionSet;
    }

    public static DocumentEntry createDocumentEntry(Identifiable patientID, XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
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

        docEntry.setEntryUuid(xdsProvidedRegisterDTO.getDocEntryUuid());
        docEntry.getEventCodeList().add(new Code("code9", new LocalizedString("code9"), "scheme9"));
        docEntry.setFormatCode(new Code("code3", new LocalizedString("code3"), "scheme3"));
        docEntry.setHash("1234567890123456789012345678901234567890");
        docEntry.setHealthcareFacilityTypeCode(new Code("code4", new LocalizedString("code4"), "scheme4"));
        docEntry.setLanguageCode("en-US");
        docEntry.setLegalAuthenticator(new Person(new Identifiable("legal", new AssigningAuthority("1.7")), new XpnName("Gustav", (String)null, (String)null, (String)null, (String)null, (String)null)));
        docEntry.setMimeType("application/octet-stream");

        docEntry.setPatientId(patientID);
        docEntry.setPracticeSettingCode(new Code("code5", new LocalizedString("code5"), "scheme5"));
        docEntry.setRepositoryUniqueId(xdsProvidedRegisterDTO.getRepositoryUniqueId());
        docEntry.setServiceStartTime("198012");
        docEntry.setServiceStopTime("198101");
        docEntry.setSize(123L);
        // 设置源患者信息
        docEntry.setSourcePatientId(new Identifiable("source", new AssigningAuthority("4.1")));
        docEntry.setSourcePatientInfo(patientInfo);
        docEntry.setTitle(new LocalizedString(xdsProvidedRegisterDTO.getDocEntryUuid(), "en-US", "UTF8"));
        docEntry.setTypeCode(new Code("code6", new LocalizedString("code6"), "scheme6"));

        docEntry.setUniqueId(xdsProvidedRegisterDTO.getDocEntryUniqueId());
        docEntry.setUri("http://hierunten.com");
        docEntry.getReferenceIdList().add(new ReferenceId("ref-id-1", new CXiAssigningAuthority("ABCD", "1.1.2.3", "ISO"), "urn:ihe:iti:xds:2013:order"));
        docEntry.getReferenceIdList().add(new ReferenceId("ref-id-2", new CXiAssigningAuthority("DEFG", "2.1.2.3", "ISO"), "vendor-defined"));
        return docEntry;
    }

    public static ProvideAndRegisterDocumentSet createMultipleDocument(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {

        return null;
    }

    public static QueryRegistry createFindDocumentsQuery(Identifiable patientId) {
        FindDocumentsQuery query = new FindDocumentsQuery();
        query.setPatientId(patientId);
        query.setStatus(Arrays.asList(AvailabilityStatus.APPROVED, AvailabilityStatus.SUBMITTED));
        query.setDocumentEntryTypes(Collections.singletonList(DocumentEntryType.STABLE));
        query.setDocumentAvailability(Collections.singletonList(DocumentAvailability.ONLINE));
        query.setMetadataLevel(1);
        return new QueryRegistry(query);
    }

    public static RetrieveDocumentSet createRetrieveDocumentSet(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        RetrieveDocumentSet request = new RetrieveDocumentSet();
        DocumentReference documentReference = new DocumentReference();
        documentReference.setDocumentUniqueId(xdsProvidedRegisterDTO.getDocEntryUniqueId());
        documentReference.setRepositoryUniqueId(xdsProvidedRegisterDTO.getRepositoryUniqueId());
        request.getDocuments().add(documentReference);
        return request;
    }

    /**
     * 进行文档注册,即保存元数据,
     *
     * @author bovane
     * [xdsProvidedRegisterDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.requests.RegisterDocumentSet
     */
    public static RegisterDocumentSet createRegisterDocumentSet(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        // 创建患者标识信息
        Identifiable patientID = new Identifiable(xdsProvidedRegisterDTO.getPatientId(), new AssigningAuthority(xdsProvidedRegisterDTO.getAssigningAuthorityId()));

        // 创建提交集合
        SubmissionSet submissionSet = createSubmissionSet(patientID,xdsProvidedRegisterDTO);

        // 创建文档元的一些信息
        DocumentEntry docEntry = createDocumentEntry(patientID,xdsProvidedRegisterDTO);

        // 创建文件夹
        Folder folder = createFolder(patientID, xdsProvidedRegisterDTO);

        // 创建联系
        Association docAssociation = createAssociationDocEntryToSubmissionSet(xdsProvidedRegisterDTO);
        Association folderAssociation = createAssociationFolderToSubmissionSet(xdsProvidedRegisterDTO);
        Association docFolderAssociation = createAssociationDocEntryToFolder(xdsProvidedRegisterDTO);

        // 组装RegisterDocumentSet
        RegisterDocumentSet request = new RegisterDocumentSet();
        request.setSubmissionSet(submissionSet);
        request.getDocumentEntries().add(docEntry);
        request.getFolders().add(folder);
        request.getAssociations().add(docAssociation);
        request.getAssociations().add(folderAssociation);
        request.getAssociations().add(docFolderAssociation);
        return request;
    }



    private static Association createAssociationDocEntryToFolder(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        Association docFolderAssociation = new Association();
        docFolderAssociation.setAssociationType(AssociationType.HAS_MEMBER);
        docFolderAssociation.setSourceUuid(xdsProvidedRegisterDTO.getFolderDocEntrySourceUuid());
        docFolderAssociation.setTargetUuid(xdsProvidedRegisterDTO.getFolderDocEntryTargetUuid());
        docFolderAssociation.setEntryUuid(xdsProvidedRegisterDTO.getFolderDocEntryAssUuid());
        return docFolderAssociation;
    }

    private static Association createAssociationFolderToSubmissionSet(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        Association folderAssociation = new Association();
        folderAssociation.setAssociationType(AssociationType.HAS_MEMBER);
        folderAssociation.setSourceUuid(xdsProvidedRegisterDTO.getFolderSourceUuid());
        folderAssociation.setTargetUuid(xdsProvidedRegisterDTO.getFolderTargetUuid());
        folderAssociation.setEntryUuid(xdsProvidedRegisterDTO.getFolderAssUuid());
        folderAssociation.setPreviousVersion("110");
        return folderAssociation;
    }

    private static Association createAssociationDocEntryToSubmissionSet(XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        Association docAssociation = new Association();
        docAssociation.setAssociationType(AssociationType.HAS_MEMBER);
        docAssociation.setSourceUuid(xdsProvidedRegisterDTO.getDocEntrySourceUuid());
        docAssociation.setTargetUuid(xdsProvidedRegisterDTO.getDocEntryTargetUuid());
        docAssociation.setLabel(AssociationLabel.ORIGINAL);
        docAssociation.setEntryUuid(xdsProvidedRegisterDTO.getDocEntryAssUuid());
        docAssociation.setPreviousVersion("111");
        return docAssociation;
    }

    public static Folder createFolder(Identifiable patientID, XdsProvidedRegisterDTO xdsProvidedRegisterDTO) {
        Folder folder = new Folder();
        folder.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        folder.getCodeList().add(new Code("code7", new LocalizedString("code7"), "scheme7"));
        folder.setComments(new LocalizedString("comments3"));
        folder.setEntryUuid(xdsProvidedRegisterDTO.getFolderUuid());
        folder.setLastUpdateTime("19820910121315");
        folder.setPatientId(patientID);
        folder.setTitle(new LocalizedString(xdsProvidedRegisterDTO.getFolderUuid(), "en-US", "UTF8"));
        folder.setUniqueId(xdsProvidedRegisterDTO.getFolderUniqueId());
        return folder;
    }



}
