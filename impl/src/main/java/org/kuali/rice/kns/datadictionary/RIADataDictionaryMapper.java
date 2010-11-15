/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kew.ria.bo.RIADocTypeMap;
import org.kuali.rice.kew.ria.document.RIADocument;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * DataDictionaryMapper impl that consults the RIA table for reverse
 * workflow document type to DocumentEntry lookup
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RIADataDictionaryMapper extends DataDictionaryIndexMapper {
	private static final Logger LOG = Logger.getLogger(RIADataDictionaryMapper.class);
	
	/**
	 * The simple name of the RIADocument class.  It is the simple name that is used
	 * as a key by the DDIndex 
	 */
	public static final String RIA_KNS_DOC_CLASS = RIADocument.class.getSimpleName();
	public static final String RIA_DOC_TYPE_NAME = "riaDocTypeName";
	
	/**
	 * Overridden to consult the RIA document service
	 * @see org.kuali.rice.kns.datadictionary.DataDictionaryMapper#getDocumentEntry(org.kuali.rice.kns.datadictionary.DataDictionaryIndex, java.lang.String)
	 */
	public DocumentEntry getDocumentEntry(DataDictionaryIndex index,
			String documentTypeDDKey) {
		// this is a hack (for now) that allows the dynamic overriding/rewriting
		// of the document type being queried (and thereby the KNS doc entry returned)
		if (isMapped(documentTypeDDKey)) {
			documentTypeDDKey = RIA_KNS_DOC_CLASS; // look it up directly by class name
		}

		return super.getDocumentEntry(index, documentTypeDDKey);
	}

	/**
	 * Checks if the given documentTypeName is in the mapping table.
	 * 
	 * @return true if documentTypeName exists in the mapping table and false otherwise
	 */
	private boolean isMapped(String documentTypeName) {
		Map map = new HashMap(1);
		map.put(RIA_DOC_TYPE_NAME, documentTypeName);
		RIADocTypeMap riaDocTypeMap = (RIADocTypeMap) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(RIADocTypeMap.class, map);
		if (riaDocTypeMap != null 
			&& StringUtils.isNotEmpty(riaDocTypeMap.getRiaDocTypeName())) {
			return true;
		}
		return false;
	}
}
