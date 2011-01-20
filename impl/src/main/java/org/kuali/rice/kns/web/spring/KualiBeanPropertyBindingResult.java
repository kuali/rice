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

import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.util.Assert;
import org.springframework.validation.AbstractPropertyBindingResult;
import org.springframework.validation.BeanPropertyBindingResult;

/**
 * This is a custom binding result class for use by the RICE module KRAD
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KualiBeanPropertyBindingResult extends AbstractPropertyBindingResult {

	private final Object target;
	private final boolean autoGrowNestedPaths;
	private transient KualiBeanWrapper beanWrapper;

	private DocumentEntry documentEntry;
	private BusinessObjectEntry businessObjectEntry;

	/**
	 * Creates a new instance of the {@link BeanPropertyBindingResult} class.
	 * @param target the target bean to bind onto
	 * @param objectName the name of the target object
	 */
	public KualiBeanPropertyBindingResult(Object target, String objectName) {
		this(target, objectName, true);
	}

	/**
	 * Creates a new instance of the {@link BeanPropertyBindingResult} class.
	 * @param target the target bean to bind onto
	 * @param objectName the name of the target object
	 * @param autoGrowNestedPaths whether to "auto-grow" a nested path that contains a null value
	 */
	public KualiBeanPropertyBindingResult(Object target, String objectName, boolean autoGrowNestedPaths) {
		super(objectName);
		this.target = target;
		this.autoGrowNestedPaths = autoGrowNestedPaths;
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
	public final Object getTarget() {
		return this.target;
	}

	/**
	 * Returns the {@link BeanWrapper} that this instance uses.
	 * Creates a new one if none existed before.
	 * @see #createBeanWrapper()
	 */
	@Override
	public final ConfigurablePropertyAccessor getPropertyAccessor() {
		if (this.beanWrapper == null) {
			this.beanWrapper = createBeanWrapper();
			this.beanWrapper.setExtractOldValueForEditor(true);
			this.beanWrapper.setAutoGrowNestedPaths(this.autoGrowNestedPaths);
			this.beanWrapper.setDocumentEntry(getDocumentEntry());
			this.beanWrapper.setBusinessObjectEntry(getBusinessObjectEntry());
		}
		return this.beanWrapper;
	}

	/**
	 * Create a new {@link BeanWrapper} for the underlying target object.
	 * @see #getTarget()
	 */
	protected KualiBeanWrapper createBeanWrapper() {
		Assert.state(this.target != null, "Cannot access properties on null bean instance '" + getObjectName() + "'!");
		return new KualiBeanWrapperImpl(this.target);
	}

}
