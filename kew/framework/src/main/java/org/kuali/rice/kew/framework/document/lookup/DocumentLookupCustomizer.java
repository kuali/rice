/*
 * Copyright 2011 The Kuali Foundation
 *
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kew.framework.document.lookup;

import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResult;

import java.util.List;

/**
 * Provides for various ways for an application to customize the behavior of the Document Lookup functionality for
 * their document types.  Client applications should provide implementations of this interface if they need to
 * enact some of these customizations on document lookups.  These customizers then get mapped to the appropriate
 * document types via the KEW extension framework (see
 * {@link org.kuali.rice.kew.api.extension.ExtensionRepositoryService}).
 *
 * <p>The customization functionality includes the ability to process document lookup criteria before it gets submitted
 * to the search engine, effectively allowing for a level of customization to the search.</p>
 *
 * <p>This also includes the ability to define how results from document lookup should be displayed and processed.
 * Implementations of this class can be created by application owners and tied to Document Types as attributes in order
 * to customize the behavior of document lookup for documents of that type.</p>
 *
 * <p>Since some of the operations on this component could potentially add expense to the overall search process in the
 * cases where customization is only done on certain document types or only certain customization features are being
 * utilized, this interface provides for a set of boolean operations which indicate which customization
 * features should be activated by the document lookup framework for a given document type.  It's expected that KEW
 * will check each of these flags prior to invoking the corresponding method that it "gaurds" and, if the customization
 * flag is disabled, it should refrain from executing that method.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentLookupCustomizer {

    DocumentLookupCriteria customizeCriteria(DocumentLookupCriteria documentLookupCriteria);

    DocumentLookupCriteria customizeClearCriteria(DocumentLookupCriteria documentLookupCriteria);

    DocumentLookupResultValues customizeResults(DocumentLookupCriteria documentLookupCriteria, List<DocumentLookupResult> defaultResults);

    DocumentLookupResultSetConfiguration customizeResultSetConfiguration(DocumentLookupCriteria documentLookupCriteria);

    boolean isCustomizeCriteriaEnabled(String documentTypeName);

    boolean isCustomizeClearCriteriaEnabled(String documentTypeName);

    boolean isCustomizeResultsEnabled(String documentTypeName);

    boolean isCustomizeResultSetFieldsEnabled(String documentTypeName);

}
