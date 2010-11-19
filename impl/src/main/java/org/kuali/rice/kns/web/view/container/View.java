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
package org.kuali.rice.kns.web.view.container;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.web.view.navigation.Navigation;

/**
 * This is a description of what this class does - jkneal don't forget to fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class View extends ContainerBase {
	private String currentPageId;
	private String stateHandler;
	
	private Object data;
	private Navigation navigation;

	private List<Page> items;
	private List<String> additionalScriptFiles;

	public View() {

	}

	/**
	 * Returns the Page instance associated with the current page id
	 * 
	 * @return
	 */
	public Page getCurrentPage() {
		return null;
	}
	
	/**
	 * @see org.kuali.rice.kns.web.view.container.ContainerBase#getSupportedComponents()
	 */
	public List<Class> getSupportedComponents() {
		List<Class> supportedComponents = new ArrayList<Class>();
		supportedComponents.add(Page.class);
		
		return supportedComponents;
	}
}
