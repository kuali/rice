/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.ParameterDetailType;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.datadictionary.TransactionalDocumentEntry;
import org.kuali.core.util.BeanPropertyComparator;

public class ParameterDetailTypeLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static HashSet<ParameterDetailType> components = new HashSet<ParameterDetailType>(); 
    
    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String,String> fieldValues) {

        List baseLookup = super.getSearchResults(fieldValues);
        getDataDictionaryService().getDataDictionary().forceCompleteDataDictionaryLoad();
        
        // all step beans
        // all BO beans
        // all trans doc beans 
        
        if ( components.isEmpty() ) {
            // get all components from the DD and make a list.
            // hold statically since it won't change after DD load is complete
            DataDictionary dd = getDataDictionaryService().getDataDictionary();
            for ( String boClassName : dd.getBusinessObjectClassNames() ) {
                String simpleName = StringUtils.substringAfterLast(boClassName, ".");
            	if ( StringUtils.isBlank( simpleName ) ) continue;
                components.add( new ParameterDetailType( "N/A", simpleName, simpleName ) );
            }
            Map<String,DocumentEntry> ddDocuments = dd.getDocumentEntries();
            for ( String transDocName : ddDocuments.keySet() ) {
            	if ( StringUtils.isBlank( transDocName ) ) continue;
                DocumentEntry doc = ddDocuments.get(transDocName);
                if ( doc instanceof TransactionalDocumentEntry ) {
                    components.add( new ParameterDetailType( "N/A", doc.getDocumentTypeName(), doc.getLabel() ) );
                }
            }
        }
        
        if ( baseLookup instanceof CollectionIncomplete ) {
            long originalCount = ((CollectionIncomplete)baseLookup).getActualSizeIfTruncated();
            baseLookup = new ArrayList( baseLookup );
            baseLookup.addAll( components );
            baseLookup = new CollectionIncomplete( baseLookup, originalCount + components.size() );
        }
        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(baseLookup, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        
        return baseLookup;
    }
    
    /**
     * Prevent the edit and copy links on virtual detail types.
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.core.bo.BusinessObject)
     */
    @Override
    public String getActionUrls(BusinessObject businessObject) {
    	if ( !((ParameterDetailType)businessObject).isVirtualDetailType()  ) {
    		return super.getActionUrls( businessObject );
    	} else {
    		return "";
    	}
    }
}
