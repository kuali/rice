/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.lookup;

import java.util.Collection;
import java.util.Set;

import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.util.KRADPropertyConstants;

/**
 * The LookupResultsSupportStrategyService implementation which supports DataObjectBase objects, simply enough
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * @deprecated Only used by KNS classes, use KRAD.
 */
@Deprecated
public class DataObjectBaseLookupResultsSupportStrategyImpl
		implements LookupResultsSupportStrategyService {

	/**
	 * Returns the object id
	 *
	 * @see org.kuali.rice.kns.lookup.LookupResultsSupportStrategyService#getLookupIdForBusinessObject(org.kuali.rice.krad.bo.BusinessObject)
	 */
	@Override
    public String getLookupIdForBusinessObject(BusinessObject businessObject) {
		DataObjectBase pbo = (DataObjectBase)businessObject;
		return pbo.getObjectId();
	}

	/**
	 * Uses the BusinessObjectService to retrieve a collection of PersistableBusinessObjects
	 *
	 * @see org.kuali.rice.kns.lookup.LookupResultsSupportStrategyService#retrieveSelectedResultBOs(java.lang.String, java.lang.Class, java.lang.String)
	 */
	@Override
    public <T extends BusinessObject> Collection<T> retrieveSelectedResultBOs(Class<T> boClass, Set<String> lookupIds)
			throws Exception {

        return KradDataServiceLocator.getDataObjectService().findMatching(
                boClass,
                QueryByCriteria.Builder.fromPredicates( PredicateFactory.in(KRADPropertyConstants.OBJECT_ID, lookupIds) )).getResults();
	}


	/**
	 * Sees if the class implements the PersistableBusinessObject interface; if so, then yes, the BO qualifies!
	 * @see org.kuali.rice.kns.lookup.LookupResultsSupportStrategyService#qualifiesForStrategy(java.lang.Class)
	 */
	@Override
    public boolean qualifiesForStrategy(Class<? extends BusinessObject> boClass) {
		return DataObjectBase.class.isAssignableFrom(boClass);
	}

}
