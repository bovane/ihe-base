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
import org.openehealth.ipf.commons.ihe.xds.core.requests.QueryRegistry
import org.openehealth.ipf.commons.ihe.xds.core.requests.query.QueryReturnType
import org.openehealth.ipf.commons.ihe.xds.core.responses.QueryResponse
import org.openehealth.ipf.commons.ihe.xds.core.validate.ValidationMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import java.util.function.Function

import static org.openehealth.ipf.commons.ihe.xds.core.requests.query.QueryType.*
import static org.openehealth.ipf.commons.ihe.xds.core.responses.Status.SUCCESS
import static org.openehealth.ipf.platform.camel.ihe.xds.XdsCamelValidators.iti18RequestValidator
import static org.openehealth.ipf.platform.camel.ihe.xds.XdsCamelValidators.iti18ResponseValidator
import static org.openehealth.ipf.tutorials.xds.SearchResult.*
/**
 * Route builder for ITI-18.
 * @author Jens Riemschneider
 */
@Component
class Iti18RouteBuilder extends RouteBuilder {
    private final static Logger log = LoggerFactory.getLogger(Iti18RouteBuilder.class)
    
    static final def DOCS = 'resp.documentEntries'
    static final def FOLDERS = 'resp.folders'
    static final def SETS = 'resp.submissionSets'
    static final def ASSOCS = 'resp.associations'
    static final def QUERY = 'req.query'
    static final def UNIQUE_ID = 'req.query.uniqueId'
    static final def UNIQUE_IDS = 'req.query.uniqueIds'
    static final def UUID = 'req.query.uuid'
    static final def UUIDS = 'req.query.uuids'
    static final def CONF_CODES = 'req.query.confidentialityCodes'
    static final def FORMAT_CODES = 'req.query.formatCodes'
    static final def ASSOC_TYPES = 'req.query.associationTypes'
    static final def PATIENT_ID = 'req.query.patientId'
    
    @Override
    void configure() throws Exception {
        errorHandler(noErrorHandler())
        
        // Entry point for Stored Query

        from('xds-iti18:xds-iti18'+
                "?inInterceptors=#serverInLogger" +
                "&inFaultInterceptors=#serverInLogger" +
                "&outInterceptors=#serverOutLogger" +
                "&outFaultInterceptors=#serverOutLogger")
            .logExchange(log) { 'received iti18: ' + it.in.getBody(QueryRegistry.class) }
            .process(iti18RequestValidator())
            .transform().exchange({exchange ->
                [ 'req': exchange.in.getBody(QueryRegistry.class), 'resp': new QueryResponse(SUCCESS) ]
            } as Function)
            // Dispatch to the correct query implementation
            .choice()
                .when().exchange { queryType(it) == FIND_DOCUMENTS }.to('direct:findDocs')
                .when().exchange { queryType(it) == FIND_SUBMISSION_SETS }.to('direct:findSets')
                .when().exchange { queryType(it) == FIND_FOLDERS }.to('direct:findFolders')
                .when().exchange { queryType(it) == GET_SUBMISSION_SET_AND_CONTENTS }.to('direct:getSetAndContents')
                .when().exchange { queryType(it) == GET_DOCUMENTS }.to('direct:getDocs')
                .when().exchange { queryType(it) == GET_FOLDER_AND_CONTENTS }.to('direct:getFolderAndContents')
                .when().exchange { queryType(it) == GET_FOLDERS }.to('direct:getFolders')
                .when().exchange { queryType(it) == GET_SUBMISSION_SETS }.to('direct:getSets')
                .when().exchange { queryType(it) == GET_ASSOCIATIONS }.to('direct:getAssocs')                
                .when().exchange { queryType(it) == GET_DOCUMENTS_AND_ASSOCIATIONS }.to('direct:getDocsAndAssocs')
                .when().exchange { queryType(it) == GET_FOLDERS_FOR_DOCUMENT }.to('direct:getFoldersForDoc')
                .when().exchange { queryType(it) == GET_RELATED_DOCUMENTS }.to('direct:getRelatedDocs')                
                .otherwise().fail(ValidationMessage.UNKNOWN_QUERY_TYPE)
            .end()
            // Convert to object references if requested
            .choice()
                .when().body({body -> body.req.returnType == QueryReturnType.OBJECT_REF } as Function )
                    .to('direct:convertToObjRefs')
                .otherwise()
            .end()
            .transform().body({body -> body.resp } as Function)
            .process(iti18ResponseValidator())     // This includes the check for RESULT_NOT_SINGLE_PATIENT
            .logExchange(log) { 'response iti18: ' + it.in.body }

        // Converts all results into ObjectReferences
        from('direct:convertToObjRefs')
            .convertToObjectRefs{it.resp.submissionSets}
            .convertToObjectRefs{it.resp.documentEntries}
            .convertToObjectRefs{it.resp.folders}
            .convertToObjectRefs{it.resp.associations}
            
        // FindDocumentsQuery logic
        from('direct:findDocs')
            .search(DOC_ENTRY).byQuery(QUERY).patientId(PATIENT_ID).into(DOCS)

        // FindSubmissionSetsQuery logic
        from('direct:findSets')
            .search(SUB_SET).byQuery(QUERY).patientId(PATIENT_ID).into(SETS)

        // FindFoldersQuery logic
        from('direct:findFolders')
            .search(FOLDER).byQuery(QUERY).patientId(PATIENT_ID).into(FOLDERS)

        // GetDocumentsQuery logic
        from('direct:getDocs')
            .search(DOC_ENTRY).uniqueIds(UNIQUE_IDS).uuids(UUIDS).into(DOCS)

        // GetFoldersQuery logic
        from('direct:getFolders')
            .search(FOLDER).uniqueIds(UNIQUE_IDS).uuids(UUIDS).into(FOLDERS)

        // GetDocumentsAndAssociationsQuery logic
        from('direct:getDocsAndAssocs')
            .to('direct:getDocs')
            .search(ASSOC).targets(DOCS).into(ASSOCS)
            .search(ASSOC).sources(DOCS).into(ASSOCS)
            
        // GetFoldersForDocumentQuery logic
        from('direct:getFoldersForDoc')
            .search(DOC_ENTRY).uniqueId(UNIQUE_ID).uuid(UUID).into('doc')
            .search(ASSOC_SOURCE).hasMember().targets('doc').into('sources')
            .search(FOLDER).uuids('sources').into(FOLDERS)
        
        // GetRelatedDocumentsQuery logic
        from('direct:getRelatedDocs')
            .search(DOC_ENTRY).uniqueId(UNIQUE_ID).uuid(UUID).into('doc')
            .search(ASSOC_SOURCE).types(ASSOC_TYPES).targets('doc').into('sources')
            .search(DOC_ENTRY).uuids('sources').into('sourceDocs')
            .search(ASSOC_TARGET).types(ASSOC_TYPES).sources('doc').into('targets')
            .search(DOC_ENTRY).uuids('targets').into('targetDocs')
            .search(ASSOC).types(ASSOC_TYPES).sources('sourceDocs').targets('doc').into(ASSOCS)
            .search(ASSOC).types(ASSOC_TYPES).sources('doc').targets('targetDocs').into(ASSOCS)
            .search(DOC_ENTRY).referenced(ASSOCS).into(DOCS)            
            
        // GetSubmissionSetQuery logic
        from('direct:getSets')
            .search(ASSOC_SOURCE).hasMember().targetUuids(UUIDS).into('sources') 
            .search(SUB_SET).uuids('sources').into(SETS)
            .search(ASSOC).hasMember().sources(SETS).targetUuids(UUIDS).into(ASSOCS) 
            
        // GetAssociationsQuery logic
        from('direct:getAssocs')
            .search(ASSOC).sourceUuids(UUIDS).into(ASSOCS)
            .search(ASSOC).targetUuids(UUIDS).into(ASSOCS)
        
        // GetFolderAndContentsQuery logic
        from('direct:getFolderAndContents')
            .search(FOLDER).uniqueId(UNIQUE_ID).uuid(UUID).into(FOLDERS)
            .search(ASSOC_TARGET).hasMember().sources(FOLDERS).into('members')
            .search(DOC_ENTRY).confCodes(CONF_CODES).formatCodes(FORMAT_CODES).uuids('members').into(DOCS)
            .search(ASSOC).hasMember().sources(FOLDERS).targets(DOCS).into(ASSOCS)
            
        // GetSubmissionSetAndContentsQuery logic
        from('direct:getSetAndContents')
            .search(SUB_SET).uniqueId(UNIQUE_ID).uuid(UUID).into(SETS)
            .search(ASSOC_TARGET).hasMember().sources(SETS).into('members')
            .search(DOC_ENTRY).confCodes(CONF_CODES).formatCodes(FORMAT_CODES).uuids('members').into(DOCS)
            .search(FOLDER).uuids('members').into(FOLDERS)
            .search(ASSOC).hasMember().sources(SETS).targets(DOCS).into(ASSOCS)
            .search(ASSOC).hasMember().sources(SETS).targets(FOLDERS).into(ASSOCS)
            .search(ASSOC).hasMember().sources(FOLDERS).targets(DOCS).into(ASSOCS)
            .search(ASSOC).hasMember().sources(SETS).targets(ASSOCS).into(ASSOCS)
    }

    static def queryType(exchange) { exchange.in.body.req.query.type }
}

