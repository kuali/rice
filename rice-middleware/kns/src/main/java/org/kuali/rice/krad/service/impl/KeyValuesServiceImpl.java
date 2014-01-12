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
package org.kuali.rice.krad.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KeyValuesService;
import org.kuali.rice.krad.service.ModuleService;

/**
 * This class provides collection retrievals to populate key value pairs of business objects.
 */
@Deprecated
public class KeyValuesServiceImpl implements KeyValuesService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KeyValuesServiceImpl.class);

    /**
     * @see org.kuali.rice.krad.service.KeyValuesService#findAll(java.lang.Class)
     */
    @Override
	public <T> Collection<T> findAll(Class<T> clazz) {
    	ModuleService responsibleModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(clazz);
		if(responsibleModuleService!=null && responsibleModuleService.isExternalizable(clazz)){
			return (Collection<T>) responsibleModuleService.getExternalizableBusinessObjectsList((Class<ExternalizableBusinessObject>) clazz, Collections.<String, Object>emptyMap());
		}
        if (containsActiveIndicator(clazz)) {
        	return KRADServiceLocatorWeb.getLegacyDataAdapter().findMatching(clazz, Collections.singletonMap(CoreConstants.CommonElements.ACTIVE, true));
        }
        if (LOG.isDebugEnabled()) {
			LOG.debug("Active indicator not found for class " + clazz.getName());
		}
        return KRADServiceLocatorWeb.getLegacyDataAdapter().findAll(clazz);
    }

    /**
     * @see org.kuali.rice.krad.service.KeyValuesService#findAllOrderBy(java.lang.Class, java.lang.String, boolean)
     */
    @Override
	public <T> Collection<T> findAllOrderBy(Class<T> clazz, String sortField, boolean sortAscending) {
        if (containsActiveIndicator(clazz)) {
            return KRADServiceLocatorWeb.getLegacyDataAdapter().findMatchingOrderBy(clazz, Collections.singletonMap(CoreConstants.CommonElements.ACTIVE, true), sortField, sortAscending);
        }
        if (LOG.isDebugEnabled()) {
			LOG.debug("Active indicator not found for class " + clazz.getName());
		}
        return KRADServiceLocatorWeb.getLegacyDataAdapter().findMatchingOrderBy(clazz, new HashMap<String,Object>(), sortField, sortAscending);
    }

    /**
     * @see org.kuali.rice.krad.service.BusinessObjectService#findMatching(java.lang.Class, java.util.Map)
     */
    @Override
	public <T> Collection<T> findMatching(Class<T> clazz, Map<String, Object> fieldValues) {
        if (containsActiveIndicator(clazz)) {
        	// copying the map since we need to change it and don't know if it is unmodifiable
        	Map<String,Object> criteria = new HashMap<String, Object>( fieldValues );
        	criteria.put(CoreConstants.CommonElements.ACTIVE, true);
            return KRADServiceLocatorWeb.getLegacyDataAdapter().findMatching(clazz, criteria);
        }
        if (LOG.isDebugEnabled()) {
			LOG.debug("Active indicator not found for class " + clazz.getName());
		}
        return KRADServiceLocatorWeb.getLegacyDataAdapter().findMatching(clazz, fieldValues);
    }

    /**
     * Checks whether the class implements the Inactivatable interface.
     *
     * NOTE: This is different than earlier checks, as it assumes that the active flag
     * is persistent.
     *
     * @param clazz
     * @return boolean if active column is mapped for Class
     */
    private <T> boolean containsActiveIndicator(Class<T> clazz) {
    	return Inactivatable.class.isAssignableFrom(clazz);
    }

    @Override
	public <T> Collection<T> findAllInactive(Class<T> clazz) {
        if (containsActiveIndicator(clazz)) {
        	return KRADServiceLocatorWeb.getLegacyDataAdapter().findMatching(clazz, Collections.singletonMap(CoreConstants.CommonElements.ACTIVE, false));
        }
		LOG.warn("Active indicator not found for class.  Assuming all are active. " + clazz.getName());
        return Collections.emptyList();
    }

}
