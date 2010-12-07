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

import java.util.Iterator;
import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * This is a description of what this class does - jkneal don't forget to fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class JsfView extends AbstractUrlBasedView {

	/**
	 * @see org.springframework.web.servlet.view.AbstractView#renderMergedOutputModel(java.util.Map,
	 *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Lifecycle facesLifecycle = createFacesLifecycle();
		boolean created = false;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext == null) {
			facesContext = createFacesContext(request, response, facesLifecycle);
			created = true;
		}

		populateRequestMap(facesContext, model);

		ViewHandler viewHandler = facesContext.getApplication().getViewHandler();
		viewHandler.initView(facesContext);

		UIViewRoot viewRoot = viewHandler.createView(facesContext, getUrl());
		viewRoot.setLocale(RequestContextUtils.getLocale(request));

		facesContext.setViewRoot(viewRoot);
		try {
			facesLifecycle.render(facesContext);
		} finally {
			if (created) {
				facesContext.release();
			}
		}
	}

	private void populateRequestMap(FacesContext facesContext, Map model) {
		Iterator i = model.keySet().iterator();
		while (i.hasNext()) {
			String key = i.next().toString();
			facesContext.getExternalContext().getRequestMap().put(key, model.get(key));
		}
	}

	private FacesContext createFacesContext(HttpServletRequest request, HttpServletResponse response,
			Lifecycle facesLifecycle) {
		FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
				.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		return facesContextFactory.getFacesContext(getServletContext(), request, response, facesLifecycle);
	}

	private Lifecycle createFacesLifecycle() {
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		return lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
	}

}
