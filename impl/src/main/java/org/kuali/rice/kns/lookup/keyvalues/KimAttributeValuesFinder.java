/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kns.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimAttributeValuesFinder extends KeyValuesBase {

	private String kimTypeName;
	private String kimAttributeName; 
	
	/**
	 * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
	 */
	public List<KeyLabelPair> getKeyValues() {
		Logger.getLogger(getClass()).warn("KimAttributeValueFinder is not implemented - code is never used.");
//		Map<String,String> criteria = new HashMap<String,String>();
//        criteria.put("name", kimTypeName);
//        Collection<KimTypeInfo> typeList = KIMServiceLocator.getTypeInfoService().getKimTypeByName(namespaceCode, kimTypeName);
//        if ( !typeList.isEmpty() ) {
//	        KimTypeInfo kimType = typeList.iterator().next();
//	        KimTypeService service = KimCommonUtils.getKimTypeService(kimType);
//	        if ( service != null ) {
//				return service.getAttributeValidValues(kimAttributeName);
//	        }
//        }
        return new ArrayList<KeyLabelPair>(0);
	}

	/**
	 * @return the kimTypeName
	 */
	public String getKimTypeName() {
		return this.kimTypeName;
	}

	/**
	 * @param kimTypeName the kimTypeName to set
	 */
	public void setKimTypeName(String kimTypeName) {
		this.kimTypeName = kimTypeName;
	}

	/**
	 * @return the kimAttributeName
	 */
	public String getKimAttributeName() {
		return this.kimAttributeName;
	}

	/**
	 * @param kimAttributeName the kimAttributeName to set
	 */
	public void setKimAttributeName(String kimAttributeName) {
		this.kimAttributeName = kimAttributeName;
	}

}
