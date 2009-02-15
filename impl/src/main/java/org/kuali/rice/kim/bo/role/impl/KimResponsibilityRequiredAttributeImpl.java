/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.role.impl;

import java.util.LinkedHashMap;

import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimResponsibilityRequiredAttributeImpl extends PersistableBusinessObjectBase implements Inactivateable {

	protected String kimResponsibilityRequiredAttributeId;
	protected String responsibilityId;
	protected String kimAttributeId;
	protected boolean active;
	
	protected KimAttributeImpl kimAttribute;
	
	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "kimResponsibilityRequiredAttributeId", kimResponsibilityRequiredAttributeId );
		m.put( "responsibilityId", responsibilityId );
		m.put( "kimAttributeId", kimAttributeId );
		return m;
	}

	public String getKimResponsibilityRequiredAttributeId() {
		return this.kimResponsibilityRequiredAttributeId;
	}

	public void setKimResponsibilityRequiredAttributeId(String kimResponsibilityRequiredAttributeId) {
		this.kimResponsibilityRequiredAttributeId = kimResponsibilityRequiredAttributeId;
	}

	public String getResponsibilityId() {
		return this.responsibilityId;
	}

	public void setResponsibilityId(String responsibilityId) {
		this.responsibilityId = responsibilityId;
	}

	public String getKimAttributeId() {
		return this.kimAttributeId;
	}

	public void setKimAttributeId(String kimAttributeId) {
		this.kimAttributeId = kimAttributeId;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public KimAttributeImpl getKimAttribute() {
		return this.kimAttribute;
	}

	public void setKimAttribute(KimAttributeImpl kimAttribute) {
		this.kimAttribute = kimAttribute;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringBuilder(java.util.LinkedHashMap)
	 */
	@Override
    public String toStringBuilder(LinkedHashMap mapper) {
        if(getKimAttribute() != null){
        	return getKimAttributeLabelFromDD(getKimAttribute().getAttributeName());
        }
        else {
            return super.toStringBuilder(mapper);
        }
    }

	protected String getKimAttributeLabelFromDD(String attributeName){
    	return KNSServiceLocator.getDataDictionaryService().getAttributeLabel(KimAttributes.class, attributeName);
    }
}
