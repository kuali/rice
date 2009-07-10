/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.document;

import org.apache.log4j.Logger;
import org.kuali.rice.kim.bo.impl.ResponsibilityImpl;
import org.kuali.rice.kim.bo.impl.ReviewResponsibility;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ReviewResponsibilityMaintainable extends KualiMaintainableImpl {

	private static final Logger LOG = Logger.getLogger( ReviewResponsibilityMaintainable.class );
	
	private static final long serialVersionUID = -8102504656976243468L;

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#setBusinessObject(org.kuali.rice.kns.bo.PersistableBusinessObject)
	 */
	@Override
	public void setBusinessObject(PersistableBusinessObject businessObject) {
		if ( LOG.isInfoEnabled() ) {
			LOG.info( "setBO called with: " + businessObject );
		}
		// TODO: probably need to convert object to the ReviewResponsibility object at this point
		super.setBusinessObject(businessObject);
	}
	/**
	 * Saves the responsibility via the responsibility update service
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
	 */
	@Override
	public void saveBusinessObject() {
		if ( LOG.isInfoEnabled() ) {
			LOG.info( "Attempting to save ReviewResponsibility BO via ResponsibilityUpdateService:" + getBusinessObject() );
		}
		// build the AttributeSet with the details
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getBoClass()
	 */
	@Override
	public Class<? extends BusinessObject> getBoClass() {
		return ReviewResponsibility.class;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#isExternalBusinessObject()
	 */
	@Override
	public boolean isExternalBusinessObject() {
		return true;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#prepareBusinessObject(org.kuali.rice.kns.bo.BusinessObject)
	 */
	@Override
	public void prepareBusinessObject(BusinessObject businessObject) {
		if ( businessObject == null ) {
			throw new RuntimeException( "Configuration ERROR: ReviewResponsibilityMaintainable.prepareBusinessObject passed a null object." );
		}
		if ( businessObject instanceof ResponsibilityImpl ) {
			KimResponsibilityImpl resp = getBusinessObjectService().findBySinglePrimaryKey(KimResponsibilityImpl.class, ((ResponsibilityImpl)businessObject).getResponsibilityId() );
			businessObject = new ReviewResponsibility( resp );
		} else if ( businessObject instanceof ReviewResponsibility ) {
			// lookup the KimResponsibilityImpl and convert to a ReviewResponsibility
			KimResponsibilityImpl resp = getBusinessObjectService().findBySinglePrimaryKey(KimResponsibilityImpl.class, ((ReviewResponsibility)businessObject).getResponsibilityId() );		
			((ReviewResponsibility)businessObject).loadFromKimResponsibility(resp);
		} else {
			throw new RuntimeException( "Configuration ERROR: ReviewResponsibilityMaintainable passed an unsupported object type: " + businessObject.getClass() );
		}
		if ( businessObject instanceof PersistableBusinessObject ) {
			setBusinessObject( (PersistableBusinessObject)businessObject );
		}
		
		super.prepareBusinessObject(businessObject);
	}
	
}
