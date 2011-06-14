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
package org.kuali.rice.krad.lookup.keyvalues;

import org.apache.log4j.Logger;
import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.service.KIMServiceLocatorWeb;
import org.kuali.rice.kim.service.support.KimTypeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimAttributeValuesFinder extends KeyValuesBase {

	private static final Logger LOG = Logger.getLogger( KimAttributeValuesFinder.class );
	
	protected String kimTypeId;
	protected String kimAttributeName; 
	
	/**
	 * @see org.kuali.rice.krad.lookup.keyvalues.KeyValuesFinder#getKeyValues()
	 */
	@Override
	public List<KeyValue> getKeyValues() {
        KimType kimType = KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId);
        if ( kimType != null ) {
	        KimTypeService service = KIMServiceLocatorWeb.getKimTypeService(kimType);
	        if ( service != null ) {
				return new ArrayList<KeyValue>(service.getAttributeValidValues(kimTypeId,kimAttributeName));
	        } 
	        LOG.error( "Unable to get type service " + kimType.getServiceName() );
        } else {
        	LOG.error( "Unable to obtain KIM type for kimTypeId=" + kimTypeId );
        }
        return Collections.emptyList();
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

	/**
	 * @return the kimTypeId
	 */
	public String getKimTypeId() {
		return this.kimTypeId;
	}

	/**
	 * @param kimTypeId the kimTypeId to set
	 */
	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}

}
