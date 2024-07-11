/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.tutorials.xds

import org.apache.camel.builder.RouteBuilder
import org.openehealth.ipf.commons.ihe.xds.core.metadata.Association
import org.openehealth.ipf.commons.ihe.xds.core.requests.ProvideAndRegisterDocumentSet
import org.openehealth.ipf.commons.ihe.xds.core.requests.RegisterDocumentSet
import org.openehealth.ipf.commons.ihe.xds.core.responses.Response
import org.openehealth.ipf.commons.ihe.xds.core.responses.Status
import org.openehealth.ipf.tutorials.xds.processor.Iti41Processor
import org.openehealth.ipf.tutorials.xds.processor.Iti42Processor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.activation.DataHandler
import java.util.function.Function
import java.util.function.Supplier

import static org.openehealth.ipf.commons.ihe.xds.core.metadata.AssociationType.*
import static org.openehealth.ipf.commons.ihe.xds.core.metadata.AvailabilityStatus.APPROVED
import static org.openehealth.ipf.commons.ihe.xds.core.metadata.AvailabilityStatus.DEPRECATED
import static org.openehealth.ipf.commons.ihe.xds.core.requests.RegisterDocumentSet.supportiveBuilderWith
import static org.openehealth.ipf.commons.ihe.xds.core.validate.ValidationMessage.*
import static org.openehealth.ipf.platform.camel.ihe.xds.XdsCamelValidators.iti41RequestValidator
import static org.openehealth.ipf.platform.camel.ihe.xds.XdsCamelValidators.iti42RequestValidator

/**
 * Route builder for ITI-41 and -42.
 * @author Jens Riemschneider
 */
@Component
class Iti4142RouteBuilder extends RouteBuilder {

    private final static Logger log = LoggerFactory.getLogger(Iti4142RouteBuilder.class)

    @Override
    void configure() throws Exception {
        errorHandler(noErrorHandler())

        // Entry point for Provide and Register Document Set
        from('xds-iti41:xds-iti41'+
                "?inInterceptors=#serverInLogger" +
                "&inFaultInterceptors=#serverInLogger" +
                "&outInterceptors=#serverOutLogger" +
                "&outFaultInterceptors=#serverOutLogger")
            .logExchange(log) { 'received iti41: ' + it.in.getBody(ProvideAndRegisterDocumentSet.class) }
            .process(new Iti41Processor())
            // Validate and convert the request
            .process(iti41RequestValidator())
            .transform().exchange ({exchange ->
                [ 'req': exchange.in.getBody(ProvideAndRegisterDocumentSet.class), 'uuidMap': [:] ]} as Function)
            // Make the dataHandlers re-readable
            .to('direct:makeDocsReReadable')
            // Further validation based on the registry content
            .to('direct:checkForAssociationToDeprecatedObject', 'direct:checkPatientIds', 'direct:checkHashAndSize')
            // Store the individual entries contained in the request
            .to('direct:storeDocs')
            .to('direct:updateDocEntriesFromProvide')
            .log('Transform to RegisterDocumentSetRequest')
            // Transform to ITI-42 RegisterDocumentSet Request
            .transform().body({entry -> supportiveBuilderWith(entry.req.submissionSet)
                                            .withDocuments(entry.req.documents*.documentEntry)
                                            .withFolders(entry.req.folders)
                                            .withAssociations(entry.req.associations).build()} as Function)
//            .setHeader("port", {"" + getPort()}  as Supplier)
            .setHeader("port", {"" + 9091}  as Supplier)
            .log('Send to ITI-42 endpoint: xds-iti42://localhost:${header.port}/services/xds-iti42')
            .toD('xds-iti42://localhost:${header.port}/services/xds-iti42')

        // Entry point for Register Document Set
        from('xds-iti42:xds-iti42'+
                "?inInterceptors=#serverInLogger" +
                "&inFaultInterceptors=#serverInLogger" +
                "&outInterceptors=#serverOutLogger" +
                "&outFaultInterceptors=#serverOutLogger")
            .logExchange(log) { 'received iti42: ' + it.in.getBody(RegisterDocumentSet.class) }
            .process(new Iti42Processor())
            // Validate and convert the request
            .process(iti42RequestValidator())
            .transform().exchange ( {exchange ->
                [ 'req': exchange.in.getBody(RegisterDocumentSet.class), 'uuidMap': [:] ]} as Function
            )
            // Further validation based on the registry content
            .to('direct:checkForAssociationToDeprecatedObject', 'direct:checkPatientIds', 'direct:checkHash')
            // Store the individual entries contained in the request
            .multicast().to(
                'direct:storeDocEntriesFromRegister',
                'direct:storeFolders',
                'direct:storeSubmissionSet',
                'direct:storeAssociations')
            .end()
            // Create success response
            .transform ( constant(new Response(Status.SUCCESS)) )

        // Deprecated documents should not be transformed any further
        // 已弃用的文档不应该进一步转换
        from('direct:checkForAssociationToDeprecatedObject')
            .splitEntries {
                it.req.associations.findAll { assoc ->
                    assoc.associationType == APPEND || assoc.associationType == TRANSFORM
                }
            }
            .search(SearchResult.DOC_ENTRY).uuid('entry.targetUuid').status(DEPRECATED).into('deprecatedDocs')
            .splitEntries { it.deprecatedDocs }
            .fail(DEPRECATED_OBJ_CANNOT_BE_TRANSFORMED)

        // All entries in the request must have the same patient ID, no matter if
        // they are only referenced or contained in the request itself. Also check
        // for a patient ID that we shouldn't store documents for.
        // 请求中的所有条目必须具有相同的患者 ID,无论它们是仅被引用还是包含在请求本身中。
        // 同时也要检查我们不应该为之存储文档的患者 ID。
        from('direct:checkPatientIds')
            .choice().when().body({ body -> body.req.submissionSet.patientId.id == '1111111' } as Function)
                .fail(UNKNOWN_PATIENT_ID)
                .otherwise()
            .end()
            .search([SearchResult.DOC_ENTRY, SearchResult.FOLDER])
                .referenced('req.associations')
                .withoutPatientId('req.submissionSet.patientId')
                .into('otherPatientsEntries')
            .splitEntries { it.otherPatientsEntries }
            .fail(FOLDER_PATIENT_ID_WRONG)

        // Document submissions that specify a size and hash must have correct values
        // 指定大小和哈希的文档提交必须具有正确的值
        from('direct:checkHashAndSize')
            .splitEntries { it.req.documents }
            .choice()
                .when().body({ body ->
                    def hash = body.entry.documentEntry.hash
                    hash != null && hash != ContentUtils.sha1(body.entry.getContent(DataHandler))
                } as Function).fail(INCORRECT_HASH)
            .end()
            .choice()
            .when().body({ body ->
                    def size = body.entry.documentEntry.size
                    size != null && size != ContentUtils.size(body.entry.getContent(DataHandler))
                } as Function).fail(INCORRECT_SIZE)
            .end()

        // Resubmitted documents must have the same hash code as the version already in the store
        // 重新提交的文档必须具有与已存储版本相同的哈希码
        from('direct:checkHash')
            .splitEntries { it.req.documentEntries }
            .search(SearchResult.DOC_ENTRY).uniqueId('entry.uniqueId').withoutHash('entry.hash').into('docsWithOtherHash')
            .splitEntries { it.docsWithOtherHash }
            .fail(DIFFERENT_HASH_CODE_IN_RESUBMISSION)

        // Make documents re-readable, otherwise we loose the content of the stream after the first read
        // 使文档可重复读取,否则我们将在第一次读取后丢失流的内容
        from('direct:makeDocsReReadable')
            .splitEntries { it.req.documents }
            .processBody {
                def dataHandler = it.entry.getContent(DataHandler)
                def content = ContentUtils.getContent(dataHandler)
                it.entry.setContent(DataHandler,
                        new DataHandler(new ByteArrayDataSource(content, dataHandler.contentType)))
            }

        // Put all documents in the store
        // 将所有文档放入存储
        from('direct:storeDocs')
            .splitEntries { it.req.documents }
            .store()

        // calculate hash + size
        // 计算哈希 + 大小
        from('direct:updateDocEntriesFromProvide')
            .splitEntries { it.req.documents }
            // Calculate some additional meta data values
            .updateWithRepositoryData()
            .processBody { it.entry = it.entry.documentEntry }

        // Put all document entries in the store
        // 将所有文档条目放入存储
        from('direct:storeDocEntriesFromRegister')
            .splitEntries { it.req.documentEntries }
            .to('direct:store')

        // Put all folders in the store
        // 将所有文件夹放入存储
        from('direct:storeFolders')
            .splitEntries { it.req.folders }
            .updateTimeStamp()
            .to('direct:store')

        // Put the submission set in the store
        // 将提交集放入存储
        from('direct:storeSubmissionSet')
            .processBody { it.entry = it.req.submissionSet }
            .to('direct:store')

        // Finalizes the new entry and stores it
        // 完成新条目的存储
        from('direct:store')
            .status(APPROVED)
            .assignUuid()
            .store()

        // Put all associations in the store
        // 存储所有的 associations
        from('direct:storeAssociations')
            .splitEntries { it.req.associations }
            .assignUuid()
            .changeAssociationUuids()
            .store()
            .multicast().to('direct:checkReplace', 'direct:updateTime')

        // Replace associations must deprecate the replaced document and copy   
        // the new document into all folders of the original one
        // 替换关联必须弃用被替换的文档,并将新文档复制到原始文档的所有文件夹中
        from('direct:checkReplace')
            .choice().when().body({ body -> body.entry.associationType.isReplace() } as Function )
                .multicast().to('direct:copyFolderMembership', 'direct:deprecateTargetDocs').end()
                .otherwise()
            .end()

        // Copy the new document into all folders of the original one
        from('direct:copyFolderMembership')
            .search(SearchResult.ASSOC_SOURCE).hasMember().targetUuid('entry.targetUuid').into('containers')
            .search(SearchResult.FOLDER).uuids('containers').into('foldersContainingTarget')
            .processBody { it.assoc = it.entry }
            .splitEntries { it.foldersContainingTarget }
            .updateTimeStamp()
            .processBody {
                it.entry = new Association(HAS_MEMBER,
                        'urn:uuid:' + UUID.randomUUID(),
                        it.entry.entryUuid,
                        it.assoc.sourceUuid)
            }
            .store()

        // Deprecate all replaced documents
        // 弃用所有被替换的文档
        from('direct:deprecateTargetDocs')
            .search(SearchResult.DOC_ENTRY).uuid('entry.targetUuid').into('targetDocs')
            .splitEntries { it.targetDocs }
            .to('direct:deprecateDocEntry')

        // Deprecate a single replaced document
        // 弃用单个被替换的文档
        from('direct:deprecateDocEntry')
            .logExchange(log) { 'deprecating: ' + it.in.body.entry.entryUuid }
            .status(DEPRECATED)
            // Any other transformation or addendum to the deprecated document must
            // be deprecated as well. Clear fields from previous usage.
            .processBody { it.targetUuidsOfDeprecated = [] }
            .processBody { it.targetsOfDeprecated = [] }
            .search(SearchResult.ASSOC_SOURCE)
                .targetUuid('entry.entryUuid')
                .isOfTypes([TRANSFORM, APPEND])
                .into('targetUuidsOfDeprecated')
            .search(SearchResult.DOC_ENTRY).uuids('targetUuidsOfDeprecated').into('targetsOfDeprecated')
            .splitEntries { it.targetsOfDeprecated }
            .to('direct:deprecateDocEntry')

        // Any folders that are related to the association need an update of their time stamp
        // 与关联相关的任何文件夹都需要更新其时间戳
        from('direct:updateTime')
            .search(SearchResult.FOLDER).uuid('entry.sourceUuid').into('folders')
            .splitEntries { it.folders }
            .updateTimeStamp()
    }
}
