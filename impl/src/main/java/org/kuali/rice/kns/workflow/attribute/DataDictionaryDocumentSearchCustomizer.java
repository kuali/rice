/*
 * Copyright 2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.workflow.attribute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchContext;
import org.kuali.rice.kew.docsearch.DocumentSearchGenerator;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.docsearch.DocumentSearchResultProcessor;
import org.kuali.rice.kew.docsearch.SearchableAttribute;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.kns.web.ui.Row;

public class DataDictionaryDocumentSearchCustomizer implements DocumentSearchResultProcessor, SearchableAttribute, DocumentSearchGenerator {
    
    private DocumentSearchGenerator documentSearchGenerator; 
    private DocumentSearchResultProcessor documentSearchResultProcessor;
    private SearchableAttribute searchableAttribute = new DataDictionarySearchableAttribute();

    protected DocumentSearchGenerator getStandardDocumentSearchGenerator() { 
	    if (documentSearchGenerator == null) { 
	    	documentSearchGenerator = KEWServiceLocator.getDocumentSearchService().getStandardDocumentSearchGenerator(); 
	    } 
	    return documentSearchGenerator; 
    } 

    protected DocumentSearchResultProcessor getStandardDocumentSearchResultProcessor() { 
	    if (documentSearchResultProcessor == null) { 
	    	documentSearchResultProcessor = KEWServiceLocator.getDocumentSearchService().getStandardDocumentSearchResultProcessor(); 
	    } 
	    return documentSearchResultProcessor; 
    } 
    public DocSearchCriteriaDTO clearSearch(DocSearchCriteriaDTO searchCriteria) {
        return getStandardDocumentSearchGenerator().clearSearch(searchCriteria);
    }

    public String generateSearchSql(DocSearchCriteriaDTO searchCriteria) {
        return getStandardDocumentSearchGenerator().generateSearchSql(searchCriteria);
    }

    public int getDocumentSearchResultSetLimit() {
        return getStandardDocumentSearchGenerator().getDocumentSearchResultSetLimit();
    }

    public List<WorkflowServiceError> performPreSearchConditions(String principalId, DocSearchCriteriaDTO searchCriteria) {
        return getStandardDocumentSearchGenerator().performPreSearchConditions(principalId, searchCriteria);
    }

    public List<DocSearchDTO> processResultSet(Statement searchAttributeStatement, ResultSet resultSet, DocSearchCriteriaDTO searchCriteria, String principalId) throws SQLException {
        return getStandardDocumentSearchGenerator().processResultSet(searchAttributeStatement, resultSet, searchCriteria, principalId);
    }

    @Deprecated
    public List<DocSearchDTO> processResultSet(Statement searchAttributeStatement, ResultSet resultSet, DocSearchCriteriaDTO searchCriteria) throws SQLException {
        return getStandardDocumentSearchGenerator().processResultSet(searchAttributeStatement, resultSet, searchCriteria);
    }    

    public void setSearchingUser(String principalId) {
    	getStandardDocumentSearchGenerator().setSearchingUser(principalId);
    }

    public List<WorkflowServiceError> validateSearchableAttributes(DocSearchCriteriaDTO searchCriteria) {
        return getStandardDocumentSearchGenerator().validateSearchableAttributes(searchCriteria);
    }

    public boolean isProcessResultSet() {
        return getStandardDocumentSearchGenerator().isProcessResultSet();
    }
    public void setProcessResultSet(boolean isProcessResultSet){
    	getStandardDocumentSearchGenerator().setProcessResultSet(isProcessResultSet);
    }
    public MessageMap getMessageMap(DocSearchCriteriaDTO searchCriteria) {
        return getStandardDocumentSearchGenerator().getMessageMap(searchCriteria);
    }

    public String getSearchContent(DocumentSearchContext documentSearchContext) {
        return searchableAttribute.getSearchContent(documentSearchContext);
    }

    public List<Row> getSearchingRows(DocumentSearchContext documentSearchContext) {
        return searchableAttribute.getSearchingRows(documentSearchContext);
    }

    public List<SearchableAttributeValue> getSearchStorageValues(DocumentSearchContext documentSearchContext) {
        return searchableAttribute.getSearchStorageValues(documentSearchContext);
    }

    public List<WorkflowAttributeValidationError> validateUserSearchInputs(Map<Object, Object> paramMap, DocumentSearchContext searchContext) {
        return searchableAttribute.validateUserSearchInputs(paramMap, searchContext);
    }

	public boolean isProcessFinalResults() {		
		return getStandardDocumentSearchResultProcessor().isProcessFinalResults();
	}

	public DocumentSearchResultComponents processIntoFinalResults(
			List<DocSearchDTO> docSearchResultRows,
			DocSearchCriteriaDTO criteria, String principalId)
			throws SQLException {
		
		return getStandardDocumentSearchResultProcessor().processIntoFinalResults(docSearchResultRows, criteria, principalId);
	}

	public void setProcessFinalResults(boolean isProcessFinalResults) {
		getStandardDocumentSearchResultProcessor().setProcessFinalResults(isProcessFinalResults);
		
	}

}
