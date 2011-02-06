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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.UifConstants.ViewType;
import org.kuali.rice.kns.uif.UifConstants.ViewTypeParameterNames;
import org.kuali.rice.kns.uif.service.ViewService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.spring.form.InquiryForm;
import org.kuali.rice.kns.web.spring.form.UifFormBase;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * This class is overloaded in order to hook in the UIF Binder classes. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class UifAnnotationMethodHandleAdapter extends AnnotationMethodHandlerAdapter {
    
    protected ViewService viewService;

    @Override
    protected ServletRequestDataBinder createBinder(HttpServletRequest request, Object target, String objectName) throws Exception {
        if (target != null) {
            // only override for UifFormBase models so that non KRAD spring MVC
            // can be used in same dispatcher servlet.
            if (target instanceof UifFormBase) {
                String viewId = request.getParameter(UifConstants.RequestParameterName.VIEW_ID);
                
				if (viewId == null) {
					String viewTypeName = request.getParameter(UifConstants.RequestParameterName.VIEW_TYPE_NAME);
					viewId = getViewService().getViewIdByType(viewTypeName,
							WebUtils.translateRequestParameterMap(request.getParameterMap()));
				}
                
                // TODO what to do here if viewId is not set, are all urls going to have
                // view id added or will we need other logic
                return new UifServletRequestDataBinder(target, objectName, viewId);
            }
        }
        return super.createBinder(request, target, objectName);
    }

    public ViewService getViewService() {
        if(viewService == null) {
            viewService = KNSServiceLocator.getViewService();
        }
        return this.viewService;
    }

    public void setViewService(ViewService viewService) {
        this.viewService = viewService;
    }

}
