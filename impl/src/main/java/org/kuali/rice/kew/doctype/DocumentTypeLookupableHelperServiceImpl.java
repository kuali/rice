/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.doctype;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWPropertyConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.BeanPropertyComparator;
import org.kuali.rice.kns.util.KNSConstants;

import java.util.*;

/**
 * This is a description of what this class does - chb don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentTypeLookupableHelperServiceImpl extends
		KualiLookupableHelperServiceImpl
{

    private static final long serialVersionUID = -5162632536083995637L;

    /**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
	 */
	@Override
	protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded)
	{
        setBackLocation(fieldValues.get(KNSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KNSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KNSConstants.REFERENCES_TO_REFRESH));

        DocumentType documentType = loadDocumentTypeForSearch(fieldValues);

        String descendHierarchyValue = fieldValues.get("descendHierarchy");
        boolean descend = false;

        if( "Y".equals(descendHierarchyValue) || "Yes".equals(descendHierarchyValue))
		{
        	descend = true;
		}

        Collection docTypesFound =
        	KEWServiceLocator.getDocumentTypeService().find(documentType, fieldValues.get("parentDocType.name"), descend);

        List<? extends BusinessObject> searchResults =
        	new ArrayList<BusinessObject>( (Collection<? extends BusinessObject>)docTypesFound );

        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        return searchResults;
    }

	protected DocumentType loadDocumentTypeForSearch(Map<String, String> fieldValues) {
        DocumentType documentType = new DocumentType();

        String activeIndicator = (String) fieldValues.get(KEWPropertyConstants.ACTIVE);
        String docTypeLabel = (String) fieldValues.get(KEWPropertyConstants.DOC_TYP_LABEL);
        String documentTypeId = (String) fieldValues.get(KEWPropertyConstants.DOCUMENT_TYPE_ID);
        String docTypeName = (String) fieldValues.get(KEWPropertyConstants.NAME);
        String serviceNamespace = (String) fieldValues.get(KEWPropertyConstants.SERVICE_NAMESPACE);

        if ("Y".equals(activeIndicator)) {
            documentType.setActive(Boolean.TRUE);
        } else if ("N".equals(activeIndicator)) {
            documentType.setActive(Boolean.FALSE);
        } else {
            documentType.setActive(null);
        }

        if (docTypeLabel != null && !"".equals(docTypeLabel.trim())) {
            docTypeLabel = docTypeLabel.replace('*', '%');
            documentType.setLabel("%" + docTypeLabel.trim() + "%");
        }
        if (docTypeName != null && !"".equals(docTypeName.trim())) {
            documentType.setName(docTypeName.trim());
        }

        if (documentTypeId != null && !"".equals(documentTypeId.trim())) {
            try {
                documentType.setDocumentTypeId(new Long(documentTypeId.trim()));
            } catch (NumberFormatException e) {
                documentType.setDocumentTypeId(new Long(-1));
            }
        }
        if (!StringUtils.isBlank(serviceNamespace)) {
            documentType.setActualServiceNamespace(serviceNamespace);
        }

        return documentType;
    }
}
