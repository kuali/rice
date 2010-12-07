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
package org.kuali.rice.krad.web.jsf;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

/**
 * This is a description of what this class does - jkneal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class NullELResolver extends ELResolver {

	/**
	 * @see javax.el.ELResolver#getValue(javax.el.ELContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object getValue(ELContext context, Object base, Object property) {
		if (base == null) {
			
		}
		return null;
	}

	/**
	 * @see javax.el.ELResolver#getType(javax.el.ELContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Class<?> getType(ELContext context, Object base, Object property) {
		if (base == null) {
			
		}
		return null;
	}

	/**
	 * @see javax.el.ELResolver#setValue(javax.el.ELContext, java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setValue(ELContext context, Object base, Object property, Object value) {

	}

	/**
	 * @see javax.el.ELResolver#isReadOnly(javax.el.ELContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) {
		return false;
	}

	/**
	 * @see javax.el.ELResolver#getFeatureDescriptors(javax.el.ELContext, java.lang.Object)
	 */
	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	/**
	 * @see javax.el.ELResolver#getCommonPropertyType(javax.el.ELContext, java.lang.Object)
	 */
	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		return null;
	}

}
