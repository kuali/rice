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
import org.kuali.rice.kew.api.document.DocumentWithContent;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResult;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.framework.document.attribute.SearchableAttribute;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupCustomizer;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupResultSetConfiguration;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupResultValues;
import org.kuali.rice.kew.framework.document.lookup.NullDocumentLookupCustomizer;

import java.util.List;
import java.util.Map;

public class DataDictionaryDocumentSearchCustomizer implements SearchableAttribute, DocumentLookupCustomizer {

    private SearchableAttribute searchableAttribute;
    private DocumentLookupCustomizer documentLookupCustomizer;

    public DataDictionaryDocumentSearchCustomizer() {
        this(new DataDictionarySearchableAttribute(), new NullDocumentLookupCustomizer());
    }

    public DataDictionaryDocumentSearchCustomizer(SearchableAttribute searchableAttribute,
            DocumentLookupCustomizer documentLookupCustomizer) {
        this.searchableAttribute = searchableAttribute;
        this.documentLookupCustomizer = documentLookupCustomizer;
    }

    @Override
    public final String generateSearchContent(ExtensionDefinition extensionDefinition,
            String documentTypeName,
            WorkflowAttributeDefinition attributeDefinition) {
        return getSearchableAttribute().generateSearchContent(extensionDefinition, documentTypeName,
                attributeDefinition);
    }

    @Override
    public final List<DocumentAttribute> extractDocumentAttributes(ExtensionDefinition extensionDefinition,
            DocumentWithContent documentWithContent) {
        return getSearchableAttribute().extractDocumentAttributes(extensionDefinition, documentWithContent);
    }

    @Override
    public final List<RemotableAttributeField> getSearchFields(ExtensionDefinition extensionDefinition,
            String documentTypeName) {
        return getSearchableAttribute().getSearchFields(extensionDefinition, documentTypeName);
    }

    @Override
    public final List<RemotableAttributeError> validateSearchFieldParameters(ExtensionDefinition extensionDefinition,
            Map<String, List<String>> parameters,
            String documentTypeName) {
        return getSearchableAttribute().validateSearchFieldParameters(extensionDefinition, parameters, documentTypeName);
    }

    @Override
    public final DocumentLookupCriteria customizeCriteria(DocumentLookupCriteria documentLookupCriteria) {
        return getDocumentLookupCustomizer().customizeCriteria(documentLookupCriteria);
    }

    @Override
    public final DocumentLookupCriteria customizeClearCriteria(DocumentLookupCriteria documentLookupCriteria) {
        return getDocumentLookupCustomizer().customizeClearCriteria(documentLookupCriteria);
    }

    @Override
    public final DocumentLookupResultValues customizeResults(DocumentLookupCriteria documentLookupCriteria,
            List<DocumentLookupResult> defaultResults) {
        return getDocumentLookupCustomizer().customizeResults(documentLookupCriteria, defaultResults);
    }

    @Override
    public DocumentLookupResultSetConfiguration customizeResultSetConfiguration(
            DocumentLookupCriteria documentLookupCriteria) {
        return getDocumentLookupCustomizer().customizeResultSetConfiguration(documentLookupCriteria);
    }

    @Override
    public final boolean isCustomizeCriteriaEnabled(String documentTypeName) {
        return getDocumentLookupCustomizer().isCustomizeCriteriaEnabled(documentTypeName);
    }

    @Override
    public final boolean isCustomizeClearCriteriaEnabled(String documentTypeName) {
        return getDocumentLookupCustomizer().isCustomizeClearCriteriaEnabled(documentTypeName);
    }

    @Override
    public final boolean isCustomizeResultsEnabled(String documentTypeName) {
        return getDocumentLookupCustomizer().isCustomizeResultsEnabled(documentTypeName);
    }

    @Override
    public final boolean isCustomizeResultSetFieldsEnabled(String documentTypeName) {
        return getDocumentLookupCustomizer().isCustomizeResultSetFieldsEnabled(documentTypeName);
    }

	protected SearchableAttribute getSearchableAttribute() {
        return this.searchableAttribute;
	}

    public void setSearchableAttribute(SearchableAttribute searchableAttribute) {
        this.searchableAttribute = searchableAttribute;
    }

    protected DocumentLookupCustomizer getDocumentLookupCustomizer() {
        return this.documentLookupCustomizer;
    }

    public void setDocumentLookupCustomizer(DocumentLookupCustomizer documentLookupCustomizer) {
        this.documentLookupCustomizer = documentLookupCustomizer;
    }


}
