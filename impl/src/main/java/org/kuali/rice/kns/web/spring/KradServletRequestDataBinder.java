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

import javax.servlet.ServletRequest;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestDataBinder;

/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KradServletRequestDataBinder extends ServletRequestDataBinder {

	/**
     * This constructs a ...
     * 
     * @param target
     * @param objectName
     */
    public KradServletRequestDataBinder(Object target, String objectName) {
	    super(target, objectName);
	    
	    if(target instanceof KualiForm) {
	    	((KualiForm) target).setUsingSpring(true);
	    }
    }

	/**
     * This constructs a ...
     * 
     * @param target
     */
    public KradServletRequestDataBinder(Object target) {
	    super(target);
	    
	    if(target instanceof KualiForm) {
	    	((KualiForm) target).setUsingSpring(true);
	    }
    }

	@Override
    public void bind(ServletRequest request) {
	    super.bind(request);
    }

    /**
     *  OVERRIDEN FROM DataBinder CLASS
     */

    /**
     *  FROM DataBinder CLASS
     */

    @Override
    protected void applyPropertyValues(MutablePropertyValues mpvs) {
	    super.applyPropertyValues(mpvs);
    }

	@Override
    protected void checkAllowedFields(MutablePropertyValues mpvs) {
	    super.checkAllowedFields(mpvs);
    }

	@Override
    protected void checkRequiredFields(MutablePropertyValues mpvs) {
	    super.checkRequiredFields(mpvs);
    }

	@Override
    public <T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam) throws TypeMismatchException {
	    return super.convertIfNecessary(value, requiredType, methodParam);
    }

	@Override
    public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException {
	    return super.convertIfNecessary(value, requiredType);
    }

	@Override
    public String[] getDisallowedFields() {
	    return super.getDisallowedFields();
    }

	@Override
    public String[] getRequiredFields() {
	    return super.getRequiredFields();
    }

	@Override
    protected boolean isAllowed(String field) {
	    return super.isAllowed(field);
    }

	@Override
    public void validate() {
	    super.validate();
    }

}
