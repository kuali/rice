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
package org.kuali.rice.krad.web.jsf.struts;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewDeclarationLanguage;

import org.apache.commons.lang.StringUtils;

/**
 * Custom ViewHandler implementation to fix the action URL on the generated form (for integration with struts)
 */
public class ViewHandlerImpl extends ViewHandler {
	private ViewHandler handler = null;

	public ViewHandlerImpl(ViewHandler handler) {
		this.handler = handler;
	}

	public Locale calculateLocale(FacesContext context) {
		return handler.calculateLocale(context);
	}

	public String calculateRenderKitId(FacesContext context) {
		return handler.calculateRenderKitId(context);
	}

	public UIViewRoot createView(FacesContext context, String viewId) {
		return handler.createView(context, viewId);
	}

	/**
	 * <p>
	 * Default implementation of this method assumes the request came in through the faces servlet. However
	 * for the case of integration with struts we are coming in through the struts servlet and forwarding to
	 * the JSF page. When the form is rendered on the page then the action gets set as this same struts URL.
	 * However, we need it to post back through the faces servlet (so the JSF lifecycle can take place).
	 * </p>
	 * 
	 * <p>
	 * Overridding here to return the corresponding faces servlet mapping for the JSF page (by replacing the
	 * JSF page extension with the faces servlet mapping.
	 * </p>
	 * 
	 * @see javax.faces.application.ViewHandler#getActionURL(javax.faces.context.FacesContext,
	 *      java.lang.String)
	 */
	public String getActionURL(FacesContext context, String viewId) {
		if (viewId == null || !viewId.startsWith("/")) {
			throw new IllegalArgumentException("ViewId must start with a '/': " + viewId);
		}

		ExternalContext externalContext = context.getExternalContext();
		String contextPath = externalContext.getRequestContextPath();

		// TODO: this is assuming the faces servlet mapping is .faces should be reading the configuration
		viewId = StringUtils.substringBeforeLast(viewId, ".") + ".faces";

		return contextPath + viewId;
	}

	public String getResourceURL(FacesContext context, String viewId) {
		return handler.getResourceURL(context, viewId);
	}

	public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
		handler.renderView(context, viewToRender);
	}

	public UIViewRoot restoreView(FacesContext context, String viewId) {
		return handler.restoreView(context, viewId);
	}

	public void writeState(FacesContext context) throws IOException {
		handler.writeState(context);
	}

	@Override
	public String deriveViewId(FacesContext context, String input) {
		return handler.deriveViewId(context, input);
	}

	@Override
	public String getBookmarkableURL(FacesContext context, String viewId, Map<String, List<String>> parameters,
			boolean includeViewParams) {
		return handler.getBookmarkableURL(context, viewId, parameters, includeViewParams);
	}

	@Override
	public ViewDeclarationLanguage getViewDeclarationLanguage(FacesContext context, String viewId) {
		return handler.getViewDeclarationLanguage(context, viewId);
	}

	@Override
	public String getRedirectURL(FacesContext context, String viewId, Map<String, List<String>> parameters,
			boolean includeViewParams) {
		return handler.getRedirectURL(context, viewId, parameters, includeViewParams);
	}

	@Override
	public void initView(FacesContext context) throws FacesException {
		handler.initView(context);
	}
	
	@Override
	public String calculateCharacterEncoding(FacesContext context) {
		return handler.calculateCharacterEncoding(context);
	}

	/**
	 * <p>
	 * Return the <code>ViewHandler</code> instance we are decorating.
	 * </p>
	 */
	public ViewHandler getHandler() {
		return this.handler;
	}

	/**
	 * <p>
	 * Set the <code>ViewHandler</code> instance we are decorating.
	 * </p>
	 * 
	 * @param handler
	 *            <code>ViewHandler</code> instance to decorate
	 */
	public void setHandler(ViewHandler handler) {
		this.handler = handler;
	}

}
