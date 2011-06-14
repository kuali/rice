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
package org.kuali.rice.krad.workflow.attribute;

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
import org.kuali.rice.kew.docsearch.StandardDocumentSearchGenerator;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchResultProcessor;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.web.ui.Row;

public class DataDictionaryDocumentSearchCustomizer implements SearchableAttribute, DocumentSearchGenerator, DocumentSearchResultProcessor {
    // SEARCH GENERATOR IMPLEMENTATION
    protected StandardDocumentSearchGenerator documentSearchGenerator = null;
    // SEARCHABLE ATTRIBUTE IMPLEMENTATION
    protected SearchableAttribute searchableAttribute = null;
    // SEARCH RESULT PROCESSOR IMPLEMENTATION
    protected DocumentSearchResultProcessor searchResultProcessor = null;
    
    public DocSearchCriteriaDTO clearSearch(DocSearchCriteriaDTO searchCriteria) {
        return getDocumentSearchGenerator().clearSearch(searchCriteria);
    }

    public String generateSearchSql(DocSearchCriteriaDTO searchCriteria) {
        return getDocumentSearchGenerator().generateSearchSql(searchCriteria);
    }

    public int getDocumentSearchResultSetLimit() {
        return getDocumentSearchGenerator().getDocumentSearchResultSetLimit();
    }

    public List<WorkflowServiceError> performPreSearchConditions(String principalId, DocSearchCriteriaDTO searchCriteria) {
        return getDocumentSearchGenerator().performPreSearchConditions(principalId, searchCriteria);
    }

    public List<DocSearchDTO> processResultSet(Statement searchAttributeStatement, ResultSet resultSet, DocSearchCriteriaDTO searchCriteria, String principalId) throws SQLException {
        return getDocumentSearchGenerator().processResultSet(searchAttributeStatement, resultSet, searchCriteria, principalId);
    }

    @Deprecated
    public List<DocSearchDTO> processResultSet(Statement searchAttributeStatement, ResultSet resultSet, DocSearchCriteriaDTO searchCriteria) throws SQLException {
        return getDocumentSearchGenerator().processResultSet(searchAttributeStatement, resultSet, searchCriteria);
    }    

    public void setSearchingUser(String principalId) {
    	getDocumentSearchGenerator().setSearchingUser(principalId);
    }

    public List<WorkflowServiceError> validateSearchableAttributes(DocSearchCriteriaDTO searchCriteria) {
        return getDocumentSearchGenerator().validateSearchableAttributes(searchCriteria);
    }

    public boolean isProcessResultSet() {
        return getDocumentSearchGenerator().isProcessResultSet();
    }
    public void setProcessResultSet(boolean isProcessResultSet){
    	getDocumentSearchGenerator().setProcessResultSet(isProcessResultSet);
    }
    public MessageMap getMessageMap(DocSearchCriteriaDTO searchCriteria) {
        return getDocumentSearchGenerator().getMessageMap(searchCriteria);
    }    

    public String getSearchContent(DocumentSearchContext documentSearchContext) {
        return getSearchableAttribute().getSearchContent(documentSearchContext);
    }

    public List<Row> getSearchingRows(DocumentSearchContext documentSearchContext) {
        return getSearchableAttribute().getSearchingRows(documentSearchContext);
    }

    public List<SearchableAttributeValue> getSearchStorageValues(DocumentSearchContext documentSearchContext) {
        return getSearchableAttribute().getSearchStorageValues(documentSearchContext);
    }

    public List<WorkflowAttributeValidationError> validateUserSearchInputs(Map<Object, Object> paramMap, DocumentSearchContext searchContext) {
        return getSearchableAttribute().validateUserSearchInputs(paramMap, searchContext);
    }

	/**
	 * This overridden method calls the currently set searchResultProcessor's processIntoFinalResults
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentSearchResultProcessor#processIntoFinalResults(java.util.List, org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO, java.lang.String)
	 */
	public DocumentSearchResultComponents processIntoFinalResults(
			List<DocSearchDTO> docSearchResultRows,
			DocSearchCriteriaDTO criteria, String principalId)
			throws SQLException {
		
		return this.getSearchResultProcessor().processIntoFinalResults(docSearchResultRows, criteria, principalId);
	}

	/**
	 * This overridden method returns the searchResultProcessor
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentSearchResultProcessor#setProcessFinalResults(boolean)
	 */
	public void setProcessFinalResults(boolean isProcessFinalResults) {
		this.getSearchResultProcessor().setProcessFinalResults(isProcessFinalResults);
		
	}

	/**
	 * This overridden method returns if the searchResultProcessor has processed final results
	 * 
	 * @see org.kuali.rice.kew.docsearch.DocumentSearchResultProcessor#isProcessFinalResults()
	 */
	public boolean isProcessFinalResults() {
		return this.getSearchResultProcessor().isProcessFinalResults();
	}
	
	/**
	 * @param documentSearchGenerator the documentSearchGenerator to set
	 */
	public void setDocumentSearchGenerator(
			StandardDocumentSearchGenerator documentSearchGenerator) {
		this.documentSearchGenerator = documentSearchGenerator;
	}
	
	/**
	 * @param searchResultProcessor the searchResultProcessor to set
	 */
	public void setSearchResultProcessor(
			DocumentSearchResultProcessor searchResultProcessor) {
		this.searchResultProcessor = searchResultProcessor;
	}

	/**
	 * 
	 * This method sets a list of searchable attributes on the DocumentSearchGenerator.
	 * Do not confuse this with "setSearchableAttribute"
	 * 
	 * @param searchableAttributes
	 */
	public void setSearchableAttributes(List<SearchableAttribute> searchableAttributes) {
		getDocumentSearchGenerator().setSearchableAttributes(searchableAttributes);
    }
	
	/**
	 * @param searchableAttribute the searchableAttribute to set
	 */
	public void setSearchableAttribute(SearchableAttribute searchableAttribute) {
		this.searchableAttribute = searchableAttribute;
	}
	
	/**
	 * @return the searchableAttribute
	 */
	public SearchableAttribute getSearchableAttribute() {
		if(this.searchableAttribute == null){
			this.searchableAttribute = new DataDictionarySearchableAttribute();
		}
		return this.searchableAttribute;
	}

	/**
	 * @return the documentSearchGenerator
	 */
	public StandardDocumentSearchGenerator getDocumentSearchGenerator() {
		if(this.documentSearchGenerator == null){
			this.documentSearchGenerator = new StandardDocumentSearchGenerator();
		}
		return this.documentSearchGenerator;
	}

	/**
	 * @return the searchResultProcessor
	 */
	public DocumentSearchResultProcessor getSearchResultProcessor() {
		if(this.searchResultProcessor == null){
			this.searchResultProcessor = new StandardDocumentSearchResultProcessor();
		}
		return this.searchResultProcessor;
	}

	
	

}
