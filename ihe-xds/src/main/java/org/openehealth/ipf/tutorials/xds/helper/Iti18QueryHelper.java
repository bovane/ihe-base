package org.openehealth.ipf.tutorials.xds.helper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.*;
import org.openehealth.ipf.commons.ihe.xds.core.requests.QueryRegistry;
import org.openehealth.ipf.commons.ihe.xds.core.requests.query.*;
import org.openehealth.ipf.tutorials.xds.dto.QueryDTO;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author bovane bovane.ch@gmial.com
 * @create 2024/7/21
 */
@Data
@Slf4j
public abstract class Iti18QueryHelper {

    /**
     * 根据患者ID等信息,获取一个documentEntry列表
     *
     * @author bovane
     * @return org.openehealth.ipf.commons.ihe.xds.core.requests.QueryRegistry
     */
    public static QueryRegistry createFindDocumentsQuery() {
        FindDocumentsQuery query = new FindDocumentsQuery();
        populateDocumentsQuery(query);
        query.setPatientId(new Identifiable("id3", new AssigningAuthority("1.3")));
        query.setStatus(Arrays.asList(AvailabilityStatus.APPROVED, AvailabilityStatus.SUBMITTED));
        query.setDocumentEntryTypes(Collections.singletonList(DocumentEntryType.STABLE));
        query.setDocumentAvailability(Collections.singletonList(DocumentAvailability.ONLINE));
        query.setMetadataLevel(1);
        return new QueryRegistry(query);
    }

    /***
     * 填装 DocumentsQuery
     *
     * @author bovane
     * [query]
     * @return void
     */
    private static void populateDocumentsQuery(DocumentsQuery query) {
        query.setHomeCommunityId("12.21.41");
        query.setClassCodes(Arrays.asList(new Code("code1", (LocalizedString)null, "scheme1"), new Code("code2", (LocalizedString)null, "scheme2")));
        query.setTypeCodes(Arrays.asList(new Code("codet1", (LocalizedString)null, "schemet1"), new Code("codet2", (LocalizedString)null, "schemet2")));
        query.setPracticeSettingCodes(Arrays.asList(new Code("code3", (LocalizedString)null, "scheme3"), new Code("code4", (LocalizedString)null, "scheme4")));
        query.getCreationTime().setFrom("1980");
        query.getCreationTime().setTo("1981");
        query.getServiceStartTime().setFrom("1982");
        query.getServiceStartTime().setTo("1983");
        query.getServiceStopTime().setFrom("1984");
        query.getServiceStopTime().setTo("1985");
        query.setHealthcareFacilityTypeCodes(Arrays.asList(new Code("code5", (LocalizedString)null, "scheme5"), new Code("code6", (LocalizedString)null, "scheme6")));
        QueryList<Code> eventCodes = new QueryList();
        eventCodes.getOuterList().add(Arrays.asList(new Code("code7", (LocalizedString)null, "scheme7"), new Code("code8", (LocalizedString)null, "scheme8")));
        eventCodes.getOuterList().add(Collections.singletonList(new Code("code9", (LocalizedString)null, "scheme9")));
        query.setEventCodes(eventCodes);
        QueryList<Code> confidentialityCodes = new QueryList();
        confidentialityCodes.getOuterList().add(Arrays.asList(new Code("code10", (LocalizedString)null, "scheme10"), new Code("code11", (LocalizedString)null, "scheme11")));
        confidentialityCodes.getOuterList().add(Collections.singletonList(new Code("code12", (LocalizedString)null, "scheme12")));
        query.setConfidentialityCodes(confidentialityCodes);
        query.setAuthorPersons(Arrays.asList("per'son1", "person2"));
        query.setFormatCodes(Arrays.asList(new Code("code13", (LocalizedString)null, "scheme13"), new Code("code14", (LocalizedString)null, "scheme14")));
    }

    /***
     * 创建查询提交集的查询体
     *
     * @author bovane
     * [queryDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.requests.QueryRegistry
     */
    public static QueryRegistry createFindSubmissionSetsQuery(QueryDTO queryDTO) {
        FindSubmissionSetsQuery query = new FindSubmissionSetsQuery();
        query.setHomeCommunityId("12.21.41");
        query.setPatientId(new Identifiable("id1", new AssigningAuthority("1.2")));
        query.getSubmissionTime().setFrom("1980");
        query.getSubmissionTime().setTo("1981");
        query.setAuthorPerson("per'son1");
        query.setSourceIds(Arrays.asList("1.2.3", "3.2.1"));
        query.setContentTypeCodes(Arrays.asList(new Code("code1", (LocalizedString) null, "scheme1"), new Code("code2", (LocalizedString) null, "scheme2")));
        query.setStatus(Arrays.asList(AvailabilityStatus.APPROVED, AvailabilityStatus.SUBMITTED));
        return new QueryRegistry(query);

    }


    /***
     * 创建查询文件夹的查询体
     *
     * @author bovane
     * [queryDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.requests.QueryRegistry
     */
    public static QueryRegistry createFindFoldersQuery(QueryDTO queryDTO) {
        FindFoldersQuery query = new FindFoldersQuery();
        query.setHomeCommunityId("12.21.41");
        query.setPatientId(new Identifiable("id1", new AssigningAuthority("1.2")));
        query.getLastUpdateTime().setFrom("1980");
        query.getLastUpdateTime().setTo("1981");
        QueryList<Code> codes = new QueryList();
        codes.getOuterList().add(Arrays.asList(new Code("code7", (LocalizedString)null, "scheme7"), new Code("code8", (LocalizedString)null, "scheme8")));
        codes.getOuterList().add(Collections.singletonList(new Code("code9", (LocalizedString)null, "scheme9")));
        query.setCodes(codes);
        query.setStatus(Arrays.asList(AvailabilityStatus.APPROVED, AvailabilityStatus.SUBMITTED));
        return new QueryRegistry(query);
    }


    /***
     * 创建查询Association的查询体
     *
     * @author bovane
     * [queryDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.requests.QueryRegistry
     */
    public static QueryRegistry createGetAssociationsQuery(QueryDTO queryDTO) {
        GetAssociationsQuery query = new GetAssociationsQuery();
        query.setHomeCommunityId("12.21.41");
        query.setUuids(Arrays.asList("urn:uuid:1.2.3.4", "urn:uuid:2.3.4.5"));
        return new QueryRegistry(query);
    }

    /***
     * 创建查询 GetDocumentsAndAssociations 的查询实体
     *
     * @author bovane
     * [QueryDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.requests.QueryRegistry
     */
    public static QueryRegistry createGetDocumentsAndAssociationsQuery(QueryDTO queryDTO) {
        GetDocumentsAndAssociationsQuery query = new GetDocumentsAndAssociationsQuery();
        query.setHomeCommunityId("12.21.41");
        query.setUuids(Arrays.asList("urn:uuid:1.2.3.4", "urn:uuid:2.3.4.5"));
        query.setUniqueIds(Arrays.asList("12.21.34", "43.56.89"));
        return new QueryRegistry(query);
    }


    /***
     * 创建GetSubmissionSets的实体
     *
     * @author bovane
     * [queryDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.requests.QueryRegistry
     */
    public static QueryRegistry createGetSubmissionSetsQuery(QueryDTO queryDTO) {
        GetSubmissionSetsQuery query = new GetSubmissionSetsQuery();
        query.setHomeCommunityId("12.21.41");
        query.setUuids(Arrays.asList("urn:uuid:1.2.3.4", "urn:uuid:2.3.4.5"));
        return new QueryRegistry(query);
    }

    /***
     * 创建取文件和内容的查询体
     *
     * @author bovane
     * [queryDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.requests.QueryRegistry
     */
    public static QueryRegistry createGetFolderAndContentsQuery(QueryDTO queryDTO) {
        GetFolderAndContentsQuery query = new GetFolderAndContentsQuery();
        query.setHomeCommunityId("12.21.41");
        query.setUuid("urn:uuid:1.2.3.4");
        query.setUniqueId("12.21.34");
        QueryList<Code> confidentialityCodes = new QueryList();
        confidentialityCodes.getOuterList().add(Arrays.asList(new Code("code10", (LocalizedString)null, "scheme10"), new Code("code11", (LocalizedString)null, "scheme11")));
        confidentialityCodes.getOuterList().add(Collections.singletonList(new Code("code12", (LocalizedString)null, "scheme12")));
        query.setConfidentialityCodes(confidentialityCodes);
        query.setFormatCodes(Arrays.asList(new Code("code13", (LocalizedString)null, "scheme13"), new Code("code14", (LocalizedString)null, "scheme14")));
        query.setDocumentEntryTypes(Collections.singletonList(DocumentEntryType.STABLE));
        return new QueryRegistry(query);
    }



    /***
     * 根据文档 UUID ID,去获取文档相关信息,获取指定的唯一文档元数据
     *
     * @author bovane
     * [queryDTO]
     * @return org.openehealth.ipf.commons.ihe.xds.core.requests.QueryRegistry
     */
    public static QueryRegistry createGetDocumentsQuery(QueryDTO queryDTO) {
        GetDocumentsQuery query = new GetDocumentsQuery();
        query.setHomeCommunityId("urn:oid:1.2.3.14.15.926");
        query.setUuids(Collections.singletonList("document01"));
        return new QueryRegistry(query);
    }


}
