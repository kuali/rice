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

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kns.web.spring.form.KualiSpringInterceptorForm;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KradAnnotationMethodHandlerAdapter extends AnnotationMethodHandlerAdapter {

	@Override
    protected ServletRequestDataBinder createBinder(HttpServletRequest request, Object target, String objectName) throws Exception {
		if (target != null) {
			if (target instanceof KualiSpringInterceptorForm) {
				KualiSpringInterceptorForm kualiForm = (KualiSpringInterceptorForm) target;
				if (kualiForm.isUsingSpring()) {
					return new KradServletRequestDataBinder(target, objectName);
				}
			}
		}
	    return super.createBinder(request, target, objectName);
    }

}
