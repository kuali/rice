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

import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.jaxb.MultiValuedStringMapAdapter;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchGenerator;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.docsearch.DocumentSearchResultProcessor;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchGenerator;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchResultProcessor;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.kew.framework.document.lookup.DocumentSearchContext;
import org.kuali.rice.kew.framework.document.lookup.SearchableAttribute;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.util.MessageMap;

import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

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

    public List<RemotableAttributeError> validateSearchableAttributes(DocSearchCriteriaDTO searchCriteria) {
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

    @Override
    public String generateSearchContent(ExtensionDefinition extensionDefinition,
            String documentTypeName,
            WorkflowAttributeDefinition attributeDefinition) {
        return getSearchableAttribute().generateSearchContent(extensionDefinition, documentTypeName, attributeDefinition);
    }

    @Override
    public List<DocumentAttribute> getDocumentAttributes(ExtensionDefinition extensionDefinition,
            DocumentSearchContext documentSearchContext) {
        return getSearchableAttribute().getDocumentAttributes(extensionDefinition, documentSearchContext);
    }

    @Override
    public List<RemotableAttributeField> getSearchFields(ExtensionDefinition extensionDefinition,
            String documentTypeName) {
        return getSearchableAttribute().getSearchFields(extensionDefinition, documentTypeName);
    }

    @Override
    public List<RemotableAttributeError> validateSearchFieldParameters(ExtensionDefinition extensionDefinition,
            Map<String, List<String>> parameters,
            String documentTypeName) {
        return getSearchableAttribute().validateSearchFieldParameters(extensionDefinition, parameters, documentTypeName);
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
