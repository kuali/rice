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
package org.kuali.rice.kns.web.spring;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.PropertyBatchUpdateException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;

/**
 * This class is a custom bean wrapper instance to plug the spring binding
 * mechanism into the Kuali DataDictionary
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class KualiBeanWrapperImpl extends BeanWrapperImpl implements KualiBeanWrapper {

	private DocumentEntry documentEntry;
	private BusinessObjectEntry businessObjectEntry;

    public KualiBeanWrapperImpl(Object object) {
	    super(object);
    }

	/**
	 * Create new KualiBeanWrapperImpl for the given object,
	 * registering a nested path that the object is in.
	 * @param object object wrapped by this BeanWrapper
	 * @param nestedPath the nested path of the object
	 * @param superBw the containing BeanWrapper (must not be <code>null</code>)
	 */
	private KualiBeanWrapperImpl(Object object, String nestedPath, BeanWrapperImpl superBw) {
		setWrappedInstance(object, nestedPath, superBw.getWrappedInstance());
		setExtractOldValueForEditor(superBw.isExtractOldValueForEditor());
		setAutoGrowNestedPaths(superBw.isAutoGrowNestedPaths());
		setConversionService(superBw.getConversionService());
		setSecurityContext(superBw.getSecurityContext());
	}

    /**
     * This overridden method will use this class to create a new Bean Wrapper for nested beans
     * 
     * @see org.springframework.beans.BeanWrapperImpl#newNestedBeanWrapper(java.lang.Object, java.lang.String)
     */
    @Override
	protected KualiBeanWrapperImpl newNestedBeanWrapper(Object object, String nestedPath) {
		return new KualiBeanWrapperImpl(object, nestedPath, this);
	}

	public DocumentEntry getDocumentEntry() {
    	return this.documentEntry;
    }

	public void setDocumentEntry(DocumentEntry documentEntry) {
    	this.documentEntry = documentEntry;
    }

	public BusinessObjectEntry getBusinessObjectEntry() {
    	return this.businessObjectEntry;
    }

	public void setBusinessObjectEntry(BusinessObjectEntry businessObjectEntry) {
    	this.businessObjectEntry = businessObjectEntry;
    }

    @Override
	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid) throws BeansException {

		List<PropertyAccessException> propertyAccessExceptions = null;
		List<PropertyValue> propertyValues = (pvs instanceof MutablePropertyValues ? ((MutablePropertyValues) pvs).getPropertyValueList() : Arrays.asList(pvs.getPropertyValues()));
		for (PropertyValue pv : propertyValues) {
			try {
				// This method may throw any BeansException, which won't be
				// caught
				// here, if there is a critical failure such as no matching
				// field.
				// We can attempt to deal only with less serious exceptions.
				setPropertyValue(pv);
			} catch (NotWritablePropertyException ex) {
				if (!ignoreUnknown) {
					throw ex;
				}
				// Otherwise, just ignore it and continue...
			} catch (NullValueInNestedPathException ex) {
				if (!ignoreInvalid) {
					throw ex;
				}
				// Otherwise, just ignore it and continue...
			} catch (PropertyAccessException ex) {
				if (propertyAccessExceptions == null) {
					propertyAccessExceptions = new LinkedList<PropertyAccessException>();
				}
				propertyAccessExceptions.add(ex);
			}
		}

		// If we encountered individual exceptions, throw the composite
		// exception.
		if (propertyAccessExceptions != null) {
			PropertyAccessException[] paeArray = propertyAccessExceptions.toArray(new PropertyAccessException[propertyAccessExceptions.size()]);
			throw new PropertyBatchUpdateException(paeArray);
		}
	}

	@Override
	public void setPropertyValue(PropertyValue pv) throws BeansException {
		PropertyValue newPv = new PropertyValue(pv, pv.getValue());
//		registerCustomEditor(Boolean.class, "this.is.my.path", new KradBooleanBinder());
		super.setPropertyValue(newPv);
	}

}
